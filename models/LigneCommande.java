package models;

/**
 * Classe LigneCommande représentant une ligne de commande (produit dans une commande)
 */
public class LigneCommande {
    private int idCommande;
    private int idProduit;
    private int quantite;
    private double prixAchat;
    
    public LigneCommande(int idCommande, int idProduit, int quantite, double prixAchat) {
        this.idCommande = idCommande;
        this.idProduit = idProduit;
        this.quantite = quantite;
        this.prixAchat = prixAchat;
    }
    
    // Getters
    public int getIdCommande() { return idCommande; }
    public int getIdProduit() { return idProduit; }
    public int getQuantite() { return quantite; }
    public double getPrixAchat() { return prixAchat; }
    
    @Override
    public String toString() {
        return "LigneCommande{idCommande=" + idCommande + ", idProduit=" + idProduit + 
               ", quantite=" + quantite + ", prixAchat=" + prixAchat + "}";
    }
}
