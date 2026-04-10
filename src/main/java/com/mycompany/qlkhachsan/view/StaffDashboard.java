package com.mycompany.qlkhachsan.view;

import com.mycompany.qlkhachsan.dao.*;
import com.mycompany.qlkhachsan.model.*;
import com.mycompany.qlkhachsan.service.BookingService;
import com.mycompany.qlkhachsan.util.UIUtils;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class StaffDashboard extends JFrame {

    // ─── DAOs ────────────────────────────────────────────────────────────────
    private final CustomerDAO    customerDAO    = new CustomerDAO();
    private final BookingDAO     bookingDAO     = new BookingDAO();
    private final PaymentDAO     paymentDAO     = new PaymentDAO();
    private final RoomDAO        roomDAO        = new RoomDAO();
    private final ServiceDAO     serviceDAO     = new ServiceDAO();
    private final ServiceUsageDAO suDAO         = new ServiceUsageDAO();
    private final BookingService bookingService = new BookingService(bookingDAO, paymentDAO);

    // ─── Table models (giữ reference để refresh) ─────────────────────────────
    private DefaultTableModel customerModel;
    private DefaultTableModel roomModel;
    private DefaultTableModel bookingModel;

    // ─── Tài khoản đang đăng nhập ────────────────────────────────────────────
    private final Account currentAccount;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ═════════════════════════════════════════════════════════════════════════
    public StaffDashboard(Account account) {
        this.currentAccount = account;
        setTitle("Nhân viên - " + account.getUserName() + " | Hotel Management");
        setSize(1200, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
    }

    // ─── HEADER ──────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(144, 238, 144));
        header.setPreferredSize(new Dimension(1200, 65));
        header.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JLabel lblTitle = new JLabel("NHÂN VIÊN LỄ TÂN  |  " + currentAccount.getUserName().toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(20, 80, 20));

        JButton btnLogout = UIUtils.makeButton("⏏ Đăng xuất", new Color(255, 99, 71));
        btnLogout.setPreferredSize(new Dimension(130, 38));
        btnLogout.addActionListener(e -> doLogout());

        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        return header;
    }

    private void doLogout() {
        if (UIUtils.confirm(this, "Bạn có muốn đăng xuất không?")) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                com.mycompany.qlkhachsan.dao.AccountDAO aDao = new com.mycompany.qlkhachsan.dao.AccountDAO();
                LoginForm lf = new LoginForm();
                new com.mycompany.qlkhachsan.controller.LoginController(lf, aDao);
                lf.setVisible(true);
            });
        }
    }

    // ─── TABS ─────────────────────────────────────────────────────────────────
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tabs.addTab("👤 Khách hàng",     createCustomerPanel());
        tabs.addTab("🏠 Phòng & Check-in", createRoomPanel());
        tabs.addTab("📋 Booking & Dịch vụ & Check-out", createCheckOutPanel());
        return tabs;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TAB 1: KHÁCH HÀNG
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel createCustomerPanel() {
        String[] cols = {"ID", "Họ và tên", "Số điện thoại", "Số CCCD/CMND"};
        customerModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(customerModel);
        UIUtils.configureTable(table);
        UIUtils.hideColumn(table, 0);
        loadCustomerData();

        JTextField txtSearch = new JTextField(22);
        JButton btnSearch = UIUtils.makeButton("🔍 Tìm kiếm", new Color(100, 149, 237));
        JButton btnReset  = UIUtils.makeButton("↺ Làm mới", new Color(200, 200, 200));
        JButton btnAdd    = UIUtils.makeButton("➕ Thêm khách", new Color(144, 238, 144));
        JButton btnEdit   = UIUtils.makeButton("✏ Sửa thông tin", new Color(255, 215, 0));

        btnSearch.addActionListener(e -> {
            customerModel.setRowCount(0);
            for (Customer c : customerDAO.search(txtSearch.getText()))
                customerModel.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getIdentityNumber()});
        });
        btnReset.addActionListener(e -> { txtSearch.setText(""); loadCustomerData(); });
        txtSearch.addActionListener(e -> btnSearch.doClick());

        btnAdd.addActionListener(e -> doAddCustomer(table));
        btnEdit.addActionListener(e -> doEditCustomer(table));

        // Right-click context menu
        JPopupMenu popup = new JPopupMenu();
        JMenuItem miEdit = new JMenuItem("✏ Sửa thông tin");
        JMenuItem miAdd  = new JMenuItem("➕ Thêm khách hàng");
        popup.add(miAdd); popup.addSeparator(); popup.add(miEdit);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e)  { showPopup(e); }
            public void mouseReleased(MouseEvent e) { showPopup(e); }
            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) { table.setRowSelectionInterval(row, row); popup.show(table, e.getX(), e.getY()); }
                }
            }
        });
        miAdd.addActionListener(e -> btnAdd.doClick());
        miEdit.addActionListener(e -> btnEdit.doClick());

        // Layout
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("Tìm (tên/SĐT/CCCD):")); top.add(txtSearch);
        top.add(btnSearch); top.add(btnReset);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        bot.add(btnAdd); bot.add(btnEdit);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        p.add(new JLabel("  QUẢN LÝ KHÁCH HÀNG  (chuột phải để thao tác)"), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(top, BorderLayout.WEST);
        southPanel.add(bot, BorderLayout.EAST);
        p.add(southPanel, BorderLayout.SOUTH);
        return p;
    }

    private void loadCustomerData() {
        customerModel.setRowCount(0);
        for (Customer c : customerDAO.getAll())
            customerModel.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getIdentityNumber()});
    }

    private void doAddCustomer(JTable table) {
        JTextField fName  = new JTextField(20);
        JTextField fPhone = new JTextField(20);
        JTextField fCCCD  = new JTextField(20);

        JPanel form = buildForm(
            "Họ và tên:", fName,
            "Số điện thoại (10 số):", fPhone,
            "Số CCCD/CMND (12 số):", fCCCD
        );

        if (JOptionPane.showConfirmDialog(this, form, "Thêm khách hàng mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String name  = fName.getText().trim();
        String phone = fPhone.getText().trim();
        String cccd  = fCCCD.getText().trim();

        if (name.isEmpty()) { UIUtils.showError(this, "Tên không được để trống."); return; }
        if (!UIUtils.isValidPhone(phone)) { UIUtils.showError(this, "Số điện thoại phải đúng 10 chữ số."); return; }
        if (!UIUtils.isValidCCCD(cccd))   { UIUtils.showError(this, "Số CCCD phải đúng 12 chữ số."); return; }
        if (customerDAO.isPhoneTaken(phone, 0)) { UIUtils.showError(this, "Số điện thoại đã tồn tại trong hệ thống."); return; }
        if (customerDAO.isCCCDTaken(cccd, 0))   { UIUtils.showError(this, "Số CCCD đã tồn tại trong hệ thống."); return; }

        Customer c = new Customer();
        c.setName(name); c.setPhone(phone); c.setIdentityNumber(cccd);
        if (customerDAO.add(c)) { loadCustomerData(); UIUtils.showInfo(this, "Thêm khách hàng thành công!"); }
        else UIUtils.showError(this, "Thêm thất bại. Vui lòng thử lại.");
    }

    private void doEditCustomer(JTable table) {
        if (!UIUtils.requireSelection(this, table)) return;
        int id = (int) customerModel.getValueAt(table.getSelectedRow(), 0);
        Customer c = customerDAO.getById(id);
        if (c == null) { UIUtils.showError(this, "Không tìm thấy khách hàng."); return; }

        JTextField fName  = new JTextField(c.getName(), 20);
        JTextField fPhone = new JTextField(c.getPhone(), 20);
        JTextField fCCCD  = new JTextField(c.getIdentityNumber(), 20);

        JPanel form = buildForm(
            "Họ và tên:", fName,
            "Số điện thoại (10 số):", fPhone,
            "Số CCCD/CMND (12 số):", fCCCD
        );

        if (JOptionPane.showConfirmDialog(this, form, "Sửa thông tin khách hàng",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String name  = fName.getText().trim();
        String phone = fPhone.getText().trim();
        String cccd  = fCCCD.getText().trim();

        if (name.isEmpty()) { UIUtils.showError(this, "Tên không được để trống."); return; }
        if (!UIUtils.isValidPhone(phone)) { UIUtils.showError(this, "Số điện thoại phải đúng 10 chữ số."); return; }
        if (!UIUtils.isValidCCCD(cccd))   { UIUtils.showError(this, "Số CCCD phải đúng 12 chữ số."); return; }
        if (customerDAO.isPhoneTaken(phone, id)) { UIUtils.showError(this, "Số điện thoại đã được dùng bởi khách khác."); return; }
        if (customerDAO.isCCCDTaken(cccd, id))   { UIUtils.showError(this, "Số CCCD đã được dùng bởi khách khác."); return; }

        c.setName(name); c.setPhone(phone); c.setIdentityNumber(cccd);
        if (customerDAO.update(c)) { loadCustomerData(); UIUtils.showInfo(this, "Cập nhật thành công!"); }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TAB 2: PHÒNG & CHECK-IN
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel createRoomPanel() {
        String[] cols = {"ID", "Số phòng", "Loại phòng", "Giá (VNĐ)", "Trạng thái"};
        roomModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(roomModel);
        UIUtils.configureTable(table);
        UIUtils.hideColumn(table, 0);

        // Tô màu theo trạng thái phòng - OPTIMIZED: Cache 1 lần thay vì gọi DB cho mỗi cell
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component comp = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected && row < roomModel.getRowCount()) {
                    int rid = (int) roomModel.getValueAt(row, 0);
                    String status = roomStatusCache.get(rid);
                    if (status != null) {
                        if ("EMPTY".equals(status))        comp.setBackground(new Color(220, 255, 220));
                        else if ("OCCUPIED".equals(status)) comp.setBackground(new Color(255, 230, 200));
                        else                                comp.setBackground(new Color(255, 240, 200));
                    }
                }
                return comp;
            }
        });

        loadRoomData();

        JButton btnCheckIn   = UIUtils.makeButton("✅ Check-in", new Color(100, 200, 100));
        JButton btnViewTenant = UIUtils.makeButton("👁 Xem thông tin", new Color(100, 149, 237));
        JButton btnRefresh   = UIUtils.makeButton("↺ Làm mới", new Color(200, 200, 200));

        btnCheckIn.addActionListener(e -> doCheckIn(table));

        btnViewTenant.addActionListener(e -> {
            if (!UIUtils.requireSelection(this, table)) return;
            int id = (int) roomModel.getValueAt(table.getSelectedRow(), 0);
            Room room = roomDAO.getById(id);
            if (!"OCCUPIED".equals(room.getStatus())) {
                UIUtils.showError(this, "Phòng này hiện đang trống, không có thông tin thuê.");
                return;
            }
            showTenantInfo(room);
        });

        btnRefresh.addActionListener(e -> loadRoomData());

        // Right-click context menu
        JPopupMenu popup = new JPopupMenu();
        JMenuItem miCheckIn    = new JMenuItem("✅ Check-in");
        JMenuItem miViewTenant = new JMenuItem("👁 Xem thông tin thuê");
        popup.add(miCheckIn);
        popup.add(miViewTenant);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { maybeShowPopup(e); }
            public void mouseReleased(MouseEvent e) { maybeShowPopup(e); }
            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) { table.setRowSelectionInterval(row, row); popup.show(table, e.getX(), e.getY()); }
                }
            }
        });
        miCheckIn.addActionListener(e -> btnCheckIn.doClick());
        miViewTenant.addActionListener(e -> btnViewTenant.doClick());

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        bot.add(btnRefresh); bot.add(btnViewTenant); bot.add(btnCheckIn);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        legend.add(makeColorLabel("Trống", new Color(220, 255, 220)));
        legend.add(makeColorLabel("Đang thuê", new Color(255, 230, 200)));
        legend.add(makeColorLabel("Bảo trì / Khác", new Color(255, 240, 200)));

        JPanel north = new JPanel(new BorderLayout());
        north.add(new JLabel("  QUẢN LÝ PHÒNG & CHECK-IN  (chuột phải để thao tác)", SwingConstants.LEFT), BorderLayout.WEST);
        north.add(legend, BorderLayout.EAST);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        p.add(north, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    private void doCheckIn(JTable table) {
        if (!UIUtils.requireSelection(this, table)) return;
        int id = (int) roomModel.getValueAt(table.getSelectedRow(), 0);
        Room room = roomDAO.getById(id);
        if (!"EMPTY".equals(room.getStatus())) {
            UIUtils.showError(this, "Chỉ có thể check-in phòng đang TRỐNG!");
            return;
        }

        // Chọn khách
        String[] opts = {"Khách hiện có (nhập ID)", "Tạo khách mới"};
        int choice = JOptionPane.showOptionDialog(this,
            "Phòng " + room.getRoomNumber() + " - Chọn cách nhập khách hàng:",
            "Check-in", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, opts, opts[0]);
        if (choice < 0) return;

        Customer customer = null;
        if (choice == 0) {
            String s = JOptionPane.showInputDialog(this, "Nhập ID khách hàng:");
            if (s == null || s.trim().isEmpty()) return;
            try { customer = customerDAO.getById(Integer.parseInt(s.trim())); }
            catch (NumberFormatException ex) { UIUtils.showError(this, "ID không hợp lệ."); return; }
            if (customer == null) { UIUtils.showError(this, "Không tìm thấy khách."); return; }
        } else {
            customer = doCreateCustomerInline();
            if (customer == null) return;
        }

        // Nhập ngày checkout dự kiến
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(editor);
        spinner.setValue(java.util.Date.from(java.time.LocalDate.now().plusDays(1).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel("Ngày checkout dự kiến: "));
        datePanel.add(spinner);
        
        int result = JOptionPane.showConfirmDialog(this, datePanel, 
            "Nhập thông tin check-in cho phòng " + room.getRoomNumber(), 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        java.util.Date selectedDate = (java.util.Date) spinner.getValue();
        LocalDate expectedCheckOut = java.time.LocalDate.ofInstant(selectedDate.toInstant(), java.time.ZoneId.systemDefault());
        
        // Kiểm tra ngày checkout dự kiến phải >= ngày checkin
        if (expectedCheckOut.isBefore(LocalDate.now())) {
            UIUtils.showError(this, "Ngày checkout dự kiến phải lớn hơn hoặc bằng hôm nay!");
            return;
        }

        if (!UIUtils.confirm(this, String.format(
                "<html>Xác nhận Check-in?<br><b>Phòng:</b> %s (%s) - %,d VNĐ<br><b>Khách:</b> %s<br><b>Checkout dự kiến:</b> %s</html>",
                room.getRoomNumber(), room.getType(), room.getPrice(), customer.getName(), expectedCheckOut.format(DATE_FMT)))) return;

        Booking b = new Booking();
        b.setCustomerId(customer.getId()); b.setRoomId(room.getId());
        b.setStaffId(currentAccount.getId()); b.setStatus("CHECKED_IN");
        b.setTotalAmount(room.getPrice());
        b.setBookingDate(LocalDate.now()); b.setCheckInDate(LocalDate.now());
        b.setExpectedCheckOutDate(expectedCheckOut);
        if (bookingDAO.add(b)) {
            room.setStatus("OCCUPIED"); roomDAO.update(room);
            loadRoomData(); loadBookingData();
            UIUtils.showInfo(this, "✅ Check-in thành công! Phòng " + room.getRoomNumber());
        } else UIUtils.showError(this, "Check-in thất bại.");
    }

    private Customer doCreateCustomerInline() {
        JTextField fName = new JTextField(20), fPhone = new JTextField(20), fCCCD = new JTextField(20);
        JPanel form = buildForm("Họ tên:", fName, "SĐT (10 số):", fPhone, "CCCD (12 số):", fCCCD);

        if (JOptionPane.showConfirmDialog(this, form, "Thêm khách mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return null;

        String name = fName.getText().trim(), phone = fPhone.getText().trim(), cccd = fCCCD.getText().trim();
        if (name.isEmpty())                    { UIUtils.showError(this, "Tên không được để trống."); return null; }
        if (!UIUtils.isValidPhone(phone))       { UIUtils.showError(this, "SĐT phải đúng 10 chữ số."); return null; }
        if (!UIUtils.isValidCCCD(cccd))         { UIUtils.showError(this, "CCCD phải đúng 12 chữ số."); return null; }
        if (customerDAO.isPhoneTaken(phone, 0)) { UIUtils.showError(this, "SĐT đã tồn tại."); return null; }
        if (customerDAO.isCCCDTaken(cccd, 0))   { UIUtils.showError(this, "CCCD đã tồn tại."); return null; }

        Customer c = new Customer(); c.setName(name); c.setPhone(phone); c.setIdentityNumber(cccd);
        if (!customerDAO.add(c)) { UIUtils.showError(this, "Thêm khách thất bại."); return null; }
        List<Customer> list = customerDAO.search(phone);
        return list.isEmpty() ? null : list.get(0);
    }

    private String getRawRoomStatus(int viewRow) {
        // Lấy trạng thái gốc từ column 4 (đã dịch sang VI), nên cần lấy từ ID
        if (viewRow < 0 || viewRow >= roomModel.getRowCount()) return "";
        int id = (int) roomModel.getValueAt(viewRow, 0);
        Room r = roomDAO.getById(id);
        return r != null ? r.getStatus() : "";
    }

    void loadRoomData() {
        // Clear cache first
        roomStatusCache.clear();
        
        roomModel.setRowCount(0);
        for (Room r : roomDAO.getAll()) {
            if (!r.isEnable()) continue;
            // Update cache
            roomStatusCache.put(r.getId(), r.getStatus());
            roomModel.addRow(new Object[]{
                r.getId(),
                r.getRoomNumber(),
                r.getType(),
                String.format("%,.0f", r.getPrice()),
                UIUtils.translateStatus(r.getStatus())
            });
        }
    }

    /** Cache for room status - avoids repeated DB calls */
    private java.util.Map<Integer, String> roomStatusCache = new java.util.HashMap<>();

    /** Hiển thị thông tin người đang thuê phòng */
    private void showTenantInfo(Room room) {
        Booking b = bookingDAO.getActiveByRoomId(room.getId());
        if (b == null) { UIUtils.showError(this, "Không tìm thấy thông tin booking cho phòng này."); return; }

        Customer c = customerDAO.getById(b.getCustomerId());
        List<ServiceUsage> usages = suDAO.getByBookingId(b.getId());

        StringBuilder svInfo = new StringBuilder();
        double svTotal = 0;
        for (ServiceUsage su : usages) {
            Service sv = serviceDAO.getById(su.getServiceId());
            if (sv != null) {
                svInfo.append(String.format("  • %s x%d = %,.0f VNĐ%n",
                        sv.getName(), su.getQuantity(), su.getPrice()));
                svTotal += su.getPrice();
            }
        }
        if (svInfo.length() == 0) svInfo.append("  (Chưa sử dụng dịch vụ nào)");

        String msg = String.format(
            "<html><h3>📋 Thông tin thuê phòng %s</h3>" +
            "<hr><b>Phòng:</b> %s | Loại: %s | Giá: %,.0f VNĐ<br><br>" +
            "<b>Khách hàng:</b> %s<br>" +
            "<b>SĐT:</b> %s &nbsp;&nbsp; <b>CCCD:</b> %s<br><br>" +
            "<b>Ngày đặt:</b> %s<br>" +
            "<b>Ngày check-in:</b> %s<br><br>" +
            "<b>Dịch vụ đã dùng:</b><br>%s<br>" +
            "<b>Tổng tiền phòng:</b> %,.0f VNĐ<br>" +
            "<b>Tổng tiền dịch vụ:</b> %,.0f VNĐ<br>" +
            "<hr><b>TỔNG CỘNG: %,.0f VNĐ</b></html>",
            room.getRoomNumber(),
            room.getRoomNumber(), room.getType(), room.getPrice(),
            c != null ? c.getName() : "?",
            c != null ? c.getPhone() : "?",
            c != null ? c.getIdentityNumber() : "?",
            b.getBookingDate() != null ? b.getBookingDate().format(DATE_FMT) : "N/A",
            b.getCheckInDate() != null ? b.getCheckInDate().format(DATE_FMT) : "N/A",
            svInfo.toString().replace("\n", "<br>"),
            room.getPrice(), svTotal,
            b.getTotalAmount()
        );
        JOptionPane.showMessageDialog(this, msg, "Thông tin thuê phòng " + room.getRoomNumber(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TAB 3: BOOKING & DỊCH VỤ & CHECK-OUT
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel createCheckOutPanel() {
        String[] cols = {"BookingID", "Phòng", "Khách hàng", "Ngày đặt", "Ngày check-in", "Checkout dự kiến",
                         "Tổng tiền (VNĐ)", "TT Thanh toán", "TT Booking"};
        bookingModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(bookingModel);
        UIUtils.configureTable(table);

        // Tô màu theo TT thanh toán
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean foc, int row, int col) {
                Component comp = super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                if (!sel && row < bookingModel.getRowCount()) {
                    String ttTT = (String) bookingModel.getValueAt(row, 6);
                    if ("Đã thanh toán".equals(ttTT)) comp.setBackground(new Color(220, 255, 220));
                    else                               comp.setBackground(new Color(255, 230, 200));
                }
                return comp;
            }
        });

        loadBookingData();

        JButton btnAddService = UIUtils.makeButton("➕ Thêm dịch vụ", new Color(100, 149, 237));
        JButton btnInvoice    = UIUtils.makeButton("🧾 Xem hóa đơn",  new Color(255, 215, 0));
        JButton btnPay        = UIUtils.makeButton("💳 Thanh toán",    new Color(144, 238, 144));
        JButton btnCheckOut   = UIUtils.makeButton("🚪 Check-out",     new Color(255, 140, 0));
        JButton btnRefresh    = UIUtils.makeButton("↺ Làm mới",        new Color(200, 200, 200));

        btnAddService.addActionListener(e -> {
            if (!UIUtils.requireSelection(this, table)) return;
            int bid = (int) bookingModel.getValueAt(table.getSelectedRow(), 0);
            doAddService(bid);
        });

        btnInvoice.addActionListener(e -> {
            if (!UIUtils.requireSelection(this, table)) return;
            int bid = (int) bookingModel.getValueAt(table.getSelectedRow(), 0);
            showInvoice(bid);
        });

        btnPay.addActionListener(e -> {
            if (!UIUtils.requireSelection(this, table)) return;
            int bid = (int) bookingModel.getValueAt(table.getSelectedRow(), 0);
            doPayment(bid);
        });

        btnCheckOut.addActionListener(e -> {
            if (!UIUtils.requireSelection(this, table)) return;
            int bid = (int) bookingModel.getValueAt(table.getSelectedRow(), 0);
            doCheckOut(bid);
        });

        btnRefresh.addActionListener(e -> loadBookingData());

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        bot.add(btnRefresh); bot.add(btnAddService); bot.add(btnInvoice); bot.add(btnPay); bot.add(btnCheckOut);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        p.add(new JLabel("  QUẢN LÝ BOOKING & TRẢ PHÒNG"), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    void loadBookingData() {
        // Cache tất cả rooms, customers để tránh gọi DB nhiều lần
        java.util.Map<Integer, Room> roomCache = new java.util.HashMap<>();
        for (Room r : roomDAO.getAll()) roomCache.put(r.getId(), r);
        
        java.util.Map<Integer, Customer> customerCache = new java.util.HashMap<>();
        for (Customer c : customerDAO.getAll()) customerCache.put(c.getId(), c);
        
        java.util.Map<Integer, Boolean> paidCache = new java.util.HashMap<>();
        for (Payment p : paymentDAO.getAll()) {
            if (!paidCache.containsKey(p.getBookingId())) {
                paidCache.put(p.getBookingId(), "PAID".equals(p.getStatus()));
            }
        }
        
        bookingModel.setRowCount(0);
        for (Booking b : bookingDAO.getAll()) {
            if ("CHECKED_OUT".equals(b.getStatus()) || "CANCELLED".equals(b.getStatus())) continue;
            Room r = roomCache.get(b.getRoomId());
            Customer c = customerCache.get(b.getCustomerId());
            boolean paid = paidCache.getOrDefault(b.getId(), false);
            bookingModel.addRow(new Object[]{
                b.getId(),
                r != null ? r.getRoomNumber() : "?",
                c != null ? c.getName() : "?",
                b.getBookingDate() != null ? b.getBookingDate().format(DATE_FMT) : "N/A",
                b.getCheckInDate() != null ? b.getCheckInDate().format(DATE_FMT) : "N/A",
                b.getExpectedCheckOutDate() != null ? b.getExpectedCheckOutDate().format(DATE_FMT) : "Chưa nhập",
                String.format("%,.0f", b.getTotalAmount()),
                paid ? "Đã thanh toán" : "Chưa thanh toán",
                UIUtils.translateStatus(b.getStatus())
            });
        }
    }

    private boolean isPaymentPaid(int bookingId) {
        for (Payment p : paymentDAO.getAll())
            if (p.getBookingId() == bookingId && "PAID".equals(p.getStatus())) return true;
        return false;
    }

    /** Thêm dịch vụ vào booking đang chọn */
    private void doAddService(int bookingId) {
        List<Service> services = serviceDAO.getAll();
        if (services.isEmpty()) { UIUtils.showError(this, "Chưa có dịch vụ nào trong hệ thống."); return; }

        Booking b = bookingDAO.getById(bookingId);
        if (b == null || !"CHECKED_IN".equals(b.getStatus())) {
            UIUtils.showError(this, "Chỉ có thể thêm dịch vụ cho booking đang CHECKED_IN."); return;
        }

        JComboBox<String> cbService = new JComboBox<>();
        for (Service s : services)
            cbService.addItem(s.getId() + " - " + s.getName() + " (" + String.format("%,.0f", s.getPrice()) + " VNĐ)");

        JSpinner spQty = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Dịch vụ:")); form.add(cbService);
        form.add(new JLabel("Số lượng:")); form.add(spQty);

        if (JOptionPane.showConfirmDialog(this, form, "Thêm dịch vụ - Booking #" + bookingId,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        Service selected = services.get(cbService.getSelectedIndex());
        int qty = (int) spQty.getValue();
        double total = selected.getPrice() * qty;

        ServiceUsage su = new ServiceUsage();
        su.setBookingId(bookingId);
        su.setServiceId(selected.getId());
        su.setQuantity(qty);
        su.setPrice(total);

        if (suDAO.add(su)) {
            // Cập nhật tổng tiền booking
            b.setTotalAmount(b.getTotalAmount() + total);
            bookingDAO.update(b);
            loadBookingData();
            UIUtils.showInfo(this, String.format("Đã thêm: %s x%d = %,.0f VNĐ", selected.getName(), qty, total));
        } else {
            UIUtils.showError(this, "Thêm dịch vụ thất bại.");
        }
    }

    /** Hiển thị hóa đơn chi tiết */
    private void showInvoice(int bookingId) {
        Booking b = bookingDAO.getById(bookingId);
        if (b == null) { UIUtils.showError(this, "Không tìm thấy booking."); return; }

        Room r     = roomDAO.getById(b.getRoomId());
        Customer c = customerDAO.getById(b.getCustomerId());
        List<ServiceUsage> usages = suDAO.getByBookingId(bookingId);
        boolean paid = isPaymentPaid(bookingId);

        StringBuilder rows = new StringBuilder();
        double svTotal = 0;
        for (ServiceUsage su : usages) {
            Service sv = serviceDAO.getById(su.getServiceId());
            if (sv != null) {
                rows.append(String.format(
                    "<tr><td>%s</td><td align='center'>%d</td><td align='right'>%,.0f</td></tr>",
                    sv.getName(), su.getQuantity(), su.getPrice()));
                svTotal += su.getPrice();
            }
        }
        if (rows.length() == 0)
            rows.append("<tr><td colspan='3' align='center'><i>Không có dịch vụ</i></td></tr>");

        String html = String.format(
            "<html><body style='font-family:Segoe UI;font-size:13px'>" +
            "<h2 align='center'>🏨 HÓA ĐƠN THANH TOÁN</h2>" +
            "<hr><b>Booking ID:</b> #%d &nbsp;&nbsp; <b>Trạng thái:</b> %s<br>" +
            "<b>Phòng:</b> %s (%s) &nbsp;&nbsp; <b>Giá phòng:</b> %,.0f VNĐ<br>" +
            "<b>Khách:</b> %s &nbsp;&nbsp; <b>SĐT:</b> %s<br>" +
            "<b>Ngày đặt:</b> %s &nbsp;&nbsp; <b>Check-in:</b> %s<br><hr>" +
            "<b>Chi tiết dịch vụ:</b><br>" +
            "<table border='1' cellpadding='4' width='100%%'>" +
            "<tr><th>Dịch vụ</th><th>SL</th><th>Thành tiền</th></tr>" +
            "%s</table><br>" +
            "<b>Tiền phòng:</b> %,.0f VNĐ<br>" +
            "<b>Tiền dịch vụ:</b> %,.0f VNĐ<br>" +
            "<hr><h3>TỔNG CỘNG: %,.0f VNĐ</h3>" +
            "<b>Trạng thái thanh toán:</b> %s" +
            "</body></html>",
            b.getId(),
            UIUtils.translateStatus(b.getStatus()),
            r != null ? r.getRoomNumber() : "?",
            r != null ? r.getType() : "?",
            r != null ? r.getPrice() : 0,
            c != null ? c.getName() : "?",
            c != null ? c.getPhone() : "?",
            b.getBookingDate() != null ? b.getBookingDate().format(DATE_FMT) : "N/A",
            b.getCheckInDate()  != null ? b.getCheckInDate().format(DATE_FMT) : "N/A",
            rows.toString(),
            r != null ? r.getPrice() : 0,
            svTotal,
            b.getTotalAmount(),
            paid ? "<font color='green'>✅ ĐÃ THANH TOÁN</font>" : "<font color='red'>❌ CHƯA THANH TOÁN</font>"
        );

        JLabel label = new JLabel(html);
        JScrollPane sp = new JScrollPane(label);
        sp.setPreferredSize(new Dimension(520, 460));
        JOptionPane.showMessageDialog(this, sp, "Hóa đơn - Booking #" + bookingId, JOptionPane.PLAIN_MESSAGE);
    }

    /** Thanh toán (chỉ 1 lần) */
    private void doPayment(int bookingId) {
        if (isPaymentPaid(bookingId)) {
            UIUtils.showInfo(this, "Booking #" + bookingId + " đã được thanh toán rồi.");
            return;
        }

        Booking b = bookingDAO.getById(bookingId);
        if (b == null) { UIUtils.showError(this, "Không tìm thấy booking."); return; }

        String[] methods = {"Tiền mặt", "Thẻ ngân hàng", "Chuyển khoản"};
        String[] rawMethods = {"CASH", "CARD", "TRANSFER"};
        JComboBox<String> cbMethod = new JComboBox<>(methods);

        String amtStr = String.format("%,.0f VNĐ", b.getTotalAmount());
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Tổng tiền:")); form.add(new JLabel(amtStr));
        form.add(new JLabel("Hình thức:")); form.add(cbMethod);

        if (JOptionPane.showConfirmDialog(this, form,
                "Xác nhận thanh toán - Booking #" + bookingId,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        if (!UIUtils.confirm(this, "Xác nhận thanh toán " + amtStr + " cho Booking #" + bookingId + "?")) return;

        Payment p = new Payment();
        p.setBookingId(bookingId);
        p.setAmount(b.getTotalAmount());
        p.setMethod(rawMethods[cbMethod.getSelectedIndex()]);
        p.setStatus("PAID");

        if (paymentDAO.add(p)) {
            loadBookingData();
            UIUtils.showInfo(this, "✅ Thanh toán thành công!\n" +
                "Booking #" + bookingId + " - " + amtStr);
        } else {
            UIUtils.showError(this, "Thanh toán thất bại. Vui lòng thử lại.");
        }
    }

    /** Check-out: phải đã thanh toán */
    private void doCheckOut(int bookingId) {
        if (!isPaymentPaid(bookingId)) {
            UIUtils.showError(this, "Vui lòng THANH TOÁN trước khi thực hiện check-out!");
            return;
        }

        Booking b = bookingDAO.getById(bookingId);
        if (b == null) { UIUtils.showError(this, "Không tìm thấy booking."); return; }

        if (!UIUtils.confirm(this, "Xác nhận CHECK-OUT cho Booking #" + bookingId + "?\nPhòng sẽ được chuyển sang trạng thái Trống."))
            return;

        b.setStatus("CHECKED_OUT");
        b.setCheckOutDate(LocalDate.now());
        bookingDAO.update(b);

        Room room = roomDAO.getById(b.getRoomId());
        if (room != null) { room.setStatus("EMPTY"); roomDAO.update(room); }

        loadRoomData();
        loadBookingData();
        UIUtils.showInfo(this, "✅ Check-out thành công!\nBooking #" + bookingId + " đã hoàn tất.");
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    /** Tạo form nhập liệu dạng label-field (3 cặp) */
    private JPanel buildForm(String l1, JComponent f1, String l2, JComponent f2, String l3, JComponent f3) {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel(l1)); form.add(f1);
        form.add(new JLabel(l2)); form.add(f2);
        form.add(new JLabel(l3)); form.add(f3);
        return form;
    }

    private JLabel makeColorLabel(String text, Color bg) {
        JLabel lbl = new JLabel("  " + text + "  ");
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return lbl;
    }
}