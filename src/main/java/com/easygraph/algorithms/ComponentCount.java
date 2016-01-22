/*
 *	Author:      Gilbert Maystre
 *	Date:        Jan 11, 2016
 */

package com.easygraph.algorithms;

import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.easygraph.graph.Graph;
import com.easygraph.graph.Vertex;

public class ComponentCount extends GraphAlgorithm {

    private final Graph graph;
    
    public ComponentCount(JProgressBar progressBar, JLabel status, Graph graph) {
        super(progressBar, status);
        this.graph = graph;
    }

    @Override
    protected String doInBackground() throws Exception {
        postHasStarted();
        postProgress(0);
        postMessage("Preparing datastructures");
        
        final HashSet<Vertex> vertices = new HashSet<>();
        SwingUtilities.invokeAndWait(new Runnable(){
            @Override
            public void run() {
                for(Vertex v : graph.getVertices())
                    vertices.add(v);
            }
        });
        LinkedList<Vertex> queue = new LinkedList<>();
        int nbOfComponent = 0;
        int nbOfVertices = vertices.size();
        int progress = 10;
        
        postProgress(progress);
        
        if(isCancelled())
            return GraphAlgorithm.CANCEL_DEFAULT_MESSAGE;
        
        
        postMessage("Running BFS");
        while(!vertices.isEmpty()){
            Vertex spring = vertices.iterator().next();
            vertices.remove(spring);
            progress += (int) 80/nbOfVertices;
            postProgress(progress);
            
            nbOfComponent++;
            
            queue.add(spring);
            while(!queue.isEmpty()){
                if(isCancelled())
                    return GraphAlgorithm.CANCEL_DEFAULT_MESSAGE;
                
                Vertex candidate = queue.poll();
                if(vertices.remove(candidate)){
                    progress += (int) 80/nbOfComponent;
                    postProgress(progress);
                }
                    
                for(Vertex v : candidate.getNeighbors()){
                    if(vertices.contains(v))
                        queue.add(v);
                }
            }
        }
        
        postMessage("Preparing answer");
        String answer = null;
        if(nbOfComponent == 1)
            answer = "The graph is strongly connected";
        else
            answer = "There are " + nbOfComponent + " components";
        
        postProgress(100);
        
        return answer;
    }

}
