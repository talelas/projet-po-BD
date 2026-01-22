package ui.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Manages scene transitions and navigation between screens
 */
public class SceneManager {
    private Stage stage;
    private static final double WINDOW_WIDTH = 1000;
    private static final double WINDOW_HEIGHT = 700;
    
    public SceneManager(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Load and display the login scene
     */
    public void showLoginScene() {
        try {
            System.out.println("Attempting to load login.fxml...");
            java.net.URL fxmlUrl = getClass().getClassLoader().getResource("ui/views/login.fxml");
            System.out.println("FXML URL: " + fxmlUrl);
            
            if (fxmlUrl == null) {
                throw new IOException("Cannot find ui/views/login.fxml in classpath");
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Pane root = (Pane) loader.load();
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            loadStylesheet(scene);
            
            stage.setScene(scene);
            stage.setTitle("Pharmacy Management System - Login");
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Failed to load login screen", e.getMessage());
        }
    }
    
    /**
     * Load and display the dashboard scene
     */
    public void showDashboardScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/views/dashboard.fxml"));
            Pane root = (Pane) loader.load();
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            loadStylesheet(scene);
            
            stage.setScene(scene);
            stage.setTitle("Pharmacy Management System - Dashboard");
            stage.setResizable(true);
        } catch (IOException e) {
            AlertHelper.showError("Failed to load dashboard", e.getMessage());
        }
    }
    
    /**
     * Load a specific screen by name
     */
    public void showScene(String sceneName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui/views/" + sceneName + ".fxml"));
            Pane root = (Pane) loader.load();
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            loadStylesheet(scene);
            
            stage.setScene(scene);
            stage.setTitle("Pharmacy Management System - " + sceneName);
        } catch (IOException e) {
            AlertHelper.showError("Failed to load scene", sceneName + ": " + e.getMessage());
        }
    }
    
    /**
     * Show Products Management screen
     */
    public void showProductsScene() {
        showScene("products");
    }
    
    /**
     * Show Settings screen
     */
    public void showSettingsScene() {
        showScene("settings");
    }
    
    /**
     * Show Clients Management screen
     */
    public void showClientsScene() {
        showScene("clients");
    }
    
    /**
     * Show Suppliers Management screen
     */
    public void showSuppliersScene() {
        showScene("suppliers");
    }
    
    /**
     * Show Reports screen
     */
    public void showReportsScene() {
        showScene("reports");
    }

    /**
     * Show Orders screen
     */
    public void showOrdersScene() {
        showScene("orders");
    }

    /**
     * Show Sales screen
     */
    public void showSalesScene() {
        showScene("sales");
    }
    
    /**
     * Apply stylesheet to scene
     */
    private void loadStylesheet(Scene scene) {
        try {
            String stylesheet = getClass().getClassLoader().getResource("ui/styles/styles.css").toExternalForm();
            scene.getStylesheets().add(stylesheet);
        } catch (Exception e) {
            System.err.println("Could not load stylesheet: " + e.getMessage());
        }
    }
}
