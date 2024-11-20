/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 28, 2015
 */

package com.easygraph.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

@SuppressWarnings("serial")
public class WelcomePanel extends JComponent{
    
    JTextPane textDisplay = new JTextPane();
    JLabel l1;
    JLabel l2;
    JLabel l3;
    
    public WelcomePanel(){
        setupComponents();
        placeComponents();
    }
    
    private void setupComponents(){
        textDisplay.setEditable(false);
        textDisplay.setOpaque(false);
        textDisplay.setContentType("text/html");
        StringBuilder bd = new StringBuilder("<html>");
        bd.append("<h1><center>EasyGraph welcomes you!</center></h1></br>");
        bd.append("<h2><u>Quick start</u></h2>");
        bd.append("<ul>");
        bd.append("<li>Use File > New graph to open a fresh new graph.</li>");
        bd.append("<li>Add a vertex by typing it's name and clicking on \"Add\"</li>");
        bd.append("<li>Delete a vertex by left-clicking on it and using backspace.</li>");
        bd.append("<li>Create an edge between two vertex by left-clicking on both.</li>");
        bd.append("<li>Delete an edge in the same fashion.</li>");
        bd.append("<ul>");
        bd.append("<h2><u>Advanced features</u></h2>");
        bd.append("<ul>");
        bd.append("<li>You can either save the graph as a picture or as text to be reused later.</li>");
        bd.append("<li>If you use a large amount of vertices, you can speedup the drawing by checking Preferences > Fast rendering.</li>");
        bd.append("<ul>");
        bd.append("<h2><u>Examples</u></h2>");
        bd.append("</html>");
        textDisplay.setText(bd.toString());
        textDisplay.setMaximumSize(textDisplay.getPreferredSize());
        
        l1 =  prepareLabel("welcome/g1.png", "K10");
        l2 =  prepareLabel("welcome/g2.png", "An icosaedro");
        l3 =  prepareLabel("welcome/g3.png", "A simple neural network");
    }
    
    private JLabel prepareLabel(String path, String description){
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(path));
        
        //making nicer icons
        double sf = 0.5;
        int nw = (int) (sf * icon.getIconWidth());
        int nh = (int) (sf * icon.getIconHeight());

        icon.setImage(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_SMOOTH));
        JLabel toReturn = new JLabel(description, icon, JLabel.CENTER);
        toReturn.setHorizontalTextPosition(JLabel.CENTER);
        toReturn.setVerticalTextPosition(JLabel.BOTTOM);
        toReturn.setOpaque(true);
        toReturn.setBackground(Color.WHITE);
        toReturn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        
        return toReturn;
    }
    
    
    private void placeComponents(){
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
        textPanel.add(Box.createHorizontalStrut(10));
        textPanel.add(Box.createHorizontalGlue());
        textPanel.add(textDisplay);
        textPanel.add(Box.createHorizontalGlue());
        textPanel.add(Box.createHorizontalStrut(10));
        
        
        JPanel imgs = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        imgs.add(l1);
        imgs.add(l2);
        imgs.add(l3);
        
        
        this.setLayout(new BorderLayout());
        this.add(textPanel, BorderLayout.PAGE_START);
        this.add(imgs);
    }
    
    public Dimension getPreferredSize(){
        return new Dimension(800, 700);
    }

    
    

}
