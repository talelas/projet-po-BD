package dao;

import model.Commande;

import java.time.LocalDate;
import java.util.List;

public interface CommandeDao {
    int insert(Commande commande);

    void deleteById(int idCommande);

    Commande findById(int idCommande);

    List<Commande> findPending();

    List<Commande> findBySupplier(int idFournisseur);

    List<Commande> findByDate(LocalDate dateCommande);

    List<Commande> findAllOrderByPeriodeReception();

    List<Commande> findAllOrderBySupplier();

    void markReceived(int idCommande);
}
