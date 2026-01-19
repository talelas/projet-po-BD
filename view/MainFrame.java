package view;

import model.Client;
import model.Commande;
import model.Product;
import model.Supplier;
import model.Vente;
import model.User;
import service.PharmacyService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainFrame extends JFrame {
    private final PharmacyService service;
    private final User user;
    private final JTabbedPane tabs = new JTabbedPane();

    public MainFrame(User user) {
        this.user = user;
        this.service = new PharmacyService();
        Theme.applyGlobal();
        initFrame();
    }

    private void initFrame() {
        setTitle("Pharmacy Back-Office - " + (user != null ? user.getUsername() : "user"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tabs.addTab("Dashboard", buildDashboard());
        tabs.addTab("Produits", buildProducts());
        tabs.addTab("Clients", buildClients());
        tabs.addTab("Fournisseurs", buildSuppliers());
        tabs.addTab("Commandes", buildCommandes());
        tabs.addTab("Ventes", buildVentes());

        add(tabs, BorderLayout.CENTER);
    }

    // Dashboard
    private JPanel buildDashboard() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setBackground(Theme.BG);

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 12, 12));
        statsPanel.setBackground(Theme.BG);
        statsPanel.add(statCard("Produits", String.valueOf(service.getAllProducts().size()), Theme.PRIMARY));
        statsPanel.add(statCard("Clients", String.valueOf(service.getAllClients().size()), Theme.PRIMARY));
        statsPanel.add(statCard("Fournisseurs", String.valueOf(service.getAllSuppliers().size()), Theme.PRIMARY));
        statsPanel.add(statCard("Commandes en attente", String.valueOf(service.getPendingCommandes().size()), Theme.WARNING));
        statsPanel.add(statCard("Ventes (total)", String.valueOf(service.getAllVentes().size()), Theme.SUCCESS));
        statsPanel.add(statCard("Stock bas", String.valueOf(service.getLowStockCount(10)), Theme.DANGER));

        JButton btnRefresh = new JButton("Actualiser");
        Theme.stylePrimaryButton(btnRefresh);
        btnRefresh.addActionListener(e -> {
            statsPanel.removeAll();
            statsPanel.add(statCard("Produits", String.valueOf(service.getAllProducts().size()), Theme.PRIMARY));
            statsPanel.add(statCard("Clients", String.valueOf(service.getAllClients().size()), Theme.PRIMARY));
            statsPanel.add(statCard("Fournisseurs", String.valueOf(service.getAllSuppliers().size()), Theme.PRIMARY));
            statsPanel.add(statCard("Commandes en attente", String.valueOf(service.getPendingCommandes().size()), Theme.WARNING));
            statsPanel.add(statCard("Ventes (total)", String.valueOf(service.getAllVentes().size()), Theme.SUCCESS));
            statsPanel.add(statCard("Stock bas", String.valueOf(service.getLowStockCount(10)), Theme.DANGER));
            statsPanel.revalidate();
            statsPanel.repaint();
        });

        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel statCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Theme.SURFACE);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Theme.MUTED_TEXT);
        lblTitle.setFont(Theme.FONT_BOLD);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(color);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    // Products tab
    private JPanel buildProducts() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Nom", "Marque", "Quantité", "Min", "Prix"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        Theme.styleTable(table);
        refreshProducts(model);

        JButton btnAdd = new JButton("Ajouter produit");
        Theme.stylePrimaryButton(btnAdd);
        btnAdd.addActionListener(e -> showProductDialog(model, null));

        JButton btnDelete = new JButton("Supprimer");
        Theme.styleSecondaryButton(btnDelete);
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            int id = (int) model.getValueAt(row, 0);
            service.deleteProduct(id);
            refreshProducts(model);
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(Theme.BG);
        actions.add(btnAdd);
        actions.add(btnDelete);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshProducts(DefaultTableModel model) {
        model.setRowCount(0);
        for (Product p : service.getAllProducts()) {
            model.addRow(new Object[]{p.getId(), p.getName(), p.getMarque(), p.getQuantity(), p.getQuantiteMinimale(), p.getPrice()});
        }
    }

    private void showProductDialog(DefaultTableModel model, Product existing) {
        JTextField name = new JTextField();
        JTextField brand = new JTextField();
        JTextField qty = new JTextField();
        JTextField minQty = new JTextField();
        JTextField price = new JTextField();
        Theme.styleTextField(name);
        Theme.styleTextField(brand);
        Theme.styleTextField(qty);
        Theme.styleTextField(minQty);
        Theme.styleTextField(price);

        if (existing != null) {
            name.setText(existing.getName());
            brand.setText(existing.getMarque());
            qty.setText(String.valueOf(existing.getQuantity()));
            minQty.setText(String.valueOf(existing.getQuantiteMinimale()));
            price.setText(String.valueOf(existing.getPrice()));
        } else {
            minQty.setText("10");
        }

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.add(new JLabel("Nom")); form.add(name);
        form.add(new JLabel("Marque")); form.add(brand);
        form.add(new JLabel("Quantité")); form.add(qty);
        form.add(new JLabel("Quantité minimale")); form.add(minQty);
        form.add(new JLabel("Prix")); form.add(price);

        int res = JOptionPane.showConfirmDialog(this, form, existing == null ? "Ajouter produit" : "Modifier produit", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int quantity = Integer.parseInt(qty.getText().trim());
                int min = Integer.parseInt(minQty.getText().trim());
                double pr = Double.parseDouble(price.getText().trim());
                Product p = new Product(existing != null ? existing.getId() : 0, name.getText().trim(), brand.getText().trim(), quantity, pr, 20.0, "");
                p.setQuantiteMinimale(min);
                if (existing == null) service.addProduct(p); else service.updateProduct(p);
                refreshProducts(model);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Valeurs invalides", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Clients tab
    private JPanel buildClients() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Nom", "Adresse", "Email", "Téléphone"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        Theme.styleTable(table);
        refreshClients(model);

        JButton btnAdd = new JButton("Ajouter client");
        Theme.stylePrimaryButton(btnAdd);
        btnAdd.addActionListener(e -> showClientDialog(model, null));

        JButton btnEdit = new JButton("Modifier");
        Theme.styleSecondaryButton(btnEdit);
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            Client c = new Client((int) model.getValueAt(row, 0),
                    (String) model.getValueAt(row, 1),
                    (String) model.getValueAt(row, 2),
                    (String) model.getValueAt(row, 3),
                    (String) model.getValueAt(row, 4));
            showClientDialog(model, c);
        });

        JButton btnDelete = new JButton("Supprimer");
        Theme.styleSecondaryButton(btnDelete);
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            int id = (int) model.getValueAt(row, 0);
            service.deleteClient(id);
            refreshClients(model);
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(Theme.BG);
        actions.add(btnAdd);
        actions.add(btnEdit);
        actions.add(btnDelete);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshClients(DefaultTableModel model) {
        model.setRowCount(0);
        for (Client c : service.getAllClients()) {
            model.addRow(new Object[]{c.getId(), c.getName(), c.getAdresse(), c.getEmail(), c.getPhone()});
        }
    }

    private void showClientDialog(DefaultTableModel model, Client existing) {
        JTextField name = new JTextField();
        JTextField address = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();
        Theme.styleTextField(name);
        Theme.styleTextField(address);
        Theme.styleTextField(email);
        Theme.styleTextField(phone);

        if (existing != null) {
            name.setText(existing.getName());
            address.setText(existing.getAdresse());
            email.setText(existing.getEmail());
            phone.setText(existing.getPhone());
        }

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.add(new JLabel("Nom")); form.add(name);
        form.add(new JLabel("Adresse")); form.add(address);
        form.add(new JLabel("Email")); form.add(email);
        form.add(new JLabel("Téléphone")); form.add(phone);

        int res = JOptionPane.showConfirmDialog(this, form, existing == null ? "Ajouter client" : "Modifier client", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            Client c = new Client(existing != null ? existing.getId() : 0, name.getText().trim(), address.getText().trim(), email.getText().trim(), phone.getText().trim());
            if (existing == null) service.addClient(c); else service.updateClient(c);
            refreshClients(model);
        }
    }

    // Suppliers tab (CRUD)
    private JPanel buildSuppliers() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Nom", "Prénom", "Téléphone", "Email"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        Theme.styleTable(table);
        refreshSuppliers(model);

        JButton btnAdd = new JButton("Ajouter fournisseur");
        Theme.stylePrimaryButton(btnAdd);
        btnAdd.addActionListener(e -> showSupplierDialog(model, null));

        JButton btnEdit = new JButton("Modifier");
        Theme.styleSecondaryButton(btnEdit);
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            Supplier s = new Supplier(
                (int) model.getValueAt(row, 0),
                (String) model.getValueAt(row, 1),
                (String) model.getValueAt(row, 2),
                (String) model.getValueAt(row, 3),
                (String) model.getValueAt(row, 4)
            );
            showSupplierDialog(model, s);
        });

        JButton btnDelete = new JButton("Supprimer");
        Theme.styleSecondaryButton(btnDelete);
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            int id = (int) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Supprimer le fournisseur #" + id + " ?", "Confirmer", JOptionPane.OK_CANCEL_OPTION);
            if (confirm == JOptionPane.OK_OPTION) {
                service.deleteSupplier(id);
                refreshSuppliers(model);
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(Theme.BG);
        actions.add(btnAdd);
        actions.add(btnEdit);
        actions.add(btnDelete);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshSuppliers(DefaultTableModel model) {
        model.setRowCount(0);
        for (Supplier s : service.getAllSuppliers()) {
            model.addRow(new Object[]{s.getId(), s.getNom(), s.getPrenom(), s.getNumeroTelephone(), s.getAdresseEmail()});
        }
    }

    private void showSupplierDialog(DefaultTableModel model, Supplier existing) {
        JTextField nom = new JTextField();
        JTextField prenom = new JTextField();
        JTextField tel = new JTextField();
        JTextField email = new JTextField();
        Theme.styleTextField(nom);
        Theme.styleTextField(prenom);
        Theme.styleTextField(tel);
        Theme.styleTextField(email);

        if (existing != null) {
            nom.setText(existing.getNom());
            prenom.setText(existing.getPrenom());
            tel.setText(existing.getNumeroTelephone());
            email.setText(existing.getAdresseEmail());
        }

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.add(new JLabel("Nom")); form.add(nom);
        form.add(new JLabel("Prénom")); form.add(prenom);
        form.add(new JLabel("Téléphone")); form.add(tel);
        form.add(new JLabel("Email")); form.add(email);

        int res = JOptionPane.showConfirmDialog(this, form, existing == null ? "Ajouter fournisseur" : "Modifier fournisseur", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            Supplier s = new Supplier(existing != null ? existing.getId() : 0,
                    nom.getText().trim(), prenom.getText().trim(), tel.getText().trim(), email.getText().trim());
            if (existing == null) service.addSupplier(s); else service.updateSupplier(s);
            refreshSuppliers(model);
        }
    }

    // Commandes tab
    private JPanel buildCommandes() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Fournisseur", "Date", "Réception (jours)", "Reçu"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        Theme.styleTable(table);
        refreshCommandes(model);

        JButton btnMark = new JButton("Marquer reçu");
        Theme.stylePrimaryButton(btnMark);
        btnMark.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            int id = (int) model.getValueAt(row, 0);
            service.markCommandeReceived(id);
            refreshCommandes(model);
        });

        // Filters
        JTextField txtSupplierId = new JTextField(8);
        JTextField txtDate = new JTextField(10);
        Theme.styleTextField(txtSupplierId);
        Theme.styleTextField(txtDate);

        JButton btnFilter = new JButton("Filtrer");
        Theme.styleSecondaryButton(btnFilter);
        btnFilter.addActionListener(e -> {
            model.setRowCount(0);
            DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE;
            try {
                String sid = txtSupplierId.getText().trim();
                String d = txtDate.getText().trim();
                List<Commande> list;
                if (!sid.isEmpty()) {
                    int idF = Integer.parseInt(sid);
                    list = service.getCommandesBySupplier(idF);
                } else if (!d.isEmpty()) {
                    list = service.getCommandesByDate(java.time.LocalDate.parse(d));
                } else {
                    list = service.getPendingCommandes();
                }
                DateTimeFormatter df = DateTimeFormatter.ISO_DATE;
                for (Commande c : list) {
                    model.addRow(new Object[]{c.getIdCommande(), c.getIdFournisseur(), c.getDateCommande() != null ? c.getDateCommande().format(df) : "", c.getPeriodeReception(), c.isRecu()});
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Filtre invalide. Utilisez AAAA-MM-JJ pour la date.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnPending = new JButton("En attente");
        Theme.styleSecondaryButton(btnPending);
        btnPending.addActionListener(e -> {
            txtSupplierId.setText("");
            txtDate.setText("");
            refreshCommandes(model);
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(Theme.BG);
        actions.add(btnMark);
        actions.add(new JLabel("Fournisseur ID"));
        actions.add(txtSupplierId);
        actions.add(new JLabel("Date (AAAA-MM-JJ)"));
        actions.add(txtDate);
        actions.add(btnFilter);
        actions.add(btnPending);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshCommandes(DefaultTableModel model) {
        model.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE;
        List<Commande> list = service.getPendingCommandes();
        for (Commande c : list) {
            model.addRow(new Object[]{c.getIdCommande(), c.getIdFournisseur(), c.getDateCommande() != null ? c.getDateCommande().format(fmt) : "", c.getPeriodeReception(), c.isRecu()});
        }
    }

    // Ventes tab
    private JPanel buildVentes() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Date", "Client"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        Theme.styleTable(table);
        refreshVentes(model);

        // Filters
        JTextField txtClientId = new JTextField(8);
        JTextField txtDate = new JTextField(10);
        Theme.styleTextField(txtClientId);
        Theme.styleTextField(txtDate);

        JButton btnFilter = new JButton("Filtrer");
        Theme.styleSecondaryButton(btnFilter);
        btnFilter.addActionListener(e -> {
            model.setRowCount(0);
            DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE;
            try {
                String cid = txtClientId.getText().trim();
                String d = txtDate.getText().trim();
                if (!cid.isEmpty()) {
                    int idClient = Integer.parseInt(cid);
                    for (Vente v : service.getVentesByClient(idClient)) {
                        model.addRow(new Object[]{v.getIdVente(), v.getDateFacture() != null ? v.getDateFacture().format(fmt) : "", v.getIdClient()});
                    }
                } else if (!d.isEmpty()) {
                    for (Vente v : service.getVentesByDate(java.time.LocalDate.parse(d))) {
                        model.addRow(new Object[]{v.getIdVente(), v.getDateFacture() != null ? v.getDateFacture().format(fmt) : "", v.getIdClient()});
                    }
                } else {
                    refreshVentes(model);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Filtre invalide. Utilisez AAAA-MM-JJ pour la date.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnReset = new JButton("Tous");
        Theme.styleSecondaryButton(btnReset);
        btnReset.addActionListener(e -> {
            txtClientId.setText("");
            txtDate.setText("");
            refreshVentes(model);
        });

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.setBackground(Theme.BG);
        filters.add(new JLabel("Client ID"));
        filters.add(txtClientId);
        filters.add(new JLabel("Date (AAAA-MM-JJ)"));
        filters.add(txtDate);
        filters.add(btnFilter);
        filters.add(btnReset);

        panel.add(filters, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void refreshVentes(DefaultTableModel model) {
        model.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE;
        for (Vente v : service.getAllVentes()) {
            model.addRow(new Object[]{v.getIdVente(), v.getDateFacture() != null ? v.getDateFacture().format(fmt) : "", v.getIdClient()});
        }
    }
}
