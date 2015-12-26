/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 25, 2015
 */

package com.easygraph.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {

    private final Map<String, Vertex> vertices;
    
    public Graph(){
        this.vertices = new HashMap<>();
    }
    
    public void addVertex(Vertex v){
        vertices.put(v.name, v);
    }
    
    public Collection<Vertex> getVertices(){
        return vertices.values();
    }
    
    public Vertex getVertex(String name){
        return vertices.get(name);
    }
    
    
    
    
    
    
    
}
