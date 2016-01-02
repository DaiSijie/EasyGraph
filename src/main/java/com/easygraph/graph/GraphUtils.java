/*
 *	Author:      Gilbert Maystre
 *	Date:        Jan 2, 2016
 */

package com.easygraph.graph;

public class GraphUtils {

    /**
     *  Values:       toReturn[0] = topMost;
     *                toReturn[1] = bottomMost;
     *                toReturn[2] = leftMost;
     *                toReturn[3] = rightMost;
     * @param g
     * @return
     */
    public static double[] findExtremas(Graph g){
        double[] toReturn = new double[4];
        
        double topMost = Double.MAX_VALUE;
        double bottomMost = Double.MIN_VALUE;
        double leftMost = Double.MAX_VALUE;
        double rightMost = Double.MIN_VALUE;
        
        for(Vertex v : g.getVertices()){
            if(v.posY < topMost)
                topMost = v.posY;
            if(v.posY > bottomMost)
                bottomMost = v.posY;
            if(v.posX < leftMost)
                leftMost = v.posX;
            if(v.posX > rightMost)
                rightMost = v.posX;
        }
        
        toReturn[0] = topMost;
        toReturn[1] = bottomMost;
        toReturn[2] = leftMost;
        toReturn[3] = rightMost;
        
        return toReturn;
    }
    
    
    
}
