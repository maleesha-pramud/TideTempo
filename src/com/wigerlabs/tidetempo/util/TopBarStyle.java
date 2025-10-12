/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wigerlabs.tidetempo.util;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author malee
 */
public class TopBarStyle {
    private static TopBarStyle instance;
    
    private TopBarStyle() {
        
    }
    
    public static TopBarStyle getInstance() {
        if(instance == null) {
            instance = new TopBarStyle();
        }
        return instance;
    }
    
    public void applyStyle(JFrame frame) {
        frame.getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_BACKGROUND, Colors.DARK_BLUE);
    }
    public void applyStyle(JDialog dialog) {
        dialog.getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_BACKGROUND, Colors.DARK_BLUE);
    }
}
