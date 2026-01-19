package dao.mysql;

import dao.SupplierDao;
import db.Db;
import model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlSupplierDao implements SupplierDao {

    @Override
    public List<Supplier> findAll() {
        String sql = "SELECT idFournisseur, nom, prenom, numeroTelephone, adresseEmail FROM Fournisseur ORDER BY idFournisseur";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Fournisseur.findAll", e);
        }
    }

    @Override
    public Supplier findById(int idFournisseur) {
        String sql = "SELECT idFournisseur, nom, prenom, numeroTelephone, adresseEmail FROM Fournisseur WHERE idFournisseur = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFournisseur);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Supplier(
                        rs.getInt("idFournisseur"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("numeroTelephone"),
                        rs.getString("adresseEmail")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Fournisseur.findById", e);
        }
    }

    @Override
    public void insert(Supplier supplier) {
        String sql = "INSERT INTO Fournisseur (nom, prenom, numeroTelephone, adresseEmail) VALUES (?,?,?,?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, supplier.getNom());
            ps.setString(2, supplier.getPrenom());
            ps.setString(3, supplier.getNumeroTelephone());
            ps.setString(4, supplier.getAdresseEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Fournisseur.insert", e);
        }
    }

    @Override
    public void update(Supplier supplier) {
        String sql = "UPDATE Fournisseur SET nom = ?, prenom = ?, numeroTelephone = ?, adresseEmail = ? WHERE idFournisseur = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, supplier.getNom());
            ps.setString(2, supplier.getPrenom());
            ps.setString(3, supplier.getNumeroTelephone());
            ps.setString(4, supplier.getAdresseEmail());
            ps.setInt(5, supplier.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Fournisseur.update", e);
        }
    }

    @Override
    public void deleteById(int idFournisseur) {
        String sql = "DELETE FROM Fournisseur WHERE idFournisseur = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFournisseur);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Fournisseur.deleteById", e);
        }
    }
}
