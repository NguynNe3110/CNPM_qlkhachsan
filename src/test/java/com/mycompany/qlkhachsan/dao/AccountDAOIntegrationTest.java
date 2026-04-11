package com.mycompany.qlkhachsan.dao;

import com.mycompany.qlkhachsan.model.Account;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountDAOIntegrationTest {
    private static AccountDAO accountDAO;

    @BeforeAll
    static void init() {
        accountDAO = new AccountDAO();
        accountDAO.initializeDefaultAdmin();
    }

    @Test
    void testLogin_Success_WithDefaultAdmin() {
        Account account = accountDAO.login("admin", "admin");
        assertNotNull(account, "Loi: Dang nhap khong thanh cong voi tai khoan mac dinh.");
        assertEquals("admin", account.getUserName(), "Loi: Ten tai khoan tra ve khong khop.");
    }

    @Test
    void testLogin_Fail_WithWrongPassword() {
        Account account = accountDAO.login("admin", "wrong_pass");
        assertNull(account, "Loi: He thong khong duoc phep cho dang nhap khi sai mat khau.");
    }

    @Test
    void testLogin_Fail_WithNonExistentUser() {
        Account account = accountDAO.login("user_xyz_invalid", "pass");
        assertNull(account, "Loi: Tai khoan khong ton tai nhung van cho phep dang nhap.");
    }
}
