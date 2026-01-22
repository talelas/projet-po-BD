package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Employe;
import dao.ProduitDAO;
import dao.ClientDAO;
import ui.utils.SessionManager;
import ui.utils.AlertHelper;
import ui.utils.SceneManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Controller for the main dashboard screen
 */
public class DashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label totalProductsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label totalClientsLabel;
    @FXML private Label statusLabel;
    
    @FXML private Button logoutButton;
    @FXML private Button productsButton;
    @FXML private Button clientsButton;
    @FXML private Button suppliersButton;
    @FXML private Button ordersButton;
    @FXML private Button salesButton;
    @FXML private Button reportsButton;
    @FXML private Button settingsButton;
    
    /**
     * Initialize the dashboard
     */
    @FXML
    public void initialize() {
        loadUserInfo();
        loadQuickStats();
        setupButtonHandlers();
    }
    
    /**
     * Load current user information
     */
    private void loadUserInfo() {
        Employe employee = SessionManager.getEmployee();
        if (employee != null) {
            welcomeLabel.setText("Welcome, " + employee.getNom() + " " + employee.getPrenom());
            
            // Settings button only visible for admin
            if (!SessionManager.isAdmin()) {
                settingsButton.setVisible(false);
                settingsButton.setManaged(false);
            }
        }
    }
    
    /**
     * Load quick statistics
     */
    private void loadQuickStats() {
        Thread statsThread = new Thread(() -> {
            try (Connection conn = ui.utils.DatabaseManager.getConnection()) {
                // Get total products
                ProduitDAO produitDAO = new ProduitDAO(conn);
                int totalProducts = produitDAO.obtenirTousProduits().size();
                
                // Get low stock count
                int lowStock = produitDAO.obtenirProduitsEnStockCritique().size();
                
                // Get total clients
                ClientDAO clientDAO = new ClientDAO(conn);
                int totalClients = clientDAO.obtenirTousClients().size();
                
                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    totalProductsLabel.setText(String.valueOf(totalProducts));
                    lowStockLabel.setText(String.valueOf(lowStock));
                    totalClientsLabel.setText(String.valueOf(totalClients));
                    statusLabel.setText("Dashboard loaded successfully");
                });
                
            } catch (SQLException e) {
                javafx.application.Platform.runLater(() -> {
                    AlertHelper.showError("Database Error", "Failed to load statistics: " + e.getMessage());
                    statusLabel.setText("Error loading statistics");
                });
            }
        });
        statsThread.setDaemon(true);
        statsThread.start();
    }
    
    /**
     * Setup button click handlers
     */
    private void setupButtonHandlers() {
        logoutButton.setOnAction(e -> handleLogout());
        productsButton.setOnAction(e -> navigateToProducts());
        clientsButton.setOnAction(e -> navigateToClients());
        suppliersButton.setOnAction(e -> navigateToSuppliers());
        ordersButton.setOnAction(e -> navigateToOrders());
        salesButton.setOnAction(e -> navigateToSales());
        reportsButton.setOnAction(e -> navigateToReports());
        settingsButton.setOnAction(e -> navigateToSettings());
    }
    
    /**
     * Handle logout
     */
    private void handleLogout() {
        boolean confirm = AlertHelper.showConfirmation(
            "Logout", 
            "Are you sure you want to logout?"
        );
        
        if (confirm) {
            SessionManager.logout();
            
            // Get stage and show login screen
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            SceneManager sceneManager = new SceneManager(stage);
            sceneManager.showLoginScene();
            
            statusLabel.setText("Logged out successfully");
        }
    }
    
    /**
     * Show coming soon message for unimplemented screens
     */
    private void showComingSoon(String screenName) {
        AlertHelper.showWarning(
            "Coming Soon", 
            screenName + " screen is not yet implemented.\n\nWe'll create it next!"
        );
    }
    
    /**
     * Navigate to Products screen
     */
    private void navigateToProducts() {
        Stage stage = (Stage) productsButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showProductsScene();
    }
    
    /**
     * Navigate to Settings screen
     */
    private void navigateToSettings() {
        Stage stage = (Stage) settingsButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showSettingsScene();
    }
    
    /**
     * Navigate to Clients screen
     */
    private void navigateToClients() {
        Stage stage = (Stage) clientsButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showClientsScene();
    }
    
    /**
     * Navigate to Suppliers screen
     */
    private void navigateToSuppliers() {
        Stage stage = (Stage) suppliersButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showSuppliersScene();
    }
    
    /**
     * Navigate to Reports screen
     */
    private void navigateToReports() {
        Stage stage = (Stage) reportsButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showReportsScene();
    }

    /**
     * Navigate to Orders screen
     */
    private void navigateToOrders() {
        Stage stage = (Stage) ordersButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showOrdersScene();
    }

    /**
     * Navigate to Sales screen
     */
    private void navigateToSales() {
        Stage stage = (Stage) salesButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showSalesScene();
    }
}
