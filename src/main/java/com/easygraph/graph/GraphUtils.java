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
            double top = v.posY - v.representation.getHeight()/2;
            double bot = v.posY + v.representation.getHeight()/2;
            double left = v.posX - v.representation.getWidth()/2;
            double right = v.posX + v.representation.getWidth()/2;
            
            if(top < topMost)
                topMost = top;
            if(bot > bottomMost)
                bottomMost = bot;
            if(left < leftMost)
                leftMost = left;
            if(right > rightMost)
                rightMost = right;
        }
        
        toReturn[0] = topMost;
        toReturn[1] = bottomMost;
        toReturn[2] = leftMost;
        toReturn[3] = rightMost;
        
        return toReturn;
    }
    
    
    
}
