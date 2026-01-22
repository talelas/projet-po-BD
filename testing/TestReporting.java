import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.List;

import dao.ReportingDAO;
import dao.ReportingDAO.PerformanceFournisseur;
import dao.ReportingDAO.RevenueParClient;
import dao.ReportingDAO.RevenueParProduit;

public class TestReporting {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/pharmacydb1";
        String username = "root";
        String password = "140406";

        LocalDate debut = LocalDate.of(2025, 1, 1);
        LocalDate fin = LocalDate.now();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            ReportingDAO reportingDAO = new ReportingDAO(conn);

            System.out.println("=== CHIFFRE D'AFFAIRES GLOBAL ===");
            double ca = reportingDAO.getChiffreAffaires(debut, fin);
            System.out.printf("CA du %s au %s: %.2f TND%n%n", debut, fin, ca);

            System.out.println("=== CA PAR PRODUIT ===");
            List<RevenueParProduit> parProduit = reportingDAO.getChiffreAffairesParProduit(debut, fin);
            for (RevenueParProduit r : parProduit) {
                System.out.printf("Produit #%d (%s): quantite=%d, CA=%.2f TND%n",
                    r.idProduit, r.nomProduit, r.quantiteVendue, r.chiffreAffaires);
            }
            System.out.println();

            System.out.println("=== CA PAR CLIENT ===");
            List<RevenueParClient> parClient = reportingDAO.getChiffreAffairesParClient(debut, fin);
            for (RevenueParClient r : parClient) {
                System.out.printf("Client #%d (%s): CA=%.2f TND%n",
                    r.idClient, r.nomClient, r.chiffreAffaires);
            }
            System.out.println();

            System.out.println("=== PERFORMANCE FOURNISSEURS ===");
            List<PerformanceFournisseur> perf = reportingDAO.getPerformanceFournisseurs(debut, fin);
            for (PerformanceFournisseur p : perf) {
                System.out.printf(
                    "Fournisseur #%d (%s %s): commandes=%d, recues=%d, taux=%.2f, montant=%.2f TND%n",
                    p.idFournisseur, p.nom, p.prenom, p.totalCommandes, p.commandesRecues,
                    p.tauxReception, p.montantTotalAchete);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
