package com.mycompany.qlkhachsan.dao;

import com.mycompany.qlkhachsan.config.DBConfig;
import com.mycompany.qlkhachsan.model.Account;
import com.mycompany.qlkhachsan.util.PasswordUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO implements BaseDAO<Account> {

    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account(
            rs.getInt("id"),
            rs.getString("userName"),
            rs.getString("password"),
            rs.getString("role"),
            rs.getBoolean("isLogin"),
            rs.getBoolean("enable")
        );
    }

    @Override
    public List<Account> getAll() {
        List<Account> list = new ArrayList<>();
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Account");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Account getById(int id) {
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Account WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Đăng nhập hỗ trợ cả plain text (DB cũ) lẫn SHA-256.
     * Khi login bằng plain text thành công → tự động hash và lưu lại (migrate 1 lần).
     */
    public Account login(String username, String password) {
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM Account WHERE userName = ? AND enable = 1")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Account a = mapRow(rs);
                String stored = a.getPassword();
                boolean match;

                if (PasswordUtils.isHashed(stored)) {
                    // Đã hash → verify bình thường
                    match = PasswordUtils.verify(password, stored);
                } else {
                    // Plain text cũ → so sánh trực tiếp
                    match = stored.equals(password);
                    if (match) {
                        // Migrate: lưu hash thay plain text
                        String h = PasswordUtils.hash(password);
                        try (PreparedStatement ps2 = con.prepareStatement(
                                "UPDATE Account SET password=? WHERE id=?")) {
                            ps2.setString(1, h); ps2.setInt(2, a.getId());
                            ps2.executeUpdate();
                        }
                        a.setPassword(h);
                        System.out.println("[AccountDAO] Migrated password for: " + username);
                    }
                }
                return match ? a : null;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Kiểm tra username đã tồn tại chưa (loại trừ id hiện tại khi update) */
    public boolean isUsernameTaken(String username, int excludeId) {
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT COUNT(*) FROM Account WHERE userName = ? AND id != ?")) {
            ps.setString(1, username); ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean add(Account a) {
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO Account (userName, password, role, isLogin, enable) VALUES (?,?,?,?,?)")) {
            ps.setString(1, a.getUserName());
            // Luôn lưu hash
            String pw = PasswordUtils.isHashed(a.getPassword())
                    ? a.getPassword() : PasswordUtils.hash(a.getPassword());
            ps.setString(2, pw);
            ps.setString(3, a.getRole());
            ps.setBoolean(4, false);
            ps.setBoolean(5, a.isEnable());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean update(Account a) {
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Account SET userName=?,password=?,role=?,isLogin=?,enable=? WHERE id=?")) {
            ps.setString(1, a.getUserName());
            String pw = PasswordUtils.isHashed(a.getPassword())
                    ? a.getPassword() : PasswordUtils.hash(a.getPassword());
            ps.setString(2, pw);
            ps.setString(3, a.getRole());
            ps.setBoolean(4, a.isLogin());
            ps.setBoolean(5, a.isEnable());
            ps.setInt(6, a.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean delete(int id) {
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Account SET enable = 0 WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Tạo admin mặc định "admin/admin" nếu bảng trống.
     * Nếu DB đã có admin plain-text, login() sẽ tự migrate.
     */
    public void initializeDefaultAdmin() {
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM Account");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                Account admin = new Account(0, "admin", PasswordUtils.hash("admin"), "ADMIN", false, true);
                add(admin);
                System.out.println("[AccountDAO] Default account created: admin / admin");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}