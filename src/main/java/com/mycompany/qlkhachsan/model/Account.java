package com.mycompany.qlkhachsan.model;

public class Account {
    private int id;
    private String userName;
    private String password;
    private String role; // ADMIN or STAFF
    private boolean isLogin;
    private boolean enable;

    public Account() {}

    public Account(int id, String userName, String password, String role, boolean isLogin, boolean enable) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.isLogin = isLogin;
        this.enable = enable;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isLogin() { return isLogin; }
    public void setLogin(boolean isLogin) { this.isLogin = isLogin; }

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }
}
