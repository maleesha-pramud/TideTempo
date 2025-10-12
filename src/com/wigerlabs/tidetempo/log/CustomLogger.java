/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wigerlabs.tidetempo.log;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author malee
 */
public class CustomLogger {
    
    public static final Logger logger = Logger.getLogger("com.wigerlabs.tidetempo");
    public static CustomLogger customLogger;
    
    static {
        try {
            FileHandler handler = new FileHandler("app.log", true);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private CustomLogger() {
        
    }
    
    public static Logger getLogger(Class<?> c) {
        return Logger.getLogger(c.getClass().getName());
    }
}
