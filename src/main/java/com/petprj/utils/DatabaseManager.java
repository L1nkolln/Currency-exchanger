package com.petprj.utils;

import java.io.File;
import java.io.IOException;
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

//    public static void initDatabase() {
//        var file = new File("src/main/resources/exchanger.db");
//        if (!file.exists()) {
//            System.out.println("File not found");
//        }
//    }
//}






//-- DROP TABLE sqlite_master
//
//CREATE TABLE Currencies
//        (
//                ID       integer primary key,
//                Code     text unique,
//                FullName text,
//                Sigh     text
//        );
//
//-- DROP TABLE Currencies;
//
//INSERT INTO Currencies (Code, FullName, Sigh)
//VALUES ('USD', 'US Dollar', '$'),
//       ('EUR', 'Euro', '€'),
//               ('RUB', 'Russian Ruble', '₽'),
//               ('BYN', 'Belarussian Ruble', 'BYN'),
//               ('CNY', 'Yuan Renminbi', '¥'),
//               ('PLN', 'Zloty', 'zł');
//
//CREATE TABLE ExchangeRates
//        (
//                ID               integer primary key ,
//                BaseCurrencyId   int,
//                TargetCurrencyId int,
//                rate             real,
//                foreign key (BaseCurrencyId) REFERENCES Currencies (ID),
//foreign key (TargetCurrencyId) REFERENCES Currencies (ID)
//        );
//
//        -- DROP TABLE ExchangeRates;
//
//INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, rate)
//VALUES (1, 2, 0.92),
//       (1, 3, 95.5),
//               (1, 4, 3.25),
//               (1, 5, 7.10),
//               (1, 6, 4.05);
//
//        SELECT Code, Sigh
//FROM Currencies;


