package com.mycompany.qlkhachsan.dao;

import com.mycompany.qlkhachsan.model.Account;
import com.mycompany.qlkhachsan.config.DBConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO implements BaseDAO<Account> {
    @Override
    public List<Account> getAll() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM Account";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Account a = new Account(
                    rs.getInt("id"),
                    rs.getString("userName"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getBoolean("isLogin"),
                    rs.getBoolean("enable")
                );
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Account getById(int id) {
        String sql = "SELECT * FROM Account WHERE id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getInt("id"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("isLogin"),
                        rs.getBoolean("enable")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account login(String username, String password) {
        String sql = "SELECT * FROM Account WHERE userName = ? AND password = ? AND enable = 1";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                        rs.getInt("id"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("isLogin"),
                        rs.getBoolean("enable")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean add(Account a) {
        String sql = "INSERT INTO Account (userName, password, role, isLogin, enable) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getUserName());
            ps.setString(2, a.getPassword());
            ps.setString(3, a.getRole());
            ps.setBoolean(4, a.isLogin());
            ps.setBoolean(5, a.isEnable());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Account a) {
        String sql = "UPDATE Account SET userName=?, password=?, role=?, isLogin=?, enable=? WHERE id=?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getUserName());
            ps.setString(2, a.getPassword());
            ps.setString(3, a.getRole());
            ps.setBoolean(4, a.isLogin());
            ps.setBoolean(5, a.isEnable());
            ps.setInt(6, a.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "UPDATE Account SET enable = 0 WHERE id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void initializeDefaultAdmin() {
        String sql = "SELECT COUNT(*) FROM Account";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Add default admin if table is empty
                Account admin = new Account(0, "admin", "admin", "ADMIN", false, true);
                add(admin);
                System.out.println("No accounts found. Default admin created: admin/admin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
