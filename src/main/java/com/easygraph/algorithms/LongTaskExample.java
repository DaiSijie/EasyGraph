/*
 *	Author:      Gilbert Maystre
 *	Date:        Jan 9, 2016
 */

package com.easygraph.algorithms;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.easygraph.graph.Graph;

public class LongTaskExample extends GraphAlgorithm {

    private final Graph g;
    
    public LongTaskExample(JProgressBar progressBar, JLabel status, Graph g) {
        super(progressBar, status);
        this.g = g;
    }

    @Override
    protected String doInBackground() throws Exception {
        
        postHasStarted();
        
        for(int i = 1; i <= 100; i++){
            Thread.sleep(100);
            postProgress(i);
            
            if(isCancelled()){
                System.out.println("Ok! I'm cancelled!");
                return GraphAlgorithm.CANCEL_DEFAULT_MESSAGE;
            }
            
            if(i == 20)
                postMessage("Nucleotisation des rollers...");
            if(i == 60)
                postMessage("Mais bien sur, c'est clair!");
            if(i == 90)
                postMessage("Almost done...");
        }
        
        return "There are exactly " + g.getNumberOfVertices() + " vertices.";
    }

}
