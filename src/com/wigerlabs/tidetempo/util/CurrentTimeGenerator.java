/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wigerlabs.tidetempo.util;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author malee
 */
public class CurrentTimeGenerator {
    private static CurrentTimeGenerator instance;
    
    private CurrentTimeGenerator() {}

    public static CurrentTimeGenerator getInstance() {
        if(instance == null) {
            instance = new CurrentTimeGenerator();
        }
        return instance;
    }
    
    public Timestamp getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        return Timestamp.valueOf(now);
    }
}
