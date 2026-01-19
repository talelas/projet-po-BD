# PHARMACIE MANAGEMENT SYSTEM - API DOCUMENTATION

## MODEL CLASSES

### class Produit
**Package:** models

**Properties:**
- int idProduit (private)
- String nomProduit (private)
- String marque (private)
- int quantite (private)
- int quantiteMinimale (private)
- double prix (private)
- double tva (private)
- String type (private)

**Constructors:**
- Produit(String nomProduit, String marque, int quantite, int quantiteMinimale, double prix, double tva, String type)
- Produit(int idProduit, String nomProduit, String marque, int quantite, int quantiteMinimale, double prix, double tva, String type)

**Getters:**
- int getId() → idProduit
- String getNom() → nomProduit
- String getMarque() → marque
- int getQuantite() → quantite
- int getQuantiteMinimale() → quantiteMinimale
- double getPrix() → prix
- double getTva() → tva
- String getType() → type

**Setters:**
- void setPrix(double nouveauPrix) → throws IllegalArgumentException if prix <= 0
- void setTva(double nouvelleTva) → throws IllegalArgumentException if tva not in [0,100]
- void setQuantiteMinimale(int qMin) → throws IllegalArgumentException if qMin < 0

**Methods:**
- boolean estEnStockCritique() → returns true if quantite <= quantiteMinimale
- void augmenterQuantite(int quantiteAjouter) → throws IllegalArgumentException if quantiteAjouter <= 0
- void diminuerQuantite(int quantiteOter) → throws IllegalArgumentException if quantiteOter <= 0 or insufficient stock
- String toString() → returns formatted product string

---

### class Client
**Package:** models

**Properties:**
- int idClient (private)
- String nom (private)
- String adresse (private)
- String email (private)
- String nTelephone (private)

**Constructors:**
- Client(String nom, String adresse, String email, String nTelephone)
- Client(int idClient, String nom, String adresse, String email, String nTelephone)

**Getters:**
- int getId() → idClient
- String getNom() → nom
- String getAdresse() → adresse
- String getEmail() → email
- String getTelephone() → nTelephone

**Setters:**
- void setNom(String nom) → throws IllegalArgumentException if nom is empty
- void setAdresse(String adresse)
- void setEmail(String email)
- void setTelephone(String nTelephone)

**Methods:**
- String toString() → returns formatted client string

---

### class Fournisseur
**Package:** models

**Properties:**
- int idFournisseur (private)
- String nom (private)
- String prenom (private)
- String numeroTelephone (private)
- String adresseEmail (private)

**Constructors:**
- Fournisseur(String nom, String prenom, String numeroTelephone, String adresseEmail)
- Fournisseur(int idFournisseur, String nom, String prenom, String numeroTelephone, String adresseEmail)

**Getters:**
- int getId() → idFournisseur
- String getNom() → nom
- String getPrenom() → prenom
- String getTelephone() → numeroTelephone
- String getEmail() → adresseEmail

**Setters:**
- void setNom(String nom) → throws IllegalArgumentException if nom is empty
- void setPrenom(String prenom)
- void setTelephone(String numeroTelephone)
- void setEmail(String adresseEmail)

**Methods:**
- String toString() → returns formatted supplier string

---

### class Employe
**Package:** models

**Properties:**
- int idEmploye (private)
- String nom (private)
- String prenom (private)
- String login (private)
- String motDePasse (private)
- String email (private)
- LocalDate dateEmbauche (private)
- boolean actif (private)

**Constructors:**
- Employe(String nom, String prenom, String login, String motDePasse, String email, LocalDate dateEmbauche)
- Employe(int idEmploye, String nom, String prenom, String login, String motDePasse, String email, LocalDate dateEmbauche, boolean actif)

**Getters:**
- int getId() → idEmploye
- String getNom() → nom
- String getPrenom() → prenom
- String getLogin() → login
- String getMotDePasse() → motDePasse
- String getEmail() → email
- LocalDate getDateEmbauche() → dateEmbauche
- boolean isActif() → actif

**Setters:**
- void setActif(boolean actif)
- void setMotDePasse(String motDePasse)

**Methods:**
- String toString() → returns formatted employee string

---

### class Commande
**Package:** models

