/*
 *	Author:      Gilbert Maystre
 *	Date:        Jan 7, 2016
 */

package com.easygraph.algorithms;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.easygraph.gui.ColorTheme;

public abstract class GraphAlgorithm extends SwingWorker<String, String> {

    public static final String CANCEL_DEFAULT_MESSAGE = "Operation was canceled.";

    private final JProgressBar progressBar;
    private final JLabel status;


    public GraphAlgorithm(JProgressBar progressBar, final JLabel status){
        this.progressBar = progressBar;
        this.status = status;

        final boolean[] already = {false};//smart hack

        this.addPropertyChangeListener(new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(isCancelled() && !already[0]){
                    already[0] = true;
                    setStatusHelper(CANCEL_DEFAULT_MESSAGE, ColorTheme.STATUS_ERROR_COLOR);
                }
                if(isDone() && !isCancelled()){
                    try {
                        setStatusHelper(get(), ColorTheme.STATUS_RESULT_COLOR);
                    } catch (Exception e) {
                        setStatusHelper("Sorry, an unexpected error happend", ColorTheme.STATUS_ERROR_COLOR);
                        e.printStackTrace();
                    }
                }
                if(isDone())
                    setProgressHelper(0);
            }

        });
    }

    protected void postHasStarted(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setIndeterminate(false);
            }
        });
    }

    protected void postMessage(String message){
        if(!isCancelled())
            setStatusHelper(message, ColorTheme.STATUS_ALG_MESSG_COLOR);
    }

    protected void postProgress(int progress){
        if(!isCancelled())
            setProgressHelper(progress);
    }

    private void setStatusHelper(final String message, final Color color){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                status.setForeground(color);
                status.setText(message);
            }
        });
    }
    
    private void setProgressHelper(final int progress){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                    progressBar.setValue(progress);
            }
        });
    }
}
