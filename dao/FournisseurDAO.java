package dao;

import models.Fournisseur;
import models.Produit;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des fournisseurs et de la table produit_fournisseur
 */
public class FournisseurDAO {
    private Connection conn;
    
    public FournisseurDAO(Connection conn) {
        this.conn = conn;
    }
    
    /**
     * Ajoute un nouveau fournisseur
     */
    public void ajouterFournisseur(Fournisseur f) {
        String sql = "INSERT INTO Fournisseur (nom, prenom, numeroTelephone, adresseEmail) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, f.getNom());
            pstmt.setString(2, f.getPrenom());
            pstmt.setString(3, f.getTelephone());
            pstmt.setString(4, f.getEmail());
            pstmt.executeUpdate();
            System.out.println("✓ Fournisseur '" + f.getNom() + " " + f.getPrenom() + "' ajouté!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Récupère tous les fournisseurs
     */
    public List<Fournisseur> obtenirTousFournisseurs() {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM Fournisseur";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                fournisseurs.add(extraireFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return fournisseurs;
    }
    
    /**
     * Récupère un fournisseur par ID
     */
    public Fournisseur obtenirFournisseurParId(int idFournisseur) {
        String sql = "SELECT * FROM Fournisseur WHERE idFournisseur = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idFournisseur);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extraireFournisseur(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Recherche fournisseurs par nom
     */
    public List<Fournisseur> obtenirFournisseurParNom(String nom) {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM Fournisseur WHERE nom LIKE ? OR prenom LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            pstmt.setString(2, "%" + nom + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                fournisseurs.add(extraireFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return fournisseurs;
    }
    
    /**
     * Modifie les informations d'un fournisseur
     */
    public void modifierFournisseur(Fournisseur f) {
        String sql = "UPDATE Fournisseur SET nom = ?, prenom = ?, numeroTelephone = ?, adresseEmail = ? WHERE idFournisseur = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, f.getNom());
            pstmt.setString(2, f.getPrenom());
            pstmt.setString(3, f.getTelephone());
            pstmt.setString(4, f.getEmail());
            pstmt.setInt(5, f.getId());
            pstmt.executeUpdate();
            System.out.println("✓ Fournisseur modifié!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Supprime un fournisseur
     */
    public void supprimerFournisseur(int idFournisseur) {
        String sql = "DELETE FROM Fournisseur WHERE idFournisseur = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idFournisseur);
            pstmt.executeUpdate();
            System.out.println("✓ Fournisseur supprimé!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    // ===== GESTION TABLE PRODUIT_FOURNISSEUR (Junction Table) =====
    
    /**
     * Associe un produit à un fournisseur dans la table produit_fournisseur
     */
    public void lierProduitFournisseur(int idProduit, int idFournisseur) {
        String sql = "INSERT INTO produit_fournisseur (idProduit, idFournisseur) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProduit);
            pstmt.setInt(2, idFournisseur);
            pstmt.executeUpdate();
            System.out.println("✓ Produit " + idProduit + " lié au fournisseur " + idFournisseur);
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Supprime l'association entre un produit et un fournisseur
     */
    public void delierProduitFournisseur(int idProduit, int idFournisseur) {
        String sql = "DELETE FROM produit_fournisseur WHERE idProduit = ? AND idFournisseur = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProduit);
            pstmt.setInt(2, idFournisseur);
            pstmt.executeUpdate();
            System.out.println("✓ Produit " + idProduit + " délié du fournisseur " + idFournisseur);
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Récupère tous les produits fournis par un fournisseur donné
     */
    public List<Produit> obtenirProduitsDuFournisseur(int idFournisseur) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.* FROM Produit p " +
                     "JOIN produit_fournisseur pf ON p.idProduit = pf.idProduit " +
                     "WHERE pf.idFournisseur = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idFournisseur);
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
     * Récupère tous les fournisseurs d'un produit donné
     */
    public List<Fournisseur> obtenirFournisseursDuProduit(int idProduit) {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT f.* FROM Fournisseur f " +
                     "JOIN produit_fournisseur pf ON f.idFournisseur = pf.idFournisseur " +
                     "WHERE pf.idProduit = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProduit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                fournisseurs.add(extraireFournisseur(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return fournisseurs;
    }
    
    private Fournisseur extraireFournisseur(ResultSet rs) throws SQLException {
        return new Fournisseur(
            rs.getInt("idFournisseur"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("numeroTelephone"),
            rs.getString("adresseEmail")
        );
    }
    
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
}
