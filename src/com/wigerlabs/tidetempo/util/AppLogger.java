package com.wigerlabs.tidetempo.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppLogger {
    private static final Logger logger = LogManager.getLogger("TideTempoLogger");

    public static void init() {
        // Log4j2 automatically configures itself via log4j2.xml
    }

    public static Logger getLogger() {
        return logger;
    }
}
