package dao.mysql;

import dao.CommandeDao;
import db.Db;
import model.Commande;
import model.LigneCommande;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MySqlCommandeDao implements CommandeDao {

    @Override
    public int insert(Commande commande) {
        String sql = "INSERT INTO Commande (idFournisseur, dateCommande, periodeReception, recu, dateReception) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, commande.getIdFournisseur());
            ps.setDate(2, Date.valueOf(commande.getDateCommande()));
            ps.setInt(3, parsePeriodeReception(commande.getPeriodeReception()));
            ps.setBoolean(4, commande.isRecu());
            if (commande.getDateReception() == null) {
                ps.setDate(5, null);
            } else {
                ps.setDate(5, Date.valueOf(commande.getDateReception()));
            }
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Commande.insert", e);
        }
    }

    @Override
    public void deleteById(int idCommande) {
        try (Connection c = Db.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement("DELETE FROM LigneCommande WHERE idCommande = ?")) {
                ps.setInt(1, idCommande);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement("DELETE FROM Commande WHERE idCommande = ?")) {
                ps.setInt(1, idCommande);
                ps.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Commande.deleteById", e);
        }
    }

    @Override
    public Commande findById(int idCommande) {
        String sql = "SELECT idCommande, idFournisseur, dateCommande, periodeReception, recu, dateReception FROM Commande WHERE idCommande = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapCommande(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Commande.findById", e);
        }
    }

    @Override
    public List<Commande> findPending() {
        String sql = "SELECT idCommande, idFournisseur, dateCommande, periodeReception, recu, dateReception FROM Commande WHERE recu = FALSE ORDER BY dateCommande";
        return queryList(sql, ps -> {});
    }

    @Override
    public List<Commande> findBySupplier(int idFournisseur) {
        String sql = "SELECT idCommande, idFournisseur, dateCommande, periodeReception, recu, dateReception FROM Commande WHERE idFournisseur = ? ORDER BY dateCommande DESC";
        return queryList(sql, ps -> ps.setInt(1, idFournisseur));
    }

    @Override
    public List<Commande> findByDate(LocalDate dateCommande) {
        String sql = "SELECT idCommande, idFournisseur, dateCommande, periodeReception, recu, dateReception FROM Commande WHERE dateCommande = ?";
        return queryList(sql, ps -> ps.setDate(1, Date.valueOf(dateCommande)));
    }

    @Override
    public List<Commande> findAllOrderByPeriodeReception() {
        String sql = "SELECT idCommande, idFournisseur, dateCommande, periodeReception, recu, dateReception FROM Commande ORDER BY periodeReception ASC, dateCommande DESC";
        return queryList(sql, ps -> {});
    }

    @Override
    public List<Commande> findAllOrderBySupplier() {
        String sql = "SELECT idCommande, idFournisseur, dateCommande, periodeReception, recu, dateReception FROM Commande ORDER BY idFournisseur ASC, dateCommande DESC";
        return queryList(sql, ps -> {});
    }

    @Override
    public void markReceived(int idCommande) {
        try (Connection c = Db.getConnection()) {
            c.setAutoCommit(false);

            List<LigneCommande> lignes;
            try (PreparedStatement ps = c.prepareStatement("SELECT idCommande, idProduit, quantite, prixAchat FROM LigneCommande WHERE idCommande = ?")) {
                ps.setInt(1, idCommande);
                try (ResultSet rs = ps.executeQuery()) {
                    lignes = new ArrayList<>();
                    while (rs.next()) {
                        lignes.add(new LigneCommande(
                                rs.getInt("idCommande"),
                                rs.getInt("idProduit"),
                                rs.getInt("quantite"),
                                rs.getDouble("prixAchat")
                        ));
                    }
                }
            }

            // increment stock for each product
            String incSql = "UPDATE Produit SET quantite = quantite + ? WHERE idProduit = ?";
            try (PreparedStatement ps = c.prepareStatement(incSql)) {
                for (LigneCommande ligne : lignes) {
                    ps.setInt(1, ligne.getQuantite());
                    ps.setInt(2, ligne.getIdProduit());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // mark received
            String updSql = "UPDATE Commande SET recu = TRUE, dateReception = ? WHERE idCommande = ?";
            try (PreparedStatement ps = c.prepareStatement(updSql)) {
                ps.setDate(1, Date.valueOf(LocalDate.now()));
                ps.setInt(2, idCommande);
                ps.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Commande.markReceived", e);
        }
    }

    private interface StatementConfigurer {
        void apply(PreparedStatement ps) throws SQLException;
    }

    private List<Commande> queryList(String sql, StatementConfigurer configurer) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            configurer.apply(ps);
            try (ResultSet rs = ps.executeQuery()) {
                List<Commande> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(mapCommande(rs));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error in Commande.query", e);
        }
    }

    private static Commande mapCommande(ResultSet rs) throws SQLException {
        LocalDate dateCommande = rs.getDate("dateCommande").toLocalDate();
        Date reception = rs.getDate("dateReception");
        LocalDate dateReception = reception == null ? null : reception.toLocalDate();

        return new Commande(
                rs.getInt("idCommande"),
                rs.getInt("idFournisseur"),
                dateCommande,
                String.valueOf(rs.getInt("periodeReception")),
                rs.getBoolean("recu"),
                dateReception
        );
    }

    private static int parsePeriodeReception(String value) {
        if (value == null) return 0;
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return 0;
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException ignored) {
            // best-effort: extract leading digits (e.g. "7 days")
            String digits = trimmed.replaceAll("[^0-9]", "");
            if (digits.isEmpty()) return 0;
            try {
                return Integer.parseInt(digits);
            } catch (NumberFormatException ignored2) {
                return 0;
            }
        }
    }
}
