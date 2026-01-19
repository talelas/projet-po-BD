package dao.mysql;

import dao.LigneCommandeDao;
import db.Db;
import model.LigneCommande;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlLigneCommandeDao implements LigneCommandeDao {

    @Override
    public void insert(LigneCommande ligneCommande) {
        String sql = "INSERT INTO LigneCommande (idCommande, idProduit, quantite, prixAchat) VALUES (?, ?, ?, ?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ligneCommande.getIdCommande());
            ps.setInt(2, ligneCommande.getIdProduit());
            ps.setInt(3, ligneCommande.getQuantite());
            ps.setDouble(4, ligneCommande.getPrixAchat());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in LigneCommande.insert", e);
        }
    }

    @Override
    public List<LigneCommande> findByCommande(int idCommande) {
        String sql = "SELECT idCommande, idProduit, quantite, prixAchat FROM LigneCommande WHERE idCommande = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            try (ResultSet rs = ps.executeQuery()) {
                List<LigneCommande> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new LigneCommande(
                            rs.getInt("idCommande"),
                            rs.getInt("idProduit"),
                            rs.getInt("quantite"),
                            rs.getDouble("prixAchat")
                    ));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in LigneCommande.findByCommande", e);
        }
    }

    @Override
    public void deleteByCommande(int idCommande) {
        String sql = "DELETE FROM LigneCommande WHERE idCommande = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in LigneCommande.deleteByCommande", e);
        }
    }
}
