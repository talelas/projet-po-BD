package main;

import view.LoginFrame;
import view.Theme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                try {
                    // Set system look and feel for better appearance
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                   System.err.println("Warning: Could not set system look and feel.");
                }
                Theme.applyGlobal();
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Fatal Error: " + e.getMessage(), "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
