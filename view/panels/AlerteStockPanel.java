package view.panels;

import model.Product;
import service.PharmacyService;
import view.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AlerteStockPanel extends JPanel {
    private PharmacyService pharmacyService;
    private JTable table;
    private DefaultTableModel tableModel;

    public AlerteStockPanel(PharmacyService service) {
        this.pharmacyService = service;
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        JLabel header = new JLabel("Alertes Stock Critique (<= min stock)", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(Theme.DANGER);
        add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Quantity", "Supplier"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        Theme.styleTable(table);
        
        // Highlight rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(254, 226, 226));
                } else {
                    c.setBackground(new Color(255, 241, 242));
                }
                c.setForeground(Theme.TEXT);
                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        
        JButton btnRefresh = new JButton("Refresh Alerts");
        Theme.styleSecondaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadData());
        add(btnRefresh, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (Product p : pharmacyService.getCriticalStockProducts()) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getQuantity(), p.getSupplier()});
        }
    }
}
