package dao;

import model.LigneVente;
import model.Vente;

import java.util.List;

public interface VenteDao {
    void insertVente(Vente vente);

    void insertLigneVente(LigneVente ligne);

    List<Vente> findAll();

    List<Vente> findByClient(int idClient);

    List<Vente> findByDate(java.time.LocalDate date);
}
