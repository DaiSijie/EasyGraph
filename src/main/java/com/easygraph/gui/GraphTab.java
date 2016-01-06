/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 27, 2015
 */

package com.easygraph.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.easygraph.graph.Graph;
import com.easygraph.graph.GraphFile;

@SuppressWarnings("serial")
public class GraphTab extends JPanel {

    private final Graph graph;

    private final JButton addVertex = new JButton();
    private final JTextField vertexName = new JTextField("", 5);

    private final GraphDisplay display;

    private final EasyGraph context;

    private File physicalFile;
    private boolean changes;

    public GraphTab(Graph g, EasyGraph context){
        this.context = context;
        this.graph = g;
        this.display = new GraphDisplay(g, context, this);

        changes = false;

        setupComponents();
        addListeners();
        placeComponents();
    }

    public GraphDisplay getDisplay(){
        return display;
    }

    public File getPhysicalFile(){
        return this.physicalFile;
    }

    public boolean hasChanges(){
        return changes;
    }

    public void notifyWasFlushed(){
        changes = false;
    }

    public void setPhysicalFile(File physical){
        this.physicalFile = physical;
    }

    public Graph getReferencedGraph(){
        return graph;
    }

    private void setupComponents(){
        addVertex.setText("Add");
        vertexName.setMaximumSize(vertexName.getPreferredSize());
    }

    private void addListeners(){
        addVertex.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                addVertexAction();
            }
        });

        vertexName.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && !vertexName.getText().equals(""))
                    addVertexAction();
            }

        });
    }

    public void setHasChanges(boolean hasChanges){
        this.changes = hasChanges;
    }

    private void placeComponents(){
        //first the tool panel
        JPanel tools = new JPanel();
        tools.setLayout(new BoxLayout(tools, BoxLayout.X_AXIS));

        tools.add(Box.createHorizontalStrut(10));
        tools.add(Box.createHorizontalGlue());
        tools.add(new JLabel("New vertex: "));
        tools.add(vertexName);
        tools.add(addVertex);
        tools.add(Box.createHorizontalGlue());
        tools.add(Box.createHorizontalStrut(10));

        this.setLayout(new BorderLayout());
        this.add(display, BorderLayout.CENTER);
        this.add(tools, BorderLayout.PAGE_END);

    }

    private void addVertexAction(){
        try{
            if(GraphFile.isLegalName(vertexName.getText())){
                graph.addVertex(vertexName.getText());
                vertexName.setText("");
                display.notifyChangesInGraph();
                changes = true;
                context.notifyGraphHasChanges(this);
            }
            else{
                vertexName.setText("");
                JFrame enclosing = (JFrame) SwingUtilities.getWindowAncestor(this);
                DialogsUtility.displayError("Illegal name, would be impossible to save.", enclosing);
            }

        }
        catch(IllegalArgumentException e){
            vertexName.setText("");
            JFrame enclosing = (JFrame) SwingUtilities.getWindowAncestor(this);
            DialogsUtility.displayError(e.getMessage(), enclosing);
        }
    }

}
