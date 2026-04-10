package com.mycompany.qlkhachsan.util;

import com.mycompany.qlkhachsan.config.DBConfig;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        String[] createQueries = {
            "CREATE TABLE IF NOT EXISTS Account (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "userName VARCHAR(50) UNIQUE, " +
                "password VARCHAR(100), " +
                "role VARCHAR(20), " +
                "isLogin BOOLEAN DEFAULT FALSE, " +
                "enable BOOLEAN DEFAULT TRUE)",

            "CREATE TABLE IF NOT EXISTS Customer (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100), " +
                "phone VARCHAR(15) UNIQUE, " +
                "identityNumber VARCHAR(20) UNIQUE)",

            "CREATE TABLE IF NOT EXISTS ROOM (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "roomNumber VARCHAR(10) UNIQUE, " +
                "type VARCHAR(50), " +
                "status VARCHAR(30) DEFAULT 'EMPTY', " +
                "price DOUBLE, " +
                "enable BOOLEAN DEFAULT TRUE)",

            "CREATE TABLE IF NOT EXISTS Booking (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "customerId INT, " +
                "roomId INT, " +
                "staffId INT, " +
                "status VARCHAR(20) DEFAULT 'CHECKED_IN', " +
                "totalAmount DOUBLE DEFAULT 0, " +
                "bookingDate DATE, " +
                "checkInDate DATE, " +
                "checkOutDate DATE, " +
                "expectedCheckOutDate DATE, " +
                "actualCheckOutDate DATE)",

            "CREATE TABLE IF NOT EXISTS Service (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) UNIQUE, " +
                "price DOUBLE)",

            "CREATE TABLE IF NOT EXISTS ServiceUsage (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "bookingId INT, " +
                "serviceId INT, " +
                "quantity INT DEFAULT 1, " +
                "price DOUBLE)",

            "CREATE TABLE IF NOT EXISTS Payment (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "bookingId INT UNIQUE, " +
                "amount DOUBLE, " +
                "method VARCHAR(20), " +
                "status VARCHAR(20) DEFAULT 'UNPAID')"
        };

        // ALTER TABLE: thêm các cột mới nếu chưa tồn tại (cho DB cũ)
        String[] alterQueries = {
            "ALTER TABLE Booking ADD COLUMN bookingDate DATE",
            "ALTER TABLE Booking ADD COLUMN checkInDate DATE",
            "ALTER TABLE Booking ADD COLUMN checkOutDate DATE",
            "ALTER TABLE Booking ADD COLUMN expectedCheckOutDate DATE",
            "ALTER TABLE Booking ADD COLUMN actualCheckOutDate DATE",
            "ALTER TABLE Payment ADD CONSTRAINT uq_payment_booking UNIQUE (bookingId)"
        };

        try (Connection con = DBConfig.getConnection();
             Statement st = con.createStatement()) {

            for (String sql : createQueries) {
                st.executeUpdate(sql);
            }

            // Thử ALTER TABLE - bỏ qua lỗi nếu cột đã tồn tại
            for (String sql : alterQueries) {
                try {
                    st.executeUpdate(sql);
                } catch (Exception ignored) {
                    // Cột đã tồn tại hoặc constraint đã có → bỏ qua
                }
            }

            System.out.println("Database tables initialized successfully.");
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
}