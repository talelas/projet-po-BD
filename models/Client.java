package models;

/**
 * Classe Client représentant un client de la pharmacie
 */
public class Client {
    private int idClient;
    private String nom;
    private String adresse;
    private String email;
    private String nTelephone;
    
    /**
     * Constructeur pour créer un nouveau client (sans ID)
     */
    public Client(String nom, String adresse, String email, String nTelephone) {
        this.nom = nom;
        this.adresse = adresse;
        this.email = email;
        this.nTelephone = nTelephone;
    }
    
    /**
     * Constructeur avec ID (pour les clients venant de la BD)
     */
    public Client(int idClient, String nom, String adresse, String email, String nTelephone) {
        this.idClient = idClient;
        this.nom = nom;
        this.adresse = adresse;
        this.email = email;
        this.nTelephone = nTelephone;
    }
    
    // ===== GETTERS =====
    public int getId() { return idClient; }
    public String getNom() { return nom; }
    public String getAdresse() { return adresse; }
    public String getEmail() { return email; }
    public String getTelephone() { return nTelephone; }
    
    // ===== SETTERS =====
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty())
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        this.nom = nom;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setTelephone(String nTelephone) {
        this.nTelephone = nTelephone;
    }
    
    @Override
    public String toString() {
        return "Client{" +
                "id=" + idClient +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", email='" + email + '\'' +
                ", tel='" + nTelephone + '\'' +
                '}';
    }
}
