/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 28, 2015
 */

package com.easygraph.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WelcomePanel extends JComponent{
    
    public WelcomePanel(){
        setLayout(new BorderLayout());
        add(new JLabel("Welcome!"));
        
    }
    
    public Dimension getPreferredSize(){
        return new Dimension(800, 600);
    }

}
