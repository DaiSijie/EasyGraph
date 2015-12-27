/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 26, 2015
 */

package com.easygraph.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.easygraph.graph.Graph;
import com.easygraph.graph.GraphFile;

public class EasyGraph {

    private final List<GraphTab> tabs = new ArrayList<>();
    
    private final JPanel main = new JPanel();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenuItem newFile = new JMenuItem("New graph");
    private final JMenuItem openFile = new JMenuItem("Open...");
    private final JMenuItem saveFile = new JMenuItem("Save");
    private final JMenuItem saveFileAs = new JMenuItem("Save as...");
    private final JMenuItem closeFile = new JMenuItem("Close graph");
    
  //Create a file chooser
    final JFileChooser fc = new JFileChooser();

    
    private final JFrame frame;
    
    public static void main(String[] args){
        new EasyGraph();
    }
    
    public EasyGraph(){        
        setupComponents();
        addListeners();
        placeComponents();
        
        frame = new JFrame();
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
        
        m1.add(newFile);        
        m1.add(openFile);
        m1.addSeparator();
        m1.add(saveFile);
        
        saveFile.setEnabled(false);
        
        m1.add(saveFileAs);
        m1.addSeparator();
        m1.add(closeFile);
            
        menuBar.add(m1);
        
        Graph g = new Graph();
        GraphTab luz = new GraphTab(g, this);
        tabbedPane.add("new graph", luz);
    }
    
    private void addListeners(){
        tabbedPane.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                GraphTab tab = (GraphTab) tabbedPane.getSelectedComponent();
                saveFile.setEnabled(tab.getPhysicalFile() == null);
            }
        });
        
        newFile.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                newFileAction();
            }
            
        });
        
        openFile.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                openFileAction();
            }
            
        });
        
        saveFileAs.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                saveFileAsAction();
                
            }
            
        });
        
        saveFile.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                saveFileAction();
                
            }
            
        });
    }
    
    private void placeComponents(){
        main.setLayout(new BorderLayout());
        main.add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void newFileAction(){
        Graph g = new Graph();
        GraphTab luz = new GraphTab(g, this);
        tabbedPane.add("new graph", luz);
    }
    
    private void openFileAction(){
        if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                File selected = fc.getSelectedFile();
                Graph g = GraphFile.fetchGraph(selected);
                GraphTab toPut = new GraphTab(g, this);
                toPut.setPhysicalFile(selected);
                tabbedPane.add(cuteName(selected.getName()), toPut);
                
            } catch (Exception e) {
                throwError(e.getMessage());
            }
        }
    }
    
    public void notifyGraphHasChanges(GraphTab gt){
        for(int i = 0; i < tabbedPane.getTabCount(); i++){
            if(tabbedPane.getComponentAt(i) == gt){
                tabbedPane.setTitleAt(i, tabbedPane.getTitleAt(i)+"*");
                return;
            }
        }
    }
    
    
    private void saveFileAsAction(){
        if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                File selected = fc.getSelectedFile();
                
                GraphTab tab = (GraphTab) tabbedPane.getSelectedComponent();
                tab.setPhysicalFile(selected);
                GraphFile.flushGraph(selected, tab.getReferencedGraph());
                
                
                
                tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), cuteName(fc.getName()));
                
                
                
                
            } catch (Exception e) {
                throwError(e.getMessage());
            }
        }
    }
    
    
    private static String cuteName(String str){
        if(str.endsWith(".graph")){
           return str.substring(0, str.length() - 6); 
        }
        return str;
    }
    
    private void saveFileAction(){
        GraphTab tab = (GraphTab) tabbedPane.getSelectedComponent();
        try {
            GraphFile.flushGraph(tab.getPhysicalFile(), tab.getReferencedGraph());
        } catch (IOException e) {
            throwError(e.getMessage());
        }
    }
        
    public void throwError(String message){
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
}
