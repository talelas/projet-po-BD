package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.LigneCommande;

/**
 * DAO pour gérer les lignes de commande
 */
public class LigneCommandeDAO {
    private Connection conn;
    
    public LigneCommandeDAO(Connection conn) {
        this.conn = conn;
    }
    
    /**
     * Ajoute une ligne à une commande
     */
    public void ajouterLigneCommande(LigneCommande ligne) {
        String sql = "INSERT INTO LigneCommande (idCommande, idProduit, quantite, prixAchat) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ligne.getIdCommande());
            pstmt.setInt(2, ligne.getIdProduit());
            pstmt.setInt(3, ligne.getQuantite());
            pstmt.setDouble(4, ligne.getPrixAchat());
            pstmt.executeUpdate();
            System.out.println("✓ Ligne commande ajoutée!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Obtient toutes les lignes d'une commande
     */
    public List<LigneCommande> obtenirLignesCommande(int idCommande) {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT * FROM LigneCommande WHERE idCommande = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCommande);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lignes.add(new LigneCommande(
                    rs.getInt("idCommande"),
                    rs.getInt("idProduit"),
                    rs.getInt("quantite"),
                    rs.getDouble("prixAchat")
                ));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return lignes;
    }
    
    /**
     * Supprime toutes les lignes d'une commande (cascade delete helper)
     */
    public void supprimerLignesCommande(int idCommande) {
        String sql = "DELETE FROM LigneCommande WHERE idCommande = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCommande);
            int rows = pstmt.executeUpdate();
            System.out.println("✓ " + rows + " ligne(s) supprimée(s)");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }

    /**
     * Supprime une seule ligne d'une commande identifiée par (idCommande, idProduit)
     */
    public void supprimerLigneCommande(int idCommande, int idProduit) {
        String sql = "DELETE FROM LigneCommande WHERE idCommande = ? AND idProduit = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCommande);
            pstmt.setInt(2, idProduit);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
}
