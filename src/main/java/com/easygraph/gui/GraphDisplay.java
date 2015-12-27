/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 26, 2015
 */

package com.easygraph.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.easygraph.graph.Graph;
import com.easygraph.graph.Vertex;

@SuppressWarnings("serial")
public class GraphDisplay extends JComponent{

    public final static double R = 25;

    private static final Color BCK_COLOR = Color.LIGHT_GRAY;

    private static final Color REG_COLOR = Color.BLUE;
    private static final Color SEL_COLOR = Color.RED;
    private static final Color TXT_COLOR = Color.BLACK;

    private static final Color EDG_COLOR = Color.BLACK;

    private final Graph ref;
    
    public Vertex selectedVertex;
    
    public GraphDisplay(Graph g){
        this.ref = g;
        
        this.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Hello");
                
                //find selected vertex
                for(Vertex v : ref.getVertices()){
                    //compute dst
                    double sqdst = (v.posX - e.getX())*(v.posX - e.getX()) + (v.posY - e.getY())*(v.posY - e.getY());
                    
                    System.out.println("SQd"+sqdst);
                    
                    if(sqdst < GraphDisplay.R*GraphDisplay.R){
                        System.out.println("Selected :_)");
                        selectedVertex = v;
                        return;
                    }
                    
                    selectedVertex = null;
                }
                
                repaint();
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectedVertex = null;
                repaint();
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        this.addMouseMotionListener(new MouseMotionListener(){

            @Override
            public void mouseDragged(MouseEvent e) {
                if(selectedVertex != null){
                    selectedVertex.posX = e.getX();
                    selectedVertex.posY = e.getY();
                }
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        
    }

    public void notifyChangesInGraph(){
        repaint();
    }
    
    public void paintComponent(Graphics g0){
        Graphics2D g = (Graphics2D) g0;

        g.setColor(BCK_COLOR);
        g.fill(getVisibleRect());

        drawEdges(g);
        drawVertices(g, selectedVertex);
    }

    public void drawEdges(Graphics2D g){
        g.setColor(EDG_COLOR);
        for(Vertex v1 : ref.getVertices()){
            for(Vertex v2 : v1.getNeighbors()){
                if(v1.name.compareTo(v2.name) > 0){
                    g.draw(new Line2D.Double(v1.posX, v1.posY, v2.posX, v2.posY));
                }
            }
        }
    }
    
    

    public void drawVertices(Graphics2D g, Vertex selected){
        for(Vertex v : ref.getVertices()){
            
            if(v != null && selected != null)
                System.out.println("Equality on: "+v.name+" and "+selected.name+" ?? "+v.equals(selected));
            
            
            
            g.setColor(v.equals(selected)? SEL_COLOR : REG_COLOR);
            g.fill(new Ellipse2D.Double(v.posX - R, v.posY - R, 2*R, 2*R));

            g.setColor(TXT_COLOR);
            Rectangle2D bounds = g.getFontMetrics().getStringBounds(v.name, g);
            g.drawString(v.name, (float) (v.posX - bounds.getWidth()/2), (float) (v.posY + bounds.getHeight()/4)); 
        }
    }


    public Dimension getPreferredSize(){
        return new Dimension(800, 600);
    }

}
