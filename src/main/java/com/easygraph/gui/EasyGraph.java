/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 26, 2015
 */

package com.easygraph.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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

import com.easygraph.graph.ClassicGraphs;
import com.easygraph.graph.Graph;
import com.easygraph.graph.GraphFile;

public class EasyGraph {

    private final JPanel main = new JPanel();
    private final JFrame frame = new JFrame();

    //tabs
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final WelcomePanel welcomePanel = new WelcomePanel();

    //menu bar
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenuItem newFile = new JMenuItem("New graph");
    private final JMenuItem openFile = new JMenuItem("Open...");
    private final JMenuItem saveFile = new JMenuItem("Save");
    private final JMenuItem saveFileAs = new JMenuItem("Save as...");
    private final JMenuItem closeFile = new JMenuItem("Close graph");
    
    private final JMenuItem complete5 = new JMenuItem("K5");
    private final JMenuItem complete10 = new JMenuItem("K10");
    private final JMenuItem completeN = new JMenuItem("Order n...");
    private final JMenuItem cyclic5 = new JMenuItem("C5");
    private final JMenuItem cyclic10 = new JMenuItem("C10");
    private final JMenuItem cyclicN = new JMenuItem("Order n...");
    private final JMenuItem inverseGraph = new JMenuItem("Inverse graph");
    
    private final JMenuItem ssAsSeen = new JMenuItem("As displayed");
    private final JMenuItem ssSmart = new JMenuItem("Smart centering");

    //file chooser instance for the whole app
    final JFileChooser fc = new JFileChooser();

    public static void main(String[] args){
        new EasyGraph();
    }

    public EasyGraph(){        
        setupComponents();
        addListeners();
        placeComponents();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("@EasyGraph");
        frame.setContentPane(main);
        frame.setJMenuBar(menuBar);
        frame.setSize(main.getPreferredSize());
        frame.setMinimumSize(main.getPreferredSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setupComponents(){
        saveFile.setEnabled(false);
        closeFile.setEnabled(false);
        saveFileAs.setEnabled(false);
        
        JMenu m1 = new JMenu("File");

        m1.add(newFile);        
        m1.add(openFile);
        m1.addSeparator();
        m1.add(saveFile);
        m1.add(saveFileAs);
        m1.addSeparator();
        m1.add(closeFile);

        menuBar.add(m1);
        
        
        JMenu m2 = new JMenu("Quick graphs");
        JMenu m21 = new JMenu("Complete");
        JMenu m22 = new JMenu("Cyclcic");
        
        m21.add(complete5);
        m21.add(complete10);
        m21.add(completeN);
        m2.add(m21);
        
        m22.add(cyclic5);
        m22.add(cyclic10);
        m22.add(cyclicN);
        m2.add(m22);
        
        m2.addSeparator();
        m2.add(inverseGraph);
        
        menuBar.add(m2);
        
        JMenu m3 = new JMenu("Screenshot");
        m3.add(ssAsSeen);
        m3.add(ssSmart);
        
        menuBar.add(m3);


        tabbedPane.add("Welcome", welcomePanel);
    }

    private void addListeners(){
        frame.addWindowListener(new WindowListener(){

            @Override
            public void windowOpened(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowClosing(WindowEvent e) {
                for(int i = 1; i < tabbedPane.getTabCount(); i++){
                    tabbedPane.setSelectedIndex(i);
                    closeFileAction();
                }
                
            }

            @Override
            public void windowClosed(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowIconified(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowActivated(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        
        tabbedPane.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                boolean isGraphTab = tabbedPane.getSelectedComponent() instanceof GraphTab;

                closeFile.setEnabled(isGraphTab);
                saveFileAs.setEnabled(isGraphTab);
                saveFile.setEnabled(false);
                
                if(isGraphTab){
                    GraphTab tab = (GraphTab) tabbedPane.getSelectedComponent();
                    saveFile.setEnabled(tab.getPhysicalFile() != null);
                }
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

        closeFile.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                closeFileAction();
            }
        });
        
        
        complete5.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                openCompleteAction(5);
                
            }
            
        });

        
        complete10.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                openCompleteAction(10);
                
            }
            
        });
        
        cyclic5.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                openCyclicAction(5);
                
            }
            
        });
        
        cyclic10.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                openCyclicAction(10);
                
            }
            
        });
    }

    private void openCompleteAction(int order){
        Graph g = ClassicGraphs.createComplete(order);
        GraphTab luz = new GraphTab(g, this);
        tabbedPane.add("Complete"+order, luz);
        tabbedPane.setSelectedIndex(tabbedPane.getComponentCount() - 1);
    }
    
    private void openCyclicAction(int order){
        Graph g = ClassicGraphs.createCyclic(order);
        GraphTab luz = new GraphTab(g, this);
        tabbedPane.add("Cyclic"+order, luz);
        tabbedPane.setSelectedIndex(tabbedPane.getComponentCount() - 1);
    }
    
    
    private void placeComponents(){
        main.setLayout(new BorderLayout());
        main.add(tabbedPane, BorderLayout.CENTER);
    }

    private void newFileAction(){
        Graph g = new Graph();
        GraphTab luz = new GraphTab(g, this);
        tabbedPane.add("new graph", luz);
        tabbedPane.setSelectedIndex(tabbedPane.getComponentCount() - 1);
    }

    private void openFileAction(){
        if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                File selected = fc.getSelectedFile();
                Graph g = GraphFile.fetchGraph(selected);
                GraphTab toPut = new GraphTab(g, this);
                toPut.setPhysicalFile(selected);
                tabbedPane.add(selected.getName(), toPut);
                tabbedPane.setSelectedIndex(tabbedPane.getComponentCount() - 1);
            } catch (Exception e) {
                throwError(e.getMessage());
            }
        }
    }

    public void notifyGraphHasChanges(GraphTab gt){
        for(int i = 0; i < tabbedPane.getTabCount(); i++){
            if(tabbedPane.getComponentAt(i) == gt){
                if(!tabbedPane.getTitleAt(i).endsWith("*"))
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

                tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), selected.getName());
            } catch (Exception e) {
                throwError(e.getMessage());
            }
        }
    }

    private void closeFileAction(){
        GraphTab sel = (GraphTab) tabbedPane.getSelectedComponent();
        if(sel.hasChanges()){
            int answer = areYouSurePopup("Graph has unsaved changes, do you want to save and close?");
            if(answer == 2){//yes
                saveFileAction();
                tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
            }
            else if(answer == 1)//close
                tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
        }
        else{
            tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());  
        }
    }

    private void saveFileAction(){
        GraphTab tab = (GraphTab) tabbedPane.getSelectedComponent();
        
        if(tab.getPhysicalFile() == null){
            saveFileAsAction();
            return;
        }
        if(tab.hasChanges()){
            try {
                GraphFile.flushGraph(tab.getPhysicalFile(), tab.getReferencedGraph());
                tab.notifyWasFlushed();
                
                int index = tabbedPane.getSelectedIndex();
                String title = tabbedPane.getTitleAt(index);
                title = title.substring(0, title.length() - 1);
                
                
                tabbedPane.setTitleAt(index, title);
            } catch (IOException e) {
                throwError(e.getMessage());
            } catch (ParseException e) {
                throwError(e.getMessage());
            }
        }
    }

    public void throwError(String message){
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public int areYouSurePopup(String question){
        //Custom button text
        Object[] options = {"Cancel", "Close", "Yes"};
        return JOptionPane.showOptionDialog(frame, question, "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
    }

}
