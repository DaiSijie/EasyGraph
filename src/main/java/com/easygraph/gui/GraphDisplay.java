/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 26, 2015
 */

package com.easygraph.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import com.easygraph.graph.Graph;
import com.easygraph.graph.GraphUtils;
import com.easygraph.graph.Vertex;

import static com.easygraph.gui.ColorTheme.*;

@SuppressWarnings("serial")
public class GraphDisplay extends JComponent{

    /*
     * LEFT CLICK: adding edges
     * RIGHT CLICK: selecting vertex to delete or move them
     */

    private final Graph graph;

    public static final Dimension FLAWLESS_DIMENSION = new Dimension(800, 627);
    public static final double R = 20;
    private static final double GRID_SPACING = 50;

    //display pref
    private boolean showGrid = false;
    public static boolean antiAliasingOn = true;
    
    //logic behind selection
    private Vertex edgeVertex;

    private boolean movingSomeVertex;
    private Vertex leader;
    private Set<Vertex> selectedVertices = new HashSet<>();

    private boolean showSelectionRectangle;
    private Point2D startOfMultiSelect;
    private Point2D endOfMultiSelect;

    public GraphDisplay(Graph g, final EasyGraph context, GraphTab enclosing){
        this.graph = g;
        putToNormalState();
        addListeners(enclosing, context);
    }

