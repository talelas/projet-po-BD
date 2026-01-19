package model;

public class LigneVente {
    private final int idVente;
    private final int idProduit;
    private final int quantite;
    private final double prixUnite;

    public LigneVente(int idVente, int idProduit, int quantite, double prixUnite) {
        this.idVente = idVente;
        this.idProduit = idProduit;
        this.quantite = quantite;
        this.prixUnite = prixUnite;
    }

    public int getIdVente() {
        return idVente;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public int getQuantite() {
        return quantite;
    }

    public double getPrixUnite() {
        return prixUnite;
    }
}
