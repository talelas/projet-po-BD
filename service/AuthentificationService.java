package service;

import exception.AuthentificationException;
import model.User;
import db.Db;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthentificationService {

    private Map<String, User> users = new HashMap<>();
    private boolean dbAvailable = true;

    public AuthentificationService() {
        // Dummy data
        users.put("admin", new User("admin", "admin123", true));
        users.put("user", new User("user", "user123", false));

        // Check DB availability once
        try (Connection ignored = Db.getConnection()) {
            dbAvailable = true;
        } catch (Exception e) {
            dbAvailable = false;
        }
    }

    public User authenticate(String username, String password) throws AuthentificationException {
        // Prefer DB when available
        if (dbAvailable) {
            User dbUser = authenticateDb(username, password);
            if (dbUser != null) return dbUser;
        }

        if (users.containsKey(username)) {
            User user = users.get(username);
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new AuthentificationException("Nom d'utilisateur ou mot de passe incorrect");
    }

    private User authenticateDb(String username, String password) {
        String sql = "SELECT login, motDePasse, actif FROM Employe WHERE login = ? AND motDePasse = ?";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean actif = rs.getBoolean("actif");
                    if (!actif) {
                        throw new AuthentificationException("Compte désactivé");
                    }
                    // No admin flag in schema; default to false
                    return new User(username, password, false);
                }
            }
        } catch (SQLException | AuthentificationException e) {
            throw new RuntimeException("DB auth error", e);
        }
        return null;
    }
}
