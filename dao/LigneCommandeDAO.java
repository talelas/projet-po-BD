package dao;

import model.LigneCommande;

import java.util.List;

public interface LigneCommandeDao {
    void insert(LigneCommande ligneCommande);

    List<LigneCommande> findByCommande(int idCommande);

    void deleteByCommande(int idCommande);
}
