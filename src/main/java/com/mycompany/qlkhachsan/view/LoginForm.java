package com.mycompany.qlkhachsan.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginForm() {
        setTitle("Hotel Management System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel with light background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(230, 242, 255)); // Light blue tint
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("LOGIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.BLACK); // Black text
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        mainPanel.add(new JLabel("Username:"), gbc);
        txtUsername = new JTextField(15);
        txtUsername.setForeground(Color.BLACK);
        gbc.gridx = 1;
        mainPanel.add(txtUsername, gbc);

        // Password
        gbc.gridy = 2; gbc.gridx = 0;
        JLabel lblPwd = new JLabel("Password:");
        lblPwd.setForeground(Color.BLACK);
        mainPanel.add(lblPwd, gbc);
        txtPassword = new JPasswordField(15);
        txtPassword.setForeground(Color.BLACK);
        gbc.gridx = 1;
        mainPanel.add(txtPassword, gbc);

        // Login Button
        btnLogin = new JButton("Sign In");
        btnLogin.setBackground(new Color(135, 206, 235)); // Consistent Sky Blue
        btnLogin.setForeground(Color.BLACK); // Clearly Black
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(btnLogin, gbc);

        add(mainPanel);
    }

    public String getUsername() { return txtUsername.getText(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }

    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}
