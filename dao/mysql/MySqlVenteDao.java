package dao.mysql;

import dao.VenteDao;
import db.Db;
import model.LigneVente;
import model.Vente;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MySqlVenteDao implements VenteDao {

    @Override
    public void insertVente(Vente vente) {
        String sql = "INSERT INTO Vente (idVente, dateFacture, idClient) VALUES (?,?,?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vente.getIdVente());
            ps.setDate(2, Date.valueOf(vente.getDateFacture()));
            ps.setInt(3, vente.getIdClient());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Vente.insert", e);
        }
    }

    @Override
    public void insertLigneVente(LigneVente ligne) {
        String sql = "INSERT INTO LigneVente (idVente, idProduit, quantite, prixUnite) VALUES (?,?,?,?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ligne.getIdVente());
            ps.setInt(2, ligne.getIdProduit());
            ps.setInt(3, ligne.getQuantite());
            ps.setDouble(4, ligne.getPrixUnite());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in LigneVente.insert", e);
        }
    }

    @Override
    public List<Vente> findAll() {
        // Minimal: fetch ventes only (without lignes) to support reporting later.
        String sql = "SELECT idVente, dateFacture, idClient FROM Vente ORDER BY dateFacture DESC, idVente DESC";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Vente> out = new ArrayList<>();
            while (rs.next()) {
                LocalDate date = rs.getDate("dateFacture").toLocalDate();
                out.add(new Vente(rs.getInt("idVente"), date, rs.getInt("idClient")));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Vente.findAll", e);
        }
    }

    @Override
    public List<Vente> findByClient(int idClient) {
        String sql = "SELECT idVente, dateFacture, idClient FROM Vente WHERE idClient = ? ORDER BY dateFacture DESC, idVente DESC";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idClient);
            try (ResultSet rs = ps.executeQuery()) {
                List<Vente> out = new ArrayList<>();
                while (rs.next()) {
                    LocalDate date = rs.getDate("dateFacture").toLocalDate();
                    out.add(new Vente(rs.getInt("idVente"), date, rs.getInt("idClient")));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Vente.findByClient", e);
        }
    }

    @Override
    public List<Vente> findByDate(LocalDate date) {
        String sql = "SELECT idVente, dateFacture, idClient FROM Vente WHERE dateFacture = ? ORDER BY idVente DESC";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                List<Vente> out = new ArrayList<>();
                while (rs.next()) {
                    LocalDate d = rs.getDate("dateFacture").toLocalDate();
                    out.add(new Vente(rs.getInt("idVente"), d, rs.getInt("idClient")));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Vente.findByDate", e);
        }
    }
}
