package com.petprj.utils;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_URL = "exchanger.db";

    public static Connection getConnection() throws SQLException {

        String dbPath = DatabaseManager.class
                .getClassLoader()
                .getResource("exchanger.db")
                .getPath();

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }

        URL resource = DatabaseManager.class.getClassLoader().getResource(DB_URL);
        if (resource == null) {
            throw new RuntimeException("Database file not found in resources: " + DB_URL);
        }

        String path = resource.getPath();
        String url = "jdbc:sqlite:" + dbPath;

        return DriverManager.getConnection(url);}
    }