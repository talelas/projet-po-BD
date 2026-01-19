package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vente {
    private final int idVente;
    private final LocalDate dateFacture;
    private final int idClient;
    private final List<LigneVente> lignes = new ArrayList<>();

    public Vente(int idVente, LocalDate dateFacture, int idClient) {
        this.idVente = idVente;
        this.dateFacture = dateFacture;
        this.idClient = idClient;
    }

    public int getIdVente() {
        return idVente;
    }

    public LocalDate getDateFacture() {
        return dateFacture;
    }

    public int getIdClient() {
        return idClient;
    }

    public List<LigneVente> getLignes() {
        return Collections.unmodifiableList(lignes);
    }

    public void addLigne(LigneVente ligne) {
        if (ligne == null) return;
        lignes.add(ligne);
    }
}
