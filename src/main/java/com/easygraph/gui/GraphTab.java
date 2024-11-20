/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 27, 2015
 */

package com.easygraph.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.easygraph.algorithms.BipartiteTesting;
import com.easygraph.algorithms.ComponentCount;
import com.easygraph.algorithms.GraphAlgorithm;
import com.easygraph.algorithms.LongTaskExample;
import com.easygraph.graph.Graph;
import com.easygraph.graph.GraphFile;

@SuppressWarnings("serial")
public class GraphTab extends JPanel {

    private ActionListener runAction;
    
    private final Graph graph;

    private JPanel vertexAdd = new JPanel();
    private final JButton addVertex = new JButton();
    private final JTextField vertexName = new JTextField("", 5);
    private final JButton showAlgs = new JButton("Show alg. panel");

    private final JPanel algorithmRunner = new JPanel();
    private final JComboBox<String> algorithmsCombo = new JComboBox<>();
    private final JButton run = new JButton("Run");
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel status = new JLabel();

    private final GraphDisplay display;
    
    private final Map<String, String> results = new HashMap<>();

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

        algorithmsCombo.addItem("Bipartite test");
        algorithmsCombo.addItem("Planarity test");
        algorithmsCombo.addItem("Isomorphism test");
        algorithmsCombo.addItem("Very long algorithm demo");
        algorithmsCombo.addItem("Component count");
        algorithmsCombo.setSelectedIndex(0);
        algorithmsCombo.setMaximumSize(algorithmsCombo.getPreferredSize());

        progressBar.setMinimum(0);
        progressBar.setMaximum(100);

        progressBar.setMaximumSize(progressBar.getPreferredSize());
        progressBar.setMinimumSize(progressBar.getPreferredSize());

        algorithmRunner.setVisible(false);

        status.setForeground(ColorTheme.STATUS_INFO_COLOR);
        status.setText("First run an algorithm.");
        status.setMaximumSize(new Dimension(300, status.getPreferredSize().height));
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

        showAlgs.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!algorithmRunner.isVisible()){
                    showAlgs.setText("Hide alg. panel");
                    buildVertexAddPanel(true); //repaint will be processed by setVisible.
                    algorithmRunner.setVisible(true);
                    repaint();
                }
                else{
                    showAlgs.setText("Show alg. panel");
                    buildVertexAddPanel(false); //repaint will be processed by setVisible.
                    algorithmRunner.setVisible(false);
                    repaint();
                }
            }

        });
        
        algorithmsCombo.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(results.containsKey(algorithmsCombo.getSelectedItem())){
                    status.setForeground(ColorTheme.STATUS_RESULT_COLOR);
                    status.setText(results.get(algorithmsCombo.getSelectedItem()));
                }
                else{
                    status.setForeground(ColorTheme.STATUS_INFO_COLOR);
                    status.setText("First run an algorithm.");
                }                
            }
        });
        
        runAction = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String algName = (String) algorithmsCombo.getSelectedItem();
                switch(algName){
                case "Very long algorithm demo": launchAlg(new LongTaskExample(progressBar, status, graph), "Very long algorithm demo"); break;
                case "Component count": launchAlg(new ComponentCount(progressBar, status, graph), "Component count"); break;
                case "Bipartite test": 
                    
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                        
                        launchAlg(new BipartiteTesting(progressBar, status, graph, (Frame) SwingUtilities.getWindowAncestor(GraphTab.this)), "Bipartite test");
                        }
                        });
                    break;
                default: status.setForeground(Color.red); status.setText("Not implemented yet");
                }
            }     
        };

        run.addActionListener(runAction);
    }

    private void launchAlg(final GraphAlgorithm alg, final String name){
        System.out.println("Launched alg...");
        
        //reset the run button
        run.setText("Stop");
        run.removeActionListener(runAction);
        status.setForeground(ColorTheme.STATUS_INFO_COLOR);
        status.setText("Started algorithm...");
        
        final ActionListener cancelAction = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                alg.cancel(false);
            }
        };
        
        
        run.addActionListener(cancelAction);

        
        alg.addPropertyChangeListener(new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(alg.isDone()){
                    if(!alg.isCancelled()){
                        try {
                            results.put(name, alg.get());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    algorithmsCombo.requestFocus();
                    run.setText("Run");
                    run.removeActionListener(cancelAction);
                    run.addActionListener(runAction);   
                }

            }
        });
        
        progressBar.setIndeterminate(true);

        alg.execute();

    }

    public void setHasChanges(boolean hasChanges){
        this.changes = hasChanges;
    }

    private void buildVertexAddPanel(boolean showAlgsFlag){
        vertexAdd.removeAll();
        if(showAlgsFlag){
            vertexAdd.add(Box.createHorizontalStrut(10));
            vertexAdd.add(new JLabel("New vertex: "));
            vertexAdd.add(vertexName);
            vertexAdd.add(addVertex);
            vertexAdd.add(Box.createHorizontalGlue());
            vertexAdd.add(showAlgs);
            vertexAdd.add(Box.createHorizontalStrut(10));
        }
        else{
            vertexAdd.add(Box.createHorizontalStrut(10 + (int) (showAlgs.getPreferredSize().getWidth())));
            vertexAdd.add(Box.createHorizontalGlue());
            vertexAdd.add(new JLabel("New vertex: "));
            vertexAdd.add(vertexName);
            vertexAdd.add(addVertex);
            vertexAdd.add(Box.createHorizontalGlue());
            vertexAdd.add(showAlgs);
            vertexAdd.add(Box.createHorizontalStrut(10));
        }
    }

    private void placeComponents(){
        //first the tool panel
        vertexAdd.setLayout(new BoxLayout(vertexAdd, BoxLayout.X_AXIS));
        buildVertexAddPanel(false);

        //algorithm runner panel
        algorithmRunner.setLayout(new BoxLayout(algorithmRunner, BoxLayout.X_AXIS));
        algorithmRunner.add(Box.createHorizontalStrut(10));
        algorithmRunner.add(algorithmsCombo);
        algorithmRunner.add(run);
        algorithmRunner.add(Box.createHorizontalStrut(30));
        algorithmRunner.add(progressBar);
        algorithmRunner.add(Box.createHorizontalStrut(10));
        algorithmRunner.add(new JLabel("Status: "));

        algorithmRunner.add(status);
        algorithmRunner.add(Box.createHorizontalGlue());
        algorithmRunner.add(Box.createHorizontalStrut(10));

        algorithmRunner.addHierarchyBoundsListener(new HierarchyBoundsAdapter(){
            @Override
            public void ancestorResized(HierarchyEvent e) {
                //Kind of a hacky way.
                int newWidth = algorithmRunner.getSize().width - 60 - algorithmsCombo.getPreferredSize().width - run.getPreferredSize().width
                        - progressBar.getPreferredSize().width- (new JLabel("Status: ")).getPreferredSize().width;

                Dimension newDimension = new Dimension(newWidth, status.getPreferredSize().height);
                status.setPreferredSize(newDimension);
                status.setMaximumSize(newDimension);
                status.invalidate();
            }
        });


        JPanel pd = new JPanel();
        pd.setLayout(new BoxLayout(pd, BoxLayout.Y_AXIS));

        pd.add(Box.createVerticalStrut(10));
        pd.add(vertexAdd);
        pd.add(algorithmRunner);
        pd.add(Box.createVerticalStrut(10));


        this.setLayout(new BorderLayout());
        this.add(display, BorderLayout.CENTER);
        this.add(pd, BorderLayout.PAGE_END);

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
