import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import dao.CommandeDAO;
import dao.EmployeDAO;
import dao.FournisseurDAO;
import dao.LigneCommandeDAO;
import dao.ProduitDAO;
import models.Commande;
import models.Employe;
import models.Fournisseur;
import models.LigneCommande;
import models.Produit;

/**
 * Tests complets du système de commandes et d'authentification
 */
public class TestCommandes {
    
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/pharmacydb1";
        String user = "root";
        String password = "140406";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✓ Connexion établie!\n");
            
            // DAOs
            EmployeDAO employeDAO = new EmployeDAO(conn);
            FournisseurDAO fournisseurDAO = new FournisseurDAO(conn);
            ProduitDAO produitDAO = new ProduitDAO(conn);
            CommandeDAO commandeDAO = new CommandeDAO(conn);
            LigneCommandeDAO ligneDAO = new LigneCommandeDAO(conn);
            
            // ===== TEST 1: SYSTÈME DE LOGIN =====
            System.out.println("\n========== TEST 1: SYSTÈME D'AUTHENTIFICATION ==========");
            
            // Créer un employé
            Employe emp = new Employe("Alami", "Fatima", "falami", "pass123", 
                                      "f.alami@pharma.ma", LocalDate.now());
            employeDAO.ajouterEmploye(emp);
            
            // Test login valide
            System.out.println("\n--- Tentative de login valide ---");
            Employe logged = employeDAO.login("falami", "pass123");
            if (logged != null) {
                System.out.println("Bienvenue " + logged.getPrenom() + " " + logged.getNom());
            }
            
            // Test login invalide
            System.out.println("\n--- Tentative de login invalide ---");
            Employe failed = employeDAO.login("falami", "wrongpass");
            
            
            // ===== TEST 2: CRÉER FOURNISSEUR ET PRODUITS =====
            System.out.println("\n\n========== TEST 2: PRÉPARATION DONNÉES ==========");
            
            Fournisseur f1 = new Fournisseur("Bennani", "Said", "0522123456", "s.bennani@medsupply.ma");
            fournisseurDAO.ajouterFournisseur(f1);
            int idF1 = fournisseurDAO.obtenirTousFournisseurs().get(0).getId();
            System.out.println("Fournisseur créé avec ID: " + idF1);
            
            Fournisseur f2 = new Fournisseur("Alaoui", "Amina", "0537987654", "a.alaoui@pharmadistrib.ma");
            fournisseurDAO.ajouterFournisseur(f2);
            int idF2 = fournisseurDAO.obtenirTousFournisseurs().get(1).getId();
            System.out.println("Fournisseur créé avec ID: " + idF2);
            
            Produit p1 = new Produit("Paracétamol 500mg", "Doliprane", 10, 20, 15.50, 20.0, "Médicament");
            produitDAO.ajouterProduit(p1);
            int idP1 = produitDAO.obtenirTousProduits().get(0).getId();
            System.out.println("Produit créé avec ID: " + idP1 + " (stock initial: 10)");
            
            Produit p2 = new Produit("Ibuprofène 400mg", "Advil", 5, 15, 22.00, 20.0, "Médicament");
            produitDAO.ajouterProduit(p2);
            int idP2 = produitDAO.obtenirTousProduits().get(1).getId();
            System.out.println("Produit créé avec ID: " + idP2 + " (stock initial: 5)");
            
            
            // ===== TEST 3: CRÉER COMMANDES =====
            System.out.println("\n\n========== TEST 3: CRÉER COMMANDES ==========");
            
            // Commande 1 pour fournisseur 1
            Commande cmd1 = new Commande(idF1, LocalDate.now().minusDays(5), 7);
            int idCmd1 = commandeDAO.ajouterCommande(cmd1);
            
            // Ajouter lignes à commande 1
            ligneDAO.ajouterLigneCommande(new LigneCommande(idCmd1, idP1, 50, 12.00)); // 50 Paracétamol
            ligneDAO.ajouterLigneCommande(new LigneCommande(idCmd1, idP2, 30, 18.00)); // 30 Ibuprofène
            
            // Commande 2 pour fournisseur 2
            Commande cmd2 = new Commande(idF2, LocalDate.now().minusDays(3), 5);
            int idCmd2 = commandeDAO.ajouterCommande(cmd2);
            
            ligneDAO.ajouterLigneCommande(new LigneCommande(idCmd2, idP1, 100, 11.50)); // 100 Paracétamol
            
            // Commande 3 (plus récente)
            Commande cmd3 = new Commande(idF1, LocalDate.now(), 10);
            int idCmd3 = commandeDAO.ajouterCommande(cmd3);
            