**Properties:**
- int idCommande (private)
- int idFournisseur (private)
- LocalDate dateCommande (private)
- int periodeReception (private)
- boolean recu (private)
- LocalDate dateReception (private)

**Constructors:**
- Commande(int idFournisseur, LocalDate dateCommande, int periodeReception)
- Commande(int idCommande, int idFournisseur, LocalDate dateCommande, int periodeReception, boolean recu, LocalDate dateReception)

**Getters:**
- int getId() → idCommande
- int getIdFournisseur() → idFournisseur
- LocalDate getDateCommande() → dateCommande
- int getPeriodeReception() → periodeReception
- boolean isRecu() → recu
- LocalDate getDateReception() → dateReception

**Setters:**
- void setRecu(boolean recu)
- void setDateReception(LocalDate dateReception)

**Methods:**
- String toString() → returns formatted order string

---

### class LigneCommande
**Package:** models

**Properties:**
- int idCommande (private)
- int idProduit (private)
- int quantite (private)
- double prixAchat (private)

**Constructors:**
- LigneCommande(int idCommande, int idProduit, int quantite, double prixAchat)

**Getters:**
- int getIdCommande() → idCommande
- int getIdProduit() → idProduit
- int getQuantite() → quantite
- double getPrixAchat() → prixAchat

**Setters:**
- void setQuantite(int quantite)
- void setPrixAchat(double prixAchat)

**Methods:**
- String toString() → returns formatted order line string

---

### class Vente
**Package:** models

**Properties:**
- int idVente (private)
- LocalDate dateFacture (private)
- int idClient (private)

**Constructors:**
- Vente(int idClient, LocalDate dateFacture)
- Vente(int idVente, int idClient, LocalDate dateFacture)

**Getters:**
- int getId() → idVente
- int getIdClient() → idClient
- LocalDate getDateFacture() → dateFacture

**Setters:**
- void setDateFacture(LocalDate dateFacture)

**Methods:**
- String toString() → returns formatted sale string

---

### class LigneVente
**Package:** models

**Properties:**
- int idVente (private)
- int idProduit (private)
- int quantite (private)
- double prixUnite (private)

**Constructors:**
- LigneVente(int idVente, int idProduit, int quantite, double prixUnite)

**Getters:**
- int getIdVente() → idVente
- int getIdProduit() → idProduit
- int getQuantite() → quantite
- double getPrixUnite() → prixUnite

**Setters:**
- void setQuantite(int quantite)
- void setPrixUnite(double prixUnite)

**Methods:**
- String toString() → returns formatted sale line string

---

## DAO CLASSES

### class ProduitDAO
**Package:** dao

**Constructor:**
- ProduitDAO(Connection conn)

**Methods:**
- void ajouterProduit(Produit produit) → void
- void modifierProduit(Produit produit) → void
- Produit obtenirProduitParId(int idProduit) → Produit or null
- Produit obtenirProduitParNom(String nom) → Produit or null
- List<Produit> obtenirTousProduits() → List of all products
- void supprimerProduit(int idProduit) → void
- List<Produit> obtenirStocksCritiques() → List of products with critical stock
- boolean decrementerStockAtomic(int idProduit, int quantite) → boolean (success)
- boolean incrementerStockAtomic(int idProduit, int quantite) → boolean (success)

---

### class ClientDAO
**Package:** dao

**Constructor:**
- ClientDAO(Connection conn)

**Methods:**
- void ajouterClient(Client client) → void
- void modifierClient(Client client) → void
- Client obtenirClientParId(int idClient) → Client or null
- Client obtenirClientParNom(String nom) → Client or null
- List<Client> obtenirTousClients() → List of all clients
- void supprimerClient(int idClient) → void

---

### class FournisseurDAO
**Package:** dao

**Constructor:**
- FournisseurDAO(Connection conn)

**Methods:**
- void ajouterFournisseur(Fournisseur f) → void
- List<Fournisseur> obtenirTousFournisseurs() → List of all suppliers
- Fournisseur obtenirFournisseurParId(int idFournisseur) → Fournisseur or null
- List<Fournisseur> obtenirFournisseurParNom(String nom) → List of suppliers by name
- void modifierFournisseur(Fournisseur f) → void
- void supprimerFournisseur(int idFournisseur) → void
- void lierProduitFournisseur(int idProduit, int idFournisseur) → void
- void delierProduitFournisseur(int idProduit, int idFournisseur) → void
- List<Produit> obtenirProduitsDuFournisseur(int idFournisseur) → List of products
- List<Fournisseur> obtenirFournisseursDuProduit(int idProduit) → List of suppliers

