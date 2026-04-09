
package com.mycompany.qlkhachsan;

import java.sql.Connection;
import java.sql.DriverManager;
import com.mycompany.qlkhachsan.config.DBConfig;

public class TestDB {
    public static void main(String[] args) {

        try {
            Connection conn = DriverManager.getConnection(
                    DBConfig.getUrl(),
                    DBConfig.getUser(),
                    DBConfig.getPassword()
            );

            System.out.println("OK CONNECT!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}