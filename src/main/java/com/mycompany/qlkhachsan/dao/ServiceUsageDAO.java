package com.mycompany.qlkhachsan.dao;

import com.mycompany.qlkhachsan.config.DBConfig;
import com.mycompany.qlkhachsan.model.ServiceUsage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUsageDAO implements BaseDAO<ServiceUsage> {
    @Override
    public List<ServiceUsage> getAll() {
        List<ServiceUsage> list = new ArrayList<>();
        String sql = "SELECT * FROM ServiceUsage";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ServiceUsage su = new ServiceUsage();
                su.setId(rs.getInt("id"));
                su.setBookingId(rs.getInt("bookingId"));
                su.setServiceId(rs.getInt("serviceId"));
                su.setQuantity(rs.getInt("quantity"));
                su.setPrice(rs.getDouble("price"));
                list.add(su);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ServiceUsage getById(int id) {
        String sql = "SELECT * FROM ServiceUsage WHERE id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ServiceUsage su = new ServiceUsage();
                    su.setId(rs.getInt("id"));
                    su.setBookingId(rs.getInt("bookingId"));
                    su.setServiceId(rs.getInt("serviceId"));
                    su.setQuantity(rs.getInt("quantity"));
                    su.setPrice(rs.getDouble("price"));
                    return su;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ServiceUsage> getByBookingId(int bookingId) {
        List<ServiceUsage> list = new ArrayList<>();
        String sql = "SELECT * FROM ServiceUsage WHERE bookingId = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ServiceUsage su = new ServiceUsage();
                    su.setId(rs.getInt("id"));
                    su.setBookingId(rs.getInt("bookingId"));
                    su.setServiceId(rs.getInt("serviceId"));
                    su.setQuantity(rs.getInt("quantity"));
                    su.setPrice(rs.getDouble("price"));
                    list.add(su);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean add(ServiceUsage su) {
        String sql = "INSERT INTO ServiceUsage (bookingId, serviceId, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, su.getBookingId());
            ps.setInt(2, su.getServiceId());
            ps.setInt(3, su.getQuantity());
            ps.setDouble(4, su.getPrice());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(ServiceUsage su) {
        String sql = "UPDATE ServiceUsage SET bookingId=?, serviceId=?, quantity=?, price=? WHERE id=?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, su.getBookingId());
            ps.setInt(2, su.getServiceId());
            ps.setInt(3, su.getQuantity());
            ps.setDouble(4, su.getPrice());
            ps.setInt(5, su.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM ServiceUsage WHERE id = ?";
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
