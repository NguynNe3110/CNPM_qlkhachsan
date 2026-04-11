package com.mycompany.qlkhachsan.dao;

import com.mycompany.qlkhachsan.model.Account;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountDAOIntegrationTest {
    private static AccountDAO accountDAO;

    // Hàm này chạy ĐẦU TIÊN và DUY NHẤT 1 LẦN trước khi tất cả các bài test bắt đầu
    @BeforeAll
    static void init() {
        accountDAO = new AccountDAO();
        // Đảm bảo có sẵn tài khoản admin/admin trong Database để chạy thử
        accountDAO.initializeDefaultAdmin();
    }
     //Trường hợp 1: Đăng nhập THÀNH CÔNG với thông tin chính xác
    @Test
    void testLogin_Success_WithDefaultAdmin() {
        // Thực hiện hành động: Gọi hàm Login với admin/admin
        Account account = accountDAO.login("admin", "admin");

        // Kiểm tra kết quả:
        // 1. Phải lấy được đối tượng Account (không được NULL)
        assertNotNull(account, "Đăng nhập phải thành công với tài khoản đúng");
        // 2. Tên người dùng trong DB trả về phải là 'admin'
        assertEquals("admin", account.getUserName());
        // 3. Quyền hạn phải là 'ADMIN'
        assertEquals("ADMIN", account.getRole());
    }

    // Trường hợp 2: Đăng nhập THẤT BẠI do sai mật khẩu
    @Test
    void testLogin_Fail_WithWrongPassword() {
        // Hành động: Nhập đúng username nhưng sai password
        Account account = accountDAO.login("admin", "wrongpassword123");

        // Kiểm tra: Kết quả trả về phải là NULL (hệ thống chặn không cho vào)
        assertNull(account, "Đăng nhập phải thất bại nếu sai mật khẩu");
    }

    // Trường hợp 3: Đăng nhập THẤT BẠI do người dùng không tồn tại
    @Test
    void testLogin_Fail_WithNonExistentUser() {
        // Hành động: Nhập một username không có trong DB
        Account account = accountDAO.login("user_khong_ton_tai", "password");

        // Kiểm tra: Kết quả phải là NULL
        assertNull(account, "Đăng nhập phải thất bại nếu tài khoản không tồn tại");
    }
}
