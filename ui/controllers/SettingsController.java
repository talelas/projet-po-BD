package ui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import models.Employe;
import dao.EmployeDAO;
import ui.utils.SessionManager;
import ui.utils.AlertHelper;
import ui.utils.SceneManager;
import ui.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SettingsController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Label passwordStatusLabel;
    @FXML private Button backButton;
    @FXML private Button refreshEmployeesButton;
    @FXML private Button addEmployeeButton;
    @FXML private Button editEmployeeButton;
    @FXML private Button deactivateEmployeeButton;
    @FXML private Button changePasswordButton;
    @FXML private TableView<Employe> employeesTable;
    @FXML private TableColumn<Employe, Integer> empIdColumn;
    @FXML private TableColumn<Employe, String> empLoginColumn;
    @FXML private TableColumn<Employe, String> empNomColumn;
    @FXML private TableColumn<Employe, String> empPrenomColumn;
    @FXML private TableColumn<Employe, String> empEmailColumn;
    @FXML private TableColumn<Employe, String> empTelColumn;
    @FXML private TableColumn<Employe, Boolean> empActiveColumn;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    
    private ObservableList<Employe> employeesList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupUserInfo();
        setupTableColumns();
        setupButtonHandlers();
        loadEmployees();
    }
    
    private void setupUserInfo() {
        if (SessionManager.isLoggedIn()) {
            welcomeLabel.setText("Welcome, " + SessionManager.getEmployeeName());
        }
    }
    
    private void setupTableColumns() {
        empIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        empLoginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        empNomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        empPrenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        empEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        empTelColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty("N/A"));
        empActiveColumn.setCellValueFactory(new PropertyValueFactory<>("actif"));
        
        empActiveColumn.setCellFactory(column -> new TableCell<Employe, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "✓ Active" : "✗ Inactive");
                    if (item) {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        employeesTable.setItems(employeesList);
        
        employeesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editEmployeeButton.setDisable(!hasSelection);
            deactivateEmployeeButton.setDisable(!hasSelection || !newSelection.isActif());
        });
    }
    
    private void setupButtonHandlers() {
        backButton.setOnAction(e -> goBack());
        refreshEmployeesButton.setOnAction(e -> loadEmployees());
        addEmployeeButton.setOnAction(e -> showAddEmployeeDialog());
        editEmployeeButton.setOnAction(e -> showEditEmployeeDialog());
        deactivateEmployeeButton.setOnAction(e -> showDeactivateConfirmation());
        changePasswordButton.setOnAction(e -> changePassword());
    }
    
    private void loadEmployees() {
        statusLabel.setText("Loading employees...");
        Thread loadThread = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                EmployeDAO dao = new EmployeDAO(conn);
                List<Employe> employees = dao.obtenirTousEmployes();
                
                Platform.runLater(() -> {
                    employeesList.clear();
                    employeesList.addAll(employees);
                    statusLabel.setText("Employees loaded: " + employees.size());
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    AlertHelper.showError("Database Error", "Failed to load employees");
                    statusLabel.setText("Error loading employees");
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }
    
    private void changePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            passwordStatusLabel.setText("❌ All fields are required");
            passwordStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            passwordStatusLabel.setText("❌ New passwords do not match");
            passwordStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
            return;
        }
        
        if (newPassword.length() < 4) {
            passwordStatusLabel.setText("❌ Password must be at least 4 characters");
            passwordStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
            return;
        }
        
        Thread changePasswordThread = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                EmployeDAO dao = new EmployeDAO(conn);
                Employe current = SessionManager.getEmployee();
                Employe loginCheck = dao.login(current.getLogin(), currentPassword);
                
                if (loginCheck != null) {
                    dao.changerMotDePasse(SessionManager.getEmployeeId(), currentPassword, newPassword);
                    
                    Platform.runLater(() -> {
                        passwordStatusLabel.setText("✓ Password changed successfully!");
                        passwordStatusLabel.setStyle("-fx-text-fill: #4CAF50;");
                        currentPasswordField.clear();
                        newPasswordField.clear();
                        confirmPasswordField.clear();
                        AlertHelper.showSuccess("Success", "Your password has been changed!");
                    });
                } else {
                    Platform.runLater(() -> {
                        passwordStatusLabel.setText("❌ Current password is incorrect");
                        passwordStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
                    });
                }
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    passwordStatusLabel.setText("❌ Database error");
                    passwordStatusLabel.setStyle("-fx-text-fill: #D32F2F;");
                    AlertHelper.showError("Database Error", e.getMessage());
                });
            }
        });
        changePasswordThread.setDaemon(true);
        changePasswordThread.start();
    }
    
    /**
     * Show dialog to add a new employee
     */
    private void showAddEmployeeDialog() {
        Dialog<Employe> dialog = new Dialog<>();
        dialog.setTitle("Add New Employee");
        dialog.setHeaderText("Enter employee details");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        
        TextField nomField = new TextField();
        nomField.setPromptText("Last Name");
        
        TextField prenomField = new TextField();
        prenomField.setPromptText("First Name");
        
        TextField loginField = new TextField();
        loginField.setPromptText("Username");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");
        
        grid.add(new Label("Last Name:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Login:"), 0, 2);
        grid.add(loginField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    String nom = nomField.getText().trim();
                    String prenom = prenomField.getText().trim();
                    String login = loginField.getText().trim();
                    String password = passwordField.getText();
                    String email = emailField.getText().trim();
                    
                    if (nom.isEmpty()) throw new IllegalArgumentException("Last name is required");
                    if (prenom.isEmpty()) throw new IllegalArgumentException("First name is required");
                    if (login.isEmpty()) throw new IllegalArgumentException("Login is required");
                    if (password.isEmpty() || password.length() < 4) throw new IllegalArgumentException("Password must be at least 4 characters");
                    if (email.isEmpty() || !email.contains("@")) throw new IllegalArgumentException("Valid email is required");
                    
                    return new Employe(nom, prenom, login, password, email, LocalDate.now());
                } catch (IllegalArgumentException e) {
                    AlertHelper.showError("Validation Error", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        Optional<Employe> result = dialog.showAndWait();
        result.ifPresent(employee -> {
            Thread addThread = new Thread(() -> {
                try (Connection conn = DatabaseManager.getConnection()) {
                    EmployeDAO dao = new EmployeDAO(conn);
                    dao.ajouterEmploye(employee);
                    Platform.runLater(() -> {
                        AlertHelper.showSuccess("Success", "Employee '" + employee.getPrenom() + " " + employee.getNom() + "' added successfully!");
                        loadEmployees();
                    });
                } catch (SQLException e) {
                    Platform.runLater(() -> AlertHelper.showError("Database Error", e.getMessage()));
                }
            });
            addThread.setDaemon(true);
            addThread.start();
        });
    }
    
    /**
     * Show dialog to edit selected employee
     */
    private void showEditEmployeeDialog() {
        Employe selected = employeesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No Selection", "Please select an employee to edit");
            return;
        }
        
        if (selected.getId() == 1) {
            AlertHelper.showWarning("Cannot Edit Admin", "Cannot edit the admin account");
            return;
        }
        
        Dialog<Object[]> dialog = new Dialog<>();
        dialog.setTitle("Edit Employee");
        dialog.setHeaderText("Edit: " + selected.getPrenom() + " " + selected.getNom());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        
        TextField nomField = new TextField(selected.getNom());
        TextField prenomField = new TextField(selected.getPrenom());
        TextField emailField = new TextField(selected.getEmail());
        
        Label noteLabel = new Label("Note: Login cannot be changed");
        noteLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #666666; -fx-font-size: 10px;");
        
        grid.add(new Label("Last Name:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(noteLabel, 0, 3, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    String nom = nomField.getText().trim();
                    String prenom = prenomField.getText().trim();
                    String email = emailField.getText().trim();
                    
                    if (nom.isEmpty()) throw new IllegalArgumentException("Last name is required");
                    if (prenom.isEmpty()) throw new IllegalArgumentException("First name is required");
                    if (email.isEmpty() || !email.contains("@")) throw new IllegalArgumentException("Valid email is required");
                    
                    return new Object[]{selected.getId(), nom, prenom, email};
                } catch (IllegalArgumentException e) {
                    AlertHelper.showError("Validation Error", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        Optional<Object[]> result = dialog.showAndWait();
        result.ifPresent(values -> {
            if (values != null) {
                int id = (int) values[0];
                String nom = (String) values[1];
                String prenom = (String) values[2];
                String email = (String) values[3];
                
                Thread editThread = new Thread(() -> {
                    try (Connection conn = DatabaseManager.getConnection()) {
                        EmployeDAO dao = new EmployeDAO(conn);
                        dao.modifierEmploye(id, nom, prenom, email);
                        Platform.runLater(() -> {
                            AlertHelper.showSuccess("Success", "Employee updated successfully!");
                            loadEmployees();
                        });
                    } catch (SQLException e) {
                        Platform.runLater(() -> AlertHelper.showError("Database Error", e.getMessage()));
                    }
                });
                editThread.setDaemon(true);
                editThread.start();
            }
        });
    }
    
    /**
     * Show deactivate confirmation and deactivate employee
     */
    private void showDeactivateConfirmation() {
        Employe selected = employeesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No Selection", "Please select an employee to deactivate");
            return;
        }
        
        if (selected.getId() == 1) {
            AlertHelper.showError("Cannot Deactivate Admin", "Cannot deactivate the admin account");
            return;
        }
        
        if (!selected.isActif()) {
            AlertHelper.showWarning("Already Inactive", "This employee is already deactivated");
            return;
        }
        
        boolean confirm = AlertHelper.showConfirmation(
            "Deactivate Employee",
            "Are you sure you want to deactivate '" + selected.getPrenom() + " " + selected.getNom() + "'?\\nThey will no longer be able to login."
        );
        
        if (confirm) {
            Thread deactivateThread = new Thread(() -> {
                try (Connection conn = DatabaseManager.getConnection()) {
                    EmployeDAO dao = new EmployeDAO(conn);
                    dao.desactiverEmploye(selected.getId());
                    Platform.runLater(() -> {
                        AlertHelper.showSuccess("Success", "Employee deactivated successfully!");
                        loadEmployees();
                    });
                } catch (SQLException e) {
                    Platform.runLater(() -> AlertHelper.showError("Database Error", e.getMessage()));
                }
            });
            deactivateThread.setDaemon(true);
            deactivateThread.start();
        }
    }
    
    private void goBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showDashboardScene();
    }
}
