package com.mycompany.qlkhachsan.dao;

import com.mycompany.qlkhachsan.config.DBConfig;
import com.mycompany.qlkhachsan.model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO implements BaseDAO<Booking> {
    @Override
    public List<Booking> getAll() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM Booking";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Booking b = new Booking();
                b.setId(rs.getInt("id"));
                b.setCustomerId(rs.getInt("customerId"));
                b.setRoomId(rs.getInt("roomId"));
                b.setStaffId(rs.getInt("staffId"));
                b.setStatus(rs.getString("status"));
                b.setTotalAmount(rs.getDouble("totalAmount"));
                list.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Booking getById(int id) {
        String sql = "SELECT * FROM Booking WHERE id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Booking b = new Booking();
                    b.setId(rs.getInt("id"));
                    b.setCustomerId(rs.getInt("customerId"));
                    b.setRoomId(rs.getInt("roomId"));
                    b.setStaffId(rs.getInt("staffId"));
                    b.setStatus(rs.getString("status"));
                    b.setTotalAmount(rs.getDouble("totalAmount"));
                    return b;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean add(Booking b) {
        String sql = "INSERT INTO Booking (customerId, roomId, staffId, status, totalAmount) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getCustomerId());
            ps.setInt(2, b.getRoomId());
            ps.setInt(3, b.getStaffId());
            ps.setString(4, b.getStatus());
            ps.setDouble(5, b.getTotalAmount());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Booking b) {
        String sql = "UPDATE Booking SET customerId=?, roomId=?, staffId=?, status=?, totalAmount=? WHERE id=?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getCustomerId());
            ps.setInt(2, b.getRoomId());
            ps.setInt(3, b.getStaffId());
            ps.setString(4, b.getStatus());
            ps.setDouble(5, b.getTotalAmount());
            ps.setInt(6, b.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Booking WHERE id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
