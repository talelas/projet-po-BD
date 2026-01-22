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
import models.Client;
import dao.ClientDAO;
import ui.utils.SessionManager;
import ui.utils.AlertHelper;
import ui.utils.SceneManager;
import ui.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ClientsController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Label countLabel;
    @FXML private TextField searchField;
    @FXML private Button backButton;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private TableView<Client> clientsTable;
    @FXML private TableColumn<Client, Integer> idColumn;
    @FXML private TableColumn<Client, String> nameColumn;
    @FXML private TableColumn<Client, String> addressColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TableColumn<Client, String> phoneColumn;
    
    private ObservableList<Client> clientsList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupUserInfo();
        setupTableColumns();
        setupButtonHandlers();
        loadClients();
    }
    
    private void setupUserInfo() {
        if (SessionManager.isLoggedIn()) {
            welcomeLabel.setText("Welcome, " + SessionManager.getEmployeeName());
        }
    }
    
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        clientsTable.setItems(clientsList);
        
        clientsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });
    }
    
    private void setupButtonHandlers() {
        backButton.setOnAction(e -> goBack());
        refreshButton.setOnAction(e -> loadClients());
        searchButton.setOnAction(e -> searchClients());
        addButton.setOnAction(e -> showAddClientDialog());
        editButton.setOnAction(e -> showEditClientDialog());
        deleteButton.setOnAction(e -> showDeleteConfirmation());
        searchField.setOnAction(e -> searchClients());
    }
    
    private void loadClients() {
        statusLabel.setText("Loading clients...");
        Thread loadThread = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                ClientDAO dao = new ClientDAO(conn);
                List<Client> clients = dao.obtenirTousClients();
                
                Platform.runLater(() -> {
                    clientsList.clear();
                    clientsList.addAll(clients);
                    countLabel.setText("Total: " + clients.size() + " clients");
                    statusLabel.setText("Clients loaded");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    AlertHelper.showError("Database Error", "Failed to load clients");
                    statusLabel.setText("Error loading clients");
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }
    
    private void searchClients() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadClients();
            return;
        }
        
        statusLabel.setText("Searching...");
        Thread searchThread = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                ClientDAO dao = new ClientDAO(conn);
                List<Client> filtered = dao.obtenirClientParNom(keyword);
                
                Platform.runLater(() -> {
                    clientsList.clear();
                    clientsList.addAll(filtered);
                    countLabel.setText("Found: " + filtered.size() + " clients");
                    statusLabel.setText("Search completed");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Error", "Search failed"));
            }
        });
        searchThread.setDaemon(true);
        searchThread.start();
    }
    
    private void showAddClientDialog() {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Add New Client");
        dialog.setHeaderText("Enter client details");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        
        TextField nomField = new TextField();
        nomField.setPromptText("Client Name");
        
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        
        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");
        
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Address:"), 0, 1);
        grid.add(addressField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    String nom = nomField.getText().trim();
                    String address = addressField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    
                    if (nom.isEmpty()) throw new IllegalArgumentException("Client name is required");
                    if (email.isEmpty() || !email.contains("@")) throw new IllegalArgumentException("Valid email is required");
                    if (phone.isEmpty()) throw new IllegalArgumentException("Phone number is required");
                    
                    return new Client(nom, address, email, phone);
                } catch (IllegalArgumentException e) {
                    AlertHelper.showError("Validation Error", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        Optional<Client> result = dialog.showAndWait();
        result.ifPresent(client -> {
            Thread addThread = new Thread(() -> {
                try (Connection conn = DatabaseManager.getConnection()) {
                    ClientDAO dao = new ClientDAO(conn);
                    dao.ajouterClient(client);
                    Platform.runLater(() -> {
                        AlertHelper.showSuccess("Success", "Client '" + client.getNom() + "' added successfully!");
                        loadClients();
                    });
                } catch (SQLException e) {
                    Platform.runLater(() -> AlertHelper.showError("Database Error", e.getMessage()));
                }
            });
            addThread.setDaemon(true);
            addThread.start();
        });
    }
    
    private void showEditClientDialog() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No Selection", "Please select a client to edit");
            return;
        }
        
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Edit Client");
        dialog.setHeaderText("Edit: " + selected.getNom());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        
        TextField nomField = new TextField(selected.getNom());
        TextField addressField = new TextField(selected.getAdresse());
        TextField emailField = new TextField(selected.getEmail());
        TextField phoneField = new TextField(selected.getTelephone());
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Address:"), 0, 1);
        grid.add(addressField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    String nom = nomField.getText().trim();
                    String address = addressField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    
                    if (nom.isEmpty()) throw new IllegalArgumentException("Client name is required");
                    if (email.isEmpty() || !email.contains("@")) throw new IllegalArgumentException("Valid email is required");
                    if (phone.isEmpty()) throw new IllegalArgumentException("Phone number is required");
                    
                    return new Client(selected.getId(), nom, address, email, phone);
                } catch (IllegalArgumentException e) {
                    AlertHelper.showError("Validation Error", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        Optional<Client> result = dialog.showAndWait();
        result.ifPresent(client -> {
            Thread editThread = new Thread(() -> {
                try (Connection conn = DatabaseManager.getConnection()) {
                    ClientDAO dao = new ClientDAO(conn);
                    dao.modifierClient(client);
                    Platform.runLater(() -> {
                        AlertHelper.showSuccess("Success", "Client updated successfully!");
                        loadClients();
                    });
                } catch (SQLException e) {
                    Platform.runLater(() -> AlertHelper.showError("Database Error", e.getMessage()));
                }
            });
            editThread.setDaemon(true);
            editThread.start();
        });
    }
    
    private void showDeleteConfirmation() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No Selection", "Please select a client to delete");
            return;
        }
        
        boolean confirm = AlertHelper.showConfirmation(
            "Delete Client",
            "Are you sure you want to delete '" + selected.getNom() + "'?\nThis action cannot be undone."
        );
        
        if (confirm) {
            Thread deleteThread = new Thread(() -> {
                try (Connection conn = DatabaseManager.getConnection()) {
                    ClientDAO dao = new ClientDAO(conn);
                    dao.supprimerClient(selected.getId());
                    Platform.runLater(() -> {
                        AlertHelper.showSuccess("Success", "Client deleted successfully!");
                        loadClients();
                    });
                } catch (SQLException e) {
                    Platform.runLater(() -> AlertHelper.showError("Database Error", e.getMessage()));
                }
            });
            deleteThread.setDaemon(true);
            deleteThread.start();
        }
    }
    
    private void goBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showDashboardScene();
    }
}
