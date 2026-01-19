package dao;

import model.Supplier;

import java.util.List;

public interface SupplierDao {
    List<Supplier> findAll();

    Supplier findById(int idFournisseur);

    void insert(Supplier supplier);

    void update(Supplier supplier);

    void deleteById(int idFournisseur);
}
