package model;

public class Client {
    private int id;
    private String name;
    private String adresse;
    private String email;
    private String phone;

    public Client(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.adresse = "";
        this.email = email;
        this.phone = phone;
    }

    public Client(int id, String name, String adresse, String email, String phone) {
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.email = email;
        this.phone = phone;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAdresse() { return adresse; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
