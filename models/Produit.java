package models;

public class Produit {
    private int idProduit;
    private String nomProduit;
    private String marque;
    private int quantite;
    private int quantiteMinimale;
    private double prix;
    private double tva;
    private String type;
    

    public Produit(String nomProduit, String marque, int quantite, 
                   int quantiteMinimale, double prix, double tva, String type) {
        this.nomProduit = nomProduit;
        this.marque = marque;
        this.quantite = quantite;
        this.quantiteMinimale = quantiteMinimale;
        this.prix = prix;
        this.tva = tva;
        this.type = type;
    }
    
   
    public Produit(int idProduit, String nomProduit, String marque, int quantite, 
                   int quantiteMinimale, double prix, double tva, String type) {
        this.idProduit = idProduit;
        this.nomProduit = nomProduit;
        this.marque = marque;
        this.quantite = quantite;
        this.quantiteMinimale = quantiteMinimale;
        this.prix = prix;
        this.tva = tva;
        this.type = type;
    }
    public int getId() { return idProduit; }
    public String getNom() { return nomProduit; }
    public String getMarque() { return marque; }
    public int getQuantite() { return quantite; }
    public int getQuantiteMinimale() { return quantiteMinimale; }
    public double getPrix() { return prix; }
    public double getTva() { return tva; }
    public String getType() { return type; }
    


    public void setPrix(double nouveauPrix) {
        if (nouveauPrix <= 0) 
            throw new IllegalArgumentException("Prix doit être > 0");
        this.prix = nouveauPrix;
    }
    
    public void setTva(double nouvelleTva) {
        if (nouvelleTva < 0 || nouvelleTva > 100) 
            throw new IllegalArgumentException("TVA doit être entre 0 et 100");
        this.tva = nouvelleTva;
    }
    
    public void setQuantiteMinimale(int qMin) {
        if (qMin < 0) 
            throw new IllegalArgumentException("Quantité minimale doit être >= 0");
        this.quantiteMinimale = qMin;
    }
    

    public boolean estEnStockCritique() {
        return quantite <= quantiteMinimale;
    }
    
    public void augmenterQuantite(int quantiteAjouter) {
        if (quantiteAjouter <= 0)
            throw new IllegalArgumentException("Quantité à ajouter doit être > 0");
        this.quantite += quantiteAjouter;
    }
    

    public void diminuerQuantite(int quantiteOter) {
        if (quantiteOter <= 0)
            throw new IllegalArgumentException("Quantité à retirer doit être > 0");
        if (this.quantite < quantiteOter)
            throw new IllegalArgumentException("Stock insuffisant. Disponible: " + this.quantite);
        this.quantite -= quantiteOter;
    }
    
    public String toString() {
        return "Produit{" +
                "id=" + idProduit +
                ", nom='" + nomProduit + '\'' +
                ", marque='" + marque + '\'' +
                ", quantite=" + quantite +
                ", quantiteMinimale=" + quantiteMinimale +
                ", prix=" + prix +
                ", tva=" + tva +
                ", type='" + type + '\'' +
                ", critique=" + estEnStockCritique() +
                '}';
    }
}
