package dao;

import models.Vente;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour gérer les ventes/factures
 */
public class VenteDAO {
    private Connection conn;
    private LigneVenteDAO ligneDAO;
    private ProduitDAO produitDAO;
    
    public VenteDAO(Connection conn) {
        this.conn = conn;
        this.ligneDAO = new LigneVenteDAO(conn);
        this.produitDAO = new ProduitDAO(conn);
    }
    
    /**
     * Ajoute une vente et retourne son ID généré
     */
    public int ajouterVente(Vente vente) {
        String sql = "INSERT INTO Vente (idClient, dateFacture) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, vente.getIdClient());
            pstmt.setDate(2, Date.valueOf(vente.getDateFacture()));
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.println("✓ Vente créée avec ID: " + id);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Supprime une vente (avec ses lignes)
     */
    public void supprimerVente(int idVente) {
        try {
            conn.setAutoCommit(false);
            
            // Supprimer les lignes d'abord
            ligneDAO.supprimerLignesVente(idVente);
            
            // Puis la vente
            String sql = "DELETE FROM Vente WHERE idVente = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idVente);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            System.out.println("✓ Vente supprimée!");
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            System.err.println("✗ Erreur: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }
    
    /**
     * Obtient toutes les ventes
     */
    public List<Vente> obtenirToutesVentes() {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM Vente ORDER BY dateFacture DESC";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ventes.add(extraireVente(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return ventes;
    }
    
    /**
     * Obtient les ventes d'un client
     */
    public List<Vente> obtenirVentesParClient(int idClient) {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM Vente WHERE idClient = ? ORDER BY dateFacture DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idClient);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ventes.add(extraireVente(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return ventes;
    }
    
    /**
     * Obtient les ventes d'une date spécifique
     */
    public List<Vente> obtenirVentesParDate(LocalDate date) {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM Vente WHERE dateFacture = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ventes.add(extraireVente(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return ventes;
    }
    
    /**
     * Obtient une vente par ID
     */
    public Vente obtenirVente(int idVente) {
        String sql = "SELECT * FROM Vente WHERE idVente = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idVente);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extraireVente(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Affiche une facture complète avec détails client et produits
     */
    public void afficherFacture(int idVente) {
        String sql = "SELECT v.idVente, v.dateFacture, c.nom, c.nTelephone, " +
                     "lv.idProduit, p.nomProduit, lv.quantite, lv.prixUnite " +
                     "FROM Vente v " +
                     "JOIN Client c ON v.idClient = c.idClient " +
                     "LEFT JOIN LigneVente lv ON v.idVente = lv.idVente " +
                     "LEFT JOIN Produit p ON lv.idProduit = p.idProduit " +
                     "WHERE v.idVente = ? " +
                     "ORDER BY lv.idProduit";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idVente);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("\n╔════════════════════════════════════════════════════════╗");
                System.out.println("║              FACTURE DE VENTE #" + String.format("%04d", rs.getInt("idVente")) + "                ║");
                System.out.println("╚════════════════════════════════════════════════════════╝");
                System.out.println("Date: " + rs.getDate("dateFacture"));
                System.out.println("Client: " + rs.getString("nom"));
                System.out.println("Téléphone: " + rs.getString("nTelephone"));
                System.out.println("┌────────────────────────────────────────────────────────┐");
                System.out.println("│ PRODUITS");
                System.out.println("├────────────────────────────────────────────────────────┤");
                
                double totalFacture = 0;
                do {
                    if (rs.getInt("idProduit") > 0) {
                        String nomProduit = rs.getString("nomProduit");
                        int quantite = rs.getInt("quantite");
                        double prixUnite = rs.getDouble("prixUnite");
                        double sousTotal = quantite * prixUnite;
                        totalFacture += sousTotal;
                        
                        System.out.printf("│ %-30s %3d × %.2f = %.2f TND%n", 
                            nomProduit, quantite, prixUnite, sousTotal);
                    }
                } while (rs.next());
                
                System.out.println("├────────────────────────────────────────────────────────┤");
                System.out.printf("│ TOTAL: %.2f TND%n", totalFacture);
                System.out.println("└────────────────────────────────────────────────────────┘\n");
            } else {
                System.out.println("Vente #" + idVente + " non trouvée");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    private Vente extraireVente(ResultSet rs) throws SQLException {
        return new Vente(
            rs.getInt("idVente"),
            rs.getInt("idClient"),
            rs.getDate("dateFacture").toLocalDate()
        );
    }
}
