package dao;

import models.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des clients
 */
public class ClientDAO {
    private Connection conn;
    
    public ClientDAO(Connection conn) {
        this.conn = conn;
    }
    
    /**
     * Ajoute un nouveau client
     */
    public void ajouterClient(Client c) {
        String sql = "INSERT INTO Client (nom, adresse, email, nTelephone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getNom());
            pstmt.setString(2, c.getAdresse());
            pstmt.setString(3, c.getEmail());
            pstmt.setString(4, c.getTelephone());
            pstmt.executeUpdate();
            System.out.println("✓ Client '" + c.getNom() + "' ajouté!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Récupère tous les clients
     */
    public List<Client> obtenirTousClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                clients.add(extraireClient(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return clients;
    }
    
    /**
     * Récupère un client par ID
     */
    public Client obtenirClientParId(int idClient) {
        String sql = "SELECT * FROM Client WHERE idClient = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idClient);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extraireClient(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Recherche clients par nom
     */
    public List<Client> obtenirClientParNom(String nom) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM Client WHERE nom LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nom + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                clients.add(extraireClient(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
        return clients;
    }
    
    /**
     * Modifie les informations d'un client
     */
    public void modifierClient(Client c) {
        String sql = "UPDATE Client SET nom = ?, adresse = ?, email = ?, nTelephone = ? WHERE idClient = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getNom());
            pstmt.setString(2, c.getAdresse());
            pstmt.setString(3, c.getEmail());
            pstmt.setString(4, c.getTelephone());
            pstmt.setInt(5, c.getId());
            pstmt.executeUpdate();
            System.out.println("✓ Client modifié!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Supprime un client
     */
    public void supprimerClient(int idClient) {
        String sql = "DELETE FROM Client WHERE idClient = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idClient);
            pstmt.executeUpdate();
            System.out.println("✓ Client supprimé!");
        } catch (SQLException e) {
            System.err.println("✗ Erreur: " + e.getMessage());
        }
    }
    
    private Client extraireClient(ResultSet rs) throws SQLException {
        return new Client(
            rs.getInt("idClient"),
            rs.getString("nom"),
            rs.getString("adresse"),
            rs.getString("email"),
            rs.getString("nTelephone")
        );
    }
}
