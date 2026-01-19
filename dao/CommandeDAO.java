package dao;

import models.Commande;
import models.LigneCommande;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour gérer les commandes avec toutes les fonctionnalités demandées
 */
public class CommandeDAO {
    private Connection conn;
    private LigneCommandeDAO ligneDAO;
    private ProduitDAO produitDAO;
    
    public CommandeDAO(Connection conn) {
        this.conn = conn;
        this.ligneDAO = new LigneCommandeDAO(conn);
        this.produitDAO = new ProduitDAO(conn);
    }
    
    /**
     * Ajoute une commande et retourne son ID généré
     */
    public int ajouterCommande(Commande commande) {
        String sql = "INSERT INTO Commande (idFournisseur, dateCommande, periodeReception, recu, dateReception) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, commande.getIdFournisseur());
            pstmt.setDate(2, Date.valueOf(commande.getDateCommande()));
            pstmt.setInt(3, commande.getPeriodeReception());
            pstmt.setBoolean(4, commande.isRecu());
            pstmt.setDate(5, commande.getDateReception() != null ? Date.valueOf(commande.getDateReception()) : null);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.println("✓ Commande créée avec ID: " + id);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Supprime une commande (avec ses lignes)
     */
    public void supprimerCommande(int idCommande) {
        try {
            conn.setAutoCommit(false);
            
            // Supprimer les lignes d'abord
            ligneDAO.supprimerLignesCommande(idCommande);
            
            // Puis la commande
            String sql = "DELETE FROM Commande WHERE idCommande = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idCommande);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            System.out.println("✓ Commande supprimée!");
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            System.err.println("✗ Erreur: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }
    
    /**
     * Obtient les commandes en attente (non reçues)
     */
    public List<Commande> obtenirCommandesEnAttente() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM Commande WHERE recu = FALSE ORDER BY dateCommande";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                commandes.add(extraireCommande(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return commandes;
    }
    
    /**
     * Obtient les commandes d'un fournisseur
     */
    public List<Commande> obtenirCommandesParFournisseur(int idFournisseur) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM Commande WHERE idFournisseur = ? ORDER BY dateCommande DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idFournisseur);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                commandes.add(extraireCommande(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return commandes;
    }
    
    /**
     * Obtient les commandes passées à une date spécifique
     */
    public List<Commande> obtenirCommandesParDate(LocalDate date) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM Commande WHERE dateCommande = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                commandes.add(extraireCommande(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return commandes;
    }
    
    /**
     * Classement des commandes par période de réception (croissant)
     */
    public List<Commande> obtenirCommandesTrieesParPeriode() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM Commande ORDER BY periodeReception ASC, dateCommande DESC";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                commandes.add(extraireCommande(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return commandes;
    }
    
    /**
     * Classement des commandes par fournisseur puis date
     */
    public List<Commande> obtenirCommandesTrieesParFournisseur() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM Commande ORDER BY idFournisseur ASC, dateCommande DESC";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                commandes.add(extraireCommande(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return commandes;
    }
    
    /**
     * Marque une commande comme reçue et met à jour les quantités en stock
     * C'est la fonction clé qui fait le lien avec ProduitDAO!
     */
    public void marquerCommandeRecue(int idCommande) {
        try {
            conn.setAutoCommit(false);
            
            // 1. Obtenir toutes les lignes de la commande
            List<LigneCommande> lignes = ligneDAO.obtenirLignesCommande(idCommande);
            
            // 2. Incrémenter le stock pour chaque produit
            for (LigneCommande ligne : lignes) {
                boolean success = produitDAO.incrementerStockAtomic(ligne.getIdProduit(), ligne.getQuantite());
                if (!success) {
                    throw new SQLException("Impossible d'incrémenter le stock pour produit " + ligne.getIdProduit());
                }
                System.out.println("  → Stock produit " + ligne.getIdProduit() + " augmenté de " + ligne.getQuantite());
            }
            
            // 3. Marquer la commande comme reçue
            String sql = "UPDATE Commande SET recu = TRUE, dateReception = ? WHERE idCommande = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDate(1, Date.valueOf(LocalDate.now()));
                pstmt.setInt(2, idCommande);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            System.out.println("✓ Commande marquée reçue et stock mis à jour!");
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            System.err.println("✗ Erreur: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }
    
    /**
     * Historique de toutes les commandes d'un produit avec détails fournisseur
     * Affiche: date commande, fournisseur (nom, tel), quantité, prix d'achat
     */
    public void afficherHistoriqueCommandesProduit(int idProduit) {
        String sql = "SELECT c.idCommande, c.dateCommande, c.recu, " +
                     "f.nom, f.prenom, f.numeroTelephone, " +
                     "lc.quantite, lc.prixAchat " +
                     "FROM LigneCommande lc " +
                     "JOIN Commande c ON lc.idCommande = c.idCommande " +
                     "JOIN Fournisseur f ON c.idFournisseur = f.idFournisseur " +
                     "WHERE lc.idProduit = ? " +
                     "ORDER BY c.dateCommande DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProduit);
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("\n=== HISTORIQUE COMMANDES PRODUIT #" + idProduit + " ===");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("Commande #" + rs.getInt("idCommande"));
                System.out.println("Date: " + rs.getDate("dateCommande"));
                System.out.println("Statut: " + (rs.getBoolean("recu") ? "✓ Reçue" : "⏳ En attente"));
                System.out.println("Fournisseur: " + rs.getString("nom") + " " + rs.getString("prenom"));
                System.out.println("Téléphone: " + rs.getString("numeroTelephone"));
                System.out.println("Quantité commandée: " + rs.getInt("quantite"));
                System.out.println("Prix d'achat unitaire: " + rs.getDouble("prixAchat") + " TND");
                System.out.println("Total ligne: " + (rs.getInt("quantite") * rs.getDouble("prixAchat")) + " TND");
            }
            if (!found) {
                System.out.println("Aucune commande trouvée pour ce produit.");
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Obtient une commande par ID
     */
    public Commande obtenirCommande(int idCommande) {
        String sql = "SELECT * FROM Commande WHERE idCommande = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCommande);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extraireCommande(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return null;
    }
    
    private Commande extraireCommande(ResultSet rs) throws SQLException {
        return new Commande(
            rs.getInt("idCommande"),
            rs.getInt("idFournisseur"),
            rs.getDate("dateCommande").toLocalDate(),
            rs.getInt("periodeReception"),
            rs.getBoolean("recu"),
            rs.getDate("dateReception") != null ? rs.getDate("dateReception").toLocalDate() : null
        );
    }
}
