package model;

public class ProduitFournisseur {
    private final int idProduit;
    private final int idFournisseur;

    public ProduitFournisseur(int idProduit, int idFournisseur) {
        this.idProduit = idProduit;
        this.idFournisseur = idFournisseur;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public int getIdFournisseur() {
        return idFournisseur;
    }
}
