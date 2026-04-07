package com.mycompany.qlkhachsan.util;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
    public static void customizeButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(120, 35));
    }
    
    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}
