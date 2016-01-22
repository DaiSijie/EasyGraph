/*
 *	Author:      Gilbert Maystre
 *	Date:        Jan 8, 2016
 */

package com.easygraph.algorithms;

import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.easygraph.graph.Graph;
import com.easygraph.graph.Vertex;

public class BipartiteTesting extends GraphAlgorithm {

    private final Graph graph;
    
    public BipartiteTesting(JProgressBar progressBar, JLabel status, Graph graph) {
        super(progressBar, status);
        this.graph = graph;
    }

    @Override
    protected String doInBackground() throws Exception {
        
        final HashSet<Vertex> vertices = new HashSet<>();
        SwingUtilities.invokeAndWait(new Runnable(){
            @Override
            public void run() {
                for(Vertex v : graph.getVertices())
                    vertices.add(v);
            }
        });
        
        postHasStarted();
        postProgress(0);
        postMessage("Preparing datastructures");
        
        LinkedList<Vertex> queue = new LinkedList<>();
        HashSet<Vertex> blue = new HashSet<>();
        HashSet<Vertex> red = new HashSet<>();
        int nbOfVertices = vertices.size();
        String wrong = "This graph is not bipartite.";
        
        postProgress(10);
        if(isCancelled())
            return GraphAlgorithm.CANCEL_DEFAULT_MESSAGE;
        
        postMessage("Running BFS");
        while(!vertices.isEmpty()){
            Vertex spring = vertices.iterator().next();
           
            queue.add(spring);
            //assign initial color
            blue.add(spring);
            
            while(!queue.isEmpty()){
                //take all queue neigbhors
                Vertex current = queue.poll();
                vertices.remove(current);
                
                // thread info
                int progress = (int) (10 + 80*(1 - (vertices.size() / (double) nbOfVertices)));
                postProgress(progress);
                if(isCancelled())
                    return GraphAlgorithm.CANCEL_DEFAULT_MESSAGE;
                
                boolean isBlue = blue.contains(current);
                for(Vertex v : current.getNeighbors()){
                    if(blue.contains(v)){
                        if(isBlue){
                            postProgress(100);
                            return wrong;
                        }
                    }
                    else if(red.contains(v)){
                        if(!isBlue){
                            postProgress(100);
                            return wrong;
                        }
                    }
                    else{
                        if(isBlue){
                            red.add(v);
                        }
                        else{
                            blue.add(v);
                        }
                        queue.add(v);
                    }
                    
                }
            }
        }
        
        postMessage("Finalising algorithm.");
        postProgress(100);
        
        return "This graph is bipartite";
    }


}
