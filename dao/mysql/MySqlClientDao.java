package dao.mysql;

import dao.ClientDao;
import db.Db;
import model.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlClientDao implements ClientDao {

    @Override
    public List<Client> findAll() {
        String sql = "SELECT idClient, nom, adresse, email, nTelephone FROM Client ORDER BY idClient";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Client> out = new ArrayList<>();
            while (rs.next()) {
                out.add(new Client(
                        rs.getInt("idClient"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("email"),
                        rs.getString("nTelephone")
                ));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Client.findAll", e);
        }
    }

    @Override
    public Client findById(int idClient) {
        String sql = "SELECT idClient, nom, adresse, email, nTelephone FROM Client WHERE idClient = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idClient);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Client(
                        rs.getInt("idClient"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("email"),
                        rs.getString("nTelephone")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Client.findById", e);
        }
    }

    @Override
    public void insert(Client client) {
        String sql = "INSERT INTO Client (nom, adresse, email, nTelephone) VALUES (?, ?, ?, ?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getAdresse());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getPhone());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Client.insert", e);
        }
    }

    @Override
    public void update(Client client) {
        String sql = "UPDATE Client SET nom = ?, adresse = ?, email = ?, nTelephone = ? WHERE idClient = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getAdresse());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getPhone());
            ps.setInt(5, client.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Client.update", e);
        }
    }

    @Override
    public void deleteById(int idClient) {
        String sql = "DELETE FROM Client WHERE idClient = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idClient);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Client.deleteById", e);
        }
    }

    @Override
    public List<Client> searchByName(String nameQuery) {
        String sql = "SELECT idClient, nom, adresse, email, nTelephone FROM Client WHERE nom LIKE ? ORDER BY idClient";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + (nameQuery == null ? "" : nameQuery) + "%");

            try (ResultSet rs = ps.executeQuery()) {
                List<Client> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new Client(
                            rs.getInt("idClient"),
                            rs.getString("nom"),
                            rs.getString("adresse"),
                            rs.getString("email"),
                            rs.getString("nTelephone")
                    ));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Client.searchByName", e);
        }
    }
}
