package com.mycompany.qlkhachsan.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConfig {

    private static Properties properties = new Properties();

    static {
        try {
            InputStream input = DBConfig.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties");

            if (input == null) {
                throw new RuntimeException("Không tìm thấy file config.properties");
            }

            properties.load(input);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

public static String getUrl() {
    String env = System.getenv("DB_URL");
    return env != null ? env : properties.getProperty("db.url");
}

public static String getUser() {
    String env = System.getenv("DB_USER");
    return env != null ? env : properties.getProperty("db.user");
}

public static String getPassword() {
    String env = System.getenv("DB_PASSWORD");
    return env != null ? env : properties.getProperty("db.password");
}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }
}~