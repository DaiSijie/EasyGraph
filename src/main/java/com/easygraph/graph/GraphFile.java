/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 25, 2015
 */

package com.easygraph.graph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class GraphFile {

    public static Graph fetchGraph(File f) throws ParseException, IOException{        
        String toParse = FileUtils.readFileToString(f, "UTF-8");
        
        int count = StringUtils.countMatches(toParse, ":");
        if(count != 2)
            throw new ParseException("three parts should be present : tags:vertices:relations", 0);
        

        String splitted[] = new String[3];
        String[] temp = toParse.split(":");
        
        if(toParse.endsWith(":")){//no edges
            splitted[0] = temp[0];
            splitted[1] = temp[1];
            splitted[2] = "";
        }
        else{
            splitted = temp;
        }

        Map<String, String> tags = getTags(splitted[0]);
        String vertices = splitted[1];
        String relations = splitted[2];
        
        if(!tags.containsKey("version"))
            throw new ParseException("version tag is missing", 0);
        
        if(tags.get("version").equals("1"))
            return parseGraphV1(vertices, relations);
        else
            throw new ParseException("unknown graph version: "+tags.get("version"), 0);
    }
    
    private static Graph parseGraphV1(String vertices, String relations) throws ParseException{
        Graph toReturn = new Graph();
        
        HashMap<String, Vertex> v = new HashMap<>();//datastrucure to avoid 
        for(String name : vertices.split(";")){
            toReturn.addVertex(name);
            v.put(name, toReturn.getVertex(name));
        }
        
        for(String rel : relations.split(";")){
            String[] splitted = rel.split(",");
            if(splitted.length != 2)
                throw new ParseException("invalid relation, correct format is a,b", 0);
            
            String a = splitted[0];
            String b = splitted[1];
            
            if(!v.keySet().contains(a) || !v.keySet().contains(b))
                throw new ParseException("invalid vertex name: <" + a + "> or <" + b + ">", 0);
            
            if(a.equals(b))
                throw new ParseException("edge from a vertex to itself: <" + a + ">", 0);
            
            //else add the relations in the vertices
            v.get(a).addNeighbor(v.get(b));
            v.get(b).addNeighbor(v.get(a));  
        }

        return toReturn;
    }
    
    private static Map<String, String> getTags(String tags) throws ParseException{
        Map<String, String> toReturn = new HashMap<>();
        
        for(String s : tags.split(";")){
            String[] splitted = s.split("=");
            if(splitted.length != 2)
                throw new ParseException("Trouble while reading tag format is key=value", 0);
            
            toReturn.put(splitted[0], splitted[1]);
        }
        
        return toReturn;
    }
    
    public static void flushGraph(File f, Graph g) throws IOException, ParseException{
        StringBuilder str = new StringBuilder("version=1:");
        StringBuilder edgeBuilder = new StringBuilder();
        
        if(g.getVertices().size() == 0)
            throw new ParseException("The graph should contain at least one vertex to be saved", 0);
        
        //add the vertices
        boolean firstVertex = true;
        boolean firstEdge = true;
        for(Vertex v : g.getVertices()){
            if(!firstVertex)
                str.append(";");
            else
                firstVertex = false;
            
            str.append(v.name);
            
            //add the edges
            for(Vertex n : v.getNeighbors()){
                if(n.name.compareTo(v.name) > 0){//this ensure that there is no doubling of edges
                    if(!firstEdge)
                        edgeBuilder.append(";");
                    else
                        firstEdge = false;
                    
                    edgeBuilder.append(n.name).append(",").append(v.name);
                }
            }
        }
        
        str.append(":").append(edgeBuilder);
        
        //the graph is baked!
        String graph = str.toString();
        
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(graph.getBytes(Charset.forName("UTF-8")));
        fos.close();
    }

}
