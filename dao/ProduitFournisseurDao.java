package dao;

import model.Product;
import model.Supplier;

import java.util.List;

/**
 * DAO for the junction table between Produit and Fournisseur.
 * Table name expected: produit_fournisseur (idProduit, idFournisseur)
 */
public interface ProduitFournisseurDao {
    void link(int idProduit, int idFournisseur);

    void unlink(int idProduit, int idFournisseur);

    List<Product> findProductsBySupplier(int idFournisseur);

    List<Supplier> findSuppliersByProduct(int idProduit);
}
