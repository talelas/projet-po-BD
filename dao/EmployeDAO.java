package dao;

import models.Employe;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des employés et l'authentification
 */
public class EmployeDAO {
    private Connection conn;
    
    public EmployeDAO(Connection conn) {
        this.conn = conn;
    }
    
    /**
     * Vérifie les credentials de login et retourne l'employé si valide
     */
    public Employe login(String login, String motDePasse) {
        String sql = "SELECT * FROM Employe WHERE login = ? AND motDePasse = ? AND actif = TRUE";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, motDePasse);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("✓ Login réussi pour " + login);
                return extraireEmploye(rs);
            } else {
                System.out.println("✗ Login échoué: credentials invalides");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Ajoute un nouvel employé
     */
    public void ajouterEmploye(Employe e) {
        String sql = "INSERT INTO Employe (nom, prenom, login, motDePasse, email, dateEmbauche, actif) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, e.getNom());
            pstmt.setString(2, e.getPrenom());
            pstmt.setString(3, e.getLogin());
            pstmt.setString(4, e.getMotDePasse());
            pstmt.setString(5, e.getEmail());
            pstmt.setDate(6, Date.valueOf(e.getDateEmbauche()));
            pstmt.setBoolean(7, e.isActif());
            pstmt.executeUpdate();
            System.out.println("✓ Employé ajouté!");
        } catch (SQLException ex) {
            System.err.println("✗ Erreur: " + ex.getMessage());
        }
    }
    
    /**
     * Change le mot de passe d'un employé
     */
    public void changerMotDePasse(int idEmploye, String ancienMdp, String nouveauMdp) {
        String sql = "UPDATE Employe SET motDePasse = ? WHERE idEmploye = ? AND motDePasse = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nouveauMdp);
            pstmt.setInt(2, idEmploye);
            pstmt.setString(3, ancienMdp);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✓ Mot de passe changé!");
            } else {
                System.out.println("✗ Ancien mot de passe incorrect");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Désactive un employé (soft delete)
     */
    public void desactiverEmploye(int idEmploye) {
        String sql = "UPDATE Employe SET actif = FALSE WHERE idEmploye = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEmploye);
            pstmt.executeUpdate();
            System.out.println("✓ Employé désactivé!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    public List<Employe> obtenirTousEmployes() {
        List<Employe> employes = new ArrayList<>();
        String sql = "SELECT * FROM Employe WHERE actif = TRUE";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                employes.add(extraireEmploye(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return employes;
    }
    
    private Employe extraireEmploye(ResultSet rs) throws SQLException {
        return new Employe(
            rs.getInt("idEmploye"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("login"),
            rs.getString("motDePasse"),
            rs.getString("email"),
            rs.getDate("dateEmbauche") != null ? rs.getDate("dateEmbauche").toLocalDate() : null,
            rs.getBoolean("actif")
        );
    }
}
