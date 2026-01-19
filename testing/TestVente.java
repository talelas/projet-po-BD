import dao.*;
import models.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Tests complets du système de ventes
 */
public class TestVente {
    
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/pharmacydb1";
        String user = "root";
        String password = "140406";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✓ Connexion établie!\n");
            
            // DAOs
            ClientDAO clientDAO = new ClientDAO(conn);
            ProduitDAO produitDAO = new ProduitDAO(conn);
            VenteDAO venteDAO = new VenteDAO(conn);
            LigneVenteDAO ligneDAO = new LigneVenteDAO(conn);
            
            // ===== TEST 1: PRÉPARER DONNÉES =====
            System.out.println("\n========== TEST 1: PRÉPARATION DONNÉES ==========");
            
            // Créer des clients
            Client c1 = new Client("Samir Ben ali", "Tunis", "samir@email.com", "98123456");
            clientDAO.ajouterClient(c1);
            int idC1 = clientDAO.obtenirTousClients().get(0).getId();
            System.out.println("Client créé avec ID: " + idC1);
            
            Client c2 = new Client("Amira Khaled", "Sfax", "amira@email.com", "97654321");
            clientDAO.ajouterClient(c2);
            int idC2 = clientDAO.obtenirTousClients().get(1).getId();
            System.out.println("Client créé avec ID: " + idC2);
            
            // Créer des produits
            Produit p1 = new Produit("Paracétamol 500mg", "Doliprane", 145, 20, 15.50, 20.0, "Médicament");
            produitDAO.ajouterProduit(p1);
            int idP1 = produitDAO.obtenirTousProduits().get(0).getId();
            System.out.println("Produit créé avec ID: " + idP1 + " (stock: 145)");
            
            Produit p2 = new Produit("Ibuprofène 400mg", "Advil", 63, 15, 22.00, 20.0, "Médicament");
            produitDAO.ajouterProduit(p2);
            int idP2 = produitDAO.obtenirTousProduits().get(1).getId();
            System.out.println("Produit créé avec ID: " + idP2 + " (stock: 63)");
            
            
            // ===== TEST 2: CRÉER VENTES =====
            System.out.println("\n\n========== TEST 2: CRÉER VENTES ==========");
            
            // Vente 1
            Vente v1 = new Vente(idC1, LocalDate.now());
            int idV1 = venteDAO.ajouterVente(v1);
            
            // Ajouter produits à vente 1 (et décrémenter stock)
            System.out.println("\n--- Vente #" + idV1 + " pour client #" + idC1 + " ---");
            System.out.println("État AVANT vente:");
            Produit avantP1 = produitDAO.obtenirProduitParId(idP1);
            Produit avantP2 = produitDAO.obtenirProduitParId(idP2);
            System.out.println("  Stock Paracétamol: " + avantP1.getQuantite());
            System.out.println("  Stock Ibuprofène: " + avantP2.getQuantite());
            
            ligneDAO.ajouterLigneVente(new LigneVente(idV1, idP1, 10, 18.60)); // 10 Paracétamol
            ligneDAO.ajouterLigneVente(new LigneVente(idV1, idP2, 5, 26.40)); // 5 Ibuprofène
            
            System.out.println("\nÉtat APRÈS vente:");
            Produit apresP1 = produitDAO.obtenirProduitParId(idP1);
            Produit apresP2 = produitDAO.obtenirProduitParId(idP2);
            System.out.println("  Stock Paracétamol: " + avantP1.getQuantite() + " → " + apresP1.getQuantite() + 
                               " (-" + (avantP1.getQuantite() - apresP1.getQuantite()) + ")");
            System.out.println("  Stock Ibuprofène: " + avantP2.getQuantite() + " → " + apresP2.getQuantite() + 
                               " (-" + (avantP2.getQuantite() - apresP2.getQuantite()) + ")");
            
            // Vente 2
            Vente v2 = new Vente(idC2, LocalDate.now());
            int idV2 = venteDAO.ajouterVente(v2);
            
            System.out.println("\n--- Vente #" + idV2 + " pour client #" + idC2 + " ---");
            ligneDAO.ajouterLigneVente(new LigneVente(idV2, idP1, 20, 18.60)); // 20 Paracétamol
            ligneDAO.ajouterLigneVente(new LigneVente(idV2, idP2, 3, 26.40)); // 3 Ibuprofène
            
            
            // ===== TEST 3: VOIR VENTES =====
            System.out.println("\n\n========== TEST 3: VOIR TOUTES LES VENTES ==========");
            List<Vente> toutesVentes = venteDAO.obtenirToutesVentes();
            System.out.println("Nombre total de ventes: " + toutesVentes.size());
            for (Vente v : toutesVentes) {
                System.out.println("  - Vente #" + v.getId() + " | Client #" + v.getIdClient() + 
                                   " | Date: " + v.getDateFacture());
            }
            
            
            // ===== TEST 4: VENTES PAR CLIENT =====
            System.out.println("\n\n========== TEST 4: VENTES PAR CLIENT ==========");
            List<Vente> ventesC1 = venteDAO.obtenirVentesParClient(idC1);
            System.out.println("Ventes du client #" + idC1 + ": " + ventesC1.size());
            for (Vente v : ventesC1) {
                System.out.println("  - Vente #" + v.getId() + " du " + v.getDateFacture());
            }
            
            
            // ===== TEST 5: VENTES PAR DATE =====
            System.out.println("\n\n========== TEST 5: VENTES D'AUJOURD'HUI ==========");
            List<Vente> ventesAujourdhui = venteDAO.obtenirVentesParDate(LocalDate.now());
            System.out.println("Ventes d'aujourd'hui: " + ventesAujourdhui.size());
            
            
            // ===== TEST 6: AFFICHER FACTURES =====
            System.out.println("\n\n========== TEST 6: FACTURES DÉTAILLÉES ==========");
            venteDAO.afficherFacture(idV1);
            venteDAO.afficherFacture(idV2);
            
            
            System.out.println("\n✓✓✓ TOUS LES TESTS TERMINÉS! ✓✓✓");
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
