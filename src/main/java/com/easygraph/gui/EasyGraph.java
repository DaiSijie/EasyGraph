/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 26, 2015
 */

package com.easygraph.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.easygraph.graph.ClassicGraphs;
import com.easygraph.graph.Graph;
import com.easygraph.graph.GraphFile;
import com.easygraph.graph.PermutationGraph;

public class EasyGraph {

    private final JPanel main = new JPanel();
    private final JFrame frame = new JFrame();

    //tabs
    private final CustomTabbedPane tabbedPane = new CustomTabbedPane();
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
    private final JMenuItem permutationN = new JMenuItem("Permutation graph...");
    private final JMenuItem inverseGraph = new JMenuItem("Inverse graph");
    
    private final JMenuItem ssAsSeen = new JMenuItem("As displayed...");
    private final JMenuItem ssSmart = new JMenuItem("Smart centering...");
    
    private final JRadioButtonMenuItem blackAndWhiteColors = new JRadioButtonMenuItem("Black and white");
    private final JRadioButtonMenuItem regularColors = new JRadioButtonMenuItem("Regular colors");
    private final JCheckBoxMenuItem fastRendering = new JCheckBoxMenuItem("Fast rendering", false);
    
    public static void main(String[] args){
        System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS"); //fires event when CMD+Q is thrown     

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EasyGraph();
            }
          });
    }

    public EasyGraph(){        
        setupComponents();
        addListeners();
        placeComponents();

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
        ssAsSeen.setEnabled(false);
        ssSmart.setEnabled(false);
        inverseGraph.setEnabled(false);
        
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.META_MASK));
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.META_MASK));
        ssSmart.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.META_MASK));
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.META_MASK));
        
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
        m2.add(this.permutationN);
        m2.addSeparator();
        m2.add(inverseGraph);

        
        menuBar.add(m2);
        
        JMenu m3 = new JMenu("Screenshot");
        m3.add(ssAsSeen);
        m3.add(ssSmart);
        
        menuBar.add(m3);
        
        ButtonGroup group = new ButtonGroup();
        group.add(blackAndWhiteColors);
        group.add(regularColors);
        
        regularColors.setSelected(true);
        
        JMenu m4 = new JMenu("Preferences");
        JMenu m41 = new JMenu("Color theme");
        m41.add(blackAndWhiteColors);
        m41.add(regularColors);
        m4.add(m41);
        m4.add(fastRendering);

        
        menuBar.add(m4);
        
        
        tabbedPane.setDefaultCloseAction(this);
        tabbedPane.add("Welcome", welcomePanel);
    }

    private void addListeners(){
        
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                int yeeh = tabbedPane.getTabCount();
                boolean cancel = false;
       
                for(int i = yeeh - 1; i >= 1; i--){
                    tabbedPane.setSelectedIndex(i);
                    if(closeFileAction((GraphTab) tabbedPane.getSelectedComponent())){
                        cancel = true;
                        break;
                    }
                } 
                
                if(!cancel){
                    frame.dispose();
                    System.exit(0); //this is what swing normaly does under the hood.
                }
            }            
        });
        
        tabbedPane.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                boolean isGraphTab = tabbedPane.getSelectedComponent() instanceof GraphTab;

                closeFile.setEnabled(isGraphTab);
                saveFileAs.setEnabled(isGraphTab);
                saveFile.setEnabled(isGraphTab);
                inverseGraph.setEnabled(isGraphTab);
                ssAsSeen.setEnabled(isGraphTab);
                ssSmart.setEnabled(isGraphTab);
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
                closeFileAction((GraphTab) tabbedPane.getSelectedComponent());
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
        
        completeN.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                int choosed = DialogsUtility.askForNaturalNumber(1, 50, "Enter the number of vertices", frame);
                if(choosed >= 0)
                    openCompleteAction(choosed);
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
        
        this.permutationN.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                int choosed = DialogsUtility.askForNaturalNumber(1, 50, "Enter the number of vertices", frame);
                if(choosed >= 0)
                    openPermutation(choosed);
            } 
        });
        
        cyclicN.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                int choosed = DialogsUtility.askForNaturalNumber(3, 50, "Enter the number of vertices", frame);
                
                if(choosed >= 0)
                    openCyclicAction(choosed);
            }
        });
        
        ssSmart.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                screenshotAction(tabbedPane.getSelectedIndex(), true);
            } 
        });
        
        ssAsSeen.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                screenshotAction(tabbedPane.getSelectedIndex(), false); 
            }
        });
        
        inverseGraph.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                invertGraphAction(tabbedPane.getSelectedIndex());
            }    
        });
        
        fastRendering.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                GraphDisplay.antiAliasingOn = !fastRendering.getState();
                tabbedPane.repaint();
            }
        });
        
        
        blackAndWhiteColors.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                ColorTheme.setTheme(ColorTheme.Theme.BW);
                tabbedPane.repaint();
            }
        });
        
        regularColors.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                ColorTheme.setTheme(ColorTheme.Theme.REGULAR);
                tabbedPane.repaint();
            }
        });   
        
    }


    
    private void placeComponents(){
        main.setLayout(new BorderLayout());
        main.add(tabbedPane, BorderLayout.CENTER);
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
    
    /*
     * ACTIONS
     */

    
    private void invertGraphAction(int tab){
        GraphTab gt = (GraphTab) tabbedPane.getComponentAt(tab);
        gt.getReferencedGraph().invert();
        gt.repaint();
    }
    
    private void screenshotAction(int tab, boolean smart){
        GraphTab gt = (GraphTab) tabbedPane.getComponentAt(tab);
        
        File where = DialogsUtility.askForSavingFile(frame);
        if(where != null){
            try {
                if(smart)
                    gt.getDisplay().smartScreenshot(where);
                else
                    gt.getDisplay().screenshot(where);
            } catch (IOException e) {
                DialogsUtility.displayError("Problem while saving screenshot", frame);
            }
        }
        
    }
    
    private void openPermutation(int order){
        Graph g = PermutationGraph.createPermutation(order);
        GraphTab luz = new GraphTab(g, this);
        tabbedPane.add("Complete"+order, luz, true);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }
    
    
    private void openCompleteAction(int order){
        Graph g = ClassicGraphs.createComplete(order);
        GraphTab luz = new GraphTab(g, this);
        tabbedPane.add("Complete"+order, luz, true);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }
    
    private void openCyclicAction(int order){
        Graph g = ClassicGraphs.createCyclic(order);
        GraphTab luz = new GraphTab(g, this);
        tabbedPane.add("Cyclic"+order, luz, true);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }
    
    private void newFileAction(){
        Graph g = new Graph();
        GraphTab luz = new GraphTab(g, this);
        tabbedPane.add("new graph", luz, true);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    private void openFileAction(){
        File where = DialogsUtility.askForExistingFile(frame);
        if(where != null){
            try {
                Graph g = GraphFile.fetchGraph(where);
                
                GraphTab toPut = new GraphTab(g, this);
                toPut.setPhysicalFile(where);
                
                tabbedPane.add(where.getName(), toPut, true);
                
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            } catch (Exception e) {
                e.printStackTrace();
                DialogsUtility.displayError(e.getMessage(), frame);
            }
        }
    }

    private boolean saveFileAsAction(){
        File where = DialogsUtility.askForSavingFile(frame);
        if(where != null){
            try {
                GraphTab tab = (GraphTab) tabbedPane.getSelectedComponent();
                tab.setPhysicalFile(where);
                GraphFile.flushGraph(where, tab.getReferencedGraph());
                tab.notifyWasFlushed();

                tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), where.getName());
                return true;
            } catch (Exception e) {
                DialogsUtility.displayError(e.getMessage(), frame);
            }
        }
        
        return false;
    }

    boolean closeFileAction(GraphTab sel){
        if(sel.hasChanges()){
            int answer = DialogsUtility.areYouSurePopup("Graph has unsaved changes, do you want to save and close?", frame);
            if(answer == 2){//yes
                if(saveFileAction()){
                    tabbedPane.remove(sel);
                    return true;
                }
                return false;

            }
            else if(answer == 1)//close
                tabbedPane.remove(sel);
            return answer == 0;
        }
        else{
            tabbedPane.remove(sel);
            return false;
        }
    }

    private boolean saveFileAction(){
        GraphTab tab = (GraphTab) tabbedPane.getSelectedComponent();
        
        if(tab.getPhysicalFile() == null){
            return saveFileAsAction();
        }
        if(tab.hasChanges()){
            try {
                GraphFile.flushGraph(tab.getPhysicalFile(), tab.getReferencedGraph());
                tab.notifyWasFlushed();
                
                int index = tabbedPane.getSelectedIndex();
                String title = tabbedPane.getTitleAt(index);
                title = title.substring(0, title.length() - 1);

                tabbedPane.setTitleAt(index, title);
                return true;
            } catch (Exception e) {
                DialogsUtility.displayError(e.getMessage(), frame);
                return false;
            }
        }
        return true;
    }

}
