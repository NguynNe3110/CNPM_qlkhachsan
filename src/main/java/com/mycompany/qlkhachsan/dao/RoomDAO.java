package com.mycompany.qlkhachsan.dao;

import com.mycompany.qlkhachsan.config.DBConfig;
import com.mycompany.qlkhachsan.model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO implements BaseDAO<Room> {
    @Override
    public List<Room> getAll() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM ROOM";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Room r = new Room();
                r.setId(rs.getInt("id"));
                r.setRoomNumber(rs.getString("roomNumber"));
                r.setType(rs.getString("type"));
                r.setStatus(rs.getString("status"));
                r.setPrice(rs.getDouble("price"));
                r.setEnable(rs.getBoolean("enable"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Room getById(int id) {
        String sql = "SELECT * FROM ROOM WHERE id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Room r = new Room();
                    r.setId(rs.getInt("id"));
                    r.setRoomNumber(rs.getString("roomNumber"));
                    r.setType(rs.getString("type"));
                    r.setStatus(rs.getString("status"));
                    r.setPrice(rs.getDouble("price"));
                    r.setEnable(rs.getBoolean("enable"));
                    return r;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean add(Room r) {
        String sql = "INSERT INTO ROOM (roomNumber, type, status, price, enable) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getRoomNumber());
            ps.setString(2, r.getType());
            ps.setString(3, r.getStatus());
            ps.setDouble(4, r.getPrice());
            ps.setBoolean(5, r.isEnable());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Room r) {
        String sql = "UPDATE ROOM SET roomNumber=?, type=?, status=?, price=?, enable=? WHERE id=?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getRoomNumber());
            ps.setString(2, r.getType());
            ps.setString(3, r.getStatus());
            ps.setDouble(4, r.getPrice());
            ps.setBoolean(5, r.isEnable());
            ps.setInt(6, r.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "UPDATE ROOM SET enable = 0 WHERE id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /** Kiểm tra số phòng đã tồn tại chưa (loại trừ id hiện tại khi update) */
    public boolean isRoomNumberTaken(String roomNumber, int excludeId) {
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT COUNT(*) FROM ROOM WHERE roomNumber = ? AND id != ?")) {
            ps.setString(1, roomNumber); ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
