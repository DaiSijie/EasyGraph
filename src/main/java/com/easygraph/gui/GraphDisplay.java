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
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

@SuppressWarnings("serial")
public class GraphDisplay extends JComponent{


    /*
     * LEFT CLICK: adding edges
     * RIGHT CLICK: selecting vertex to delete or move them
     */

    private final Graph ref;

    public static final Dimension FLAWLESS_DIMENSION = new Dimension(800, 627);
    public static final double R = 20;
    private static final double GRID_SPACING = 50;

    private static final Color BCK_COLOR = new Color(255, 252, 235);
    private static final Color REG_COLOR = new Color(185, 235, 250);
    private static final Color SEL_COLOR = new Color(219, 70, 70);
    private static final Color TXT_COLOR = Color.BLACK;
    private static final Color EDG_COLOR = Color.BLACK;
    private static final Color GRD_COLOR = Color.LIGHT_GRAY;

    private static final Color EDGE_ADD_COLOR = new Color(168, 247, 146);

    private static final Stroke GRD_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.f}, 0.0f);
    private static final Stroke EDG_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f);

    private boolean showGrid = false;

    private Vertex edgeVertex = null;

    private boolean movingSomeVertex = false;
    private Vertex leader = null;
    private Set<Vertex> selectedVertices = new HashSet<>();

    private boolean showSelectionRectangle = false;
    private Point2D startOfMultiSelect = null;
    private Point2D endOfMultiSelect = null;


    //private boolean multiSelect = false;
    //private boolean showSelectionRectangle = false;

    //private HashSet<Vertex> selectedVertices = new HashSet<>();

    //public Vertex selectedVertex;
    public static boolean antiAliasingOn = true;

    private void putToNormalState(){
        edgeVertex = null;
        movingSomeVertex = false;
        leader = null;
        selectedVertices.clear();
        showSelectionRectangle = false;
        startOfMultiSelect = null;
        endOfMultiSelect = null;
    }


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
                if(e.getKeyCode() == KeyEvent.VK_ALT && !showGrid){//avoids unusefull repaints
                    showGrid = true;     
                    repaint();
                }
                if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    for(Vertex v : selectedVertices)
                        ref.removeVertex(v);

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
                                    ref.addEdge(temp.name, found.name);
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

    private Vertex findVertexUnderMouse(Point mouse){
        for(Vertex v : ref.getVertices()){
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




        for(Vertex v : ref.getVertices()){

            if(v.representation.intersects(rect)){
                selectedVertices.add(v);
            }
        }
    }

    public void smartScreenshot(File where) throws IOException{
        double borderSize = 30;

        double[] extremas = GraphUtils.findExtremas(ref);

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
        drawVertices(g);

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
        drawVertices(g);
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
        g.setColor(new Color(198, 12, 12, 110));
        g.fill(rec);
        g.setColor(new Color(198, 12, 12));
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

    private void drawVertices(Graphics2D g){        
        g.setStroke(EDG_STROKE);

        for(Vertex v : ref.getVertices()){    
            Rectangle2D bounds = g.getFontMetrics().getStringBounds(v.name, g);
            if(bounds.getWidth() + 10 > 2*R)
                v.representation = new Ellipse2D.Double(v.posX - (bounds.getWidth() + 10)/2, v.posY - R, bounds.getWidth() + 10, 2*R);
            else
                v.representation = new Ellipse2D.Double(v.posX - R, v.posY - R, 2*R, 2*R);

            Color fill = null;
            if(v.equals(edgeVertex))
                fill = EDGE_ADD_COLOR;
            else if(selectedVertices.contains(v))
                fill = SEL_COLOR;
            else
                fill = REG_COLOR;

            g.setColor(fill);
            g.fill(v.representation);

            g.setColor(EDG_COLOR);
            g.draw(v.representation);

            g.setColor(TXT_COLOR);
            g.drawString(v.name, (float) (v.posX - bounds.getWidth()/2), (float) (v.posY + bounds.getHeight()/4)); 
        }
    }

}
