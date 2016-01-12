/*
 *	Author:      Gilbert Maystre
 *	Date:        Jan 8, 2016
 */

package com.easygraph.algorithms;

import java.awt.Frame;

import com.easygraph.graph.Graph;

public class BipartiteTesting extends GraphAlgorithm {

    public BipartiteTesting(Graph g, Frame context){
        super(context);
    }
    
    @Override
    protected String doInBackground(){
        try {
            for(int i = 1; i <= 10; i++){
                
                if(getProgressMonitor().isCanceled()){
                }                
                
                Thread.sleep(2000);
                setProgress(10*i);
                postMessage("Processing: "+(10*i)+"%.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return "Graph is bipartite :-)";
    }

    @Override
    public String getAlgorithmDescription() {
        return "Testing if the graph is bipartite";
    }

}
