package com.mycompany.qlkhachsan.config;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class DBIntegrationTest {

    @Test
    void testDatabaseConnection() {
        try (Connection conn = DBConfig.getConnection()) {
            assertNotNull(conn, "Loi: Ket noi Database tra ve gia tri Null.");
            assertFalse(conn.isClosed(), "Loi: Ket noi Database da bi dong hoac khong the thiet lap.");
            System.out.println("Kiem thu tich hop: Ket noi Database thanh cong.");
        } catch (SQLException e) {
            fail("Loi nghiem trong: Khong the ket noi toi CSDL. Chi tiet: " + e.getMessage());
        }
    }
}