---

### class EmployeDAO
**Package:** dao

**Constructor:**
- EmployeDAO(Connection conn)

**Methods:**
- Employe login(String login, String motDePasse) → Employe or null
- void ajouterEmploye(Employe e) → void
- void changerMotDePasse(int idEmploye, String ancienMdp, String nouveauMdp) → void
- void desactiverEmploye(int idEmploye) → void
- List<Employe> obtenirTousEmployes() → List of active employees

---

### class CommandeDAO
**Package:** dao

**Constructor:**
- CommandeDAO(Connection conn)

**Methods:**
- int ajouterCommande(Commande commande) → int (generated ID)
- void supprimerCommande(int idCommande) → void
- List<Commande> obtenirCommandesEnAttente() → List of unreceived orders
- List<Commande> obtenirCommandesParFournisseur(int idFournisseur) → List of orders by supplier
- List<Commande> obtenirCommandesParDate(LocalDate date) → List of orders by date
- List<Commande> obtenirCommandesTrieesParPeriode() → List sorted by period (ASC)
- List<Commande> obtenirCommandesTrieesParFournisseur() → List sorted by supplier
- void markerCommandeRecue(int idCommande) → void (marks received + updates stock)
- void afficherHistoriqueCommandesProduit(int idProduit) → void (prints history)
- Commande obtenirCommande(int idCommande) → Commande or null

---

### class LigneCommandeDAO
**Package:** dao

**Constructor:**
- LigneCommandeDAO(Connection conn)

**Methods:**
- void ajouterLigneCommande(LigneCommande ligne) → void
- List<LigneCommande> obtenirLignesCommande(int idCommande) → List of order lines
- void supprimerLignesCommande(int idCommande) → void

---

### class VenteDAO
**Package:** dao

**Constructor:**
- VenteDAO(Connection conn)

**Methods:**
- int ajouterVente(Vente vente) → int (generated ID)
- void supprimerVente(int idVente) → void
- List<Vente> obtenirToutesVentes() → List of all sales
- List<Vente> obtenirVentesParClient(int idClient) → List of sales by client
- List<Vente> obtenirVentesParDate(LocalDate date) → List of sales by date
- Vente obtenirVente(int idVente) → Vente or null
- void afficherFacture(int idVente) → void (prints formatted invoice)

---

### class LigneVenteDAO
**Package:** dao

**Constructor:**
- LigneVenteDAO(Connection conn)

**Methods:**
- boolean ajouterLigneVente(LigneVente ligne) → boolean (success, decrements stock atomically)
- List<LigneVente> obtenirLignesVente(int idVente) → List of sale lines
- void supprimerLignesVente(int idVente) → void

---

## DATABASE SCHEMA

**Tables:**
- Produit: idProduit (PK, AUTO_INCREMENT), nomProduit, marque, quantite, quantiteMinimale, prix, tva, type
- Client: idClient (PK, AUTO_INCREMENT), nom, adresse, email, nTelephone
- Fournisseur: idFournisseur (PK, AUTO_INCREMENT), nom, prenom, numeroTelephone, adresseEmail
- Produit_Fournisseur: idProduit (FK), idFournisseur (FK) - Junction table
- Employe: idEmploye (PK, AUTO_INCREMENT), nom, prenom, login (UNIQUE), motDePasse, email, dateEmbauche, actif (DEFAULT TRUE)
- Commande: idCommande (PK, AUTO_INCREMENT), idFournisseur (FK), dateCommande, periodeReception (INT), recu (DEFAULT FALSE), dateReception
- LigneCommande: idCommande (FK), idProduit (FK) - Composite PK, quantite, prixAchat
- Vente: idVente (PK, AUTO_INCREMENT), dateFacture, idClient (FK)
- LigneVente: idVente (FK), idProduit (FK) - Composite PK, quantite, prixUnite

**Key Features:**
- AUTO_INCREMENT on all primary keys
- Foreign Key constraints with CASCADE DELETE on junction tables
- Atomic operations for stock management (decrement on sales, increment on order receipt)
- Transaction support for critical operations
- PreparedStatements for SQL injection protection
