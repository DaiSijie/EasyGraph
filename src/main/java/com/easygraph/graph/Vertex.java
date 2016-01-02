/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 25, 2015
 */

package com.easygraph.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Vertex {

    public final String name;
    private final Set<Vertex> neighbors;
    public double posX = 0;
    public double posY = 0;

    public Vertex(String name){
        Random rand = new Random();
        posX = rand.nextInt(400);
        posY = rand.nextInt(400);
        
        this.name = name;
        this.neighbors = new HashSet<>();
    }
    
    public void removeNeighbor(Vertex v){
        neighbors.remove(v);
    }
    
    public void addNeighbor(Vertex v){
        if(this.equals(v))
            throw new IllegalArgumentException("a vertice cannot be it's own neighbor");
        
        neighbors.add(v);
    }
    
    public Set<Vertex> getNeighbors(){
        return Collections.unmodifiableSet(neighbors);
    }
    
    public int degree(){
        return neighbors.size();
    }
    
    //tests only on name
    public boolean equals(Object other){
        if(other == null || ! (other instanceof Vertex) || ! name.equals(((Vertex)other).name))
            return false;
        
        return true;
        
    }
    
    public int hashCode(){
        return name.hashCode();
    }
    
    
    
    
    
}
