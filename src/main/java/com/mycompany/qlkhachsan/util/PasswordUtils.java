package com.mycompany.qlkhachsan.util;

import java.security.MessageDigest;

public class PasswordUtils {

    /** Mã hóa SHA-256 (không dùng nữa nhưng giữ lại để tương thích) */
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi mã hóa mật khẩu", e);
        }
    }

    /** Kiểm tra chuỗi có phải SHA-256 hash không (64 ký tự hex) */
    public static boolean isHashed(String s) {
        return s != null && s.length() == 64 && s.matches("[a-f0-9]+");
    }

    public static boolean verify(String plainText, String hashed) {
        return hash(plainText).equals(hashed);
    }
    
    /** Trả về mật khẩu plain text (không mã hóa) */
    public static String toPlainText(String password) {
        return password;
    }
}