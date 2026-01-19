package models;

import java.time.LocalDate;

/**
 * Classe Vente représentant une facture/vente à un client
 */
public class Vente {
    private int idVente;
    private LocalDate dateFacture;
    private int idClient;
    
    public Vente(int idClient, LocalDate dateFacture) {
        this.idClient = idClient;
        this.dateFacture = dateFacture;
    }
    
    public Vente(int idVente, int idClient, LocalDate dateFacture) {
        this.idVente = idVente;
        this.idClient = idClient;
        this.dateFacture = dateFacture;
    }
    
    // Getters
    public int getId() { return idVente; }
    public int getIdClient() { return idClient; }
    public LocalDate getDateFacture() { return dateFacture; }
    
    // Setters
    public void setDateFacture(LocalDate dateFacture) { this.dateFacture = dateFacture; }
    
    @Override
    public String toString() {
        return "Vente{id=" + idVente + ", idClient=" + idClient + 
               ", dateFacture=" + dateFacture + "}";
    }
}
