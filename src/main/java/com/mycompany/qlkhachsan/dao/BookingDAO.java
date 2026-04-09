package com.mycompany.qlkhachsan.dao;

import com.mycompany.qlkhachsan.config.DBConfig;
import com.mycompany.qlkhachsan.model.Booking;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO implements BaseDAO<Booking> {

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setCustomerId(rs.getInt("customerId"));
        b.setRoomId(rs.getInt("roomId"));
        b.setStaffId(rs.getInt("staffId"));
        b.setStatus(rs.getString("status"));
        b.setTotalAmount(rs.getDouble("totalAmount"));
        Date bd = rs.getDate("bookingDate");
        Date ci = rs.getDate("checkInDate");
        Date co = rs.getDate("checkOutDate");
        if (bd != null) b.setBookingDate(bd.toLocalDate());
        if (ci != null) b.setCheckInDate(ci.toLocalDate());
        if (co != null) b.setCheckOutDate(co.toLocalDate());
        return b;
    }

    @Override
    public List<Booking> getAll() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM Booking";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Booking getById(int id) {
        String sql = "SELECT * FROM Booking WHERE id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Lấy booking đang CHECKED_IN theo roomId */
    public Booking getActiveByRoomId(int roomId) {
        String sql = "SELECT * FROM Booking WHERE roomId = ? AND status = 'CHECKED_IN'";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean add(Booking b) {
        String sql = "INSERT INTO Booking (customerId, roomId, staffId, status, totalAmount, bookingDate, checkInDate, checkOutDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getCustomerId());
            ps.setInt(2, b.getRoomId());
            ps.setInt(3, b.getStaffId());
            ps.setString(4, b.getStatus());
            ps.setDouble(5, b.getTotalAmount());
            ps.setDate(6, b.getBookingDate() != null ? Date.valueOf(b.getBookingDate()) : Date.valueOf(LocalDate.now()));
            ps.setDate(7, b.getCheckInDate() != null ? Date.valueOf(b.getCheckInDate()) : null);
            ps.setDate(8, b.getCheckOutDate() != null ? Date.valueOf(b.getCheckOutDate()) : null);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean update(Booking b) {
        String sql = "UPDATE Booking SET customerId=?, roomId=?, staffId=?, status=?, totalAmount=?, bookingDate=?, checkInDate=?, checkOutDate=? WHERE id=?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getCustomerId());
            ps.setInt(2, b.getRoomId());
            ps.setInt(3, b.getStaffId());
            ps.setString(4, b.getStatus());
            ps.setDouble(5, b.getTotalAmount());
            ps.setDate(6, b.getBookingDate() != null ? Date.valueOf(b.getBookingDate()) : null);
            ps.setDate(7, b.getCheckInDate() != null ? Date.valueOf(b.getCheckInDate()) : null);
            ps.setDate(8, b.getCheckOutDate() != null ? Date.valueOf(b.getCheckOutDate()) : null);
            ps.setInt(9, b.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM Booking WHERE id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}