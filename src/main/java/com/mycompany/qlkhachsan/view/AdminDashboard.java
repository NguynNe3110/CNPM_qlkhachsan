package com.mycompany.qlkhachsan.view;

import com.mycompany.qlkhachsan.dao.*;
import com.mycompany.qlkhachsan.model.*;
import com.mycompany.qlkhachsan.util.UIUtils;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private AccountDAO accountDAO = new AccountDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();
    private BookingDAO bookingDAO = new BookingDAO();

    public AdminDashboard() {
        setTitle("Admin Dashboard - Hotel Management System");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(135, 206, 235)); // Sky Blue
        JLabel lblTitle = new JLabel("  QUẢN TRỊ VIÊN - HOTEL MANAGEMENT", SwingConstants.LEFT);
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.setPreferredSize(new Dimension(1100, 70));
        add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        tabbedPane.addTab("Quản lý Tài khoản", createAccountPanel());
        tabbedPane.addTab("Quản lý Phòng", createRoomPanel());
        tabbedPane.addTab("Quản lý Dịch vụ", createServicePanel());
        tabbedPane.addTab("Danh sách Booking", createBookingPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // --- ACCOUNT PANEL ---
    private JPanel createAccountPanel() {
        String[] cols = {"ID", "Tên đăng nhập", "Mật khẩu", "Quyền", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        loadAccountData(model);

        JButton btnAdd = new JButton("Thêm mới");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Vô hiệu hóa");
        
        btnAdd.addActionListener(e -> {
            JTextField user = new JTextField();
            JTextField pass = new JTextField();
            String[] roles = {"STAFF", "ADMIN"};
            JComboBox<String> role = new JComboBox<>(roles);
            Object[] fields = {"Username:", user, "Password:", pass, "Role:", role};
            if (JOptionPane.showConfirmDialog(null, fields, "Thêm tài khoản", JOptionPane.OK_CANCEL_OPTION) == 0) {
                Account a = new Account(0, user.getText(), pass.getText(), (String)role.getSelectedItem(), false, true);
                if (accountDAO.add(a)) {
                    UIUtils.showInfo(this, "Đã thêm tài khoản mới.");
                    loadAccountData(model);
                }
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int)model.getValueAt(row, 0);
                Account a = accountDAO.getById(id);
                JTextField user = new JTextField(a.getUserName());
                JTextField pass = new JTextField(a.getPassword());
                String[] roles = {"STAFF", "ADMIN"};
                JComboBox<String> role = new JComboBox<>(roles);
                role.setSelectedItem(a.getRole());
                Object[] fields = {"Username:", user, "Password:", pass, "Role:", role};
                if (JOptionPane.showConfirmDialog(null, fields, "Sửa tài khoản", JOptionPane.OK_CANCEL_OPTION) == 0) {
                    a.setUserName(user.getText());
                    a.setPassword(pass.getText());
                    a.setRole((String)role.getSelectedItem());
                    if (accountDAO.update(a)) {
                        UIUtils.showInfo(this, "Đã cập nhật.");
                        loadAccountData(model);
                    }
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int)model.getValueAt(row, 0);
                if (JOptionPane.showConfirmDialog(this, "Vô hiệu hóa tài khoản này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == 0) {
                    accountDAO.delete(id);
                    loadAccountData(model);
                }
            }
        });

        return createLayoutWithActions("QUẢN LÝ TÀI KHOẢN", table, btnAdd, btnEdit, btnDelete);
    }

    private void loadAccountData(DefaultTableModel model) {
        model.setRowCount(0);
        for (Account a : accountDAO.getAll()) {
            model.addRow(new Object[]{a.getId(), a.getUserName(), a.getPassword(), a.getRole(), a.isEnable() ? "Hoạt động" : "Khóa"});
        }
    }

    // --- ROOM PANEL ---
    private JPanel createRoomPanel() {
        String[] cols = {"ID", "Số phòng", "Loại", "Giá", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        loadRoomData(model);

        JButton btnAdd = new JButton("Thêm phòng");
        JButton btnEdit = new JButton("Sửa giá");
        JButton btnDelete = new JButton("Gỡ");

        btnAdd.addActionListener(e -> {
            JTextField num = new JTextField();
            JTextField type = new JTextField();
            JTextField price = new JTextField();
            Object[] fields = {"Số phòng:", num, "Loại phòng:", type, "Giá niêm yết:", price};
            if (JOptionPane.showConfirmDialog(null, fields, "Thêm phòng", JOptionPane.OK_CANCEL_OPTION) == 0) {
                Room r = new Room();
                r.setRoomNumber(num.getText());
                r.setType(type.getText());
                r.setPrice(Double.parseDouble(price.getText()));
                r.setStatus("EMPTY");
                r.setEnable(true);
                if (roomDAO.add(r)) loadRoomData(model);
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int)model.getValueAt(row, 0);
                Room r = roomDAO.getById(id);
                String newPrice = JOptionPane.showInputDialog("Nhập giá mới:", r.getPrice());
                if (newPrice != null) {
                    r.setPrice(Double.parseDouble(newPrice));
                    roomDAO.update(r);
                    loadRoomData(model);
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int)model.getValueAt(row, 0);
                roomDAO.delete(id);
                loadRoomData(model);
            }
        });

        return createLayoutWithActions("QUẢN LÝ PHÒNG", table, btnAdd, btnEdit, btnDelete);
    }

    private void loadRoomData(DefaultTableModel model) {
        model.setRowCount(0);
        for (Room r : roomDAO.getAll()) {
            model.addRow(new Object[]{r.getId(), r.getRoomNumber(), r.getType(), r.getPrice(), r.getStatus()});
        }
    }

    // --- SERVICE PANEL ---
    private JPanel createServicePanel() {
        String[] cols = {"ID", "Dịch vụ", "Đơn giá"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        loadServiceData(model);

        JButton btnAdd = new JButton("Thêm DV");
        JButton btnDelete = new JButton("Xóa");

        btnAdd.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Tên dịch vụ:");
            String price = JOptionPane.showInputDialog("Giá:");
            if (name != null && price != null) {
                Service s = new Service();
                s.setName(name);
                s.setPrice(Double.parseDouble(price));
                serviceDAO.add(s);
                loadServiceData(model);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int)model.getValueAt(row, 0);
                serviceDAO.delete(id);
                loadServiceData(model);
            }
        });

        return createLayoutWithActions("QUẢN LÝ DỊCH VỤ", table, btnAdd, null, btnDelete);
    }

    private void loadServiceData(DefaultTableModel model) {
        model.setRowCount(0);
        for (Service s : serviceDAO.getAll()) {
            model.addRow(new Object[]{s.getId(), s.getName(), s.getPrice()});
        }
    }

    private JPanel createBookingPanel() {
        String[] cols = {"ID", "Khách #", "Phòng #", "N.Viên #", "Trạng thái", "Tổng tiền"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        for (Booking b : bookingDAO.getAll()) model.addRow(new Object[]{b.getId(), b.getCustomerId(), b.getRoomId(), b.getStaffId(), b.getStatus(), b.getTotalAmount()});
        
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(new JLabel("LỊCH SỬĐẶT PHÒNG"), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel createLayoutWithActions(String title, JTable table, JButton add, JButton edit, JButton delete) {
        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(new JLabel(title), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (add != null) actions.add(add);
        if (edit != null) actions.add(edit);
        if (delete != null) actions.add(delete);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }
}
