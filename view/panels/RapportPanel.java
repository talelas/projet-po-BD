package view.panels;

import service.PharmacyService;

import javax.swing.*;
import java.awt.*;

public class RapportPanel extends JPanel {
    private PharmacyService service;
    
    public RapportPanel(PharmacyService service) {
        this.service = service;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        
        JButton btnSales = new JButton("📊 Generate Sales Report");
        JButton btnStock = new JButton("📦 Generate Stock Report");
        JButton btnSupplier = new JButton("🏭 Supplier Performance");
        
        styleReportButton(btnSales, new Color(52, 152, 219));
        styleReportButton(btnStock, new Color(46, 204, 113));
        styleReportButton(btnSupplier, new Color(155, 89, 182));
        
        Dimension btnSize = new Dimension(250, 60);
        btnSales.setPreferredSize(btnSize);
        btnStock.setPreferredSize(btnSize);
        btnSupplier.setPreferredSize(btnSize);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0; gbc.gridy = 0;
        add(btnSales, gbc);
        
        gbc.gridy = 1;
        add(btnStock, gbc);
        
        gbc.gridy = 2;
        add(btnSupplier, gbc);
        
        btnSales.addActionListener(e -> generateSalesReport());
        btnStock.addActionListener(e -> generateStockReport());
        btnSupplier.addActionListener(e -> generateSupplierReport());
    }
    
    private void styleReportButton(JButton btn, Color color) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void generateSalesReport() {
        int totalSales = service.getAllVentes().size();
        double todayTotal = service.getTotalSalesToday();
        JOptionPane.showMessageDialog(this, 
            String.format("Sales Report:\n\nTotal Sales: %d\nToday's Revenue: $%.2f", 
                totalSales, todayTotal),
            "Sales Report", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void generateStockReport() {
        int lowStock = service.getLowStockCount(10);
        int totalProducts = service.getAllProducts().size();
        JOptionPane.showMessageDialog(this,
            String.format("Stock Report:\n\nTotal Products: %d\nLow Stock Items: %d",
                totalProducts, lowStock),
            "Stock Report", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void generateSupplierReport() {
        int totalSuppliers = service.getAllSuppliers().size();
        JOptionPane.showMessageDialog(this,
            String.format("Supplier Report:\n\nTotal Suppliers: %d",
                totalSuppliers),
            "Supplier Report", JOptionPane.INFORMATION_MESSAGE);
    }
}
