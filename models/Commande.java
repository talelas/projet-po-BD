package models;

import java.time.LocalDate;

/**
 * Classe Commande représentant une commande passée à un fournisseur
 */
public class Commande {
    private int idCommande;
    private int idFournisseur;
    private LocalDate dateCommande;
    private int periodeReception; // en jours
    private boolean recu;
    private LocalDate dateReception;
    
    public Commande(int idFournisseur, LocalDate dateCommande, int periodeReception) {
        this.idFournisseur = idFournisseur;
        this.dateCommande = dateCommande;
        this.periodeReception = periodeReception;
        this.recu = false;
    }
    
    public Commande(int idCommande, int idFournisseur, LocalDate dateCommande, 
                    int periodeReception, boolean recu, LocalDate dateReception) {
        this.idCommande = idCommande;
        this.idFournisseur = idFournisseur;
        this.dateCommande = dateCommande;
        this.periodeReception = periodeReception;
        this.recu = recu;
        this.dateReception = dateReception;
    }
    
    // Getters
    public int getId() { return idCommande; }
    public int getIdFournisseur() { return idFournisseur; }
    public LocalDate getDateCommande() { return dateCommande; }
    public int getPeriodeReception() { return periodeReception; }
    public boolean isRecu() { return recu; }
    public LocalDate getDateReception() { return dateReception; }
    
    // Setters
    public void setRecu(boolean recu) { this.recu = recu; }
    public void setDateReception(LocalDate dateReception) { this.dateReception = dateReception; }
    
    @Override
    public String toString() {
        return "Commande{id=" + idCommande + ", idFournisseur=" + idFournisseur + 
               ", dateCommande=" + dateCommande + ", periodeReception=" + periodeReception + 
               " jours, recu=" + recu + ", dateReception=" + dateReception + "}";
    }
}
