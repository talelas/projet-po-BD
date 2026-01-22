package ui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Produit;
import dao.ProduitDAO;
import ui.utils.SessionManager;
import ui.utils.AlertHelper;
import ui.utils.SceneManager;
import ui.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductsController {
    
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
    @FXML private Button criticalStockButton;
    @FXML private TableView<Produit> productsTable;
    @FXML private TableColumn<Produit, Integer> idColumn;
    @FXML private TableColumn<Produit, String> nameColumn;
    @FXML private TableColumn<Produit, Double> priceColumn;
    @FXML private TableColumn<Produit, Integer> quantityColumn;
    @FXML private TableColumn<Produit, Integer> minQuantityColumn;
    @FXML private TableColumn<Produit, String> categoryColumn;
    @FXML private TableColumn<Produit, String> statusColumn;
    
    private ProduitDAO produitDAO;
    
    private ObservableList<Produit> productsList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupUserInfo();
        setupTableColumns();
        setupButtonHandlers();
        loadProducts();
    }
    
    private void setupUserInfo() {
        if (SessionManager.isLoggedIn()) {
            welcomeLabel.setText("Welcome, " + SessionManager.getEmployeeName());
        }
    }
    
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        minQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantiteMinimale"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        statusColumn.setCellValueFactory(cellData -> {
            Produit p = cellData.getValue();
            if (p.getQuantite() <= 0) {
                return new javafx.beans.property.SimpleStringProperty("OUT OF STOCK");
            } else if (p.getQuantite() <= p.getQuantiteMinimale()) {
                return new javafx.beans.property.SimpleStringProperty("CRITICAL");
            } else {
                return new javafx.beans.property.SimpleStringProperty("OK");
            }
        });
        
        statusColumn.setCellFactory(column -> new TableCell<Produit, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("OUT OF STOCK")) {
                        setStyle("-fx-text-fill: white; -fx-background-color: #D32F2F; -fx-font-weight: bold;");
                    } else if (item.equals("CRITICAL")) {
                        setStyle("-fx-text-fill: white; -fx-background-color: #FF9800; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: white; -fx-background-color: #4CAF50; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        productsTable.setItems(productsList);
        
        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });
    }
    
    private void setupButtonHandlers() {
        backButton.setOnAction(e -> goBack());
        refreshButton.setOnAction(e -> loadProducts());
        searchButton.setOnAction(e -> searchProducts());
        addButton.setOnAction(e -> showAddProductDialog());
        editButton.setOnAction(e -> showEditProductDialog());
        deleteButton.setOnAction(e -> showDeleteConfirmation());
        criticalStockButton.setOnAction(e -> showCriticalStock());
        searchField.setOnAction(e -> searchProducts());
    }
    
    /**
     * Show dialog to add a new product
     */
    private void showAddProductDialog() {
        Dialog<Produit> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Enter product details");
        
        GridPane grid = createProductFormGrid();
        TextField nomField = (TextField) grid.lookup("#nomField");
        TextField marqueField = (TextField) grid.lookup("#marqueField");
        TextField prixField = (TextField) grid.lookup("#prixField");
        TextField tvaField = (TextField) grid.lookup("#tvaField");
        TextField quantiteField = (TextField) grid.lookup("#quantiteField");
        TextField quantiteMinField = (TextField) grid.lookup("#quantiteMinField");
        ComboBox<String> typeCombo = (ComboBox<String>) grid.lookup("#typeCombo");
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    String nom = nomField.getText().trim();
                    String marque = marqueField.getText().trim();
                    double prix = Double.parseDouble(prixField.getText());
                    double tva = Double.parseDouble(tvaField.getText());
                    int quantite = Integer.parseInt(quantiteField.getText());
                    int quantiteMin = Integer.parseInt(quantiteMinField.getText());
                    String type = typeCombo.getValue();
                    
                    if (nom.isEmpty()) throw new IllegalArgumentException("Product name is required");
                    if (marque.isEmpty()) throw new IllegalArgumentException("Brand is required");
                    if (prix <= 0) throw new IllegalArgumentException("Price must be > 0");
                    if (quantite < 0) throw new IllegalArgumentException("Quantity cannot be negative");
                    if (quantiteMin < 0) throw new IllegalArgumentException("Min quantity cannot be negative");
                    
                    return new Produit(nom, marque, quantite, quantiteMin, prix, tva, type);
                } catch (NumberFormatException e) {
                    AlertHelper.showError("Input Error", "Price, TVA, and quantities must be numbers");
                    return null;
                } catch (IllegalArgumentException e) {
                    AlertHelper.showError("Validation Error", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        Optional<Produit> result = dialog.showAndWait();
        result.ifPresent(product -> {
            Thread addThread = new Thread(() -> {
                try (Connection conn = DatabaseManager.getConnection()) {
                    ProduitDAO dao = new ProduitDAO(conn);
                    dao.ajouterProduit(product);
                    Platform.runLater(() -> {
                        AlertHelper.showSuccess("Success", "Product '" + product.getNom() + "' added successfully!");
                        loadProducts();
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
     * Show dialog to edit selected product
     */
    private void showEditProductDialog() {
        Produit selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No Selection", "Please select a product to edit");
            return;
        }
        
        Dialog<Object[]> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.setHeaderText("Edit: " + selected.getNom() + " (Current Qty: " + selected.getQuantite() + ")");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        
        // Only allow editing price - Quantity is managed through orders/sales
        Label priceLabel = new Label("New Price:");
        TextField priceField = new TextField(String.valueOf(selected.getPrix()));
        
        Label quantiteMinLabel = new Label("Min Quantity Threshold:");
        TextField quantiteMinField = new TextField(String.valueOf(selected.getQuantiteMinimale()));
        
        Label noteLabel = new Label("Note: Quantity is managed through Orders and Sales only");
        noteLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #666666; -fx-font-size: 10px;");
        
        grid.add(priceLabel, 0, 0);
        grid.add(priceField, 1, 0);
        grid.add(quantiteMinLabel, 0, 1);
        grid.add(quantiteMinField, 1, 1);
        grid.add(noteLabel, 0, 2, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    double newPrice = Double.parseDouble(priceField.getText());
                    int newQuantiteMin = Integer.parseInt(quantiteMinField.getText());
                    
                    if (newPrice <= 0) throw new IllegalArgumentException("Price must be > 0");
                    if (newQuantiteMin < 0) throw new IllegalArgumentException("Min quantity cannot be negative");
                    
                    return new Object[]{selected.getId(), newPrice, newQuantiteMin};
                } catch (NumberFormatException e) {
                    AlertHelper.showError("Input Error", "Price and quantities must be numbers");
                    return null;
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
                double newPrice = (double) values[1];
                int newQuantiteMin = (int) values[2];
                
                Thread editThread = new Thread(() -> {
                    try (Connection conn = DatabaseManager.getConnection()) {
                        ProduitDAO dao = new ProduitDAO(conn);
                        dao.modifierPrix(id, newPrice);
                        dao.mettreAJourQuantiteMinimale(id, newQuantiteMin);
                        Platform.runLater(() -> {
                            AlertHelper.showSuccess("Success", "Product updated successfully!");
                            loadProducts();
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
     * Show delete confirmation and delete product
     */
    private void showDeleteConfirmation() {
        Produit selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No Selection", "Please select a product to delete");
            return;
        }
        
        boolean confirm = AlertHelper.showConfirmation(
            "Delete Product",
            "Are you sure you want to delete '" + selected.getNom() + "'?\nThis action cannot be undone."
        );
        
        if (confirm) {
            Thread deleteThread = new Thread(() -> {
                try (Connection conn = DatabaseManager.getConnection()) {
                    ProduitDAO dao = new ProduitDAO(conn);
                    dao.supprimerProduit(selected.getId());
                    Platform.runLater(() -> {
                        AlertHelper.showSuccess("Success", "Product deleted successfully!");
                        loadProducts();
                    });
                } catch (SQLException e) {
                    Platform.runLater(() -> AlertHelper.showError("Database Error", e.getMessage()));
                }
            });
            deleteThread.setDaemon(true);
            deleteThread.start();
        }
    }
    
    /**
     * Create a form grid for adding products
     */
    private GridPane createProductFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        
        TextField nomField = new TextField();
        nomField.setId("nomField");
        nomField.setPromptText("Product Name");
        
        TextField marqueField = new TextField();
        marqueField.setId("marqueField");
        marqueField.setPromptText("Brand");
        
        TextField prixField = new TextField();
        prixField.setId("prixField");
        prixField.setPromptText("Price");
        
        TextField tvaField = new TextField("0");
        tvaField.setId("tvaField");
        tvaField.setPromptText("TVA (%)");
        
        TextField quantiteField = new TextField("0");
        quantiteField.setId("quantiteField");
        quantiteField.setPromptText("Quantity");
        
        TextField quantiteMinField = new TextField("5");
        quantiteMinField.setId("quantiteMinField");
        quantiteMinField.setPromptText("Min Quantity");
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.setId("typeCombo");
        typeCombo.getItems().addAll("Medication", "Supplement", "Medical Device", "Other");
        typeCombo.setValue("Medication");
        
        grid.add(new Label("Product Name:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Brand:"), 0, 1);
        grid.add(marqueField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(prixField, 1, 2);
        grid.add(new Label("TVA (%):"), 0, 3);
        grid.add(tvaField, 1, 3);
        grid.add(new Label("Quantity:"), 0, 4);
        grid.add(quantiteField, 1, 4);
        grid.add(new Label("Min Quantity:"), 0, 5);
        grid.add(quantiteMinField, 1, 5);
        grid.add(new Label("Type:"), 0, 6);
        grid.add(typeCombo, 1, 6);
        
        return grid;
    }
    
    private void loadProducts() {
        statusLabel.setText("Loading products...");
        Thread loadThread = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                ProduitDAO dao = new ProduitDAO(conn);
                List<Produit> products = dao.obtenirTousProduits();
                
                Platform.runLater(() -> {
                    productsList.clear();
                    productsList.addAll(products);
                    countLabel.setText("Total: " + products.size() + " products");
                    statusLabel.setText("Products loaded");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    AlertHelper.showError("Database Error", "Failed to load products");
                    statusLabel.setText("Error loading products");
                });
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }
    
    private void searchProducts() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadProducts();
            return;
        }
        
        statusLabel.setText("Searching...");
        Thread searchThread = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                ProduitDAO dao = new ProduitDAO(conn);
                List<Produit> allProducts = dao.obtenirTousProduits();
                List<Produit> filtered = allProducts.stream()
                    .filter(p -> p.getNom().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
                
                Platform.runLater(() -> {
                    productsList.clear();
                    productsList.addAll(filtered);
                    countLabel.setText("Found: " + filtered.size() + " products");
                    statusLabel.setText("Search completed");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Error", "Search failed"));
            }
        });
        searchThread.setDaemon(true);
        searchThread.start();
    }
    
    private void showCriticalStock() {
        statusLabel.setText("Loading critical stock...");
        Thread loadThread = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                ProduitDAO dao = new ProduitDAO(conn);
                List<Produit> critical = dao.obtenirProduitsEnStockCritique();
                
                Platform.runLater(() -> {
                    productsList.clear();
                    productsList.addAll(critical);
                    countLabel.setText("Critical: " + critical.size() + " products");
                    statusLabel.setText("Showing critical stock");
                    
                    if (critical.isEmpty()) {
                        AlertHelper.showSuccess("Stock Status", "No products in critical stock! ✓");
                    }
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Error", "Failed to load critical stock"));
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }
    
    private void goBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showDashboardScene();
    }
}