            ligneDAO.ajouterLigneCommande(new LigneCommande(idCmd3, idP2, 40, 17.50)); // 40 Ibuprofène
            
            
            // ===== TEST 4: VOIR COMMANDES EN ATTENTE =====
            System.out.println("\n\n========== TEST 4: COMMANDES EN ATTENTE ==========");
            List<Commande> attente = commandeDAO.obtenirCommandesEnAttente();
            System.out.println("Nombre de commandes en attente: " + attente.size());
            for (Commande c : attente) {
                System.out.println("  - Cmd #" + c.getId() + " | Fournisseur #" + c.getIdFournisseur() + 
                                   " | Date: " + c.getDateCommande() + " | Période: " + c.getPeriodeReception() + " jours");
            }
            
            
            // ===== TEST 5: COMMANDES PAR FOURNISSEUR =====
            System.out.println("\n\n========== TEST 5: COMMANDES PAR FOURNISSEUR ==========");
            List<Commande> cmdsF1 = commandeDAO.obtenirCommandesParFournisseur(idF1);
            System.out.println("Commandes du fournisseur #" + idF1 + ": " + cmdsF1.size());
            for (Commande c : cmdsF1) {
                System.out.println("  - Cmd #" + c.getId() + " du " + c.getDateCommande());
            }
            
            
            // ===== TEST 6: COMMANDES PAR DATE =====
            System.out.println("\n\n========== TEST 6: COMMANDES PAR DATE ==========");
            List<Commande> cmdsDate = commandeDAO.obtenirCommandesParDate(LocalDate.now());
            System.out.println("Commandes d'aujourd'hui: " + cmdsDate.size());
            
            
            // ===== TEST 7: CLASSEMENT PAR PÉRIODE =====
            System.out.println("\n\n========== TEST 7: CLASSEMENT PAR PÉRIODE DE RÉCEPTION ==========");
            List<Commande> triPeriode = commandeDAO.obtenirCommandesTrieesParPeriode();
            for (Commande c : triPeriode) {
                System.out.println("  - Cmd #" + c.getId() + " | Période: " + c.getPeriodeReception() + 
                                   " jours | Date: " + c.getDateCommande());
            }
            
            
            // ===== TEST 8: CLASSEMENT PAR FOURNISSEUR =====
            System.out.println("\n\n========== TEST 8: CLASSEMENT PAR FOURNISSEUR ==========");
            List<Commande> triFournisseur = commandeDAO.obtenirCommandesTrieesParFournisseur();
            for (Commande c : triFournisseur) {
                System.out.println("  - Fournisseur #" + c.getIdFournisseur() + " | Cmd #" + c.getId() + 
                                   " | Date: " + c.getDateCommande());
            }
            
            
            // ===== TEST 9: MARQUER COMMANDE REÇUE (LE PLUS IMPORTANT!) =====
            System.out.println("\n\n========== TEST 9: RÉCEPTION COMMANDE & MAJ STOCK ==========");
            
            System.out.println("--- État AVANT réception ---");
            Produit avant1 = produitDAO.obtenirProduitParId(idP1);
            Produit avant2 = produitDAO.obtenirProduitParId(idP2);
            System.out.println("Stock Paracétamol #" + idP1 + ": " + avant1.getQuantite());
            System.out.println("Stock Ibuprofène #" + idP2 + ": " + avant2.getQuantite());
            
            System.out.println("\n--- Réception de la commande #" + idCmd1 + " ---");
            System.out.println("Cette commande contient:");
            List<LigneCommande> lignesCmd1 = ligneDAO.obtenirLignesCommande(idCmd1);
            for (LigneCommande lc : lignesCmd1) {
                System.out.println("  → Produit #" + lc.getIdProduit() + ": " + lc.getQuantite() + " unités @ " + lc.getPrixAchat() + " TND");
            }
            
            commandeDAO.marquerCommandeRecue(idCmd1);
            
            System.out.println("\n--- État APRÈS réception ---");
            Produit apres1 = produitDAO.obtenirProduitParId(idP1);
            Produit apres2 = produitDAO.obtenirProduitParId(idP2);
            System.out.println("Stock Paracétamol #" + idP1 + ": " + avant1.getQuantite() + " → " + apres1.getQuantite() + 
                               " (+" + (apres1.getQuantite() - avant1.getQuantite()) + ")");
            System.out.println("Stock Ibuprofène #" + idP2 + ": " + avant2.getQuantite() + " → " + apres2.getQuantite() + 
                               " (+" + (apres2.getQuantite() - avant2.getQuantite()) + ")");
            
            
            // ===== TEST 10: HISTORIQUE COMMANDES D'UN PRODUIT =====
            System.out.println("\n\n========== TEST 10: HISTORIQUE PRODUIT AVEC FOURNISSEURS ==========");
            commandeDAO.afficherHistoriqueCommandesProduit(idP1);
            
            
            // ===== TEST 11: SUPPRIMER COMMANDE =====
            System.out.println("\n\n========== TEST 11: SUPPRESSION COMMANDE ==========");
            System.out.println("Commandes avant suppression: " + commandeDAO.obtenirCommandesEnAttente().size());
            commandeDAO.supprimerCommande(idCmd3);
            System.out.println("Commandes après suppression: " + commandeDAO.obtenirCommandesEnAttente().size());
            
            
            System.out.println("\n\n✓✓✓ TOUS LES TESTS TERMINÉS! ✓✓✓");
            
        } catch (SQLException e) {
            System.err.println("✗ Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
