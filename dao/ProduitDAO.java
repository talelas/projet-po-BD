package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.Produit;

/**
 * DAO (Data Access Object) pour la gestion des produits en base de données
 */
public class ProduitDAO {
    private Connection conn;
    
    public ProduitDAO(Connection conn) {
        this.conn = conn;
    }
    
    /**
     * Ajoute un nouveau produit à la base de données
     */
    public void ajouterProduit(Produit p) {
        String sql = "INSERT INTO Produit (nomProduit, marque, quantite, quantiteMinimale, prix, tva, type) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getNom());
            pstmt.setString(2, p.getMarque());
            pstmt.setInt(3, p.getQuantite());
            pstmt.setInt(4, p.getQuantiteMinimale());
            pstmt.setDouble(5, p.getPrix());
            pstmt.setDouble(6, p.getTva());
            pstmt.setString(7, p.getType());
            pstmt.executeUpdate();
            System.out.println("✓ Produit '" + p.getNom() + "' ajouté avec succès!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de l'ajout du produit: " + e.getMessage());
        }
    }
    
    /**
     * Récupère tous les produits actifs
     */
    public List<Produit> obtenirTousProduits() {
        return obtenirProduits("SELECT * FROM Produit ");
    }
    
    /**
     * Récupère les produits en stock critique
     */
    public List<Produit> obtenirProduitsEnStockCritique() {
        return obtenirProduits("SELECT * FROM Produit WHERE quantite <= quantiteMinimale ");
    }
    
    /**
     * Récupère un produit par ID
     */
    public Produit obtenirProduitParId(int idProduit) {
        String sql = "SELECT * FROM Produit WHERE idProduit = ? ";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProduit);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extraireProduit(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Récupère les produits par nom (search)
     */
    public List<Produit> obtenirProduitParNom(String nom) {
        String sql = "SELECT * FROM Produit WHERE nomProduit LIKE ? ";
        List<Produit> produits = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                produits.add(extraireProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return produits;
    }
    
    /**
     * Récupère les produits par type
     */
    public List<Produit> obtenirProduitParType(String type) {
        String sql = "SELECT * FROM Produit WHERE type = ? ";
        List<Produit> produits = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                produits.add(extraireProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return produits;
    }
    
    /**
     * Récupère la quantité d'un produit par ID
     */
    public int obtenirQuantite(int idProduit) {
        String sql = "SELECT quantite FROM Produit WHERE idProduit = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProduit);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantite");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Récupère le prix d'un produit par ID
     */
    public double obtenirPrix(int idProduit) {
        String sql = "SELECT prix FROM Produit WHERE idProduit = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProduit);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("prix");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Modifie le prix d'un produit
     */
    public void modifierPrix(int idProduit, double nouveauPrix) {
        if (nouveauPrix <= 0) {
            System.err.println("✗ Le prix doit être > 0");
            return;
        }
        String sql = "UPDATE Produit SET prix = ? WHERE idProduit = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, nouveauPrix);
            pstmt.setInt(2, idProduit);
            pstmt.executeUpdate();
            System.out.println("✓ Prix modifié avec succès!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Modifie la TVA d'un produit
     */
    public void modifierTva(int idProduit, double nouvelleTva) {
        if (nouvelleTva < 0 || nouvelleTva > 100) {
            System.err.println("✗ La TVA doit être entre 0 et 100");
            return;
        }
        String sql = "UPDATE Produit SET tva = ? WHERE idProduit = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, nouvelleTva);
            pstmt.setInt(2, idProduit);
            pstmt.executeUpdate();
            System.out.println("✓ TVA modifiée avec succès!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Modifie la quantité d'un produit
     */
    public void mettreAJourQuantite(int idProduit, int nouvelleQuantite) {
        if (nouvelleQuantite < 0) {
            System.err.println("✗ La quantité ne peut pas être négative");
            return;
        }
        String sql = "UPDATE Produit SET quantite = ? WHERE idProduit = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nouvelleQuantite);
            pstmt.setInt(2, idProduit);
            pstmt.executeUpdate();
            System.out.println("✓ Quantité mise à jour!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Supprime un produit de la base de données
     */
    public void supprimerProduit(int idProduit) {
        String sql = "DELETE FROM Produit WHERE idProduit = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProduit);
            pstmt.executeUpdate();
            System.out.println("✓ Produit supprimé!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Méthode interne pour récupérer une liste de produits
     */
    private List<Produit> obtenirProduits(String sql) {
        List<Produit> produits = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                produits.add(extraireProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return produits;
    }
    
    /**
     * Extrait un produit d'un ResultSet
     */
    private Produit extraireProduit(ResultSet rs) throws SQLException {
        return new Produit(
            rs.getInt("idProduit"),
            rs.getString("nomProduit"),
            rs.getString("marque"),
            rs.getInt("quantite"),
            rs.getInt("quantiteMinimale"),
            rs.getDouble("prix"),
            rs.getDouble("tva"),
            rs.getString("type")
        );
    }

    /**
     * Modifie la quantité minimale d'un produit
     */
    public void mettreAJourQuantiteMinimale(int idProduit, int nouvelleQuantiteMin) {
        if (nouvelleQuantiteMin < 0) {
            System.err.println("✗ La quantité minimale ne peut pas être négative");
            return;
        }
        String sql = "UPDATE Produit SET quantiteMinimale = ? WHERE idProduit = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nouvelleQuantiteMin);
            pstmt.setInt(2, idProduit);
            pstmt.executeUpdate();
            System.out.println("✓ Quantité minimale mise à jour!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }

    /**
     * Décrémente le stock de façon atomique et sûre côté base.
     * Retourne true si la mise à jour a réussi (stock suffisant), false sinon.
     * Évite les conflits entre plusieurs instances en laissant MySQL arbitrer.
     */
    public boolean decrementerStockAtomic(int idProduit, int quantiteADeduire) {
        if (quantiteADeduire <= 0) {
            System.err.println("✗ La quantité à déduire doit être > 0");
            return false;
        }
        String sql = "UPDATE Produit SET quantite = quantite - ? WHERE idProduit = ? AND quantite >= ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantiteADeduire);
            pstmt.setInt(2, idProduit);
            pstmt.setInt(3, quantiteADeduire);
            int rows = pstmt.executeUpdate();
            return rows > 0; // true si MAJ effectuée, false si stock insuffisant
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
            return false;
        }
    }

    /**
     * Incrémente le stock de façon atomique côté base.
     */
    public boolean incrementerStockAtomic(int idProduit, int quantiteAAjouter) {
        if (quantiteAAjouter <= 0) {
            System.err.println("✗ La quantité à ajouter doit être > 0");
            return false;
        }
        String sql = "UPDATE Produit SET quantite = quantite + ? WHERE idProduit = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantiteAAjouter);
            pstmt.setInt(2, idProduit);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
            return false;
        }
    }

    /**
     * Effectue une vente de manière atomique côté base et
     * retourne la quantité FRESHE (réelle) après l'opération.
     * Si la vente échoue (stock insuffisant), retourne la quantité actuelle.
     */
    public int vendreProduitAtomic(int idProduit, int quantiteVendue) {
        if (quantiteVendue <= 0) {
            System.err.println("✗ La quantité vendue doit être > 0");
            return obtenirQuantite(idProduit);
        }
        boolean ok = decrementerStockAtomic(idProduit, quantiteVendue);
        // Toujours relire la quantité réelle pour synchroniser l'UI
        int qteReelle = obtenirQuantite(idProduit);
        if (!ok) {
            System.out.println("⚠ Vente refusée: stock insuffisant. Quantité réelle: " + qteReelle);
        }
        return qteReelle;
    }
}
