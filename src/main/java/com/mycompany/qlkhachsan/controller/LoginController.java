package com.mycompany.qlkhachsan.controller;

import com.mycompany.qlkhachsan.dao.AccountDAO;
import com.mycompany.qlkhachsan.model.Account;
import com.mycompany.qlkhachsan.view.AdminDashboard;
import com.mycompany.qlkhachsan.view.LoginForm;
import com.mycompany.qlkhachsan.view.StaffDashboard;

public class LoginController {
    private LoginForm view;
    private AccountDAO model;

    public LoginController(LoginForm view, AccountDAO model) {
        this.view = view;
        this.model = model;
        this.view.addLoginListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = view.getUsername().trim();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            view.showMessage("Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        Account account = model.login(username, password);
        if (account != null) {
            view.setVisible(false);
            if ("ADMIN".equals(account.getRole())) {
                new AdminDashboard(account).setVisible(true);
            } else {
                new StaffDashboard(account).setVisible(true);
            }
            view.dispose();
        } else {
            view.showMessage("Sai tên đăng nhập / mật khẩu, hoặc tài khoản đã bị vô hiệu hóa.");
        }
    }
}