package model;

public class Supplier {
    private int id;
    // PDF/MLD fields
    private String nom;
    private String prenom;
    private String numeroTelephone;
    private String adresseEmail;

    // Backward compatibility with existing UI/table headers
    private String name;
    private String contact;
    private String email;

    public Supplier(int id, String name, String contact, String email) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.email = email;

        // Best-effort mapping
        this.nom = name;
        this.prenom = "";
        this.numeroTelephone = contact;
        this.adresseEmail = email;
    }

    public Supplier(int id, String nom, String prenom, String numeroTelephone, String adresseEmail) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.numeroTelephone = numeroTelephone;
        this.adresseEmail = adresseEmail;

        // Back-compat
        this.name = (prenom == null || prenom.isBlank()) ? nom : (nom + " " + prenom);
        this.contact = numeroTelephone;
        this.email = adresseEmail;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getEmail() { return email; }

    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getNumeroTelephone() { return numeroTelephone; }
    public String getAdresseEmail() { return adresseEmail; }
}
