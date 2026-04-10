package com.mycompany.qlkhachsan.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBConfig {
    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            try (InputStream input = DBConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    System.err.println("Sorry, unable to find config.properties");
                } else {
                    props.load(input);
                    
                    HikariConfig config = new HikariConfig();
                    config.setJdbcUrl(props.getProperty("db.url"));
                    config.setUsername(props.getProperty("db.user"));
                    config.setPassword(props.getProperty("db.password"));
                    
                    // Optimization settings for speed and stability
                    config.addDataSourceProperty("cachePrepStmts", "true");
                    config.addDataSourceProperty("prepStmtCacheSize", "250");
                    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                    config.setMaximumPoolSize(10); // Standard for desktop apps
                    config.setMinimumIdle(2);
                    config.setIdleTimeout(300000);
                    config.setConnectionTimeout(20000);
                    
                    dataSource = new HikariDataSource(config);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized properly.");
        }
        return dataSource.getConnection();
    }
}