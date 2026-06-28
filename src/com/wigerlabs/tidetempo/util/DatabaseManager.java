package com.wigerlabs.tidetempo.util;

import java.io.File;
import java.io.IOException;

public class DatabaseManager {

    // These should match your MySQL.java credentials
    private static final String DATABASE = "tidetempo";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "maleesha@2005";

    private static String resolveExecutable(String command) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            String[] searchPaths = {
                "C:\\Program Files\\MySQL\\MySQL Workbench 8.0\\",
                "C:\\Program Files\\MySQL\\MySQL Server 8.4\\bin\\",
                "C:\\Program Files\\MySQL\\MySQL Server 8.3\\bin\\",
                "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\",
                "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\",
                "C:\\xampp\\mysql\\bin\\"
            };
            for (String path : searchPaths) {
                File file = new File(path + command + ".exe");
                if (file.exists()) {
                    return file.getAbsolutePath();
                }
            }
        }
        return command; // Fallback to PATH
    }

    public static boolean backupDatabase(String targetFilePath) {
        AppLogger.getLogger().info("Starting database backup to: " + targetFilePath);
        
        // mysqldump -u root -pmaleesha@2005 tidetempo -r "path/to/backup.sql"
        ProcessBuilder processBuilder = new ProcessBuilder(
                resolveExecutable("mysqldump"),
                "-u" + USERNAME,
                "-p" + PASSWORD,
                DATABASE,
                "-r",
                targetFilePath
        );

        try {
            Process process = processBuilder.start();
            int processComplete = process.waitFor();
            if (processComplete == 0) {
                AppLogger.getLogger().info("Database backup created successfully.");
                return true;
            } else {
                AppLogger.getLogger().error("Could not create the database backup. Process exited with code: " + processComplete);
            }
        } catch (IOException | InterruptedException e) {
            AppLogger.getLogger().error("Exception occurred during database backup", e);
        }
        return false;
    }

    public static boolean restoreDatabase(String sourceFilePath) {
        AppLogger.getLogger().info("Starting database restore from: " + sourceFilePath);
        
        File sqlFile = new File(sourceFilePath);
        if (!sqlFile.exists()) {
            AppLogger.getLogger().error("Backup file does not exist: " + sourceFilePath);
            return false;
        }

        // mysql -u root -pmaleesha@2005 tidetempo -e "source path/to/backup.sql"
        ProcessBuilder processBuilder = new ProcessBuilder(
                resolveExecutable("mysql"),
                "-u" + USERNAME,
                "-p" + PASSWORD,
                DATABASE,
                "-e",
                "source " + sourceFilePath
        );

        try {
            Process process = processBuilder.start();
            int processComplete = process.waitFor();
            if (processComplete == 0) {
                AppLogger.getLogger().info("Database restored successfully.");
                return true;
            } else {
                AppLogger.getLogger().error("Could not restore the database. Process exited with code: " + processComplete);
            }
        } catch (IOException | InterruptedException e) {
            AppLogger.getLogger().error("Exception occurred during database restore", e);
        }
        return false;
    }
}
