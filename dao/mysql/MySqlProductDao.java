package dao.mysql;

import dao.ProductDao;
import db.Db;
import model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlProductDao implements ProductDao {

    @Override
    public List<Product> findAll() {
        String sql = "SELECT idProduit, nomProduit, marque, quantite, quantiteMinimale, prix, TVA, type FROM Produit ORDER BY idProduit";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Product> out = new ArrayList<>();
            while (rs.next()) {
                out.add(mapProduct(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Produit.findAll", e);
        }
    }

    @Override
    public List<Product> search(String query) {
        String q = (query == null) ? "" : query.trim();
        String sql = "SELECT idProduit, nomProduit, marque, quantite, quantiteMinimale, prix, TVA, type "
                + "FROM Produit "
                + "WHERE LOWER(nomProduit) LIKE ? OR LOWER(type) LIKE ? OR LOWER(marque) LIKE ? "
                + "ORDER BY idProduit";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + q.toLowerCase() + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);

            try (ResultSet rs = ps.executeQuery()) {
                List<Product> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(mapProduct(rs));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Produit.search", e);
        }
    }

    @Override
    public List<Product> findLowStock(int threshold) {
        String sql = "SELECT idProduit, nomProduit, marque, quantite, quantiteMinimale, prix, TVA, type FROM Produit WHERE quantite <= ? ORDER BY quantite ASC";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                List<Product> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(mapProduct(rs));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Produit.findLowStock", e);
        }
    }

    @Override
    public Product findById(int idProduit) {
        String sql = "SELECT idProduit, nomProduit, marque, quantite, quantiteMinimale, prix, TVA, type FROM Produit WHERE idProduit = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProduit);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapProduct(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Produit.findById", e);
        }
    }

    @Override
    public void insert(Product product) {
        String sql = "INSERT INTO Produit (nomProduit, marque, quantite, quantiteMinimale, prix, TVA, type) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, nullToEmpty(product.getMarque()));
            ps.setInt(3, product.getQuantity());
            ps.setInt(4, product.getQuantiteMinimale());
            ps.setDouble(5, product.getPrice());
            ps.setDouble(6, product.getTva());
            ps.setString(7, nullToEmpty(product.getType()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Produit.insert", e);
        }
    }

    @Override
    public void deleteById(int idProduit) {
        String sql = "DELETE FROM Produit WHERE idProduit = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProduit);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Produit.deleteById", e);
        }
    }

    @Override
    public void updateQuantity(int idProduit, int newQuantity) {
        String sql = "UPDATE Produit SET quantite = ? WHERE idProduit = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, idProduit);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Produit.updateQuantity", e);
        }
    }

    @Override
    public void update(Product product) {
        String sql = "UPDATE Produit SET nomProduit = ?, marque = ?, quantite = ?, quantiteMinimale = ?, prix = ?, TVA = ?, type = ? WHERE idProduit = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, nullToEmpty(product.getMarque()));
            ps.setInt(3, product.getQuantity());
            ps.setInt(4, product.getQuantiteMinimale());
            ps.setDouble(5, product.getPrice());
            ps.setDouble(6, product.getTva());
            ps.setString(7, nullToEmpty(product.getType()));
            ps.setInt(8, product.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Produit.update", e);
        }
    }

    @Override
    public List<Product> findCriticalStock() {
        String sql = "SELECT idProduit, nomProduit, marque, quantite, quantiteMinimale, prix, TVA, type FROM Produit WHERE quantite <= quantiteMinimale ORDER BY quantite ASC";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Product> out = new ArrayList<>();
            while (rs.next()) {
                out.add(mapProduct(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Produit.findCriticalStock", e);
        }
    }

    private static Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product(
                rs.getInt("idProduit"),
                rs.getString("nomProduit"),
                rs.getString("marque"),
                rs.getInt("quantite"),
                rs.getDouble("prix"),
                rs.getDouble("TVA"),
                rs.getString("type")
        );
        p.setQuantiteMinimale(rs.getInt("quantiteMinimale"));
        return p;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
