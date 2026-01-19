package dao.mysql;

import dao.ProduitFournisseurDao;
import db.Db;
import model.Product;
import model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlProduitFournisseurDao implements ProduitFournisseurDao {

    @Override
    public void link(int idProduit, int idFournisseur) {
        String sql = "INSERT INTO produit_fournisseur (idProduit, idFournisseur) VALUES (?, ?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProduit);
            ps.setInt(2, idFournisseur);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in produit_fournisseur.link", e);
        }
    }

    @Override
    public void unlink(int idProduit, int idFournisseur) {
        String sql = "DELETE FROM produit_fournisseur WHERE idProduit = ? AND idFournisseur = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProduit);
            ps.setInt(2, idFournisseur);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in produit_fournisseur.unlink", e);
        }
    }

    @Override
    public List<Product> findProductsBySupplier(int idFournisseur) {
        String sql = "SELECT p.idProduit, p.nomProduit, p.marque, p.quantite, p.prix, p.TVA, p.type "
                + "FROM Produit p "
                + "JOIN produit_fournisseur pf ON p.idProduit = pf.idProduit "
                + "WHERE pf.idFournisseur = ? "
                + "ORDER BY p.idProduit";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFournisseur);
            try (ResultSet rs = ps.executeQuery()) {
                List<Product> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new Product(
                            rs.getInt("idProduit"),
                            rs.getString("nomProduit"),
                            rs.getString("marque"),
                            rs.getInt("quantite"),
                            rs.getDouble("prix"),
                            rs.getDouble("TVA"),
                            rs.getString("type")
                    ));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in produit_fournisseur.findProductsBySupplier", e);
        }
    }

    @Override
    public List<Supplier> findSuppliersByProduct(int idProduit) {
        String sql = "SELECT f.idFournisseur, f.nom, f.prenom, f.numeroTelephone, f.adresseEmail "
                + "FROM Fournisseur f "
                + "JOIN produit_fournisseur pf ON f.idFournisseur = pf.idFournisseur "
                + "WHERE pf.idProduit = ? "
                + "ORDER BY f.idFournisseur";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProduit);
            try (ResultSet rs = ps.executeQuery()) {
                List<Supplier> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new Supplier(
                            rs.getInt("idFournisseur"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("numeroTelephone"),
                            rs.getString("adresseEmail")
                    ));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in produit_fournisseur.findSuppliersByProduct", e);
        }
    }
}
