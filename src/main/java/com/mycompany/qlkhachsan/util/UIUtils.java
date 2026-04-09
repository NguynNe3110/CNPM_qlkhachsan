package com.mycompany.qlkhachsan.util;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class UIUtils {

    // ─── Bảng dịch trạng thái sang tiếng Việt ───────────────────────────────
    private static final Map<String, String> STATUS_VI = new HashMap<>();
    static {
        STATUS_VI.put("EMPTY",        "Trống");
        STATUS_VI.put("OCCUPIED",     "Đang thuê");
        STATUS_VI.put("MAINTENANCE",  "Bảo trì");
        STATUS_VI.put("NEED_UPGRADE", "Cần nâng cấp");
        STATUS_VI.put("CHECKED_IN",   "Đã nhận phòng");
        STATUS_VI.put("CHECKED_OUT",  "Đã trả phòng");
        STATUS_VI.put("PAID",         "Đã thanh toán");
        STATUS_VI.put("UNPAID",       "Chưa thanh toán");
        STATUS_VI.put("BOOKED",       "Đã đặt trước");
        STATUS_VI.put("CANCELLED",    "Đã hủy");
        STATUS_VI.put("ADMIN",        "Quản trị viên");
        STATUS_VI.put("STAFF",        "Nhân viên");
        STATUS_VI.put("CASH",         "Tiền mặt");
        STATUS_VI.put("CARD",         "Thẻ ngân hàng");
        STATUS_VI.put("TRANSFER",     "Chuyển khoản");
    }

    public static String translateStatus(String raw) {
        if (raw == null) return "";
        return STATUS_VI.getOrDefault(raw, raw);
    }

    // ─── Cấu hình bảng: không chỉnh sửa, không di chuyển cột ────────────────
    public static void configureTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultEditor(Object.class, null);          // Không cho sửa ô
        table.getTableHeader().setReorderingAllowed(false);  // Không di chuyển cột
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setGridColor(new Color(200, 200, 200));
        table.setShowGrid(true);
    }

    /** Ẩn cột theo index (vẫn giữ dữ liệu trong model) */
    public static void hideColumn(JTable table, int columnIndex) {
        TableColumn col = table.getColumnModel().getColumn(columnIndex);
        col.setMinWidth(0);
        col.setMaxWidth(0);
        col.setWidth(0);
        col.setPreferredWidth(0);
    }

    // ─── Nút bấm ─────────────────────────────────────────────────────────────
    public static void customizeButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(140, 35));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        customizeButton(btn, bg);
        return btn;
    }

    // ─── Dialog thông báo ────────────────────────────────────────────────────
    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Trả về true nếu người dùng chọn Có */
    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Xác nhận",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    // ─── Kiểm tra có chọn dòng trong bảng chưa ──────────────────────────────
    public static boolean requireSelection(Component parent, JTable table) {
        if (table.getSelectedRow() < 0) {
            showError(parent, "Vui lòng chọn một dòng trong bảng trước.");
            return false;
        }
        return true;
    }

    // ─── Validate dữ liệu ────────────────────────────────────────────────────
    /** Kiểm tra SĐT: đúng 10 chữ số */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    /** Kiểm tra CCCD: đúng 12 chữ số */
    public static boolean isValidCCCD(String cccd) {
        return cccd != null && cccd.matches("\\d{12}");
    }

    /** Kiểm tra mật khẩu tối thiểu 6 ký tự */
    public static boolean isValidPassword(String pw) {
        return pw != null && pw.length() >= 6;
    }
}