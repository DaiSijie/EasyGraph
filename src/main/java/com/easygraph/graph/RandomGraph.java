/*
 *	Author:      Gilbert Maystre
 *	Date:        Mar 24, 2016
 */

package com.easygraph.graph;

import java.util.ArrayList;
import java.util.Random;

import com.easygraph.gui.GraphDisplay;

public class RandomGraph {

    private static final Random rand = new Random();
    
    /**
     * Creates a random graph
     * @param min The minimum number of vertices (> 0)
     * @param max The maximum number of vertices (>= min)
     * @param p The probability of having an edge between two vertices in [0,1]
     * @return
     */
    public static Graph createRandomGraph(int min, int max, double p){
        //Check args
        boolean minOk = min > 0;
        boolean maxOk = max >= min;
        boolean pOk = 0 <= p && p <= 1;
        
        if(!minOk || !maxOk || !pOk)
            throw new IllegalArgumentException("Argument out of range!");
        
        
        int nOfVertices = min + rand.nextInt((max - min) + 1);
        Graph g = new Graph();
        
        ArrayList<Vertex> vs = new ArrayList<>();
        for(int i = 0; i < nOfVertices; i++){
            Vertex current = new Vertex(ClassicGraphs.getNameForInt(i));
            g.addVertex(current);
            vs.add(current);
        }
        
        for(Vertex v1: vs){
            for(Vertex v2: vs){
                if(v1.name.compareTo(v2.name) > 0){
                    if(rand.nextDouble() <= p){
                        v1.addNeighbor(v2);
                        v2.addNeighbor(v1);
                    }
                }
            }
        }

        ClassicGraphs.putInRound(vs, GraphDisplay.FLAWLESS_DIMENSION.width/2, GraphDisplay.FLAWLESS_DIMENSION.height/2, 150);

        return g;

    }
    
    
    
}
