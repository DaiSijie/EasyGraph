/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 27, 2015
 */

package com.easygraph.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.easygraph.graph.Graph;
import com.easygraph.graph.Vertex;

public class GraphTab extends JPanel {
    
    private final Graph g;
    
    private final JButton addVertex = new JButton();
    private final JButton addEdge = new JButton();
    
    private final JTextField vertexName = new JTextField();
    private final JTextField edgeName1 = new JTextField();
    private final JTextField edgeName2 = new JTextField();
    
    private final GraphDisplay display;
    
    public GraphTab(Graph g){
        this.g = g;
        this.display = new GraphDisplay(g);
        
        setupComponents();
        addListeners();
        placeComponents();
    }
    
    public Graph getRefferencedGraph(){
        return g;
    }
    
    private void setupComponents(){
        addVertex.setText("Add");
        addEdge.setText("Add");
        
        vertexName.setColumns(5);
        edgeName1.setColumns(5);
        edgeName2.setColumns(5);
    }
    
    public Dimension getPreferredSize(){
        return display.getPreferredSize();
    }
    
    private void addListeners(){
        addVertex.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                addVertexAction();
                
            }
        });
        
        addEdge.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                addEdgeAction();
            }
        });
        

        
        
        
    }
    
    
    
    private void placeComponents(){
        //first the tool panel
        JPanel tools = new JPanel();
        tools.setLayout(new BoxLayout(tools, BoxLayout.X_AXIS));
        
        tools.add(Box.createHorizontalStrut(10));
        tools.add(vertexName);
        tools.add(addVertex);
        tools.add(Box.createHorizontalStrut(20));
        tools.add(edgeName1);
        tools.add(new JLabel(" and "));
        tools.add(edgeName2);
        tools.add(addEdge);
        tools.add(Box.createHorizontalGlue());
        tools.add(Box.createHorizontalStrut(10));
        
        this.setLayout(new BorderLayout());
        this.add(display, BorderLayout.CENTER);
        this.add(tools, BorderLayout.PAGE_END);
        
    }

    private void addVertexAction(){
        g.addVertex(new Vertex(vertexName.getText()));
        display.notifyChangesInGraph();
    }
    
    private void addEdgeAction(){
        g.addEdge(edgeName1.getText(), edgeName2.getText());
        display.notifyChangesInGraph();
    }
}
