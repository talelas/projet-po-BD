package view.panels;

import model.Supplier;
import service.PharmacyService;
import view.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FournisseurPanel extends JPanel {
    private PharmacyService pharmacyService;
    private JTable table;
    private DefaultTableModel tableModel;

    public FournisseurPanel(PharmacyService service) {
        this.pharmacyService = service;
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Theme.styleToolbar(toolbar);
        JButton btnAdd = new JButton("Add Supplier");
        JButton btnRefresh = new JButton("Refresh");
        Theme.stylePrimaryButton(btnAdd);
        Theme.styleSecondaryButton(btnRefresh);
        toolbar.add(btnAdd);
        toolbar.add(btnRefresh);
        add(toolbar, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Contact", "Email"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        Theme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> JOptionPane.showMessageDialog(this, "Add Supplier functionality not implemented in this demo."));
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Supplier> suppliers = pharmacyService.getAllSuppliers();
        for (Supplier s : suppliers) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getContact(), s.getEmail()});
        }
    }
}
