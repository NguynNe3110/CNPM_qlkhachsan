package com.mycompany.qlkhachsan;

import com.mycompany.qlkhachsan.controller.LoginController;
import com.mycompany.qlkhachsan.dao.AccountDAO;
import com.mycompany.qlkhachsan.util.DatabaseInitializer;
import com.mycompany.qlkhachsan.view.LoginForm;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Qlkhachsan {

    public static void main(String[] args) {
        // 1. Initialize Database Tables if not exist
        DatabaseInitializer.initialize();

        // 2. Set System Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            AccountDAO accountDAO = new AccountDAO();
            accountDAO.initializeDefaultAdmin(); // Create admin/admin if no accounts exist
            
            LoginForm loginForm = new LoginForm();
            new LoginController(loginForm, accountDAO);
            loginForm.setVisible(true);
        });
    }
}
