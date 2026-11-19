package com.wigerlabs.tidetempo.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseManager {

    // These should match your MySQL.java credentials
    private static final String DATABASE = "tidetempo";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "maleesha@2005";

    // Optional override: -Dtidetempo.mysql.container=<name> or env TIDETEMPO_MYSQL_CONTAINER
    private static final String CONTAINER_PROPERTY = "tidetempo.mysql.container";
    private static final String CONTAINER_ENV = "TIDETEMPO_MYSQL_CONTAINER";

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

    /**
     * Name of the Docker container running MySQL, or null if MySQL is not in Docker.
     * Uses the configured override, otherwise the running container that publishes port 3306.
     */
    private static String findMySqlContainer() {
        String configured = System.getProperty(CONTAINER_PROPERTY, System.getenv(CONTAINER_ENV));
        if (configured != null && !configured.trim().isEmpty()) {
            return configured.trim();
        }
        try {
            Process process = new ProcessBuilder("docker", "ps", "--filter", "publish=3306", "--format", "{{.Names}}").start();
            String output = readFully(process.getInputStream()).trim();
            if (process.waitFor() == 0 && !output.isEmpty()) {
                return output.split("\\R")[0].trim();
            }
        } catch (IOException e) {
            // docker is not installed / not on PATH
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * Builds "mysqldump"/"mysql" command: through "docker exec" when MySQL runs in a
     * container (password passed via MYSQL_PWD, not on the command line), otherwise the local client.
     */
    private static List<String> buildCommand(String tool, boolean interactiveStdin, String... extraArgs) {
        List<String> command = new ArrayList<>();
        String container = findMySqlContainer();
        if (container != null) {
            AppLogger.getLogger().info("Using MySQL docker container: " + container);
            command.add("docker");
            command.add("exec");
            if (interactiveStdin) {
                command.add("-i");
            }
            command.add("-e");
            command.add("MYSQL_PWD=" + PASSWORD);
            command.add(container);
            command.add(tool);
            command.add("-u" + USERNAME);
        } else {
            command.add(resolveExecutable(tool));
            command.add("-u" + USERNAME);
            command.add("-p" + PASSWORD);
        }
        command.addAll(Arrays.asList(extraArgs));
        return command;
    }

    private static String readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    public static boolean backupDatabase(String targetFilePath) {
        AppLogger.getLogger().info("Starting database backup to: " + targetFilePath);

        // [docker exec -e MYSQL_PWD=... <container>] mysqldump -u root tidetempo   (output redirected to the file)
        ProcessBuilder processBuilder = new ProcessBuilder(buildCommand("mysqldump", false, DATABASE));
        File target = new File(targetFilePath);
        processBuilder.redirectOutput(target);

        try {
            Process process = processBuilder.start();
            String errorOutput = readFully(process.getErrorStream());
            int processComplete = process.waitFor();
            if (processComplete == 0) {
                AppLogger.getLogger().info("Database backup created successfully.");
                return true;
            } else {
                AppLogger.getLogger().error("Could not create the database backup. Process exited with code: "
                        + processComplete + ". " + errorOutput);
                target.delete(); // don't leave an empty/partial backup behind
            }
        } catch (IOException e) {
            AppLogger.getLogger().error("Exception occurred during database backup (is mysqldump or docker installed?)", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            AppLogger.getLogger().error("Database backup interrupted", e);
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

        // [docker exec -i -e MYSQL_PWD=... <container>] mysql -u root tidetempo   (SQL fed through stdin)
        ProcessBuilder processBuilder = new ProcessBuilder(buildCommand("mysql", true, DATABASE));
        processBuilder.redirectInput(sqlFile);

        try {
            Process process = processBuilder.start();
            String errorOutput = readFully(process.getErrorStream());
            int processComplete = process.waitFor();
            if (processComplete == 0) {
                AppLogger.getLogger().info("Database restored successfully.");
                return true;
            } else {
                AppLogger.getLogger().error("Could not restore the database. Process exited with code: "
                        + processComplete + ". " + errorOutput);
            }
        } catch (IOException e) {
            AppLogger.getLogger().error("Exception occurred during database restore (is mysql or docker installed?)", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            AppLogger.getLogger().error("Database restore interrupted", e);
        }
        return false;
    }
}
