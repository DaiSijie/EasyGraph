/*
 *	Author:      Gilbert Maystre
 *	Date:        Jan 3, 2016
 */

package com.easygraph.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class CustomTabbedPane extends JTabbedPane {

    //assumed that each Component is unique...
    private final HashMap<Component, JButton> closeButtons;
    private final HashMap<Component, JLabel> labels;
    
    private final static int CLOSE_BUTTON_SIZE = 12;
    private final static ImageIcon NON_HOVER = makeButtonImage((new JPanel()).getBackground(), Color.BLACK, CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE);
    private final static ImageIcon HOVER = makeButtonImage(Color.RED, Color.BLACK, 12, 12);
    
    private EasyGraph eg;
    
    public CustomTabbedPane(){
        super();
        
        closeButtons = new HashMap<>();
        this.labels = new HashMap<>();
        
        this.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {                
                for(JLabel lbl : labels.values())
                    lbl.setForeground(labels.get(getSelectedComponent()) == lbl ? Color.WHITE : Color.BLACK);
            }
            
        });
    }
    
    public void add(String title, final Component component, boolean withButton){
        this.add(title, component);
        
        if(withButton){
            int index = this.getTabCount() - 1;
            
            //creating the button
            final JButton closeButton = new JButton(NON_HOVER);
            closeButton.setRolloverIcon(HOVER);
            closeButton.setOpaque(false);
            closeButton.setContentAreaFilled(false);
            closeButton.setBorderPainted(false);
            closeButton.setMaximumSize(new Dimension(12, 12));
            closeButton.setPreferredSize(new Dimension(12, 12));
            closeButton.setVisible(false);
            closeButton.addMouseListener(new MouseAdapter(){
                public void mouseExited(MouseEvent e) {
                    closeButton.setVisible(false);
                    
                }
                public void mouseEntered(MouseEvent e){
                    closeButton.setVisible(true);
                }
            });
            closeButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(eg != null){
                        for(Entry<Component, JButton> en : closeButtons.entrySet()){
                            if(en.getValue() == closeButton){
                                eg.closeFileAction((GraphTab) en.getKey());
                                break;
                            }    
                        }
                    } 
                }    
            });
            closeButtons.put(component, closeButton);

            //creation the label
            JLabel label = new JLabel(title);
            labels.put(component, label);
            
            //creating the panel
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
            panel.setOpaque(false);
            panel.addMouseListener(new MouseAdapter(){
                @Override
                public void mousePressed(MouseEvent e) {
                    for(Entry<Component, JButton> en : closeButtons.entrySet()){
                        if(en.getValue() == closeButton){
                            setSelectedComponent(component);
                            break;
                        }    
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    closeButton.setVisible(true);
                    
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    closeButton.setVisible(false);
                    
                }
                
            });

            //putting everything in place
            panel.add(label);
            panel.add(Box.createHorizontalStrut(7));
            panel.add(closeButton);

            //putting the panel in the tab
            this.setTabComponentAt(index, panel);
        }
    }

    public void setDefaultCloseAction(EasyGraph eg){
        this.eg = eg;
    }
    
    @Override
    public void removeTabAt(int index){
        super.removeTabAt(index);
        closeButtons.remove(index);
        labels.remove(index);
    }
    
    public String getTitleAt(int i){
        if(!labels.containsKey(getComponentAt(i)))
            return super.getTitleAt(i);
        
        return labels.get(getComponentAt(i)).getText();   
    }
    
    public void setTitleAt(int i, String s){
        if(labels.containsKey(getComponentAt(i)))
            labels.get(getComponentAt(i)).setText(s);
        
        //small hack to avoid repainting
        super.setTitleAt(i, s);
    }
    
    public void addCloseListener(int index, ActionListener al){
        closeButtons.get(index).addActionListener(al);
    }
    
    private static ImageIcon makeButtonImage(Color background, Color text, int width, int height){
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();        

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(background);
        g.fillRoundRect(0, 0, width-1, height-1, 6, 6);
        
        if(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()).contains("Century Gothic")){
            g.setFont(new Font("Century Gothic", Font.BOLD, 11));
        }

        String s = "x";
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(s, g);
        
        float x = (float)  ((width - bounds.getWidth()) / 2);
        float y = (float) (height/2 + bounds.getHeight()/4);
        
        g.setColor(text);
        g.drawRoundRect(0, 0, width-1, height-1, 6, 6);
        g.drawString(s, x-0.5f, y-0.5f);
        
        g.dispose();
        
        return new ImageIcon(image);
    }
    
}
