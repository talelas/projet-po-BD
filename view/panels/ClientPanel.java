package view.panels;

import model.Client;
import service.PharmacyService;
import view.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClientPanel extends JPanel {
    private PharmacyService pharmacyService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ClientPanel(PharmacyService service) {
        this.pharmacyService = service;
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Theme.styleToolbar(toolbar);
        JButton btnAdd = new JButton("Add Client");
        JButton btnHistory = new JButton("Detailed History");
        Theme.stylePrimaryButton(btnAdd);
        Theme.styleSecondaryButton(btnHistory);
        toolbar.add(btnAdd);
        toolbar.add(btnHistory);
        add(toolbar, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Email", "Phone"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        Theme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> onAddClient());
        btnHistory.addActionListener(e -> JOptionPane.showMessageDialog(this, "Show history for selected client (Not Impl)."));
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Client> clients = pharmacyService.getAllClients();
        for (Client c : clients) {
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getEmail(), c.getPhone()});
        }
    }

    private void onAddClient() {
        JTextField name = new JTextField();
        JTextField address = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();

        Object[] message = {
                "Name:", name,
                "Address:", address,
                "Email:", email,
                "Phone:", phone
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Client", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) return;

        String n = name.getText() == null ? "" : name.getText().trim();
        if (n.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Client client = new Client(
                0,
                n,
                address.getText() == null ? "" : address.getText().trim(),
                email.getText() == null ? "" : email.getText().trim(),
                phone.getText() == null ? "" : phone.getText().trim()
        );

        try {
            pharmacyService.addClient(client);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to add client: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
