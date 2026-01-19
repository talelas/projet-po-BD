package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Commande {
    private final int idCommande;
    private final int idFournisseur;
    private final LocalDate dateCommande;
    private final String periodeReception;
    private boolean recu;
    private LocalDate dateReception;
    private final List<LigneCommande> lignes = new ArrayList<>();

    public Commande(int idCommande, int idFournisseur, LocalDate dateCommande, String periodeReception, boolean recu, LocalDate dateReception) {
        this.idCommande = idCommande;
        this.idFournisseur = idFournisseur;
        this.dateCommande = dateCommande;
        this.periodeReception = periodeReception;
        this.recu = recu;
        this.dateReception = dateReception;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public int getIdFournisseur() {
        return idFournisseur;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public String getPeriodeReception() {
        return periodeReception;
    }

    public boolean isRecu() {
        return recu;
    }

    public void setRecu(boolean recu) {
        this.recu = recu;
    }

    public LocalDate getDateReception() {
        return dateReception;
    }

    public void setDateReception(LocalDate dateReception) {
        this.dateReception = dateReception;
    }

    public List<LigneCommande> getLignes() {
        return Collections.unmodifiableList(lignes);
    }

    public void addLigne(LigneCommande ligne) {
        if (ligne == null) return;
        lignes.add(ligne);
    }
}
