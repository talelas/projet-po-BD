package view.panels;

import model.Product;
import service.PharmacyService;
import view.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ProduitPanel extends JPanel {
    private PharmacyService pharmacyService;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public ProduitPanel(PharmacyService service) {
        this.pharmacyService = service;
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        // Top Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Theme.styleToolbar(toolbar);
        txtSearch = new JTextField(20);
        Theme.styleTextField(txtSearch);
        JButton btnSearch = new JButton("Search");
        JButton btnAdd = new JButton("Add Product");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        Theme.styleSecondaryButton(btnSearch);
        Theme.stylePrimaryButton(btnAdd);
        Theme.styleSecondaryButton(btnEdit);
        Theme.styleSecondaryButton(btnDelete);
        Theme.styleSecondaryButton(btnRefresh);

        toolbar.add(new JLabel("Search:"));
        toolbar.add(txtSearch);
        toolbar.add(btnSearch);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);
        add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Category", "Price", "Qty", "Expiry", "Supplier"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        Theme.styleTable(productTable);
        add(new JScrollPane(productTable), BorderLayout.CENTER);

        // Events
        btnRefresh.addActionListener(e -> loadData());
        btnSearch.addActionListener(e -> searchData());
        btnAdd.addActionListener(e -> showProductDialog(null));
        btnEdit.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                Product p = pharmacyService.getAllProducts().stream().filter(prod -> prod.getId() == id).findFirst().orElse(null);
                if (p != null) showProductDialog(p);
            } else {
                JOptionPane.showMessageDialog(this, "Select a product to edit.");
            }
        });
        btnDelete.addActionListener(e -> deleteProduct());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Product> products = pharmacyService.getAllProducts();
        for (Product p : products) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity(), p.getExpirationDate(), p.getSupplier()});
        }
    }

    private void searchData() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            loadData();
            return;
        }
        tableModel.setRowCount(0);
        List<Product> products = pharmacyService.searchProducts(query);
        for (Product p : products) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity(), p.getExpirationDate(), p.getSupplier()});
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete product " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                pharmacyService.deleteProduct(id);
                loadData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a product to delete.");
        }
    }

    private void showProductDialog(Product product) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), product == null ? "Add Product" : "Edit Product", true);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(Theme.SURFACE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(product != null ? product.getName() : "", 15);
        JTextField categoryField = new JTextField(product != null ? product.getCategory() : "", 15);
        JTextField priceField = new JTextField(product != null ? String.valueOf(product.getPrice()) : "", 15);
        JTextField qtyField = new JTextField(product != null ? String.valueOf(product.getQuantity()) : "", 15);
        JTextField supplierField = new JTextField(product != null ? product.getSupplier() : "", 15);
        Theme.styleTextField(nameField);
        Theme.styleTextField(categoryField);
        Theme.styleTextField(priceField);
        Theme.styleTextField(qtyField);
        Theme.styleTextField(supplierField);

        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; dialog.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; dialog.add(categoryField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; dialog.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; dialog.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; dialog.add(qtyField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; dialog.add(new JLabel("Supplier:"), gbc);
        gbc.gridx = 1; dialog.add(supplierField, gbc);

        JButton saveBtn = new JButton("Save");
        Theme.stylePrimaryButton(saveBtn);
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String cat = categoryField.getText();
                double price = Double.parseDouble(priceField.getText());
                int qty = Integer.parseInt(qtyField.getText());
                String sup = supplierField.getText();
                
                int id = product == null ? (int)(Math.random() * 1000) : product.getId(); // Mock ID gen
                
                Product newProd = new Product(id, name, cat, price, qty, LocalDate.now().plusYears(1), sup);
                
                if (product != null) {
                    pharmacyService.deleteProduct(product.getId()); // simple replace logic
                }
                pharmacyService.addProduct(newProd);
                
                loadData();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number format.");
            }
        });

        gbc.gridx = 1; gbc.gridy = 5;
        dialog.add(saveBtn, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
