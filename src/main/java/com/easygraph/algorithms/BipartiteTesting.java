/*
 *	Author:      Gilbert Maystre
 *	Date:        Jan 8, 2016
 */

package com.easygraph.algorithms;

import java.awt.Frame;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.easygraph.graph.Graph;
import com.easygraph.graph.Vertex;
import com.easygraph.gui.DialogsUtility;
import com.easygraph.gui.GraphDisplay;

public class BipartiteTesting extends GraphAlgorithm {

    private final Graph graph;
    private final Frame ancestor;
    
    public BipartiteTesting(JProgressBar progressBar, JLabel status, Graph graph, Frame ancestor) {
        super(progressBar, status);
        this.graph = graph;
        this.ancestor = ancestor;
    }

    @Override
    protected String doInBackground() throws Exception {
        
        System.out.println("Started process...");
        
        
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
        final HashSet<Vertex> blue = new HashSet<>();
        final HashSet<Vertex> red = new HashSet<>();
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
        postProgress(95);
                
        SwingUtilities.invokeAndWait(new Runnable(){
            @Override
            public void run() {
                boolean tidy = DialogsUtility.yesNo("Do you want to move the vertices to their respective groups?", ancestor) == 0;
                
                if(tidy){
                    double marginX = 50;
                    double marginY = 100;
                    
                    //replacing blue
                    double sizep  = (blue.size()-1) * marginX;
                    double startX = (GraphDisplay.FLAWLESS_DIMENSION.width - sizep)/2;
                    double y = (GraphDisplay.FLAWLESS_DIMENSION.height - marginY)/2;
                    
                    int i = 0;
                    for(Vertex v : blue){
                        v.posX = startX + marginX * (i++);
                        v.posY = y;
                    }
                    
                    //replacing red
                    sizep  = (red.size()-1) * marginX;
                    startX = (GraphDisplay.FLAWLESS_DIMENSION.width - sizep)/2;
                    y = (GraphDisplay.FLAWLESS_DIMENSION.height + marginY)/2;
                    
                    i = 0;
                    for(Vertex v : red){
                        v.posX = startX + marginX * (i++);
                        v.posY = y;
                    }

                    ancestor.invalidate();
                }
            }
        });
        
        postProgress(100);
        
        return "This graph is bipartite";
    }


}
