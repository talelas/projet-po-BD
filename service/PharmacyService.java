package service;

import dao.ClientDao;
import dao.CommandeDao;
import dao.ProduitFournisseurDao;
import dao.ProductDao;
import dao.SupplierDao;
import dao.VenteDao;
import dao.mysql.MySqlClientDao;
import dao.mysql.MySqlCommandeDao;
import dao.mysql.MySqlProductDao;
import dao.mysql.MySqlProduitFournisseurDao;
import dao.mysql.MySqlSupplierDao;
import dao.mysql.MySqlVenteDao;
import db.Db;
import exception.ProduitInexistantException;
import exception.StockInsuffisantException;
import model.Client;
import model.Commande;
import model.LigneVente;
import model.Product;
import model.Supplier;
import model.Vente;
import model.ProduitFournisseur;

import java.time.LocalDate;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PharmacyService {

    private final ProductDao productDao;
    private final SupplierDao supplierDao;
    private final ClientDao clientDao;
    private final VenteDao venteDao;
    private final CommandeDao commandeDao;
    private final ProduitFournisseurDao produitFournisseurDao;

    // Fallback demo mode if DB is unreachable
    private boolean dbEnabled = true;
    private List<Product> products = new ArrayList<>();
    private List<Supplier> suppliers = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();
    private List<Vente> ventes = new ArrayList<>();
    private double totalSalesToday = 0.0;

    public PharmacyService() {
        this.productDao = new MySqlProductDao();
        this.supplierDao = new MySqlSupplierDao();
        this.clientDao = new MySqlClientDao();
        this.venteDao = new MySqlVenteDao();
        this.commandeDao = new MySqlCommandeDao();
        this.produitFournisseurDao = new MySqlProduitFournisseurDao();

        // Test DB early; if it fails, keep the app usable with dummy data.
        try {
            Db.getConnection().close();
            dbEnabled = true;
        } catch (Exception e) {
            dbEnabled = false;
        }

        // Dummy Data
        products.add(new Product(1, "Paracetamol", "Analgesic", 5.0, 100, LocalDate.of(2025, 12, 31), "PharmaCorp"));
        products.add(new Product(2, "Ibuprofen", "Anti-inflammatory", 8.5, 10, LocalDate.of(2024, 6, 30), "HealthPlus")); // Low stock
        products.add(new Product(3, "Amoxicillin", "Antibiotic", 12.0, 50, LocalDate.of(2024, 11, 15), "Medico"));
        
        suppliers.add(new Supplier(1, "PharmaCorp", "John Doe", "john@pharmacorp.com"));
        suppliers.add(new Supplier(2, "HealthPlus", "Jane Smith", "jane@healthplus.com"));
        
        clients.add(new Client(1, "Alice Wonderland", "", "alice@example.com", "123456789"));
        clients.add(new Client(2, "Bob Builder", "", "bob@example.com", "987654321"));
    }

    // Product Methods
    public List<Product> getAllProducts() {
        if (!dbEnabled) return new ArrayList<>(products);
        return productDao.findAll();
    }
    
    public void addProduct(Product p) {
        if (!dbEnabled) {
            products.add(p);
            return;
        }
        productDao.insert(p);
    }
    
    public void updateProduct(Product product) {
        if (!dbEnabled) {
            products.removeIf(p -> p.getId() == product.getId());
            products.add(product);
            return;
        }
        productDao.update(product);
    }

    public void deleteProduct(int id) {
        if (!dbEnabled) {
            products.removeIf(p -> p.getId() == id);
            return;
        }
        productDao.deleteById(id);
    }

    public Product getProductById(int id) {
        if (!dbEnabled) {
            return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        }
        return productDao.findById(id);
    }
    
    public List<Product> searchProducts(String query) {
        if (!dbEnabled) {
            return products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()) ||
                            p.getCategory().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return productDao.search(query);
    }
    
    public List<Product> getLowStockProducts(int threshold) {
        if (!dbEnabled) {
            return products.stream()
                    .filter(p -> p.getQuantity() <= threshold)
                    .collect(Collectors.toList());
        }
        return productDao.findLowStock(threshold);
    }

    public List<Product> getCriticalStockProducts() {
        if (!dbEnabled) {
            return products.stream()
                    .filter(p -> p.getQuantity() <= p.getQuantiteMinimale())
                    .collect(Collectors.toList());
        }
        return productDao.findCriticalStock();
    }

    // Supplier Methods
    public List<Supplier> getAllSuppliers() {
        if (!dbEnabled) return new ArrayList<>(suppliers);
        return supplierDao.findAll();
    }

    public void addSupplier(Supplier supplier) {
        if (!dbEnabled) {
            int nextId = suppliers.stream().mapToInt(Supplier::getId).max().orElse(0) + 1;
            suppliers.add(new Supplier(nextId, supplier.getName(), supplier.getContact(), supplier.getEmail()));
            return;
        }
        supplierDao.insert(supplier);
    }

    public void updateSupplier(Supplier supplier) {
        if (!dbEnabled) {
            suppliers.removeIf(s -> s.getId() == supplier.getId());
            suppliers.add(supplier);
            return;
        }
        supplierDao.update(supplier);
    }

    public void deleteSupplier(int idSupplier) {
        if (!dbEnabled) {
            suppliers.removeIf(s -> s.getId() == idSupplier);
            return;
        }
        supplierDao.deleteById(idSupplier);
    }

    public Supplier getSupplierById(int id) {
        if (!dbEnabled) {
            return suppliers.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
        }
        return supplierDao.findById(id);
    }

    // Client Methods
    public List<Client> getAllClients() {
        if (!dbEnabled) return new ArrayList<>(clients);
        return clientDao.findAll();
    }

    public void addClient(Client client) {
        if (!dbEnabled) {
            int nextId = clients.stream().mapToInt(Client::getId).max().orElse(0) + 1;
            clients.add(new Client(nextId, client.getName(), client.getAdresse(), client.getEmail(), client.getPhone()));
            return;
        }
        clientDao.insert(client);
    }

    public void updateClient(Client client) {
        if (!dbEnabled) {
            clients.removeIf(c -> c.getId() == client.getId());
            clients.add(client);
            return;
        }
        clientDao.update(client);
    }

    public void deleteClient(int idClient) {
        if (!dbEnabled) {
            clients.removeIf(c -> c.getId() == idClient);
            return;
        }
        clientDao.deleteById(idClient);
    }

    public Client getClientById(int id) {
        if (!dbEnabled) {
            return clients.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
        }
        return clientDao.findById(id);
    }

    public List<Client> searchClientsByName(String nameQuery) {
        if (!dbEnabled) {
            String q = nameQuery == null ? "" : nameQuery.toLowerCase();
            return clients.stream()
                    .filter(c -> c.getName() != null && c.getName().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }
        return clientDao.searchByName(nameQuery);
    }

    // Sales history
    public List<Vente> getAllVentes() {
        if (!dbEnabled) return new ArrayList<>(ventes);
        return venteDao.findAll();
    }

    // Sales Logic
    public void processSale(int productId, int quantity) throws StockInsuffisantException, ProduitInexistantException {
        // Backward compatible: when client is unknown, we record with idClient = 0.
        processSale(productId, quantity, 0);
    }

    public void processSale(int productId, int quantity, int clientId) throws StockInsuffisantException, ProduitInexistantException {
        if (!dbEnabled) {
            Product product = products.stream().filter(p -> p.getId() == productId).findFirst().orElse(null);
            if (product == null) {
                throw new ProduitInexistantException("Produit avec ID " + productId + " n'existe pas.");
            }
            if (product.getQuantity() < quantity) {
                throw new StockInsuffisantException("Stock insuffisant pour " + product.getName() + ". Disponible: " + product.getQuantity());
            }

            product.setQuantity(product.getQuantity() - quantity);
            double lineTotal = product.getPrice() * quantity;
            totalSalesToday += lineTotal;

            int venteId = (int) (System.currentTimeMillis() & 0x7fffffff);
            Vente vente = new Vente(venteId, LocalDate.now(), clientId);
            vente.addLigne(new LigneVente(venteId, productId, quantity, product.getPrice()));
            ventes.add(vente);
            return;
        }

        // DB mode: transaction for stock + vente + lignevente
        int venteId = (int) (System.currentTimeMillis() & 0x7fffffff);
        LocalDate today = LocalDate.now();

        try (Connection c = Db.getConnection()) {
            c.setAutoCommit(false);

            // Lock product row
            String selectSql = "SELECT nomProduit, quantite, prix FROM Produit WHERE idProduit = ? FOR UPDATE";
            String updateSql = "UPDATE Produit SET quantite = ? WHERE idProduit = ?";
            String insertVenteSql = "INSERT INTO Vente (idVente, dateFacture, idClient) VALUES (?,?,?)";
            String insertLigneSql = "INSERT INTO LigneVente (idVente, idProduit, quantite, prixUnite) VALUES (?,?,?,?)";

            String productName;
            int currentQty;
            double prix;

            try (PreparedStatement ps = c.prepareStatement(selectSql)) {
                ps.setInt(1, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        c.rollback();
                        throw new ProduitInexistantException("Produit avec ID " + productId + " n'existe pas.");
                    }
                    productName = rs.getString("nomProduit");
                    currentQty = rs.getInt("quantite");
                    prix = rs.getDouble("prix");
                }
            }

            if (currentQty < quantity) {
                c.rollback();
                throw new StockInsuffisantException("Stock insuffisant pour " + productName + ". Disponible: " + currentQty);
            }

            int newQty = currentQty - quantity;
            try (PreparedStatement ps = c.prepareStatement(updateSql)) {
                ps.setInt(1, newQty);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement(insertVenteSql)) {
                ps.setInt(1, venteId);
                ps.setDate(2, Date.valueOf(today));
                ps.setInt(3, clientId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement(insertLigneSql)) {
                ps.setInt(1, venteId);
                ps.setInt(2, productId);
                ps.setInt(3, quantity);
                ps.setDouble(4, prix);
                ps.executeUpdate();
            }

            c.commit();
            totalSalesToday += (prix * quantity);
        } catch (SQLException e) {
            throw new RuntimeException("DB error while processing sale", e);
        }
    }
    
    public double getTotalSalesToday() {
        return totalSalesToday;
    }
    
    public int getLowStockCount(int threshold) {
        if (!dbEnabled) {
            return (int) products.stream().filter(p -> p.getQuantity() <= threshold).count();
        }
        // Backward compat: when DB is on, prefer critical stock by quantiteMinimale
        return productDao.findCriticalStock().size();
    }

    // ===== Commandes (supplier orders) =====
    public List<Commande> getPendingCommandes() {
        if (!dbEnabled) return new ArrayList<>();
        return commandeDao.findPending();
    }

    public int createCommande(Commande commande) {
        if (!dbEnabled) return -1;
        return commandeDao.insert(commande);
    }

    public void markCommandeReceived(int idCommande) {
        if (!dbEnabled) return;
        commandeDao.markReceived(idCommande);
    }

    public List<Commande> getCommandesByDate(LocalDate date) {
        if (!dbEnabled) return new ArrayList<>();
        return commandeDao.findByDate(date);
    }

    public Commande getCommandeById(int id) {
        if (!dbEnabled) return null;
        return commandeDao.findById(id);
    }

    public List<Commande> getCommandesBySupplier(int idFournisseur) {
        if (!dbEnabled) return new ArrayList<>();
        return commandeDao.findBySupplier(idFournisseur);
    }

    // ===== Sales / Ventes methods =====
    public Vente getVenteById(int id) {
        if (!dbEnabled) {
            return ventes.stream().filter(v -> v.getIdVente() == id).findFirst().orElse(null);
        }
        // Note: VenteDao needs findById method
        return null;
    }

    public List<Vente> getVentesByClient(int idClient) {
        if (!dbEnabled) {
            return ventes.stream()
                .filter(v -> v.getIdClient() == idClient)
                .collect(Collectors.toList());
        }
        return venteDao.findByClient(idClient);
    }

    public List<Vente> getVentesByDate(LocalDate date) {
        if (!dbEnabled) {
            return ventes.stream()
                .filter(v -> v.getDateFacture().equals(date))
                .collect(Collectors.toList());
        }
        return venteDao.findByDate(date);
    }

    // ===== produit_fournisseur junction =====
    public void linkProduitToFournisseur(ProduitFournisseur link) {
        if (!dbEnabled) return;
        produitFournisseurDao.link(link.getIdProduit(), link.getIdFournisseur());
    }

    public void unlinkProduitFromFournisseur(ProduitFournisseur link) {
        if (!dbEnabled) return;
        produitFournisseurDao.unlink(link.getIdProduit(), link.getIdFournisseur());
    }

}
