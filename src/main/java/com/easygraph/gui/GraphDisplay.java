/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 26, 2015
 */

package com.easygraph.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import com.easygraph.graph.Graph;
import com.easygraph.graph.GraphUtils;
import com.easygraph.graph.Vertex;

@SuppressWarnings("serial")
public class GraphDisplay extends JComponent{
    
    private final Graph ref;

    private static final double R = 20;
    private static final double GRID_SPACING = 50;

    private static final Color BCK_COLOR = new Color(255, 252, 235);
    private static final Color REG_COLOR = new Color(185, 235, 250);
    private static final Color SEL_COLOR = Color.RED;
    private static final Color TXT_COLOR = Color.BLACK;
    private static final Color EDG_COLOR = Color.BLACK;
    private static final Color GRD_COLOR = Color.LIGHT_GRAY;

    private static final Stroke GRD_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.f}, 0.0f);
    private static final Stroke EDG_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f);

    private boolean showGrid = false;
    private boolean longSelect = false; 
    public Vertex selectedVertex;
    public static boolean antiAliasingOn = true;

    public GraphDisplay(Graph g, final EasyGraph context, GraphTab enclosing){
        this.ref = g;
        addListeners(enclosing, context);
    }

    private void addListeners(final GraphTab enclosing, final EasyGraph context){

        this.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SHIFT && !showGrid){//avoids unusefull repaints
                    showGrid = true;     
                    repaint();
                }

                if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && longSelect){
                    ref.removeVertex(selectedVertex);
                    context.notifyGraphHasChanges(enclosing);
                    longSelect = false;
                    selectedVertex = null;
                    repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SHIFT){
                    showGrid = false;
                    repaint();
                }
            }

        });

        this.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();

                Vertex found = null;
                //find selected vertex
                for(Vertex v : ref.getVertices()){
                    //compute dst
                    double sqdst = (v.posX - e.getX())*(v.posX - e.getX()) + (v.posY - e.getY())*(v.posY - e.getY());

                    if(sqdst < GraphDisplay.R*GraphDisplay.R){
                        found = v;
                        break;
                    }
                }

                boolean vertexFound = found != null;

                if(e.isPopupTrigger() && vertexFound){
                    if(longSelect){
                        if(!found.equals(selectedVertex)){
                            if(selectedVertex.getNeighbors().contains(found)){
                                selectedVertex.removeNeighbor(found);
                                found.removeNeighbor(selectedVertex);
                            }
                            else
                                ref.addEdge(selectedVertex.name, found.name);
                        }
                        context.notifyGraphHasChanges(enclosing);

                        longSelect = false;
                        selectedVertex = null;
                    }
                    else{
                        longSelect = true;
                        selectedVertex = found;  
                    }
                }
                else if(!e.isPopupTrigger() && vertexFound){
                    longSelect = false;
                    selectedVertex = found;
                }
                else{
                    longSelect = false;
                    selectedVertex = null;
                }

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(!longSelect){
                    longSelect = false;
                    selectedVertex = null;
                }
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}

        });

        this.addMouseMotionListener(new MouseMotionListener(){

            @Override
            public void mouseDragged(MouseEvent e) {
                if(selectedVertex != null){
                    if(showGrid){
                        double normalizedX = e.getX() / GRID_SPACING;
                        double normalizedY = e.getY() / GRID_SPACING;


                        int x = (int) Math.round(normalizedX);
                        int y = (int) Math.round(normalizedY);

                        selectedVertex.posX = x * GRID_SPACING;
                        selectedVertex.posY = y * GRID_SPACING;

                    }
                    else{
                        selectedVertex.posX = e.getX();
                        selectedVertex.posY = e.getY(); 
                    }
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {}

        });

    }

    public void smartScreenShot(File f) throws IOException{
        double borderSize = 50;
        
        double[] extremas = GraphUtils.findExtremas(ref);
        
        double topMost = extremas[0];
        double bottomMost = extremas[1];
        double leftMost = extremas[2];
        double rightMost = extremas[3];

        double width = 2*borderSize + (rightMost - leftMost);
        double height = 2*borderSize + (bottomMost - topMost);
        
        BufferedImage image = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        if(antiAliasingOn)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(BCK_COLOR);
        g.fillRect(0, 0, (int) width, (int) height);


        g.translate(-leftMost + 50, -topMost + 50);
        drawEdges(g);
        drawVertices(g, selectedVertex);

        g.dispose();
        
        ImageIO.write(image, "PNG", f);  
    }
    
    
    public void notifyChangesInGraph(){
        repaint();
    }

    public Dimension getPreferredSize(){
        return new Dimension(800, 600);
    }

    public void paintComponent(Graphics g0){
        Graphics2D g = (Graphics2D) g0;
        
        if(antiAliasingOn)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(BCK_COLOR);
        g.fill(getVisibleRect());

        if(showGrid)
            drawGrid(g);

        drawInfos(g);
        drawEdges(g);
        drawVertices(g, selectedVertex);
    }

    private void drawGrid(Graphics2D g){
        g.setColor(GRD_COLOR);
        g.setStroke(GRD_STROKE);

        int numX = (int) Math.ceil(getVisibleRect().width / GRID_SPACING);
        int numY = (int) Math.ceil(getVisibleRect().height / GRID_SPACING);

        for(int i = 0; i < numX; i++)
            g.draw(new Line2D.Double(i * GRID_SPACING, 0, i * GRID_SPACING, getVisibleRect().height));


        for(int i = 0; i < numY; i++)
            g.draw(new Line2D.Double(0, i*GRID_SPACING, getVisibleRect().width, i*GRID_SPACING));
    }

    private void drawInfos(Graphics2D g){
        g.setColor(TXT_COLOR);

        String vertices = "Vertices: "+ref.getNumberOfVertices();
        String edges = "Edges: "+ref.getNumberOfEdges();

        Rectangle2D vBox = g.getFontMetrics().getStringBounds(vertices, g);
        Rectangle2D eBox = g.getFontMetrics().getStringBounds(edges, g);
        Rectangle2D vis = getVisibleRect();

        int max = (int) Math.max(vBox.getWidth(), eBox.getWidth()) + 7;

        g.drawString(edges, (int) (vis.getWidth() - max), (int) (vis.getHeight() - eBox.getHeight()/4 - 7));
        g.drawString(vertices, (int) (vis.getWidth() - max), (int) (vis.getHeight() - vBox.getHeight()/4 - eBox.getHeight() - 5 - 7));  




    }

    private void drawEdges(Graphics2D g){
        g.setColor(EDG_COLOR);        
        g.setStroke(EDG_STROKE);

        for(Vertex v1 : ref.getVertices()){
            for(Vertex v2 : v1.getNeighbors()){
                if(v1.name.compareTo(v2.name) > 0){
                    g.draw(new Line2D.Double(v1.posX, v1.posY, v2.posX, v2.posY));
                }
            }
        }
    }

    private void drawVertices(Graphics2D g, Vertex selected){
        g.setStroke(EDG_STROKE);

        for(Vertex v : ref.getVertices()){     
            Rectangle2D bounds = g.getFontMetrics().getStringBounds(v.name, g);

            Ellipse2D.Double vBound = null;
            if(bounds.getWidth() + 10 > 2*R)
                vBound = new Ellipse2D.Double(v.posX - (bounds.getWidth() + 10)/2, v.posY - R, bounds.getWidth() + 10, 2*R);

            else
                vBound = new Ellipse2D.Double(v.posX - R, v.posY - R, 2*R, 2*R);

            g.setColor(v.equals(selected)? SEL_COLOR : REG_COLOR);
            g.fill(vBound);

            g.setColor(EDG_COLOR);
            g.draw(vBound);

            g.setColor(TXT_COLOR);
            g.drawString(v.name, (float) (v.posX - bounds.getWidth()/2), (float) (v.posY + bounds.getHeight()/4)); 
        }
    }

}
