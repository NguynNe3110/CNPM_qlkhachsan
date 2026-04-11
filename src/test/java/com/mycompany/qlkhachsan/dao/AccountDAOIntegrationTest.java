package com.mycompany.qlkhachsan.dao;

import com.mycompany.qlkhachsan.model.Account;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử tích hợp cho chức năng Đăng nhập.
 * Giúp xác nhận tài khoản có thể truy cập hệ thống từ Database thật.
 */
public class AccountDAOIntegrationTest {
    private static AccountDAO accountDAO;

    // Chạy đầu tiên để khởi tạo các đối tượng cần thiết
    @BeforeAll
    static void init() {
        accountDAO = new AccountDAO();
        // Tạo tài khoản admin/admin nếu trong DB chưa có
        accountDAO.initializeDefaultAdmin();
    }

    // Tình huống: Người dùng nhập đúng Tên và Mật khẩu
    @Test
    void testLogin_Success_WithDefaultAdmin() {
        Account account = accountDAO.login("admin", "admin");
        
        // Khẳng định: Kết quả không được để trống
        assertNotNull(account, "Loi: Dang nhap khong thanh cong voi tai khoan mac dinh.");
        // Khẳng định: Tên người dùng phải khớp
        assertEquals("admin", account.getUserName(), "Loi: Ten tai khoan tra ve khong khop.");
    }

    // Tình huống: Người dùng nhập sai mật khẩu
    @Test
    void testLogin_Fail_WithWrongPassword() {
        Account account = accountDAO.login("admin", "mat_khau_sai");
        
        // Khẳng định: Hệ thống phải trả về Null (tức là từ chối cho vào)
        assertNull(account, "Loi: He thong khong duoc phep cho dang nhap khi sai mat khau.");
    }

    // Tình huống: Đăng nhập với một cái tên không tồn tại
    @Test
    void testLogin_Fail_WithNonExistentUser() {
        Account account = accountDAO.login("nguoi_dung_la_mat", "pass123");
        assertNull(account, "Loi: Tai khoan khong ton tai nhung van cho phep dang nhap.");
    }
}
