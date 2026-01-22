package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reporting DAO: chiffre d'affaires, revenus par produit/client et performance fournisseur.
 */
public class ReportingDAO {
    private final Connection conn;

    public ReportingDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Total chiffre d'affaires entre deux dates (incluses).
     */
    public double getChiffreAffaires(LocalDate debut, LocalDate fin) {
        String sql = "SELECT COALESCE(SUM(lv.quantite * lv.prixUnite), 0) AS ca " +
                     "FROM Vente v JOIN LigneVente lv ON v.idVente = lv.idVente " +
                     "WHERE v.dateFacture BETWEEN ? AND ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(debut));
            pstmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("ca");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur calcul CA: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Chiffre d'affaires agrégé par produit (quantite vendue + montant total).
     */
    public List<RevenueParProduit> getChiffreAffairesParProduit(LocalDate debut, LocalDate fin) {
        String sql = "SELECT lv.idProduit, p.nomProduit, " +
                     "SUM(lv.quantite) AS quantiteVendue, " +
                     "SUM(lv.quantite * lv.prixUnite) AS chiffreAffaires " +
                     "FROM Vente v " +
                     "JOIN LigneVente lv ON v.idVente = lv.idVente " +
                     "JOIN Produit p ON p.idProduit = lv.idProduit " +
                     "WHERE v.dateFacture BETWEEN ? AND ? " +
                     "GROUP BY lv.idProduit, p.nomProduit " +
                     "ORDER BY chiffreAffaires DESC";

        List<RevenueParProduit> result = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(debut));
            pstmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new RevenueParProduit(
                        rs.getInt("idProduit"),
                        rs.getString("nomProduit"),
                        rs.getInt("quantiteVendue"),
                        rs.getDouble("chiffreAffaires")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur CA par produit: " + e.getMessage());
        }
        return result;
    }

    /**
     * Chiffre d'affaires agrégé par client.
     */
    public List<RevenueParClient> getChiffreAffairesParClient(LocalDate debut, LocalDate fin) {
        String sql = "SELECT c.idClient, c.nom, " +
                     "SUM(lv.quantite * lv.prixUnite) AS chiffreAffaires " +
                     "FROM Vente v " +
                     "JOIN Client c ON c.idClient = v.idClient " +
                     "JOIN LigneVente lv ON v.idVente = lv.idVente " +
                     "WHERE v.dateFacture BETWEEN ? AND ? " +
                     "GROUP BY c.idClient, c.nom " +
                     "ORDER BY chiffreAffaires DESC";

        List<RevenueParClient> result = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(debut));
            pstmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new RevenueParClient(
                        rs.getInt("idClient"),
                        rs.getString("nom"),
                        rs.getDouble("chiffreAffaires")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur CA par client: " + e.getMessage());
        }
        return result;
    }

    /**
     * Performance des fournisseurs (commandes, reception, montant total achete) sur une periode.
     */
    public List<PerformanceFournisseur> getPerformanceFournisseurs(LocalDate debut, LocalDate fin) {
        String sql = "SELECT f.idFournisseur, f.nom, f.prenom, " +
                 "COUNT(DISTINCT c.idCommande) AS totalCommandes, " +
                 "COUNT(DISTINCT CASE WHEN c.recu THEN c.idCommande END) AS commandesRecues, " +
                 "COALESCE(SUM(lc.quantite * lc.prixAchat), 0) AS montantTotal " +
                 "FROM Fournisseur f " +
                 "LEFT JOIN Commande c ON c.idFournisseur = f.idFournisseur " +
                 "LEFT JOIN LigneCommande lc ON lc.idCommande = c.idCommande " +
                 "WHERE c.dateCommande IS NULL OR c.dateCommande BETWEEN ? AND ? " +
                 "GROUP BY f.idFournisseur, f.nom, f.prenom " +
                 "ORDER BY montantTotal DESC";

        List<PerformanceFournisseur> result = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(debut));
            pstmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int totalCmd = rs.getInt("totalCommandes");
                    int recues = rs.getInt("commandesRecues");
                    double tauxReception = totalCmd == 0 ? 0.0 : (double) recues / totalCmd;

                    result.add(new PerformanceFournisseur(
                        rs.getInt("idFournisseur"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        totalCmd,
                        recues,
                        tauxReception,
                        rs.getDouble("montantTotal")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur perf fournisseur: " + e.getMessage());
        }
        return result;
    }

    /** DTO revenu par produit. */
    public static class RevenueParProduit {
        public final int idProduit;
        public final String nomProduit;
        public final int quantiteVendue;
        public final double chiffreAffaires;

        public RevenueParProduit(int idProduit, String nomProduit, int quantiteVendue, double chiffreAffaires) {
            this.idProduit = idProduit;
            this.nomProduit = nomProduit;
            this.quantiteVendue = quantiteVendue;
            this.chiffreAffaires = chiffreAffaires;
        }
    }

    /** DTO revenu par client. */
    public static class RevenueParClient {
        public final int idClient;
        public final String nomClient;
        public final double chiffreAffaires;

        public RevenueParClient(int idClient, String nomClient, double chiffreAffaires) {
            this.idClient = idClient;
            this.nomClient = nomClient;
            this.chiffreAffaires = chiffreAffaires;
        }
    }

    /** DTO performance fournisseur. */
    public static class PerformanceFournisseur {
        public final int idFournisseur;
        public final String nom;
        public final String prenom;
        public final int totalCommandes;
        public final int commandesRecues;
        public final double tauxReception;
        public final double montantTotalAchete;

        public PerformanceFournisseur(int idFournisseur, String nom, String prenom, int totalCommandes,
                                       int commandesRecues, double tauxReception, double montantTotalAchete) {
            this.idFournisseur = idFournisseur;
            this.nom = nom;
            this.prenom = prenom;
            this.totalCommandes = totalCommandes;
            this.commandesRecues = commandesRecues;
            this.tauxReception = tauxReception;
            this.montantTotalAchete = montantTotalAchete;
        }
    }
}
