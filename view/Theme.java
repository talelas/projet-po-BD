package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class Theme {
    public static final Color BG = new Color(245, 247, 251);
    public static final Color SURFACE = new Color(255, 255, 255);
    public static final Color SIDEBAR_BG = new Color(17, 24, 39);
    public static final Color SIDEBAR_BUTTON = new Color(31, 41, 55);
    public static final Color PRIMARY = new Color(0, 0, 0);  // Black buttons
    public static final Color PRIMARY_DARK = new Color(20, 20, 20);
    public static final Color SUCCESS = new Color(16, 185, 129);
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color DANGER = new Color(239, 68, 68);
    public static final Color TEXT = new Color(17, 24, 39);
    public static final Color MUTED_TEXT = new Color(107, 114, 128);
    public static final Color BORDER = new Color(229, 231, 235);

    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);

    private Theme() { }

    public static void applyGlobal() {
        UIManager.put("Label.font", FONT_REGULAR);
        UIManager.put("Button.font", FONT_REGULAR);
        UIManager.put("TextField.font", FONT_REGULAR);
        UIManager.put("PasswordField.font", FONT_REGULAR);
        UIManager.put("ComboBox.font", FONT_REGULAR);
        UIManager.put("TextArea.font", FONT_REGULAR);
        UIManager.put("Table.font", FONT_REGULAR);
        UIManager.put("TableHeader.font", FONT_BOLD);
        UIManager.put("Menu.font", FONT_REGULAR);
        UIManager.put("MenuItem.font", FONT_REGULAR);

        // Defaults: avoid pure black text and keep a consistent palette
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Menu.foreground", TEXT);
        UIManager.put("MenuItem.foreground", TEXT);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("TableHeader.foreground", TEXT);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("PasswordField.foreground", TEXT);
        UIManager.put("TextArea.foreground", TEXT);

        UIManager.put("TextField.background", SURFACE);
        UIManager.put("PasswordField.background", SURFACE);
        UIManager.put("TextArea.background", SURFACE);
    }

    public static void stylePrimaryButton(JButton button) {
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setBackground(SURFACE);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
    }

    public static void styleToolbar(JPanel panel) {
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
    }

    public static void styleTextField(JTextField field) {
        field.setBackground(SURFACE);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
    }

    public static void stylePasswordField(JPasswordField field) {
        field.setBackground(SURFACE);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(SURFACE);
        comboBox.setForeground(TEXT);
        comboBox.setBorder(new LineBorder(BORDER, 1));
    }

    public static void styleTextArea(JTextArea area) {
        area.setBackground(SURFACE);
        area.setForeground(TEXT);
        area.setCaretColor(TEXT);
        area.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(26);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER);
        table.setBackground(SURFACE);
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT);
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(TEXT);
        header.setBorder(new LineBorder(BORDER, 1));
    }

    public static void styleMenuBar(JMenuBar menuBar) {
        menuBar.setBackground(SURFACE);
        menuBar.setBorder(new LineBorder(BORDER, 1));
    }

    public static void styleNavButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(SIDEBAR_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setHorizontalAlignment(SwingConstants.LEFT);
    }
}
