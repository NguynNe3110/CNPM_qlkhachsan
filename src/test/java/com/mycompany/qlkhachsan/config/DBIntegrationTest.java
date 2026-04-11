package com.mycompany.qlkhachsan.config;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử tích hợp (Integration Test) cho kết nối Database.
 */
public class DBIntegrationTest {

    /**
     * Kiểm tra xem cấu hình HikariCP có thể khởi tạo và lấy được kết nối
     * từ MySQL trên Cloud (Aiven) hay không.
     */
    @Test
    void testDatabaseConnection() {
        // Thử lấy một kết nối từ Pool
        try (Connection conn = DBConfig.getConnection()) {
            // Kiểm chứng: Kết nối không được rỗng
            assertNotNull(conn, "Kết nối Database không được để trống");
            // Kiểm chứng: Kết nối phải đang mở (không bị đóng/lỗi mạng)
            assertFalse(conn.isClosed(), "Kết nối Database phải đang ở trạng thái mở");
            
            System.out.println("Kiểm thử tích hợp: Kết nối Database thành công ");
        } catch (SQLException e) {
            // Nếu có lỗi, đánh dấu bài test này là Thất bại (Fail)
            fail("Kết nối Database thất bại: " + e.getMessage());
        }
    }
}
