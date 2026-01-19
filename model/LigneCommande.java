package model;

public class LigneCommande {
    private final int idCommande;
    private final int idProduit;
    private final int quantite;
    private final double prixAchat;

    public LigneCommande(int idCommande, int idProduit, int quantite, double prixAchat) {
        this.idCommande = idCommande;
        this.idProduit = idProduit;
        this.quantite = quantite;
        this.prixAchat = prixAchat;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public int getQuantite() {
        return quantite;
    }

    public double getPrixAchat() {
        return prixAchat;
    }
}
