/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 31, 2015
 */

package com.easygraph.gui;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class NumberPrompter {

    private static JOptionPane opt;
    private static JDialog my;
    private static SpinnerNumberModel model;
    
    private static JSpinner sp;
    private static JLabel lbl;
    private static JButton
    
    private NumberPrompter(){
        my.setContentPane(opt);
    }
    
    private void setupComponents(){
        sp = new JSpinner(model);
        
        model = new SpinnerNumberModel(10, 1, 20, 1);
    }
    
    
    
    
    /**
     * If user wanted to cancel, a negative number is thrown back
     * @return an int in range 
     */
    public static int askForNaturalNumber(int lower, int upper, String message){
        my.
        
        
        my.setVisible(true);
        return 
        
        my.
        return -1;
    }
    
    
    
    
}
