package com.mycompany.qlkhachsan.controller;

import com.mycompany.qlkhachsan.dao.AccountDAO;
import com.mycompany.qlkhachsan.model.Account;
import com.mycompany.qlkhachsan.view.AdminDashboard;
import com.mycompany.qlkhachsan.view.LoginForm;
import com.mycompany.qlkhachsan.view.StaffDashboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private LoginForm view;
    private AccountDAO model;

    public LoginController(LoginForm view, AccountDAO model) {
        this.view = view;
        this.model = model;

        this.view.addLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        Account account = model.login(username, password);

        if (account != null) {
            view.setVisible(false);
            if ("ADMIN".equals(account.getRole())) {
                new AdminDashboard().setVisible(true);
            } else {
                new StaffDashboard().setVisible(true);
            }
            view.dispose();
        } else {
            view.showMessage("Sai tên đăng nhập hoặc mật khẩu, hoặc tài khoản đã bị vô hiệu hóa.");
        }
    }
}
