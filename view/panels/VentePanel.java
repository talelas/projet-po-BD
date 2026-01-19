package view.panels;

import exception.ProduitInexistantException;
import exception.StockInsuffisantException;
import model.Client;
import model.Product;
import service.PharmacyService;
import view.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentePanel extends JPanel {
    private PharmacyService pharmacyService;
    private JComboBox<String> clientCombo;
    private JComboBox<String> productCombo;
    private JTextField txtQuantity;
    private JTextArea txtInvoice;

    public VentePanel(PharmacyService service) {
        this.pharmacyService = service;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        formPanel.setBackground(Theme.SURFACE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<Client> clients = pharmacyService.getAllClients();
        String[] clientNames = clients.stream().map(c -> c.getId() + " - " + c.getName()).toArray(String[]::new);
        clientCombo = new JComboBox<>(clientNames);
        Theme.styleComboBox(clientCombo);

        List<Product> products = pharmacyService.getAllProducts();
        String[] productNames = products.stream().map(p -> p.getId() + " - " + p.getName()).toArray(String[]::new);
        productCombo = new JComboBox<>(productNames);
        Theme.styleComboBox(productCombo);

        txtQuantity = new JTextField(5);
        Theme.styleTextField(txtQuantity);
        JButton btnProcess = new JButton("Process Sale");
        Theme.stylePrimaryButton(btnProcess);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Client:"), gbc);
        gbc.gridx = 1; formPanel.add(clientCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1; formPanel.add(productCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; formPanel.add(txtQuantity, gbc);

        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(btnProcess, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Invoice Area
        txtInvoice = new JTextArea();
        txtInvoice.setEditable(false);
        txtInvoice.setFont(new Font("Consolas", Font.PLAIN, 12));
        Theme.styleTextArea(txtInvoice);
        add(new JScrollPane(txtInvoice), BorderLayout.CENTER);

        // Logic
        btnProcess.addActionListener(e -> processSale());
    }

    private void processSale() {
        try {
            String selectedClient = (String) clientCombo.getSelectedItem();
            int clientId = 0;
            if (selectedClient != null && selectedClient.contains(" - ")) {
                clientId = Integer.parseInt(selectedClient.split(" - ")[0]);
            }

            String selectedProd = (String) productCombo.getSelectedItem();
            if (selectedProd == null) return;
            
            int prodId = Integer.parseInt(selectedProd.split(" - ")[0]);
            int qty = Integer.parseInt(txtQuantity.getText());

            pharmacyService.processSale(prodId, qty, clientId);
            
            txtInvoice.append("Sale Processed:\n");
            txtInvoice.append("Client ID: " + clientId + "\n");
            txtInvoice.append("Product ID: " + prodId + "\n");
            txtInvoice.append("Quantity: " + qty + "\n");
            txtInvoice.append("Status: Success\n");
            txtInvoice.append("--------------------------\n");
            
            JOptionPane.showMessageDialog(this, "Sale successful!");
        } catch (StockInsuffisantException | ProduitInexistantException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Sale Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
