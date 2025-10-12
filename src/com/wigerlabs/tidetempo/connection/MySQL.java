package com.wigerlabs.tidetempo.connection;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private static final String DATABASE = "tidetempo";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "maleesha@2005";
    private static Connection connection;

    public static Connection createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            if (connection == null) {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DATABASE, USERNAME, PASSWORD);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static ResultSet execute(String query) throws SQLException {
        Statement smt = createConnection().createStatement();
        if(query.startsWith("SELECT")) {
            ResultSet resultSet = smt.executeQuery(query);
            return resultSet;
        }else {
            smt.executeUpdate(query);
            return null;
        }
    }
}
