package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;

import dao.EmployeDAO;
import models.Employe;
import ui.utils.AlertHelper;
import ui.utils.DatabaseManager;
import ui.utils.SceneManager;
import ui.utils.SessionManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Controller for the login screen
 */
public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private CheckBox rememberMeCheckBox;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button exitButton;
    
    @FXML
    private Label errorLabel;
    
    private SceneManager sceneManager;
    
    /**
     * Initialize controller
     */
    @FXML
    public void initialize() {
        // Set up button handlers
        loginButton.setOnAction(event -> handleLogin());
        exitButton.setOnAction(event -> handleExit());
        
        // Allow Enter key to trigger login
        usernameField.setOnKeyPressed(this::handleKeyPress);
        passwordField.setOnKeyPressed(this::handleKeyPress);
        
        // Get SceneManager from primary stage (set by App.java)
        // For now, we'll create it here - will be passed from App later
    }
    
    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validation
        if (username.isEmpty()) {
            showError("Username is required");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Password is required");
            passwordField.requestFocus();
            return;
        }
        
        // Authenticate
        loginButton.setDisable(true);
        loginButton.setText("Logging in...");
        
        // Perform authentication in background to prevent UI freeze
        Thread authThread = new Thread(() -> {
            try {
                Connection conn = DatabaseManager.getConnection();
                EmployeDAO employeDAO = new EmployeDAO(conn);
                Employe employee = employeDAO.login(username, password);
                
                if (employee != null && employee.getId() > 0) {
                    // Login successful
                    SessionManager.setEmployee(employee);
                    
                    Platform.runLater(() -> {
                        AlertHelper.showSuccess("Login Successful", 
                            "Welcome " + employee.getNom() + " " + employee.getPrenom() + "!");
                        navigateToDashboard();
                    });
                } else {
                    // Login failed
                    Platform.runLater(() -> {
                        showError("Invalid username or password");
                        passwordField.clear();
                        passwordField.requestFocus();
                        loginButton.setDisable(false);
                        loginButton.setText("LOGIN");
                    });
                }
                
                conn.close();
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    AlertHelper.showError("Database Error", 
                        "Could not connect to database:\n" + e.getMessage());
                    loginButton.setDisable(false);
                    loginButton.setText("LOGIN");
                });
            }
        });
        
        authThread.setDaemon(true);
        authThread.start();
    }
    
    /**
     * Handle exit button click
     */
    @FXML
    private void handleExit() {
        if (AlertHelper.showConfirmation("Exit Application", 
            "Are you sure you want to exit?")) {
            Platform.exit();
        }
    }
    
    /**
     * Handle Enter key press
     */
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        errorLabel.setText("✗ " + message);
        errorLabel.setStyle("-fx-text-fill: #D32F2F;");
    }
    
    /**
     * Navigate to dashboard
     */
    private void navigateToDashboard() {
        try {
            // Get the stage from the login button's scene
            javafx.stage.Stage stage = (javafx.stage.Stage) loginButton.getScene().getWindow();
            sceneManager = new SceneManager(stage);
            sceneManager.showDashboardScene();
        } catch (Exception e) {
            AlertHelper.showError("Navigation Error", 
                "Failed to navigate to dashboard: " + e.getMessage());
        }
    }
}
