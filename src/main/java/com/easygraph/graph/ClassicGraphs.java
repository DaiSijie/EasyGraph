/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 30, 2015
 */

package com.easygraph.graph;

import java.util.ArrayList;
import java.util.List;

public final class ClassicGraphs {

    private ClassicGraphs(){}
    
    public static Graph createComplete(int order){
        if(order < 1)
            throw new IllegalArgumentException("A graph should have at least one vertex");
        
        Graph toReturn = new Graph();
        
        ArrayList<Vertex> vs = new ArrayList<>();
        
        for(int i = 0; i < order; i++){
            Vertex curr = new Vertex(getNameForInt(i));
            toReturn.addVertex(curr);
            vs.add(curr);
        }
        
        for(Vertex v1 : vs){
            for(Vertex v2 : vs){
                if(!v1.equals(v2)){
                    v1.addNeighbor(v2);
                    v2.addNeighbor(v1);
                }
            }
        }
        
        
        putInRound(vs, 200, 200, 150);
        
        return toReturn;    
    }
    
    public static Graph createCyclic(int order){
        if(order < 3)
            throw new IllegalArgumentException("A cyclic graph should have at least 3 vertices");
        
        Graph toReturn = new Graph();
        
        ArrayList<Vertex> vs = new ArrayList<>();
        
        for(int i = 0; i < order; i++){
            Vertex curr = new Vertex(getNameForInt(i));
            toReturn.addVertex(curr);
            vs.add(curr);
        }
        
        for(int i = 0; i < order; i++){
            int from = i;
            int to = (i + 1)%order;
            
            vs.get(from).addNeighbor(vs.get(to));
            vs.get(to).addNeighbor(vs.get(from));
        }
        
        putInRound(vs, 200, 200, 150);
                
        return toReturn;  
    }
    
    private static String getNameForInt(int order){
        if(order > 26*26 + 25)
            throw new IllegalArgumentException("Should implement for more variables...");
        
        StringBuilder bd = new StringBuilder();
        
        int unit = order % 26;
        
        if(order > 25){
            int dix = (order - unit) / 26 - 1;
            char dixChar = (char) (dix + ((int) 'a'));
            bd.append(dixChar);
        }
        
        char unitChar = (char) (unit + ((int) 'a'));
        bd.append(unitChar);
        
        return bd.toString();
    }
    
    private static void putInRound(List<Vertex> vs, double centerX, double centerY, double radius){
        double angleStep = 2*Math.PI/vs.size();
        for(int i = 0; i < vs.size(); i++){
            double x = centerX + Math.cos(angleStep * i) * radius;
            double y = centerY - Math.sin(angleStep * i) * radius;
            
            vs.get(i).posX = x;
            vs.get(i).posY = y;
        }
    }
    
    
}
