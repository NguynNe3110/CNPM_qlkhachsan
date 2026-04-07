package com.mycompany.qlkhachsan.util;

import com.mycompany.qlkhachsan.config.DBConfig;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        String[] queries = {
            "CREATE TABLE IF NOT EXISTS Account (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "userName VARCHAR(50) UNIQUE, " +
                "password VARCHAR(50), " +
                "role VARCHAR(20), " +
                "isLogin BOOLEAN, " +
                "enable BOOLEAN)",
            
            "CREATE TABLE IF NOT EXISTS Customer (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100), " +
                "phone VARCHAR(15), " +
                "identityNumber VARCHAR(20))",
            
            "CREATE TABLE IF NOT EXISTS ROOM (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "roomNumber VARCHAR(10) UNIQUE, " +
                "type VARCHAR(20), " +
                "status VARCHAR(20), " +
                "price DOUBLE, " +
                "enable BOOLEAN)",
            
            "CREATE TABLE IF NOT EXISTS Booking (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "customerId INT, " +
                "roomId INT, " +
                "staffId INT, " +
                "status VARCHAR(20), " +
                "totalAmount DOUBLE)",
            
            "CREATE TABLE IF NOT EXISTS Service (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100), " +
                "price DOUBLE)",
            
            "CREATE TABLE IF NOT EXISTS ServiceUsage (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "bookingId INT, " +
                "serviceId INT, " +
                "quantity INT, " +
                "price DOUBLE)",
            
            "CREATE TABLE IF NOT EXISTS Payment (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "bookingId INT, " +
                "amount DOUBLE, " +
                "method VARCHAR(20), " +
                "status VARCHAR(20))"
        };

        try (Connection con = DBConfig.getConnection();
             Statement st = con.createStatement()) {
            for (String sql : queries) {
                st.executeUpdate(sql);
            }
            System.out.println("Database tables initialized successfully.");
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
}
