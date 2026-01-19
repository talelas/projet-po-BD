package models;

import java.time.LocalDate;

/**
 * Classe Employe représentant un employé de la pharmacie
 */
public class Employe {
    private int idEmploye;
    private String nom;
    private String prenom;
    private String login;
    private String motDePasse;
    private String email;
    private LocalDate dateEmbauche;
    private boolean actif;
    
    public Employe(String nom, String prenom, String login, String motDePasse, String email, LocalDate dateEmbauche) {
        this.nom = nom;
        this.prenom = prenom;
        this.login = login;
        this.motDePasse = motDePasse;
        this.email = email;
        this.dateEmbauche = dateEmbauche;
        this.actif = true;
    }
    
    public Employe(int idEmploye, String nom, String prenom, String login, String motDePasse, 
                   String email, LocalDate dateEmbauche, boolean actif) {
        this.idEmploye = idEmploye;
        this.nom = nom;
        this.prenom = prenom;
        this.login = login;
        this.motDePasse = motDePasse;
        this.email = email;
        this.dateEmbauche = dateEmbauche;
        this.actif = actif;
    }
    
    // Getters
    public int getId() { return idEmploye; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getLogin() { return login; }
    public String getMotDePasse() { return motDePasse; }
    public String getEmail() { return email; }
    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public boolean isActif() { return actif; }
    
    // Setters
    public void setActif(boolean actif) { this.actif = actif; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    
    @Override
    public String toString() {
        return "Employe{id=" + idEmploye + ", nom='" + nom + "', prenom='" + prenom + 
               "', login='" + login + "', actif=" + actif + "}";
    }
}
