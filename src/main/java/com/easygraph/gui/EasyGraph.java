/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 26, 2015
 */

package com.easygraph.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.easygraph.graph.Graph;

public class EasyGraph {

    private final List<GraphTab> tabs = new ArrayList<>();
    
    private final JPanel main = new JPanel();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenuItem newFile = new JMenuItem();
    
    public static void main(String[] args){
        new EasyGraph();
    }
    
    public EasyGraph(){        
        setupComponents();
        addListeners();
        placeComponents();
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("EasyGraph");
        frame.setContentPane(main);
        frame.setJMenuBar(menuBar);
        frame.setSize(main.getPreferredSize());
        frame.setMinimumSize(main.getPreferredSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void setupComponents(){
        JMenu m1 = new JMenu("File");
        newFile.setText("New Graph");
        m1.add(newFile);
        menuBar.add(m1);
        
        Graph g = new Graph();
        GraphTab luz = new GraphTab(g);
        tabbedPane.add("new graph", luz);
    }
    
    private void addListeners(){
        newFile.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                newFileAction();
            }
            
        });
    }
    
    private void placeComponents(){
        main.setLayout(new BorderLayout());
        main.add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void newFileAction(){
        Graph g = new Graph();
        GraphTab luz = new GraphTab(g);
        tabbedPane.add("new graph", luz);
    }
    
}
