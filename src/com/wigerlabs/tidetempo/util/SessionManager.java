package com.wigerlabs.tidetempo.util;

import com.wigerlabs.tidetempo.connection.MySQL;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Properties;

public class SessionManager {

    private static final String CONFIG_DIR = System.getProperty("user.dir");
    private static final String SESSION_FILE = CONFIG_DIR + File.separator + "session.properties";

    // Ensure the config directory exists
    private static void ensureConfigDirectory() {
        File dir = new File(CONFIG_DIR);
        if (!dir.exists()) {
            dir.mkdirs(); // Create the .tidetempo directory if it doesn't exist
        }
    }

    // Save user data to session.properties
    public static void saveUserSession(int id, String name, String email, String password) {
        ensureConfigDirectory();
        Properties props = new Properties();
        props.setProperty("id", String.valueOf(id));
        props.setProperty("username", name);
        props.setProperty("email", email);
        props.setProperty("password", password);

        try (FileOutputStream fos = new FileOutputStream(SESSION_FILE)) {
            props.store(fos, "User Session");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retrieve user data from session.properties
    public static User getUserSession() {
        File sessionFile = new File(SESSION_FILE);
        if (!sessionFile.exists()) {
            return null; // No session file found
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(SESSION_FILE)) {
            props.load(fis);

            String idStr = props.getProperty("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                return null;
            }

            User sessionUser = new User();
            sessionUser.id = Integer.parseInt(idStr.trim());
            sessionUser.name = props.getProperty("username");
            sessionUser.email = props.getProperty("email");
            sessionUser.password = props.getProperty("password");
            
            return sessionUser;

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Clear Session
    public static void clearUserSession() {
        File sessionFile = new File(SESSION_FILE);
        System.out.println(SESSION_FILE);
        if (sessionFile.exists()) {
            if (sessionFile.canWrite()) {
                Properties props = new Properties();
                try (FileOutputStream fos = new FileOutputStream(SESSION_FILE)) {
                    props.store(fos, "User Session Cleared");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Failed to clear file content: " + e.getMessage());
                }
            } else {
                System.out.println("No write permission for: " + SESSION_FILE);
            }
        } else {
            System.out.println("File does not exist: " + SESSION_FILE);
        }
    }
}
