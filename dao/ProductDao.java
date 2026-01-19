package dao;

import model.Product;

import java.util.List;

public interface ProductDao {
    List<Product> findAll();

    List<Product> search(String query);

    List<Product> findLowStock(int threshold);
    // Critical stock: quantite <= quantiteMinimale
    List<Product> findCriticalStock();

    Product findById(int idProduit);

    void insert(Product product);

    void deleteById(int idProduit);

    void updateQuantity(int idProduit, int newQuantity);

    void update(Product product);
}
