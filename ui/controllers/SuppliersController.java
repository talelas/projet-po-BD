package ui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Fournisseur;
import models.Produit;
import dao.FournisseurDAO;
import dao.ProduitDAO;
import ui.utils.SessionManager;
import ui.utils.AlertHelper;
import ui.utils.SceneManager;
import ui.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

public class SuppliersController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Label countLabel;
    @FXML private TextField searchField;
    @FXML private TextField productSearchField;
    @FXML private Button backButton;
    @FXML private Button searchButton;
    @FXML private Button searchByProductButton;
    @FXML private Button refreshButton;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button linkProductButton;
    @FXML private Button viewProductsButton;
    @FXML private TableView<Fournisseur> suppliersTable;
    @FXML private TableColumn<Fournisseur, Integer> idColumn;
    @FXML private TableColumn<Fournisseur, String> nameColumn;
    @FXML private TableColumn<Fournisseur, String> prenomColumn;
    @FXML private TableColumn<Fournisseur, String> emailColumn;
    @FXML private TableColumn<Fournisseur, String> phoneColumn;
    
    private ObservableList<Fournisseur> suppliersList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupUserInfo();
        setupTableColumns();
        setupButtonHandlers();
        loadSuppliers();
    }
    
    private void setupUserInfo() {
        if (SessionManager.isLoggedIn()) {
            welcomeLabel.setText("Welcome, " + SessionManager.getEmployeeName());
        }
    }
    
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        suppliersTable.setItems(suppliersList);
        
        suppliersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
            viewProductsButton.setDisable(!hasSelection);
            linkProductButton.setDisable(!hasSelection);
        });
    }
    
    private void setupButtonHandlers() {
        backButton.setOnAction(e -> goBack());
        refreshButton.setOnAction(e -> loadSuppliers());
        searchButton.setOnAction(e -> searchSuppliers());
        searchByProductButton.setOnAction(e -> searchSuppliersByProduct());
        addButton.setOnAction(e -> showAddSupplierDialog());
        editButton.setOnAction(e -> showEditSupplierDialog());
        deleteButton.setOnAction(e -> showDeleteConfirmation());
        linkProductButton.setOnAction(e -> showLinkProductDialog());
        viewProductsButton.setOnAction(e -> showSupplierProducts());
        searchField.setOnAction(e -> searchSuppliers());
        productSearchField.setOnAction(e -> searchSuppliersByProduct());
    }
    
    private void loadSuppliers() {
        statusLabel.setText("Loading suppliers...");
        Thread loadThread = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                FournisseurDAO dao = new FournisseurDAO(conn);
                List<Fournisseur> suppliers = dao.obtenirTousFournisseurs();
                
                Platform.runLater(() -> {
                    suppliersList.clear();
                    suppliersList.addAll(suppliers);
                    countLabel.setText("Total: " + suppliers.size() + " suppliers");
                    statusLabel.setText("Suppliers loaded");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    AlertHelper.showError("Database Error", "Failed to load suppliers");
                    statusLabel.setText("Error loading suppliers");
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }
    
    private void searchSuppliers() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadSuppliers();
            return;
        }
        
        statusLabel.setText("Searching...");
        Thread searchThread = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                FournisseurDAO dao = new FournisseurDAO(conn);
                List<Fournisseur> filtered = dao.obtenirFournisseurParNom(keyword);
                
                Platform.runLater(() -> {
                    suppliersList.clear();
                    suppliersList.addAll(filtered);
                    countLabel.setText("Found: " + filtered.size() + " suppliers");
                    statusLabel.setText("Search completed");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Error", "Search failed"));
            }
        });
        searchThread.setDaemon(true);
        searchThread.start();
    }

    private void searchSuppliersByProduct() {
        String keyword = productSearchField.getText().trim();
        if (keyword.isEmpty()) {
            statusLabel.setText("Enter product name to search suppliers");
            return;
        }

        statusLabel.setText("Searching suppliers by product...");
        Thread searchThread = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                ProduitDAO produitDAO = new ProduitDAO(conn);
                FournisseurDAO fournisseurDAO = new FournisseurDAO(conn);

                List<Produit> products = produitDAO.obtenirProduitParNom(keyword);
                Set<Integer> supplierIds = new HashSet<>();
                ObservableList<Fournisseur> matched = FXCollections.observableArrayList();

                for (Produit p : products) {
                    List<Fournisseur> suppliers = fournisseurDAO.obtenirFournisseursDuProduit(p.getId());
                    for (Fournisseur f : suppliers) {
                        if (supplierIds.add(f.getId())) {
                            matched.add(f);
                        }
                    }
                }

                Platform.runLater(() -> {
                    suppliersList.clear();
                    suppliersList.addAll(matched);
                    countLabel.setText("Found: " + matched.size() + " suppliers");
                    statusLabel.setText("Search by product completed");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Error", "Search failed: " + e.getMessage()));
            }
        });
        searchThread.setDaemon(true);
        searchThread.start();
    }
    
    private void showAddSupplierDialog() {
        Dialog<Fournisseur> dialog = new Dialog<>();
        dialog.setTitle("Add New Supplier");
        dialog.setHeaderText("Enter supplier details");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        
        TextField nomField = new TextField();
        nomField.setPromptText("Last Name");
        
        TextField prenomField = new TextField();
        prenomField.setPromptText("First Name");
        
        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");
        
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        
        grid.add(new Label("Last Name:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(prenomField, 1, 1);
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
                    String prenom = prenomField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    
                    if (nom.isEmpty()) throw new IllegalArgumentException("Last name is required");
                    if (prenom.isEmpty()) throw new IllegalArgumentException("First name is required");
                    if (email.isEmpty() || !email.contains("@")) throw new IllegalArgumentException("Valid email is required");
                    if (phone.isEmpty()) throw new IllegalArgumentException("Phone number is required");
                    
                    return new Fournisseur(nom, prenom, phone, email);
                } catch (IllegalArgumentException e) {
                    AlertHelper.showError("Validation Error", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        Optional<Fournisseur> result = dialog.showAndWait();
        result.ifPresent(supplier -> {
            Thread addThread = new Thread(() -> {
                try (Connection conn = DatabaseManager.getConnection()) {
                    FournisseurDAO dao = new FournisseurDAO(conn);
                    dao.ajouterFournisseur(supplier);
                    Platform.runLater(() -> {
                        AlertHelper.showSuccess("Success", "Supplier '" + supplier.getPrenom() + " " + supplier.getNom() + "' added successfully!");
                        loadSuppliers();
                    });
                } catch (SQLException e) {
                    Platform.runLater(() -> AlertHelper.showError("Database Error", e.getMessage()));
                }
            });
            addThread.setDaemon(true);
            addThread.start();
        });
    }
    
    private void showEditSupplierDialog() {
        Fournisseur selected = suppliersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No Selection", "Please select a supplier to edit");
            return;
        }
        
        Dialog<Fournisseur> dialog = new Dialog<>();
        dialog.setTitle("Edit Supplier");
        dialog.setHeaderText("Edit: " + selected.getPrenom() + " " + selected.getNom());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        
        TextField nomField = new TextField(selected.getNom());
        TextField prenomField = new TextField(selected.getPrenom());
        TextField emailField = new TextField(selected.getEmail());
        TextField phoneField = new TextField(selected.getTelephone());
        
        grid.add(new Label("Last Name:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(prenomField, 1, 1);
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
                    String prenom = prenomField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    
                    if (nom.isEmpty()) throw new IllegalArgumentException("Last name is required");
                    if (prenom.isEmpty()) throw new IllegalArgumentException("First name is required");
                    if (email.isEmpty() || !email.contains("@")) throw new IllegalArgumentException("Valid email is required");
                    if (phone.isEmpty()) throw new IllegalArgumentException("Phone number is required");
                    
                    return new Fournisseur(selected.getId(), nom, prenom, phone, email);
                } catch (IllegalArgumentException e) {
                    AlertHelper.showError("Validation Error", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        Optional<Fournisseur> result = dialog.showAndWait();
        result.ifPresent(supplier -> {
            Thread editThread = new Thread(() -> {
                try (Connection conn = DatabaseManager.getConnection()) {
                    FournisseurDAO dao = new FournisseurDAO(conn);
                    dao.modifierFournisseur(supplier);
                    Platform.runLater(() -> {
                        AlertHelper.showSuccess("Success", "Supplier updated successfully!");
                        loadSuppliers();
                    });
                } catch (SQLException e) {
                    Platform.runLater(() -> AlertHelper.showError("Database Error", e.getMessage()));
                }
            });
            editThread.setDaemon(true);
            editThread.start();
        });
    }

    private void showLinkProductDialog() {
        Fournisseur selected = suppliersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No Selection", "Please select a supplier first");
            return;
        }

        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Link Product to Supplier");
        dialog.setHeaderText("Supplier: " + selected.getPrenom() + " " + selected.getNom());

        ComboBox<Produit> productCombo = new ComboBox<>();
        productCombo.setPrefWidth(350);
        productCombo.setPromptText("Select product");
        productCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom() + " - " + item.getMarque());
            }
        });
        productCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Produit item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select product" : item.getNom() + " - " + item.getMarque());
            }
        });

        dialog.getDialogPane().setContent(new VBox(10, new Label("Choose a product to link"), productCombo));
        dialog.getDialogPane().setPadding(new Insets(15));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // load products
        Thread loadThread = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                ProduitDAO dao = new ProduitDAO(conn);
                List<Produit> products = dao.obtenirTousProduits();
                Platform.runLater(() -> productCombo.getItems().setAll(products));
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Failed to load products"));
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Produit p = productCombo.getValue();
                if (p == null) {
                    AlertHelper.showWarning("No Product", "Please select a product");
                    return null;
                }
                return p.getId();
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(productId -> {
            Thread linkThread = new Thread(() -> {
                try (Connection conn = DatabaseManager.getConnection()) {
                    FournisseurDAO dao = new FournisseurDAO(conn);
                    dao.lierProduitFournisseur(productId, selected.getId());
                    Platform.runLater(() -> {
                        AlertHelper.showSuccess("Linked", "Product linked to supplier successfully");
                        statusLabel.setText("Linked product " + productId + " to supplier " + selected.getId());
                    });
                } catch (SQLException e) {
                    Platform.runLater(() -> AlertHelper.showError("Database Error", e.getMessage()));
                }
            });
            linkThread.setDaemon(true);
            linkThread.start();
        });
    }
    
    private void showDeleteConfirmation() {
        Fournisseur selected = suppliersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No Selection", "Please select a supplier to delete");
            return;
        }
        
        boolean confirm = AlertHelper.showConfirmation(
            "Delete Supplier",
            "Are you sure you want to delete '" + selected.getPrenom() + " " + selected.getNom() + "'?\nThis action cannot be undone."
        );
        
        if (confirm) {
            Thread deleteThread = new Thread(() -> {
                try (Connection conn = DatabaseManager.getConnection()) {
                    FournisseurDAO dao = new FournisseurDAO(conn);
                    dao.supprimerFournisseur(selected.getId());
                    Platform.runLater(() -> {
                        AlertHelper.showSuccess("Success", "Supplier deleted successfully!");
                        loadSuppliers();
                    });
                } catch (SQLException e) {
                    Platform.runLater(() -> AlertHelper.showError("Database Error", e.getMessage()));
                }
            });
            deleteThread.setDaemon(true);
            deleteThread.start();
        }
    }
    
    private void showSupplierProducts() {
        Fournisseur selected = suppliersTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Supplier Products");
        dialog.setHeaderText("Products supplied by: " + selected.getPrenom() + " " + selected.getNom());
        
        ListView<String> productList = new ListView<>();
        productList.setPrefSize(400, 300);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.getChildren().addAll(
            new Label("Loading products..."),
            productList
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        Thread loadThread = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                FournisseurDAO dao = new FournisseurDAO(conn);
                List<Produit> products = dao.obtenirProduitsDuFournisseur(selected.getId());
                
                Platform.runLater(() -> {
                    ((Label)((VBox)dialog.getDialogPane().getContent()).getChildren().get(0))
                        .setText("Total: " + products.size() + " products");
                    
                    ObservableList<String> items = FXCollections.observableArrayList();
                    for (Produit p : products) {
                        items.add(p.getNom() + " - " + p.getMarque() + " (Qty: " + p.getQuantite() + ")");
                    }
                    productList.setItems(items);
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    ((Label)((VBox)dialog.getDialogPane().getContent()).getChildren().get(0))
                        .setText("Error loading products");
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
        
        dialog.showAndWait();
    }
    
    private void goBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showDashboardScene();
    }
}
