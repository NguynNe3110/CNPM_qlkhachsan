package com.mycompany.qlkhachsan.view;

import com.mycompany.qlkhachsan.dao.*;
import com.mycompany.qlkhachsan.model.*;
import com.mycompany.qlkhachsan.service.BookingService;
import com.mycompany.qlkhachsan.util.UIUtils;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StaffDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private CustomerDAO customerDAO = new CustomerDAO();
    private BookingDAO bookingDAO = new BookingDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();
    private BookingService bookingService = new BookingService(bookingDAO, paymentDAO);

    public StaffDashboard() {
        setTitle("Staff Dashboard - Hotel Management System");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(144, 238, 144)); // Light Green
        JLabel lblTitle = new JLabel("  NHÂN VIÊN LỄ TÂN - QUẢN LÝ DỊCH VỤ", SwingConstants.LEFT);
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.setPreferredSize(new Dimension(1150, 70));
        add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        tabbedPane.addTab("Quản lý Khách hàng", createCustomerPanel());
        tabbedPane.addTab("Phòng & Check-in", createRoomPanel());
        tabbedPane.addTab("Booking & Check-out", createCheckOutPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // --- CUSTOMER MODULE ---
    private JPanel createCustomerPanel() {
        String[] cols = {"ID", "Họ tên", "SĐT", "CCCD/CMND"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        loadCustomerData(model);

        JButton btnAdd = new JButton("Thêm khách");
        JButton btnEdit = new JButton("Sửa thông tin");
        JTextField txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Tìm kiếm");

        btnSearch.addActionListener(e -> {
            model.setRowCount(0);
            for (Customer c : customerDAO.search(txtSearch.getText())) {
                model.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getIdentityNumber()});
            }
        });

        btnAdd.addActionListener(e -> {
            JTextField name = new JTextField();
            JTextField phone = new JTextField();
            JTextField idnum = new JTextField();
            Object[] fields = {"Họ tên:", name, "Số điện thoại:", phone, "Số định danh:", idnum};
            if (JOptionPane.showConfirmDialog(null, fields, "Khách hàng mới", JOptionPane.OK_CANCEL_OPTION) == 0) {
                Customer c = new Customer();
                c.setName(name.getText());
                c.setPhone(phone.getText());
                c.setIdentityNumber(idnum.getText());
                if (customerDAO.add(c)) loadCustomerData(model);
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int)model.getValueAt(row, 0);
                Customer c = customerDAO.getById(id);
                JTextField name = new JTextField(c.getName());
                JTextField phone = new JTextField(c.getPhone());
                JTextField idnum = new JTextField(c.getIdentityNumber());
                Object[] fields = {"Họ tên:", name, "Số điện thoại:", phone, "Số định danh:", idnum};
                if (JOptionPane.showConfirmDialog(null, fields, "Sửa khách hàng", JOptionPane.OK_CANCEL_OPTION) == 0) {
                    c.setName(name.getText());
                    c.setPhone(phone.getText());
                    c.setIdentityNumber(idnum.getText());
                    customerDAO.update(c);
                    loadCustomerData(model);
                }
            }
        });

        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Họ tên/SĐT/CCCD:")); top.add(txtSearch); top.add(btnSearch);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bot.add(btnAdd); bot.add(btnEdit);
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    private void loadCustomerData(DefaultTableModel model) {
        model.setRowCount(0);
        for (Customer c : customerDAO.getAll()) model.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getIdentityNumber()});
    }

    // --- CHECK-IN MODULE ---
    private JPanel createRoomPanel() {
        String[] cols = {"PhòngSố", "Loại", "Giá", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        loadRoomData(model);

        JButton btnCheckIn = new JButton("Đặt Phòng & Check-in");
        
        btnCheckIn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String status = (String)model.getValueAt(row, 3);
                if (!"EMPTY".equals(status)) {
                    UIUtils.showError(this, "Phòng này hiện đang có khách hoặc bảo trì!");
                    return;
                }
                
                String roomNum = (String)model.getValueAt(row, 0);
                String customerIdStr = JOptionPane.showInputDialog(this, "Nhập ID Khách hàng:");
                if (customerIdStr != null) {
                    try {
                        int cid = Integer.parseInt(customerIdStr);
                        if (customerDAO.getById(cid) == null) throw new Exception("Khách không tồn tại.");
                        
                        // Proceed to check-in
                        List<Room> all = roomDAO.getAll();
                        Room roomToBook = null;
                        for(Room r : all) if(r.getRoomNumber().equals(roomNum)) roomToBook = r;

                        Booking b = new Booking();
                        b.setCustomerId(cid);
                        b.setRoomId(roomToBook.getId());
                        b.setStaffId(1); // Default for demo
                        b.setStatus("CHECKED_IN");
                        b.setTotalAmount(roomToBook.getPrice());

                        if (bookingDAO.add(b)) {
                            roomToBook.setStatus("OCCUPIED");
                            roomDAO.update(roomToBook);
                            loadRoomData(model);
                            UIUtils.showInfo(this, "Đã Check-in thành công!");
                        }
                    } catch (Exception ex) { UIUtils.showError(this, "Lỗi: " + ex.getMessage()); }
                }
            }
        });

        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(new JLabel("DANH SÁCH TRẠNG THÁI PHÒNG"), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnCheckIn);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private void loadRoomData(DefaultTableModel model) {
        model.setRowCount(0);
        for (Room r : roomDAO.getAll()) model.addRow(new Object[]{r.getRoomNumber(), r.getType(), r.getPrice(), r.getStatus()});
    }

    // --- CHECK-OUT MODULE ---
    private JPanel createCheckOutPanel() {
        String[] cols = {"BookingID", "Phòng", "Giá", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        loadBookingData(model);

        JButton btnPay = new JButton("Thanh toán (Giả lập)");
        JButton btnCheckOut = new JButton("Xác nhận Check-out");

        btnPay.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int bid = (int)model.getValueAt(row, 0);
                double amt = (double)model.getValueAt(row, 2);
                Payment p = new Payment();
                p.setBookingId(bid);
                p.setAmount(amt);
                p.setMethod("CASH");
                p.setStatus("PAID");
                if (paymentDAO.add(p)) UIUtils.showInfo(this, "Thanh toán thành công! Sẵn sàng check-out.");
            }
        });

        btnCheckOut.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int bid = (int)model.getValueAt(row, 0);
                String result = bookingService.checkOut(bid);
                if (result.startsWith("SUCCESS")) {
                    UIUtils.showInfo(this, result);
                    // Free the room
                    Booking b = bookingDAO.getById(bid);
                    Room r = roomDAO.getById(b.getRoomId());
                    r.setStatus("EMPTY");
                    roomDAO.update(r);
                    loadBookingData(model);
                    loadRoomData((DefaultTableModel) ((JTable)((JScrollPane)((JPanel)tabbedPane.getComponentAt(1)).getComponent(1)).getViewport().getView()).getModel());
                } else {
                    UIUtils.showError(this, result);
                }
            }
        });

        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.add(new JLabel("QUẢN LÝ BOOKING & TRẢ PHÒNG"), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnPay); actions.add(btnCheckOut);
        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private void loadBookingData(DefaultTableModel model) {
        model.setRowCount(0);
        for (Booking b : bookingDAO.getAll()) {
            if ("CHECKED_IN".equals(b.getStatus())) {
                Room r = roomDAO.getById(b.getRoomId());
                model.addRow(new Object[]{b.getId(), r.getRoomNumber(), b.getTotalAmount(), b.getStatus()});
            }
        }
    }
}
