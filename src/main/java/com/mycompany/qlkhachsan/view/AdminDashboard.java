package com.mycompany.qlkhachsan.view;

import com.mycompany.qlkhachsan.dao.*;
import com.mycompany.qlkhachsan.model.*;
import com.mycompany.qlkhachsan.util.UIUtils;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class AdminDashboard extends JFrame {

    // ─── DAOs ────────────────────────────────────────────────────────────────
    private final AccountDAO   accountDAO  = new AccountDAO();
    private final RoomDAO      roomDAO     = new RoomDAO();
    private final ServiceDAO   serviceDAO  = new ServiceDAO();
    private final BookingDAO   bookingDAO  = new BookingDAO();
    private final PaymentDAO   paymentDAO  = new PaymentDAO();
    private final CustomerDAO  customerDAO = new CustomerDAO();
    private final ServiceUsageDAO suDAO    = new ServiceUsageDAO();

    // ─── Table models ─────────────────────────────────────────────────────────
    private DefaultTableModel accountModel;
    private DefaultTableModel roomModel;
    private DefaultTableModel serviceModel;
    private DefaultTableModel bookingModel;

    private final Account currentAccount;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ═════════════════════════════════════════════════════════════════════════
    public AdminDashboard(Account account) {
        this.currentAccount = account;
        setTitle("Quản trị viên - " + account.getUserName() + " | Hotel Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
    }

    // ─── HEADER ──────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(100, 149, 237));
        header.setPreferredSize(new Dimension(1200, 65));
        header.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JLabel lbl = new JLabel("QUẢN TRỊ VIÊN  |  " + currentAccount.getUserName().toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(Color.WHITE);

        JButton btnLogout = UIUtils.makeButton("⏏ Đăng xuất", new Color(220, 60, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setPreferredSize(new Dimension(130, 38));
        btnLogout.addActionListener(e -> doLogout());

        header.add(lbl, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        return header;
    }

    private void doLogout() {
        if (UIUtils.confirm(this, "Bạn có muốn đăng xuất không?")) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                AccountDAO aDao = new AccountDAO();
                LoginForm lf = new LoginForm();
                new com.mycompany.qlkhachsan.controller.LoginController(lf, aDao);
                lf.setVisible(true);
            });
        }
    }

    // ─── TABS ─────────────────────────────────────────────────────────────────
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.addTab("👤 Tài khoản",        createAccountPanel());
        tabs.addTab("🏠 Quản lý Phòng",     createRoomPanel());
        tabs.addTab("🛎 Dịch vụ",           createServicePanel());
        tabs.addTab("📋 Danh sách Booking", createBookingPanel());
        tabs.addTab("📊 Thống kê",          createStatPanel());
        return tabs;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TAB 1: TÀI KHOẢN
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel createAccountPanel() {
        String[] cols = {"ID", "Tên đăng nhập", "Quyền", "Trạng thái"};
        accountModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(accountModel);
        UIUtils.configureTable(table);
        UIUtils.hideColumn(table, 0);
        loadAccountData();

        JButton btnAdd    = UIUtils.makeButton("➕ Thêm tài khoản", new Color(144, 238, 144));
        JButton btnEdit   = UIUtils.makeButton("✏ Sửa",            new Color(255, 215, 0));
        JButton btnDisable = UIUtils.makeButton("🚫 Vô hiệu hóa",  new Color(255, 160, 122));
        JButton btnEnable  = UIUtils.makeButton("🔓 Mở khóa",      new Color(100, 200, 100));

        btnAdd.addActionListener(e -> doAddAccount());
        btnEdit.addActionListener(e -> doEditAccount(table));
        btnDisable.addActionListener(e -> doDisableAccount(table));
        btnEnable.addActionListener(e -> doEnableAccount(table));

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        bot.add(btnAdd); bot.add(btnEdit); bot.add(btnDisable); bot.add(btnEnable);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        p.add(new JLabel("  QUẢN LÝ TÀI KHOẢN"), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    private void loadAccountData() {
        accountModel.setRowCount(0);
        for (Account a : accountDAO.getAll())
            accountModel.addRow(new Object[]{
                a.getId(), a.getUserName(),
                UIUtils.translateStatus(a.getRole()),
                a.isEnable() ? "✅ Hoạt động" : "🔒 Khóa"
            });
    }

    private void doAddAccount() {
        JTextField fUser = new JTextField(20);
        JPasswordField fPass = new JPasswordField(20);
        JPasswordField fPass2 = new JPasswordField(20);
        String[] roles = {"STAFF", "ADMIN"};
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"Nhân viên (STAFF)", "Quản trị viên (ADMIN)"});

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Tên đăng nhập:")); form.add(fUser);
        form.add(new JLabel("Mật khẩu (≥6 ký tự):")); form.add(fPass);
        form.add(new JLabel("Xác nhận mật khẩu:")); form.add(fPass2);
        form.add(new JLabel("Quyền:")); form.add(cbRole);

        if (JOptionPane.showConfirmDialog(this, form, "Thêm tài khoản mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String username = fUser.getText().trim();
        String pw1 = new String(fPass.getPassword());
        String pw2 = new String(fPass2.getPassword());

        if (username.isEmpty())          { UIUtils.showError(this, "Tên đăng nhập không được để trống."); return; }
        if (!UIUtils.isValidPassword(pw1)){ UIUtils.showError(this, "Mật khẩu phải có ít nhất 6 ký tự."); return; }
        if (!pw1.equals(pw2))             { UIUtils.showError(this, "Mật khẩu xác nhận không khớp."); return; }
        if (accountDAO.isUsernameTaken(username, 0)) { UIUtils.showError(this, "Tên đăng nhập đã tồn tại."); return; }

        String role = roles[cbRole.getSelectedIndex()];
        Account a = new Account(0, username, pw1, role, false, true);
        if (accountDAO.add(a)) { loadAccountData(); UIUtils.showInfo(this, "Tạo tài khoản thành công!"); }
        else UIUtils.showError(this, "Tạo tài khoản thất bại.");
    }

    private void doEditAccount(JTable table) {
        if (!UIUtils.requireSelection(this, table)) return;
        int id = (int) accountModel.getValueAt(table.getSelectedRow(), 0);
        Account a = accountDAO.getById(id);

        JTextField fUser = new JTextField(a.getUserName(), 20);
        JPasswordField fPass = new JPasswordField(20);
        fPass.setToolTipText("Để trống nếu không thay đổi mật khẩu");
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"Nhân viên (STAFF)", "Quản trị viên (ADMIN)"});
        cbRole.setSelectedIndex("ADMIN".equals(a.getRole()) ? 1 : 0);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Tên đăng nhập:")); form.add(fUser);
        form.add(new JLabel("Mật khẩu mới (trống = giữ nguyên):")); form.add(fPass);
        form.add(new JLabel("Quyền:")); form.add(cbRole);

        if (JOptionPane.showConfirmDialog(this, form, "Sửa tài khoản #" + id,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String newUser = fUser.getText().trim();
        String newPw   = new String(fPass.getPassword());

        if (newUser.isEmpty()) { UIUtils.showError(this, "Tên đăng nhập không được để trống."); return; }
        if (accountDAO.isUsernameTaken(newUser, id)) { UIUtils.showError(this, "Tên đăng nhập đã tồn tại."); return; }
        if (!newPw.isEmpty() && !UIUtils.isValidPassword(newPw)) {
            UIUtils.showError(this, "Mật khẩu phải có ít nhất 6 ký tự."); return;
        }

        a.setUserName(newUser);
        a.setRole(cbRole.getSelectedIndex() == 1 ? "ADMIN" : "STAFF");
        if (!newPw.isEmpty()) a.setPassword(newPw); // update() sẽ tự hash

        if (accountDAO.update(a)) { loadAccountData(); UIUtils.showInfo(this, "Cập nhật thành công!"); }
    }

    private void doDisableAccount(JTable table) {
        if (!UIUtils.requireSelection(this, table)) return;
        int id = (int) accountModel.getValueAt(table.getSelectedRow(), 0);
        if (id == currentAccount.getId()) {
            UIUtils.showError(this, "Không thể vô hiệu hóa tài khoản đang đăng nhập!"); return;
        }
        if (!UIUtils.confirm(this, "Vô hiệu hóa tài khoản này?")) return;
        accountDAO.delete(id);
        loadAccountData();
    }

    private void doEnableAccount(JTable table) {
        if (!UIUtils.requireSelection(this, table)) return;
        int id = (int) accountModel.getValueAt(table.getSelectedRow(), 0);
        Account a = accountDAO.getById(id);
        if (a == null) {
            UIUtils.showError(this, "Không tìm thấy tài khoản!"); return;
        }
        if (a.isEnable()) {
            UIUtils.showInfo(this, "Tài khoản này đã được kích hoạt!"); return;
        }
        if (!UIUtils.confirm(this, "Mở khóa tài khoản này?")) return;
        a.setEnable(true);
        if (accountDAO.update(a)) {
            loadAccountData();
            UIUtils.showInfo(this, "Mở khóa tài khoản thành công!");
        } else {
            UIUtils.showError(this, "Mở khóa tài khoản thất bại.");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TAB 2: QUẢN LÝ PHÒNG
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel createRoomPanel() {
        String[] cols = {"ID", "Số phòng", "Loại phòng", "Giá (VNĐ)", "Trạng thái"};
        roomModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(roomModel);
        UIUtils.configureTable(table);
        UIUtils.hideColumn(table, 0);

        // Tô màu theo trạng thái - đã tối ưu: load 1 lần thay vì gọi DB cho mỗi cell
        java.util.Map<Integer, String> roomStatusCache = new java.util.HashMap<>();
        for (Room r : roomDAO.getAll()) {
            if (r.isEnable()) {
                roomStatusCache.put(r.getId(), r.getStatus());
            }
        }
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel && row < roomModel.getRowCount()) {
                    int rid = (int) roomModel.getValueAt(row, 0);
                    String status = roomStatusCache.get(rid);
                    if (status != null) {
                        switch (status) {
                            case "EMPTY":        c.setBackground(new Color(220, 255, 220)); break;
                            case "OCCUPIED":     c.setBackground(new Color(255, 220, 180)); break;
                            case "MAINTENANCE":  c.setBackground(new Color(255, 240, 180)); break;
                            case "NEED_UPGRADE": c.setBackground(new Color(230, 210, 255)); break;
                            default:             c.setBackground(Color.WHITE);
                        }
                    }
                }
                return c;
            }
        });

        loadRoomData();

        // ── Buttons ──────────────────────────────────────────────────────────
        JButton btnAdd     = UIUtils.makeButton("➕ Thêm phòng",  new Color(144, 238, 144));
        JButton btnEdit    = UIUtils.makeButton("✏ Sửa phòng",   new Color(255, 215, 0));
        JButton btnStatus  = UIUtils.makeButton("🔄 Đổi trạng thái", new Color(135, 206, 235));
        JButton btnCheckIn = UIUtils.makeButton("✅ Check-in",    new Color(100, 200, 100));
        JButton btnRemove  = UIUtils.makeButton("🗑 Gỡ bỏ",       new Color(255, 160, 122));
        JButton btnRefresh = UIUtils.makeButton("↺ Làm mới",     new Color(200, 200, 200));

        btnAdd.addActionListener(e -> doAddRoom());
        btnEdit.addActionListener(e -> doEditRoom(table));
        btnStatus.addActionListener(e -> doChangeRoomStatus(table));
        btnCheckIn.addActionListener(e -> doAdminCheckIn(table));
        btnRemove.addActionListener(e -> doRemoveRoom(table));
        btnRefresh.addActionListener(e -> loadRoomData());

        // ── Right-click context menu ─────────────────────────────────────────
        JPopupMenu popup = new JPopupMenu();
        JMenuItem miEdit    = new JMenuItem("✏ Sửa thông tin phòng");
        JMenuItem miStatus  = new JMenuItem("🔄 Đổi trạng thái");
        JMenuItem miCheckIn = new JMenuItem("✅ Check-in");
        JMenuItem miRemove  = new JMenuItem("🗑 Gỡ bỏ phòng");
        popup.add(miEdit); popup.add(miStatus); popup.addSeparator();
        popup.add(miCheckIn); popup.addSeparator(); popup.add(miRemove);

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
        miEdit.addActionListener(e -> btnEdit.doClick());
        miStatus.addActionListener(e -> btnStatus.doClick());
        miCheckIn.addActionListener(e -> btnCheckIn.doClick());
        miRemove.addActionListener(e -> btnRemove.doClick());

        // ── Legend ────────────────────────────────────────────────────────────
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        legend.add(colorLabel("Trống",        new Color(220, 255, 220)));
        legend.add(colorLabel("Đang thuê",    new Color(255, 220, 180)));
        legend.add(colorLabel("Bảo trì",      new Color(255, 240, 180)));
        legend.add(colorLabel("Cần nâng cấp", new Color(230, 210, 255)));

        JPanel north = new JPanel(new BorderLayout());
        north.add(new JLabel("  QUẢN LÝ PHÒNG  (chuột phải để thao tác)"), BorderLayout.WEST);
        north.add(legend, BorderLayout.EAST);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        bot.add(btnRefresh); bot.add(btnRemove); bot.add(btnStatus);
        bot.add(btnEdit); bot.add(btnCheckIn); bot.add(btnAdd);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        p.add(north, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    void loadRoomData() {
        roomModel.setRowCount(0);
        for (Room r : roomDAO.getAll()) {
            if (!r.isEnable()) continue;
            roomModel.addRow(new Object[]{
                r.getId(), r.getRoomNumber(), r.getType(),
                String.format("%,.0f", r.getPrice()),
                UIUtils.translateStatus(r.getStatus())
            });
        }
    }

    private void doAddRoom() {
        JTextField fNum   = new JTextField(15);
        JTextField fType  = new JTextField(15);
        JTextField fPrice = new JTextField(15);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Số phòng:")); form.add(fNum);
        form.add(new JLabel("Loại phòng:")); form.add(fType);
        form.add(new JLabel("Giá (VNĐ/đêm):")); form.add(fPrice);

        if (JOptionPane.showConfirmDialog(this, form, "Thêm phòng mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String num = fNum.getText().trim(), type = fType.getText().trim();
        if (num.isEmpty() || type.isEmpty()) { UIUtils.showError(this, "Số phòng và loại không được để trống."); return; }
        
        // Kiểm tra trùng số phòng
        if (roomDAO.isRoomNumberTaken(num, 0)) {
            UIUtils.showError(this, "Số phòng \"" + num + "\" đã tồn tại!");
            return;
        }
        
        try {
            double price = Double.parseDouble(fPrice.getText().trim());
            Room r = new Room();
            r.setRoomNumber(num); r.setType(type); r.setPrice(price);
            r.setStatus("EMPTY"); r.setEnable(true);
            if (roomDAO.add(r)) { loadRoomData(); UIUtils.showInfo(this, "Thêm phòng thành công!"); }
        } catch (NumberFormatException ex) { UIUtils.showError(this, "Giá không hợp lệ."); }
    }

    private void doEditRoom(JTable table) {
        if (!UIUtils.requireSelection(this, table)) return;
        int id = (int) roomModel.getValueAt(table.getSelectedRow(), 0);
        Room r = roomDAO.getById(id);
        if (!"EMPTY".equals(r.getStatus())) {
            UIUtils.showError(this, "Chỉ được sửa phòng đang TRỐNG. Phòng đang: "
                    + UIUtils.translateStatus(r.getStatus())); return;
        }

        JTextField fNum   = new JTextField(r.getRoomNumber(), 15);
        JTextField fType  = new JTextField(r.getType(), 15);
        JTextField fPrice = new JTextField(String.valueOf(r.getPrice()), 15);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Số phòng:")); form.add(fNum);
        form.add(new JLabel("Loại phòng:")); form.add(fType);
        form.add(new JLabel("Giá (VNĐ/đêm):")); form.add(fPrice);

        if (JOptionPane.showConfirmDialog(this, form, "Sửa thông tin phòng " + r.getRoomNumber(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String num = fNum.getText().trim(), type = fType.getText().trim();
        if (num.isEmpty() || type.isEmpty()) { UIUtils.showError(this, "Không được để trống."); return; }
        try {
            r.setRoomNumber(num); r.setType(type);
            r.setPrice(Double.parseDouble(fPrice.getText().trim()));
            roomDAO.update(r); loadRoomData();
            UIUtils.showInfo(this, "Cập nhật phòng thành công!");
        } catch (NumberFormatException ex) { UIUtils.showError(this, "Giá không hợp lệ."); }
    }

    private void doChangeRoomStatus(JTable table) {
        if (!UIUtils.requireSelection(this, table)) return;
        int id = (int) roomModel.getValueAt(table.getSelectedRow(), 0);
        Room r = roomDAO.getById(id);

        if ("OCCUPIED".equals(r.getStatus())) {
            UIUtils.showError(this, "Không thể đổi trạng thái phòng đang có khách thuê!"); return;
        }

        // Các trạng thái có thể chuyển sang (loại trừ OCCUPIED - chỉ qua check-in)
        String[] statusKeys = {"EMPTY", "MAINTENANCE", "NEED_UPGRADE"};
        String[] statusVI   = {"Trống", "Bảo trì", "Cần nâng cấp"};

        JComboBox<String> cbStatus = new JComboBox<>(statusVI);
        // Chọn sẵn trạng thái hiện tại
        for (int i = 0; i < statusKeys.length; i++)
            if (statusKeys[i].equals(r.getStatus())) cbStatus.setSelectedIndex(i);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Phòng:")); form.add(new JLabel(r.getRoomNumber()));
        form.add(new JLabel("Trạng thái hiện tại:")); form.add(new JLabel(UIUtils.translateStatus(r.getStatus())));
        form.add(new JLabel("Chuyển sang:")); form.add(cbStatus);

        if (JOptionPane.showConfirmDialog(this, form, "Đổi trạng thái phòng",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String newStatus = statusKeys[cbStatus.getSelectedIndex()];
        if (!UIUtils.confirm(this, "Xác nhận chuyển phòng " + r.getRoomNumber()
                + " sang: " + statusVI[cbStatus.getSelectedIndex()] + "?")) return;

        r.setStatus(newStatus);
        roomDAO.update(r);
        loadRoomData();
        UIUtils.showInfo(this, "Đã cập nhật trạng thái phòng!");
    }

    /** Admin check-in (giống Staff nhưng gọi trực tiếp) */
    private void doAdminCheckIn(JTable table) {
        if (!UIUtils.requireSelection(this, table)) return;
        int id = (int) roomModel.getValueAt(table.getSelectedRow(), 0);
        Room room = roomDAO.getById(id);
        if (!"EMPTY".equals(room.getStatus())) {
            UIUtils.showError(this, "Chỉ có thể check-in phòng đang TRỐNG!"); return;
        }

        // Chọn khách
        String[] opts = {"Khách hiện có (nhập ID)", "Tạo khách mới"};
        int choice = JOptionPane.showOptionDialog(this,
            "Phòng " + room.getRoomNumber() + " - Chọn cách nhập khách hàng:",
            "Check-in (Admin)", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
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

        if (!UIUtils.confirm(this, String.format(
                "<html>Xác nhận Check-in?<br><b>Phòng:</b> %s (%s) - %,.0f VNĐ<br><b>Khách:</b> %s</html>",
                room.getRoomNumber(), room.getType(), room.getPrice(), customer.getName()))) return;

        Booking b = new Booking();
        b.setCustomerId(customer.getId()); b.setRoomId(room.getId());
        b.setStaffId(currentAccount.getId()); b.setStatus("CHECKED_IN");
        b.setTotalAmount(room.getPrice());
        b.setBookingDate(LocalDate.now()); b.setCheckInDate(LocalDate.now());

        if (bookingDAO.add(b)) {
            room.setStatus("OCCUPIED"); roomDAO.update(room);
            loadRoomData(); loadBookingData();
            UIUtils.showInfo(this, "✅ Check-in thành công! Phòng " + room.getRoomNumber());
        } else UIUtils.showError(this, "Check-in thất bại.");
    }

    private Customer doCreateCustomerInline() {
        JTextField fName = new JTextField(20), fPhone = new JTextField(20), fCCCD = new JTextField(20);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Họ tên:")); form.add(fName);
        form.add(new JLabel("SĐT (10 số):")); form.add(fPhone);
        form.add(new JLabel("CCCD (12 số):")); form.add(fCCCD);

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

    private void doRemoveRoom(JTable table) {
        if (!UIUtils.requireSelection(this, table)) return;
        int id = (int) roomModel.getValueAt(table.getSelectedRow(), 0);
        Room r = roomDAO.getById(id);
        if ("OCCUPIED".equals(r.getStatus())) {
            UIUtils.showError(this, "Không thể gỡ phòng đang có khách thuê!"); return;
        }
        if (!UIUtils.confirm(this, "Gỡ bỏ phòng " + r.getRoomNumber() + "?\n(Phòng sẽ bị vô hiệu hóa, không bị xóa hoàn toàn)")) return;
        roomDAO.delete(id);
        loadRoomData();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TAB 3: DỊCH VỤ
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel createServicePanel() {
        String[] cols = {"ID", "Tên dịch vụ", "Đơn giá (VNĐ)"};
        serviceModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(serviceModel);
        UIUtils.configureTable(table);
        UIUtils.hideColumn(table, 0);
        loadServiceData();

        JButton btnAdd  = UIUtils.makeButton("➕ Thêm dịch vụ", new Color(144, 238, 144));
        JButton btnEdit = UIUtils.makeButton("✏ Sửa",           new Color(255, 215, 0));
        JButton btnDel  = UIUtils.makeButton("🗑 Xóa",           new Color(255, 160, 122));

        btnAdd.addActionListener(e -> doAddService());
        btnEdit.addActionListener(e -> doEditService(table));
        btnDel.addActionListener(e -> doDeleteService(table));

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        bot.add(btnAdd); bot.add(btnEdit); bot.add(btnDel);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        p.add(new JLabel("  QUẢN LÝ DỊCH VỤ"), BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    private void loadServiceData() {
        serviceModel.setRowCount(0);
        for (Service s : serviceDAO.getAll())
            serviceModel.addRow(new Object[]{s.getId(), s.getName(), String.format("%,.0f", s.getPrice())});
    }

    private void doAddService() {
        JTextField fName  = new JTextField(20);
        JTextField fPrice = new JTextField(20);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Tên dịch vụ:")); form.add(fName);
        form.add(new JLabel("Đơn giá (VNĐ):")); form.add(fPrice);

        if (JOptionPane.showConfirmDialog(this, form, "Thêm dịch vụ mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String name = fName.getText().trim();
        if (name.isEmpty()) { UIUtils.showError(this, "Tên dịch vụ không được để trống."); return; }

        // Kiểm tra trùng tên
        if (serviceDAO.isServiceNameTaken(name, 0)) {
            UIUtils.showError(this, "Tên dịch vụ \"" + name + "\" đã tồn tại!");
            return;
        }

        try {
            double price = Double.parseDouble(fPrice.getText().trim());
            // Yêu cầu giá tối thiểu 1.000 VNĐ
            if (price < 1000) {
                UIUtils.showError(this, "Đơn giá phải lớn hơn hoặc bằng 1.000 VNĐ!");
                return;
            }
            Service s = new Service();
            s.setName(name); s.setPrice(price);
            if (serviceDAO.add(s)) { loadServiceData(); UIUtils.showInfo(this, "Thêm dịch vụ thành công!"); }
        } catch (NumberFormatException ex) { UIUtils.showError(this, "Đơn giá không hợp lệ."); }
    }

    private void doEditService(JTable table) {
        if (!UIUtils.requireSelection(this, table)) return;
        int id = (int) serviceModel.getValueAt(table.getSelectedRow(), 0);
        Service s = serviceDAO.getById(id);

        JTextField fName  = new JTextField(s.getName(), 20);
        JTextField fPrice = new JTextField(String.valueOf(s.getPrice()), 20);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Tên dịch vụ:")); form.add(fName);
        form.add(new JLabel("Đơn giá (VNĐ):")); form.add(fPrice);

        if (JOptionPane.showConfirmDialog(this, form, "Sửa dịch vụ",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String name = fName.getText().trim();
        if (name.isEmpty()) { UIUtils.showError(this, "Tên không được để trống."); return; }
        
        // Kiểm tra trùng tên (loại trừ id hiện tại)
        if (serviceDAO.isServiceNameTaken(name, id)) {
            UIUtils.showError(this, "Tên dịch vụ \"" + name + "\" đã tồn tại!");
            return;
        }
        
        try {
            double price = Double.parseDouble(fPrice.getText().trim());
            // Yêu cầu giá tối thiểu 1.000 VNĐ
            if (price < 1000) {
                UIUtils.showError(this, "Đơn giá phải lớn hơn hoặc bằng 1.000 VNĐ!");
                return;
            }
            s.setName(name); s.setPrice(price);
            if (serviceDAO.update(s)) { loadServiceData(); UIUtils.showInfo(this, "Cập nhật thành công!"); }
        } catch (NumberFormatException ex) { UIUtils.showError(this, "Đơn giá không hợp lệ."); }
    }

    private void doDeleteService(JTable table) {
        if (!UIUtils.requireSelection(this, table)) return;
        int id = (int) serviceModel.getValueAt(table.getSelectedRow(), 0);
        if (!UIUtils.confirm(this, "Xóa dịch vụ này?")) return;
        serviceDAO.delete(id);
        loadServiceData();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TAB 4: DANH SÁCH BOOKING
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel createBookingPanel() {
        String[] cols = {"BookingID", "Phòng", "Khách hàng", "Nhân viên",
                         "Ngày đặt", "Ngày check-in", "Tổng tiền (VNĐ)", "TT Thanh toán", "TT Booking"};
        bookingModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(bookingModel);
        UIUtils.configureTable(table);

        // Tô màu dòng theo trạng thái thanh toán
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel && row < bookingModel.getRowCount()) {
                    String ttTT = (String) bookingModel.getValueAt(row, 7);
                    String ttBK = (String) bookingModel.getValueAt(row, 8);
                    if ("Đã trả phòng".equals(ttBK))   c.setBackground(new Color(230, 230, 230));
                    else if ("Đã thanh toán".equals(ttTT)) c.setBackground(new Color(220, 255, 220));
                    else                                   c.setBackground(new Color(255, 230, 200));
                }
                return c;
            }
        });

        loadBookingData();

        JButton btnRefresh = UIUtils.makeButton("↺ Làm mới", new Color(200, 200, 200));
        btnRefresh.addActionListener(e -> loadBookingData());

        // Filter combo
        JComboBox<String> cbFilter = new JComboBox<>(new String[]{
            "Tất cả", "Đang thuê (CHECKED_IN)", "Đã trả phòng (CHECKED_OUT)"
        });
        cbFilter.addActionListener(e -> {
            // Cache tất cả rooms, customers, accounts để tránh gọi DB nhiều lần
            java.util.Map<Integer, Room> roomCache = new java.util.HashMap<>();
            for (Room r : roomDAO.getAll()) roomCache.put(r.getId(), r);
            
            java.util.Map<Integer, Customer> customerCache = new java.util.HashMap<>();
            for (Customer c : customerDAO.getAll()) customerCache.put(c.getId(), c);
            
            java.util.Map<Integer, Account> accountCache = new java.util.HashMap<>();
            for (Account a : accountDAO.getAll()) accountCache.put(a.getId(), a);
            
            java.util.Map<Integer, Boolean> paidCache = new java.util.HashMap<>();
            for (Payment p : paymentDAO.getAll()) {
                if (!paidCache.containsKey(p.getBookingId())) {
                    paidCache.put(p.getBookingId(), "PAID".equals(p.getStatus()));
                }
            }
            
            bookingModel.setRowCount(0);
            for (Booking b : bookingDAO.getAll()) {
                int fi = cbFilter.getSelectedIndex();
                if (fi == 1 && !"CHECKED_IN".equals(b.getStatus())) continue;
                if (fi == 2 && !"CHECKED_OUT".equals(b.getStatus())) continue;
                addBookingRow(b, roomCache, customerCache, accountCache, paidCache);
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("Lọc:")); top.add(cbFilter); top.add(btnRefresh);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void loadBookingData() {
        // Cache tất cả rooms, customers, accounts để tránh gọi DB nhiều lần
        java.util.Map<Integer, Room> roomCache = new java.util.HashMap<>();
        for (Room r : roomDAO.getAll()) roomCache.put(r.getId(), r);
        
        java.util.Map<Integer, Customer> customerCache = new java.util.HashMap<>();
        for (Customer c : customerDAO.getAll()) customerCache.put(c.getId(), c);
        
        java.util.Map<Integer, Account> accountCache = new java.util.HashMap<>();
        for (Account a : accountDAO.getAll()) accountCache.put(a.getId(), a);
        
        java.util.Map<Integer, Boolean> paidCache = new java.util.HashMap<>();
        for (Payment p : paymentDAO.getAll()) {
            if (!paidCache.containsKey(p.getBookingId())) {
                paidCache.put(p.getBookingId(), "PAID".equals(p.getStatus()));
            }
        }
        
        bookingModel.setRowCount(0);
        for (Booking b : bookingDAO.getAll()) {
            addBookingRow(b, roomCache, customerCache, accountCache, paidCache);
        }
    }

    private void addBookingRow(Booking b, java.util.Map<Integer, Room> roomCache, 
                               java.util.Map<Integer, Customer> customerCache,
                               java.util.Map<Integer, Account> accountCache,
                               java.util.Map<Integer, Boolean> paidCache) {
        Room r     = roomCache.get(b.getRoomId());
        Customer c = customerCache.get(b.getCustomerId());
        Account  a = accountCache.get(b.getStaffId());
        boolean paid = paidCache.getOrDefault(b.getId(), false);

        bookingModel.addRow(new Object[]{
            b.getId(),
            r != null ? r.getRoomNumber() : "#" + b.getRoomId(),
            c != null ? c.getName() : "#" + b.getCustomerId(),
            a != null ? a.getUserName() : "#" + b.getStaffId(),
            b.getBookingDate()  != null ? b.getBookingDate().format(DATE_FMT)  : "N/A",
            b.getCheckInDate()  != null ? b.getCheckInDate().format(DATE_FMT)  : "N/A",
            String.format("%,.0f", b.getTotalAmount()),
            paid ? "Đã thanh toán" : "Chưa thanh toán",
            UIUtils.translateStatus(b.getStatus())
        });
    }

    // ═════════════════════════════════════════════════════════════════════════
    // TAB 5: THỐNG KÊ & DOANH THU
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel createStatPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnCalc = UIUtils.makeButton("📊 Tính thống kê", new Color(100, 149, 237));
        JTextArea txtResult = new JTextArea(20, 60);
        txtResult.setEditable(false);
        txtResult.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtResult.setLineWrap(true);

        btnCalc.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("══════════════════════════════════════════════\n");
            sb.append("         BÁO CÁO THỐNG KÊ KHÁCH SẠN\n");
            sb.append("══════════════════════════════════════════════\n\n");

            // Doanh thu tổng
            double totalRevenue = 0;
            List<Payment> payments = paymentDAO.getAll();
            for (Payment pay : payments)
                if ("PAID".equals(pay.getStatus())) totalRevenue += pay.getAmount();
            sb.append(String.format("💰 TỔNG DOANH THU: %,.0f VNĐ%n%n", totalRevenue));

            // Thống kê phòng
            List<Room> rooms = roomDAO.getAll();
            long totalRooms    = rooms.stream().filter(Room::isEnable).count();
            long emptyRooms    = rooms.stream().filter(r -> r.isEnable() && "EMPTY".equals(r.getStatus())).count();
            long occupiedRooms = rooms.stream().filter(r -> r.isEnable() && "OCCUPIED".equals(r.getStatus())).count();
            long maintenRooms  = rooms.stream().filter(r -> r.isEnable() && "MAINTENANCE".equals(r.getStatus())).count();
            sb.append(String.format("🏠 PHÒNG: Tổng %d | Trống %d | Đang thuê %d | Bảo trì %d%n%n",
                totalRooms, emptyRooms, occupiedRooms, maintenRooms));

            // Booking stats
            List<Booking> bookings = bookingDAO.getAll();
            long checkedIn  = bookings.stream().filter(b -> "CHECKED_IN".equals(b.getStatus())).count();
            long checkedOut = bookings.stream().filter(b -> "CHECKED_OUT".equals(b.getStatus())).count();
            sb.append(String.format("📋 BOOKING: Tổng %d | Đang thuê %d | Đã trả %d%n%n",
                bookings.size(), checkedIn, checkedOut));

            // Tần suất sử dụng phòng (top 5) - tối ưu: cache rooms thay vì gọi DB cho mỗi room
            sb.append("📈 TOP PHÒNG ĐƯỢC THUÊ NHIỀU NHẤT:\n");
            java.util.Map<Integer, Long> freq = new java.util.HashMap<>();
            for (Booking b : bookings)
                freq.merge(b.getRoomId(), 1L, Long::sum);
            
            // Cache tất cả rooms vào map để tránh gọi DB nhiều lần
            java.util.Map<Integer, Room> roomCache = new java.util.HashMap<>();
            for (Room r : roomDAO.getAll()) {
                roomCache.put(r.getId(), r);
            }
            
            freq.entrySet().stream()
                .sorted((a, b2) -> Long.compare(b2.getValue(), a.getValue()))
                .limit(5)
                .forEach(entry -> {
                    Room r = roomCache.get(entry.getKey());
                    sb.append(String.format("  • Phòng %-6s : %d lần thuê%n",
                        r != null ? r.getRoomNumber() : "#" + entry.getKey(), entry.getValue()));
                });

            sb.append("\n── Dịch vụ được dùng nhiều nhất ────────────\n");
            ServiceUsageDAO suD = new ServiceUsageDAO();
            java.util.Map<Integer, Long> svFreq = new java.util.HashMap<>();
            for (ServiceUsage su : suD.getAll())
                svFreq.merge(su.getServiceId(), (long)su.getQuantity(), Long::sum);
            
            // Cache tất cả services vào map để tránh gọi DB nhiều lần
            java.util.Map<Integer, Service> serviceCache = new java.util.HashMap<>();
            for (Service sv : serviceDAO.getAll()) {
                serviceCache.put(sv.getId(), sv);
            }
            
            svFreq.entrySet().stream()
                .sorted((a, b2) -> Long.compare(b2.getValue(), a.getValue()))
                .limit(5)
                .forEach(entry -> {
                    Service sv = serviceCache.get(entry.getKey());
                    sb.append(String.format("  • %-20s : %d lần%n",
                        sv != null ? sv.getName() : "#" + entry.getKey(), entry.getValue()));
                });

            sb.append("\n── Khách hàng ───────────────────────────────\n");
            sb.append(String.format("  Tổng số khách: %d%n", customerDAO.getAll().size()));
            sb.append("\n══════════════════════════════════════════════\n");
            txtResult.setText(sb.toString());
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("  THỐNG KÊ & BÁO CÁO DOANH THU"));
        top.add(Box.createHorizontalStrut(20));
        top.add(btnCalc);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(txtResult), BorderLayout.CENTER);
        return p;
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────
    private JLabel colorLabel(String text, Color bg) {
        JLabel l = new JLabel("  " + text + "  ");
        l.setOpaque(true); l.setBackground(bg);
        l.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return l;
    }
}