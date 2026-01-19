package model;

import java.time.LocalDate;

public class Product {
    private int id;
    private String name;
    // PDF/MLD fields
    private String marque;
    private int quantiteMinimale;
    private double tva;
    private String type;

    // Kept for backward compatibility with the current UI/search (maps to "type")
    private String category;
    private double price;
    private int quantity;
    private LocalDate expirationDate;
    // For now we keep a display name to avoid breaking the UI.
    // In the DB model, the relationship is many-to-many via Produit_Fournisseur.
    private String supplier;

    public Product(int id, String name, String category, double price, int quantity, LocalDate expirationDate, String supplier) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.type = category;
        this.marque = "";
        this.quantiteMinimale = 10;
        this.tva = 0.0;
        this.price = price;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.supplier = supplier;
    }

    public Product(int id, String name, String marque, int quantity, double price, double tva, String type) {
        this.id = id;
        this.name = name;
        this.marque = marque;
        this.quantity = quantity;
        this.price = price;
        this.tva = tva;
        this.type = type;
        this.category = type;
        this.expirationDate = null;
        this.supplier = "";
        this.quantiteMinimale = 10;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getMarque() { return marque; }
    public int getQuantiteMinimale() { return quantiteMinimale; }
    public double getTva() { return tva; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public String getSupplier() { return supplier; }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public void setQuantiteMinimale(int quantiteMinimale) {
        this.quantiteMinimale = quantiteMinimale;
    }

    public void setTva(double tva) {
        this.tva = tva;
    }

    public void setType(String type) {
        this.type = type;
        this.category = type;
    }
    
    // Setters omitted for brevity in this simple model
}