    private void addListeners(final GraphTab enclosing, final EasyGraph context){

        this.addFocusListener(new FocusAdapter(){
            @Override
            public void focusLost(FocusEvent e) {
                putToNormalState();
                repaint();              
            }
        });
        
        this.addKeyListener(new KeyAdapter(){

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ALT && !showGrid){//avoids unusefull repaints
                    showGrid = true;     
                    repaint();
                }
                if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    for(Vertex v : selectedVertices)
                        graph.removeVertex(v);

                    //put everything back to normal
                    selectedVertices.clear();

                    context.notifyGraphHasChanges(enclosing);
                    repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ALT){
                    showGrid = false;
                    repaint();
                }
            }

        });

        this.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();

                Vertex found = findVertexUnderMouse(e.getPoint());
                boolean vertexFound = found != null;
                boolean leftClick = e.isPopupTrigger();
                boolean rightClick = !leftClick; //pretty naïve implementation: to see again

                if(leftClick){
                    //EDGE INSERTION
                    if(vertexFound){
                        Vertex temp = edgeVertex;
                        putToNormalState();
                        boolean alreadyOneEdgeSelected = temp != null;
                        if(alreadyOneEdgeSelected){
                            //CREATE A NEW EDGE
                            if(!temp.equals(found)){
                                if(temp.getNeighbors().contains(found)){
                                    temp.removeNeighbor(found);
                                    found.removeNeighbor(temp);
                                }
                                else{
                                    graph.addEdge(temp.name, found.name);
                                }
                                enclosing.setHasChanges(true);
                                context.notifyGraphHasChanges(enclosing);
                            } 
                            edgeVertex = null;
                        }
                        else{
                            //JUST STORE THE FOUND ONE
                            edgeVertex = found;
                        }
                    }
                    //If no vertex was found, just put to normal state
                    else{
                        putToNormalState();
                    }
                }
                else if(rightClick){
                    edgeVertex = null;
                    
                    //VERTEX SELECTION
                    if(vertexFound){
                        if(e.isShiftDown()){
                            if(selectedVertices.contains(found))
                                selectedVertices.remove(found);
                            else
                                selectedVertices.add(found);
                        }
                        else{
                            movingSomeVertex = true;
                            leader = found;

                            if(!selectedVertices.contains(leader)){
                                selectedVertices.clear();
                                selectedVertices.add(leader);
                            }
                        }
                    }
                    else{                        
                        putToNormalState();
                        startOfMultiSelect = e.getPoint();
                        endOfMultiSelect = e.getPoint();
                        showSelectionRectangle = true;
                    }
                }

                repaint();

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showSelectionRectangle = false;

                if(movingSomeVertex){
                    putToNormalState();
                }

                repaint();
            }

        });

        this.addMouseMotionListener(new MouseMotionListener(){

            @Override
            public void mouseDragged(MouseEvent e) {
                if(movingSomeVertex){                    
                    double dx = 0;
                    double dy = 0;
                    
                    if(showGrid){
                        double normalizedX = e.getX() / GRID_SPACING;
                        double normalizedY = e.getY() / GRID_SPACING;

                        int x = (int) Math.round(normalizedX);
                        int y = (int) Math.round(normalizedY);

                        dx = leader.posX - x * GRID_SPACING;
                        dy = leader.posY - y * GRID_SPACING;
                    }
                    else{
                        dx = leader.posX - e.getX();
                        dy = leader.posY - e.getY();
                    }
                    
                    for(Vertex v : selectedVertices){
                        v.posX -= dx;
                        v.posY -= dy;
                    }

                                        
                    repaint();
                }

                if(showSelectionRectangle){
                    endOfMultiSelect = e.getPoint();
                    recomputeMultiSelectedVertices();
                    repaint();
                }  
            }

            @Override
            public void mouseMoved(MouseEvent e) {}

        });

    }

    private void putToNormalState(){
        edgeVertex = null;
        movingSomeVertex = false;
        leader = null;
        selectedVertices.clear();
        showSelectionRectangle = false;
        startOfMultiSelect = null;
        endOfMultiSelect = null;
    }
    
    private Vertex findVertexUnderMouse(Point mouse){
        for(Vertex v : graph.getVertices()){
            if(v.representation.contains(mouse))
                return v;
        }
        return null;
    }
     
    private void recomputeMultiSelectedVertices(){
        double x = Math.min(startOfMultiSelect.getX(), endOfMultiSelect.getX());
        double y = Math.min(startOfMultiSelect.getY(), endOfMultiSelect.getY());

        double width = Math.abs(startOfMultiSelect.getX() - endOfMultiSelect.getX());
        double height = Math.abs(startOfMultiSelect.getY() - endOfMultiSelect.getY());

        selectedVertices.clear();


        Rectangle2D.Double rect = new Rectangle2D.Double(x, y, width, height);




        for(Vertex v : graph.getVertices()){

            if(v.representation.intersects(rect)){
                selectedVertices.add(v);
            }
        }
    }

    public void smartScreenshot(File where) throws IOException{
        double borderSize = 30;

        double[] extremas = GraphUtils.findExtremas(graph);

        double topMost = extremas[0];
        double bottomMost = extremas[1];
        double leftMost = extremas[2];
        double rightMost = extremas[3];

        double width = 2*borderSize + (rightMost - leftMost);
        double height = 2*borderSize + (bottomMost - topMost);

        BufferedImage image = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(BCK_COLOR);
        g.fillRect(0, 0, (int) width, (int) height);


        g.translate(-leftMost + borderSize, -topMost + borderSize);
        drawEdges(g);
        drawVertices(g, false);

        g.dispose();

        ImageIO.write(image, "PNG", where);  
    }

    public void screenshot(File where) throws IOException{
        BufferedImage image = new BufferedImage(getVisibleRect().width, getVisibleRect().height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();        
        paintComponent(g);
        g.dispose();
        ImageIO.write(image, "PNG", where);  
    }

    public void notifyChangesInGraph(){
        repaint();
    }

    public Dimension getPreferredSize(){
        return FLAWLESS_DIMENSION;
    }

    public void paintComponent(Graphics g0){
        Graphics2D g = (Graphics2D) g0;

        if(antiAliasingOn)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(BCK_COLOR);
        g.fill(getVisibleRect());

        if(showGrid)
            drawGrid(g);

        drawEdges(g);
        drawVertices(g, true);
        drawInfos(g);

        if(showSelectionRectangle)
            paintMultiSelect(g);
    }

    private void paintMultiSelect(Graphics2D g){        
        double x = Math.min(startOfMultiSelect.getX(), endOfMultiSelect.getX());
        double y = Math.min(startOfMultiSelect.getY(), endOfMultiSelect.getY());

        double width = Math.abs(startOfMultiSelect.getX() - endOfMultiSelect.getX());
        double height = Math.abs(startOfMultiSelect.getY() - endOfMultiSelect.getY());

        RoundRectangle2D.Double rec = new RoundRectangle2D.Double(x, y, width, height, 9, 9);

        g.setColor(new Color(SQUARE_SELECT_COLOR.getRed(), SQUARE_SELECT_COLOR.getGreen(), SQUARE_SELECT_COLOR.getBlue(), 110));
        
        g.fill(rec);
        g.setColor(SQUARE_SELECT_COLOR);
        g.draw(rec);
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

        String vertices = "Vertices: "+graph.getNumberOfVertices();
        String edges = "Edges: "+graph.getNumberOfEdges();

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

        for(Vertex v1 : graph.getVertices()){
            for(Vertex v2 : v1.getNeighbors()){
                if(v1.name.compareTo(v2.name) > 0){
                    g.draw(new Line2D.Double(v1.posX, v1.posY, v2.posX, v2.posY));
                }
            }
        }
    }

    private void drawVertices(Graphics2D g, boolean followColorCode){        
        g.setStroke(EDG_STROKE);

        for(Vertex v : graph.getVertices()){    
            Rectangle2D bounds = g.getFontMetrics().getStringBounds(v.name, g);
            if(bounds.getWidth() + 10 > 2*R)
                v.representation = new Ellipse2D.Double(v.posX - (bounds.getWidth() + 10)/2, v.posY - R, bounds.getWidth() + 10, 2*R);
            else
                v.representation = new Ellipse2D.Double(v.posX - R, v.posY - R, 2*R, 2*R);

            if(followColorCode){
                if(v.equals(edgeVertex))
                    g.setColor(EDGE_ADD_COLOR);
                else if(selectedVertices.contains(v))
                    g.setColor(SEL_COLOR);
                else
                    g.setColor(REG_COLOR);
            }
            else{
                g.setColor(REG_COLOR);
            }

            g.fill(v.representation);

            g.setColor(EDG_COLOR);
            g.draw(v.representation);

            g.setColor(TXT_COLOR);
            g.drawString(v.name, (float) (v.posX - bounds.getWidth()/2), (float) (v.posY + bounds.getHeight()/4)); 
        }
    }

}
