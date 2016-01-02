/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 25, 2015
 */

package com.easygraph.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Graph {

    private final Map<String, Vertex> vertices;
    
    public Graph(){
        this.vertices = new HashMap<>();
    }
    
    public void addVertex(String v){
        if(v.equals(""))
            throw new IllegalArgumentException("The empty string is not a valid vertex name.");
        
        if(vertices.containsKey(v))
            throw new IllegalArgumentException("A vertex with the same name already exists.");
        
        vertices.put(v, new Vertex(v));
    }
    
    public void addVertex(Vertex v){
        if(vertices.keySet().contains(v.name))
            throw new IllegalArgumentException("Vertex already existing");
        
        vertices.put(v.name, v);
    }
    
    public void addEdge(String n1, String n2){
        Vertex v1 = getVertex(n1);
        Vertex v2 = getVertex(n2);
        
        if(v1.equals(v2))
            throw new IllegalArgumentException("Cannot have an edge from and to the same node.");
        
        if(v1.getNeighbors().contains(v2))
            throw new IllegalArgumentException("Edge already exists.");
        
        //finally...
        v1.addNeighbor(v2);
        v2.addNeighbor(v1);
    }
    
    public void removeVertex(Vertex v){
        if(!vertices.containsKey(v.name))
            throw new IllegalArgumentException("Unknown vertex");
        
        for(Vertex v2 : v.getNeighbors()){
            v2.removeNeighbor(v);
        }
        
        vertices.remove(v.name);
    }
    
    public Collection<Vertex> getVertices(){
        return vertices.values();
    }
    
    public Vertex getVertex(String name){
        if(!vertices.containsKey(name))
            throw new IllegalArgumentException("Unknown vertex name: \""+name+"\".");
        
        return vertices.get(name);
    }
    
    public int getNumberOfVertices(){
        return vertices.size();
    }
    
    public int getNumberOfEdges(){
        int sum = 0;
        for(Vertex v : vertices.values())
            sum += v.getNeighbors().size();
        
        return sum/2;
    }
    
    public void invert(){
        for(Vertex v1 : getVertices()){
            for(Vertex v2 : getVertices()){
                if(!v1.equals(v2)){
                    if(v1.getNeighbors().contains(v2))
                        v1.removeNeighbor(v2);
                    else v1.addNeighbor(v2);
                }
            }
        }
    }
       
}
