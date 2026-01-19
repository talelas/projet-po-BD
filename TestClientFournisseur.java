import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import dao.ClientDAO;
import dao.FournisseurDAO;
import dao.ProduitDAO;
import models.Client;
import models.Fournisseur;
import models.Produit;

/**
 * Test pour les fonctionnalités Client, Fournisseur et Junction Table produit_fournisseur
 */
public class TestClientFournisseur {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/pharmacydb1";
        String username = "root";
        String password = "140406";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Connexion réussie!\n");
            
            ClientDAO clientDAO = new ClientDAO(conn);
            FournisseurDAO fournisseurDAO = new FournisseurDAO(conn);
            ProduitDAO produitDAO = new ProduitDAO(conn);
            
            // ===== TEST 1: AJOUTER DES CLIENTS =====
            System.out.println("--- TEST 1: Ajouter des clients ---");
            Client c1 = new Client("Dupont", "123 Rue de Paris", "dupont@email.com", "0123456789");
            Client c2 = new Client("Martin", "456 Ave Lyon", "martin@email.com", "0987654321");
            clientDAO.ajouterClient(c1);
            clientDAO.ajouterClient(c2);
            System.out.println();
            
            // ===== TEST 2: AFFICHER TOUS LES CLIENTS =====
            System.out.println("--- TEST 2: Afficher tous les clients ---");
            List<Client> tousClients = clientDAO.obtenirTousClients();
            for (Client c : tousClients) {
                System.out.println(c);
            }
            System.out.println();
            
            // ===== TEST 3: RECHERCHER CLIENT PAR NOM =====
            System.out.println("--- TEST 3: Rechercher clients par nom 'Dupont' ---");
            List<Client> dupont = clientDAO.obtenirClientParNom("Dupont");
            for (Client c : dupont) {
                System.out.println(c);
            }
            System.out.println();
            
            // ===== TEST 4: MODIFIER UN CLIENT =====
            System.out.println("--- TEST 4: Modifier email du client 1 ---");
            if (!tousClients.isEmpty()) {
                Client c = tousClients.get(0);
                System.out.println("Avant: " + c);
                c.setEmail("newemail@example.com");
                clientDAO.modifierClient(c);
                Client updated = clientDAO.obtenirClientParId(c.getId());
                System.out.println("Après: " + updated);
            }
            System.out.println();
            
            // ===== TEST 5: AJOUTER DES FOURNISSEURS =====
            System.out.println("--- TEST 5: Ajouter des fournisseurs ---");
            Fournisseur f1 = new Fournisseur("Pharma", "Bernard", "0111223344", "pharma@supplier.com");
            Fournisseur f2 = new Fournisseur("Medic", "Sophie", "0555666777", "medic@supplier.com");
            fournisseurDAO.ajouterFournisseur(f1);
            fournisseurDAO.ajouterFournisseur(f2);
            System.out.println();
            
            // ===== TEST 6: AFFICHER TOUS LES FOURNISSEURS =====
            System.out.println("--- TEST 6: Afficher tous les fournisseurs ---");
            List<Fournisseur> tousFournisseurs = fournisseurDAO.obtenirTousFournisseurs();
            for (Fournisseur f : tousFournisseurs) {
                System.out.println(f);
            }
            System.out.println();
            
            // ===== TEST 7: LIER PRODUITS AUX FOURNISSEURS =====
            System.out.println("--- TEST 7: Lier produits aux fournisseurs (produit_fournisseur) ---");
            if (tousFournisseurs.size() >= 2) {
                Fournisseur first = tousFournisseurs.get(0);
                Fournisseur second = tousFournisseurs.get(1);
                // Lier produit 1 et 2 au premier fournisseur
                fournisseurDAO.lierProduitFournisseur(1, first.getId());
                fournisseurDAO.lierProduitFournisseur(2, first.getId());
                // Lier produit 3 au deuxième fournisseur
                fournisseurDAO.lierProduitFournisseur(3, second.getId());
            }
            System.out.println();
            
            // ===== TEST 8: OBTENIR PRODUITS DU FOURNISSEUR =====
            System.out.println("--- TEST 8: Obtenir produits du fournisseur 1 ---");
            if (!tousFournisseurs.isEmpty()) {
                Fournisseur f = tousFournisseurs.get(0);
                List<Produit> produitsF = fournisseurDAO.obtenirProduitsDuFournisseur(f.getId());
                System.out.println("Produits du fournisseur " + f.getNom() + ":");
                for (Produit p : produitsF) {
                    System.out.println("  - " + p.getNom() + " (id=" + p.getId() + ")");
                }
            }
            System.out.println();
            
            // ===== TEST 9: OBTENIR FOURNISSEURS DU PRODUIT =====
            System.out.println("--- TEST 9: Obtenir fournisseurs du produit 1 ---");
            List<Fournisseur> fournisseursProd = fournisseurDAO.obtenirFournisseursDuProduit(1);
            System.out.println("Fournisseurs du produit 1:");
            for (Fournisseur f : fournisseursProd) {
                System.out.println("  - " + f.getNom() + " " + f.getPrenom() + " (id=" + f.getId() + ")");
            }
            System.out.println();
            
            // ===== TEST 10: DÉLIER UN PRODUIT D'UN FOURNISSEUR =====
            System.out.println("--- TEST 10: Délier produit 2 du fournisseur 1 ---");
            if (tousFournisseurs.size() >= 1) {
                fournisseurDAO.delierProduitFournisseur(2, tousFournisseurs.get(0).getId());
                List<Produit> produitsApres = fournisseurDAO.obtenirProduitsDuFournisseur(tousFournisseurs.get(0).getId());
                System.out.println("Produits après déliaison:");
                for (Produit p : produitsApres) {
                    System.out.println("  - " + p.getNom());
                }
            }
            System.out.println();
            
            // ===== TEST 11: MODIFIER UN FOURNISSEUR =====
            System.out.println("--- TEST 11: Modifier email du fournisseur 1 ---");
            if (!tousFournisseurs.isEmpty()) {
                Fournisseur f = tousFournisseurs.get(0);
                System.out.println("Avant: " + f);
                f.setEmail("newemail@pharma.com");
                fournisseurDAO.modifierFournisseur(f);
                Fournisseur updated = fournisseurDAO.obtenirFournisseurParId(f.getId());
                System.out.println("Après: " + updated);
            }
            System.out.println();
            
            conn.close();
            System.out.println("✓ Tests Client/Fournisseur complétés avec succès!");
            
        } catch (Exception e) {
            System.out.println("✗ Erreur!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
