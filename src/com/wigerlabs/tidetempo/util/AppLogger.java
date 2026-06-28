package com.wigerlabs.tidetempo.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class AppLogger {
    private static final Logger logger = Logger.getLogger("TideTempoLogger");
    private static boolean initialized = false;

    private static final String LOG_DIR = System.getProperty("user.dir") + File.separator + "logs";
    private static final String LOG_FILE = LOG_DIR + File.separator + "application.log";

    public static void init() {
        if (initialized) return;

        try {
            // Ensure log directory exists
            File dir = new File(LOG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Remove default handlers
            Logger rootLogger = Logger.getLogger("");
            java.util.logging.Handler[] handlers = rootLogger.getHandlers();
            for (java.util.logging.Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }

            // Create a custom formatter
            Formatter customFormatter = new Formatter() {
                private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                @Override
                public String format(LogRecord record) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("[")
                           .append(dateFormat.format(new Date(record.getMillis())))
                           .append("] [")
                           .append(record.getLevel())
                           .append("] [")
                           .append(record.getSourceClassName())
                           .append(".")
                           .append(record.getSourceMethodName())
                           .append("] - ")
                           .append(formatMessage(record))
                           .append(System.lineSeparator());

                    if (record.getThrown() != null) {
                        java.io.StringWriter sw = new java.io.StringWriter();
                        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                        record.getThrown().printStackTrace(pw);
                        builder.append(sw.toString());
                    }
                    return builder.toString();
                }
            };

            // Set up File Handler (append = true, max 5MB per file, 3 rotating files)
            FileHandler fileHandler = new FileHandler(LOG_FILE, 5 * 1024 * 1024, 3, true);
            fileHandler.setFormatter(customFormatter);
            fileHandler.setLevel(Level.ALL);

            // Set up Console Handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(customFormatter);
            consoleHandler.setLevel(Level.ALL);

            logger.addHandler(fileHandler);
            logger.addHandler(consoleHandler);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);

            initialized = true;
            logger.info("Application Logger initialized successfully.");

        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    public static Logger getLogger() {
        if (!initialized) {
            init();
        }
        return logger;
    }
}
