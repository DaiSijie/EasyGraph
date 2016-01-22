/*
 *	Author:      Gilbert Maystre
 *	Date:        Dec 31, 2015
 */

package com.easygraph.gui;

import java.awt.Frame;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class DialogsUtility {

    private static final JFileChooser FC = new JFileChooser();
    
    private DialogsUtility(){}

    public static void displayError(String message, Frame frame){
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Cancel = 0, Close = 1, Yes = 2
     * @param question
     * @param frame
     * @return
     */
    public static int areYouSurePopup(String question, Frame frame){
        Object[] options = {"Cancel", "Close", "Yes"};
        return JOptionPane.showOptionDialog(frame, question, "Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
    }
    
    /**
     * Yes = 0, No = 1 
     * @param question
     * @param frame
     * @return
     */
    public static int yesNo(String question, Frame frame){
        Object[] options = {"Yes", "No"};
        return JOptionPane.showOptionDialog(frame, question, "Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }
    
    /**
     * If user has canceled, a negative number is thrown back instead.
     * 
     * @return an int in range [lower;upper] or -1
     */
    public static int askForNaturalNumber(int lower, int upper, String message, Frame context){
        if(lower < 0 || upper < 0 || lower > upper)
            throw new IllegalArgumentException("Bad scheme for lower and upper");
        
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(lower, lower, upper, 1));        
        JLabel lbl = new JLabel(message);

        final JComponent[] inputs = new JComponent[] {lbl, spinner};
        int answer = JOptionPane.showConfirmDialog(context, inputs, "Enter a number", JOptionPane.OK_CANCEL_OPTION);
        
        if(answer == JOptionPane.CANCEL_OPTION || answer == JOptionPane.CLOSED_OPTION)
            return -1;
        else
            return (Integer) spinner.getValue();
    }

    /**
     * Returns null if the user aborted the file selection
     * 
     * @param context
     * @return
     */
    public static File askForExistingFile(Frame context){
        if(FC.showOpenDialog(context) != JFileChooser.APPROVE_OPTION)
            return null;
        else return FC.getSelectedFile();
    }
    
    /**
     * Returns null if the user aborted the file selection
     * 
     * @param context
     * @return
     */
    public static File askForSavingFile(Frame context){
        if (FC.showSaveDialog(context) != JFileChooser.APPROVE_OPTION)
            return null;
        else return FC.getSelectedFile();
    }

}
