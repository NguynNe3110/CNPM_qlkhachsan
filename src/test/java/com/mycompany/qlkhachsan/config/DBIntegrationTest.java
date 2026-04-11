package com.mycompany.qlkhachsan.config;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử tích hợp khả năng kết nối tới Cơ sở dữ liệu.
 */
public class DBIntegrationTest {

    // Thử nghiệm khả năng lấy kết nối từ Connection Pool
    @Test
    void testDatabaseConnection() {
        try (Connection conn = DBConfig.getConnection()) {
            // Khẳng định: Kết nối trả về không được Null
            assertNotNull(conn, "Loi: Ket noi Database tra ve gia tri Null.");
            // Khẳng định: Kết nối phải đang mở, không bị ngắt giữa chừng
            assertFalse(conn.isClosed(), "Loi: Ket noi Database da bi dong hoac khong the thiet lap.");
            System.out.println("Kiem thu tich hop: Ket noi Database thanh cong.");
        } catch (SQLException e) {
            // Nếu có lỗi, thông báo thất bại ngay lập tức
            fail("Loi nghiem trong: Khong the ket noi toi CSDL. Chi tiet: " + e.getMessage());
        }
    }
}
