package dao;

import models.LigneVente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour gérer les lignes de vente
 */
public class LigneVenteDAO {
    private Connection conn;
    
    public LigneVenteDAO(Connection conn) {
        this.conn = conn;
    }
    
    /**
     * Ajoute une ligne à une vente (vend un produit)
     * Décrémente le stock du produit de manière atomique
     */
    public boolean ajouterLigneVente(LigneVente ligne) {
        try {
            conn.setAutoCommit(false);
            
            // 1. Ajouter la ligne de vente
            String sql = "INSERT INTO LigneVente (idVente, idProduit, quantite, prixUnite) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, ligne.getIdVente());
                pstmt.setInt(2, ligne.getIdProduit());
                pstmt.setInt(3, ligne.getQuantite());
                pstmt.setDouble(4, ligne.getPrixUnite());
                pstmt.executeUpdate();
            }
            
            // 2. Décrémenter le stock du produit (atomique!)
            ProduitDAO produitDAO = new ProduitDAO(conn);
            boolean stockOK = produitDAO.decrementerStockAtomic(ligne.getIdProduit(), ligne.getQuantite());
            
            if (!stockOK) {
                throw new SQLException("Stock insuffisant pour produit " + ligne.getIdProduit());
            }
            
            conn.commit();
            System.out.println("✓ Ligne vente ajoutée et stock décrémenté!");
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            System.err.println("✗ Erreur: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }
    
    /**
     * Obtient toutes les lignes d'une vente
     */
    public List<LigneVente> obtenirLignesVente(int idVente) {
        List<LigneVente> lignes = new ArrayList<>();
        String sql = "SELECT * FROM LigneVente WHERE idVente = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idVente);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lignes.add(new LigneVente(
                    rs.getInt("idVente"),
                    rs.getInt("idProduit"),
                    rs.getInt("quantite"),
                    rs.getDouble("prixUnite")
                ));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return lignes;
    }
    
    /**
     * Supprime toutes les lignes d'une vente (helper pour cascade delete)
     */
    public void supprimerLignesVente(int idVente) {
        String sql = "DELETE FROM LigneVente WHERE idVente = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idVente);
            int rows = pstmt.executeUpdate();
            System.out.println("✓ " + rows + " ligne(s) supprimée(s)");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
}
