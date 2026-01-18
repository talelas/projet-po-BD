package models;

/**
 * Classe Fournisseur représentant un fournisseur de produits
 */
public class Fournisseur {
    private int idFournisseur;
    private String nom;
    private String prenom;
    private String numeroTelephone;
    private String adresseEmail;
    
    /**
     * Constructeur pour créer un nouveau fournisseur (sans ID)
     */
    public Fournisseur(String nom, String prenom, String numeroTelephone, String adresseEmail) {
        this.nom = nom;
        this.prenom = prenom;
        this.numeroTelephone = numeroTelephone;
        this.adresseEmail = adresseEmail;
    }
    
    /**
     * Constructeur avec ID (pour les fournisseurs venant de la BD)
     */
    public Fournisseur(int idFournisseur, String nom, String prenom, String numeroTelephone, String adresseEmail) {
        this.idFournisseur = idFournisseur;
        this.nom = nom;
        this.prenom = prenom;
        this.numeroTelephone = numeroTelephone;
        this.adresseEmail = adresseEmail;
    }
    
    // ===== GETTERS =====
    public int getId() { return idFournisseur; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getTelephone() { return numeroTelephone; }
    public String getEmail() { return adresseEmail; }
    
    // ===== SETTERS =====
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty())
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        this.nom = nom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public void setTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }
    
    public void setEmail(String adresseEmail) {
        this.adresseEmail = adresseEmail;
    }
    
    @Override
    public String toString() {
        return "Fournisseur{" +
                "id=" + idFournisseur +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", tel='" + numeroTelephone + '\'' +
                ", email='" + adresseEmail + '\'' +
                '}';
    }
}
