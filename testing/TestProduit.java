import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import dao.ProduitDAO;
import models.Produit;

/**
 * Classe de test pour les fonctionnalités du Produit et ProduitDAO
 */
public class TestProduit {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/pharmacydb1";
        String username = "root";
        String password = "140406";
        
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Connexion réussie!\n");
            
            // Create DAO
            ProduitDAO dao = new ProduitDAO(conn);
            
            // ===== TEST 1: Ajouter des produits =====
            System.out.println("--- TEST 1: Ajouter des produits ---");
            Produit p1 = new Produit("Paracétamol", "Doliprane", 50, 10, 2.50, 5.5, "Analgésique");
            Produit p2 = new Produit("Amoxicilline", "Amoxil", 30, 5, 4.00, 5.5, "Antibiotique");
            Produit p3 = new Produit("Vitamine C", "Redoxon", 100, 20, 1.50, 5.5, "Vitamine");
            
            dao.ajouterProduit(p1);
            dao.ajouterProduit(p2);
            dao.ajouterProduit(p3);
            System.out.println();
            
            // ===== TEST 2: Afficher tous les produits =====
            System.out.println("--- TEST 2: Afficher tous les produits ---");
            List<Produit> tous = dao.obtenirTousProduits();
            for (Produit p : tous) {
                System.out.println(p);
            }
            System.out.println();
            
            // ===== TEST 3: Rechercher par type =====
            System.out.println("--- TEST 3: Produits de type 'Antibiotique' ---");
            List<Produit> antibios = dao.obtenirProduitParType("Antibiotique");
            for (Produit p : antibios) {
                System.out.println(p);
            }
            System.out.println();
            
            // ===== TEST 4: Récupérer par ID et modifier le prix =====
            System.out.println("--- TEST 4: Modifier le prix du produit 1 ---");
            Produit p = dao.obtenirProduitParId(1);
            if (p != null) {
                System.out.println("Avant: " + p);
                dao.modifierPrix(1, 3.99);
                p = dao.obtenirProduitParId(1);
                System.out.println("Après: " + p);
            }
            System.out.println();
            
            // ===== TEST 5: Modifier la TVA =====
            System.out.println("--- TEST 5: Modifier la TVA du produit 2 ---");
            dao.modifierTva(2, 7.0);
            System.out.println();
            
            // ===== TEST 6: Tester le stock critique =====
            System.out.println("--- TEST 6: Produits en stock critique ---");
            // Diminuer le stock du produit 2 pour le rendre critique
            dao.mettreAJourQuantite(2, 3);  // Quantité minima est 5
            List<Produit> critiques = dao.obtenirProduitsEnStockCritique();
            System.out.println("Nombre de produits en stock critique: " + critiques.size());
            for (Produit prod : critiques) {
                System.out.println(prod);
            }
            System.out.println();
            
            // ===== TEST 7: Tester augmenter/diminuer quantité =====
            System.out.println("--- TEST 7: Test augmenter et diminuer quantité ---");
            Produit prod = dao.obtenirProduitParId(1);
            System.out.println("Quantité initiale: " + prod.getQuantite());
            prod.augmenterQuantite(20);
            System.out.println("Après augmentation de 20: " + prod.getQuantite());
            prod.diminuerQuantite(10);
            System.out.println("Après diminution de 10: " + prod.getQuantite());
            System.out.println();
            
            // ===== TEST 8: Recherche par nom =====
            System.out.println("--- TEST 8: Recherche par nom 'Vitamine' ---");
            List<Produit> resultats = dao.obtenirProduitParNom("Vitamine");
            for (Produit prod2 : resultats) {
                System.out.println(prod2);
            }
            System.out.println();
            
            // ===== TEST 9: Récupérer quantité et prix =====
            System.out.println("--- TEST 9: Quantité et prix du produit 1 ---");
            int qte = dao.obtenirQuantite(1);
            double prix = dao.obtenirPrix(1);
            System.out.println("Quantité: " + qte);
            System.out.println("Prix: " + prix);
            System.out.println();

            // ===== TEST 10: Décrément atomique (concurrency-safe) =====
            System.out.println("--- TEST 10: Décrément atomique du stock (produit 1, -5) ---");
            boolean ok = dao.decrementerStockAtomic(1, 5);
            System.out.println("Décrément réussi? " + ok);
            System.out.println("Quantité après: " + dao.obtenirQuantite(1));
            System.out.println();
            
            conn.close();
            System.out.println("✓ Tests complétés avec succès!");
            
        } catch (Exception e) {
            System.out.println("✗ Erreur!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
