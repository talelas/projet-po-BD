package models;

/**
 * Classe LigneVente représentant une ligne de vente (produit vendu)
 */
public class LigneVente {
    private int idVente;
    private int idProduit;
    private int quantite;
    private double prixUnite;
    
    public LigneVente(int idVente, int idProduit, int quantite, double prixUnite) {
        this.idVente = idVente;
        this.idProduit = idProduit;
        this.quantite = quantite;
        this.prixUnite = prixUnite;
    }
    
    // Getters
    public int getIdVente() { return idVente; }
    public int getIdProduit() { return idProduit; }
    public int getQuantite() { return quantite; }
    public double getPrixUnite() { return prixUnite; }
    
    // Setters
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public void setPrixUnite(double prixUnite) { this.prixUnite = prixUnite; }
    
    @Override
    public String toString() {
        return "LigneVente{idVente=" + idVente + ", idProduit=" + idProduit + 
               ", quantite=" + quantite + ", prixUnite=" + prixUnite + "}";
    }
}
