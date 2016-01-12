/*
 *	Author:      Gilbert Maystre
 *	Date:        Jan 6, 2016
 */

package com.easygraph.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public class ColorTheme {

    //this implementation is rather naive, could use some inheritance, but wanted to keep things simple and concise.
    private ColorTheme(){}

    public static Color BCK_COLOR = new Color(255, 252, 235);
    public static Color REG_COLOR = new Color(185, 235, 250);
    public static Color SEL_COLOR = new Color(219, 70, 70);
    public static Color TXT_COLOR = Color.BLACK;
    public static Color EDG_COLOR = Color.BLACK;
    public static Color GRD_COLOR = Color.LIGHT_GRAY;
    public static Color SQUARE_SELECT_COLOR = new Color(198, 12, 12);

    public static Color EDGE_ADD_COLOR = new Color(168, 247, 146);
    
    public static Color STATUS_ALG_MESSG_COLOR = new Color(71,18,97);
    public static Color STATUS_INFO_COLOR = new Color(181, 102, 18);
    public static Color STATUS_ERROR_COLOR = Color.RED;
    public static Color STATUS_RESULT_COLOR = new Color(42, 130, 49);

    public static Stroke GRD_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.f}, 0.0f);
    public static Stroke EDG_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f);
    
    public static void setTheme(Theme t){
        switch(t){
        case BW : putBW(); break;
        case REGULAR: putRegular(); break;
        }
    }
    
    private static void putBW(){
        BCK_COLOR = Color.WHITE;
        REG_COLOR = new Color(219, 219, 219);
        SEL_COLOR = new Color(163, 163, 163);
        EDGE_ADD_COLOR = new Color(163, 163, 163);
        SQUARE_SELECT_COLOR = new Color(105, 105, 105);
    }
    
    private static void putRegular(){
        BCK_COLOR = new Color(255, 252, 235);
        REG_COLOR = new Color(185, 235, 250);
        SEL_COLOR = new Color(219, 70, 70);
        EDGE_ADD_COLOR = new Color(168, 247, 146);
        SQUARE_SELECT_COLOR = new Color(198, 12, 12);
    }
    
    public static enum Theme{
        BW, REGULAR
    }
    
}


