/*
 *	Author:      Gilbert Maystre
 *	Date:        Mar 8, 2016
 */

package com.easygraph.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.easygraph.gui.GraphDisplay;

public class PermutationGraph {
    
    public static Graph createPermutation(int n){
        if (n < 1)
           throw new IllegalArgumentException("A graph should have at least one vertex");
        
        Set<String> tokens = new HashSet<>();
        for(int i = 1; i <= n; i++){
            tokens.add(""+i);
        }
        
        Set<String> names = genVerticesName(tokens);
        
        Graph g = new Graph();
    
        ArrayList<Vertex> vs = new ArrayList<>();
        
        for(String s : names){
            Vertex curr = new Vertex(s);
            g.addVertex(curr);
            vs.add(curr);
        }
        
        for(Vertex v1 : vs){
            for(Vertex v2 : vs){
                if(!v1.equals(v2) && differInTwoPlaces(v1.name, v2.name)){
                    v1.addNeighbor(v2);
                    v2.addNeighbor(v1);
                }
            }
        }
                
        ClassicGraphs.putInRound(vs, GraphDisplay.FLAWLESS_DIMENSION.width/2, GraphDisplay.FLAWLESS_DIMENSION.height/2, 150);

        return g;
    }
    
    private static boolean differInTwoPlaces(String s1, String s2){
        if(s1.length() != s2.length())
            return false;
        int counter = 0;
        for(int i = 0; i < s1.length(); i++){
            if(s1.charAt(i) != s2.charAt(i))
                counter++;
        }
        
        return counter == 2;
    }
    
    private static Set<String> genVerticesName(Set<String> mix){
        if(mix.size() == 1){
            return mix;
        }
        else{
            Set<String> toReturn = new HashSet<>();
            for(String x : mix){
                Set<String> copy = new HashSet<>(mix);
                copy.remove(x);
                copy = genVerticesName(copy);
                for(String y: copy){
                    toReturn.add(x + y);
                }
            }
            
            return toReturn;
        } 
    }
    
    
    


}
