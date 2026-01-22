package ui.controllers;

import dao.CommandeDAO;
import dao.FournisseurDAO;
import dao.LigneCommandeDAO;
import dao.ProduitDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Commande;
import models.Fournisseur;
import models.LigneCommande;
import models.Produit;
import ui.utils.AlertHelper;
import ui.utils.DatabaseManager;
import ui.utils.SceneManager;
import ui.utils.SessionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersController {

    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Button backButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button addOrderButton;
    @FXML private Button markReceivedButton;
    @FXML private Button addLineButton;
    @FXML private Button removeLineButton;

    @FXML private TableView<Commande> pendingTable;
    @FXML private TableColumn<Commande, Integer> pendIdColumn;
    @FXML private TableColumn<Commande, String> pendSuppColumn;
    @FXML private TableColumn<Commande, LocalDate> pendDateColumn;
    @FXML private TableColumn<Commande, Integer> pendETAColumn;
    @FXML private TableColumn<Commande, String> pendStatusColumn;

    @FXML private TableView<Commande> allTable;
    @FXML private TableColumn<Commande, Integer> allIdColumn;
    @FXML private TableColumn<Commande, String> allSuppColumn;
    @FXML private TableColumn<Commande, LocalDate> allDateColumn;
    @FXML private TableColumn<Commande, Integer> allETAColumn;
    @FXML private TableColumn<Commande, Boolean> allReceivedColumn;
    @FXML private TableColumn<Commande, LocalDate> allRecvDateColumn;

    @FXML private TableView<LigneCommande> linesTable;
    @FXML private TableColumn<LigneCommande, String> lineProdColumn;
    @FXML private TableColumn<LigneCommande, Integer> lineQtyColumn;
    @FXML private TableColumn<LigneCommande, Double> linePriceColumn;

    private final ObservableList<Commande> pendingOrders = FXCollections.observableArrayList();
    private final ObservableList<Commande> allOrders = FXCollections.observableArrayList();
    private final ObservableList<LigneCommande> orderLines = FXCollections.observableArrayList();

    private final Map<Integer, Fournisseur> supplierCache = new HashMap<>();
    private final Map<Integer, Produit> productCache = new HashMap<>();

    @FXML
    public void initialize() {
        setupUserInfo();
        setupTables();
        setupButtons();
        loadOrders();
    }

    private void setupUserInfo() {
        if (SessionManager.isLoggedIn()) {
            welcomeLabel.setText("Welcome, " + SessionManager.getEmployeeName());
        }
    }

    private void setupTables() {
        pendIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        pendSuppColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            getSupplierName(cell.getValue().getIdFournisseur())
        ));
        pendDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        pendETAColumn.setCellValueFactory(new PropertyValueFactory<>("periodeReception"));
        pendStatusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().isRecu() ? "RECEIVED" : "PENDING"
        ));
        pendingTable.setItems(pendingOrders);

        allIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        allSuppColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            getSupplierName(cell.getValue().getIdFournisseur())
        ));
        allDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateCommande"));
        allETAColumn.setCellValueFactory(new PropertyValueFactory<>("periodeReception"));
        allReceivedColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleBooleanProperty(
            cell.getValue().isRecu()
        ).asObject());
        allRecvDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateReception"));
        allTable.setItems(allOrders);

        lineProdColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            getProductName(cell.getValue().getIdProduit())
        ));
        lineQtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        linePriceColumn.setCellValueFactory(new PropertyValueFactory<>("prixAchat"));
        linesTable.setItems(orderLines);

        pendingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                allTable.getSelectionModel().clearSelection();
            }
            onOrderSelected(newSel);
        });
        allTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                pendingTable.getSelectionModel().clearSelection();
            }
            onOrderSelected(newSel);
        });
        linesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) ->
            removeLineButton.setDisable(newSel == null)
        );
    }

    private void setupButtons() {
        backButton.setOnAction(e -> goBack());
        searchButton.setOnAction(e -> searchBySupplier());
        refreshButton.setOnAction(e -> loadOrders());
        addOrderButton.setOnAction(e -> openNewOrderDialog());
        markReceivedButton.setOnAction(e -> markOrderReceived());
        addLineButton.setOnAction(e -> addOrderLineDialog());
        removeLineButton.setOnAction(e -> removeSelectedLine());
    }

    private void loadOrders() {
        statusLabel.setText("Loading orders...");
        Thread t = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                CommandeDAO commandeDAO = new CommandeDAO(conn);
                FournisseurDAO fournisseurDAO = new FournisseurDAO(conn);
                ProduitDAO produitDAO = new ProduitDAO(conn);

                List<Fournisseur> fournisseurs = fournisseurDAO.obtenirTousFournisseurs();
                Map<Integer, Fournisseur> supMap = new HashMap<>();
                for (Fournisseur f : fournisseurs) {
                    supMap.put(f.getId(), f);
                }

                List<Produit> produits = produitDAO.obtenirTousProduits();
                Map<Integer, Produit> prodMap = new HashMap<>();
                for (Produit p : produits) {
                    prodMap.put(p.getId(), p);
                }

                List<Commande> pending = commandeDAO.obtenirCommandesEnAttente();
                List<Commande> all = commandeDAO.obtenirCommandesTrieesParPeriode();

                Platform.runLater(() -> {
                    supplierCache.clear();
                    supplierCache.putAll(supMap);
                    productCache.clear();
                    productCache.putAll(prodMap);
                    pendingOrders.setAll(pending);
                    allOrders.setAll(all);
                    orderLines.clear();
                    markReceivedButton.setDisable(true);
                    removeLineButton.setDisable(true);
                    statusLabel.setText("Orders loaded");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Failed to load orders: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void searchBySupplier() {
        String keyword = searchField.getText() != null ? searchField.getText().trim() : "";
        if (keyword.isEmpty()) {
            loadOrders();
            return;
        }

        statusLabel.setText("Searching...");
        Thread t = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                CommandeDAO commandeDAO = new CommandeDAO(conn);
                FournisseurDAO fournisseurDAO = new FournisseurDAO(conn);
                ProduitDAO produitDAO = new ProduitDAO(conn);

                List<Fournisseur> matchedSuppliers = fournisseurDAO.obtenirFournisseurParNom(keyword);
                Map<Integer, Fournisseur> supMap = new HashMap<>();
                for (Fournisseur f : matchedSuppliers) {
                    supMap.put(f.getId(), f);
                }

                List<Produit> produits = produitDAO.obtenirTousProduits();
                Map<Integer, Produit> prodMap = new HashMap<>();
                for (Produit p : produits) {
                    prodMap.put(p.getId(), p);
                }

                List<Commande> results = new ArrayList<>();
                for (Fournisseur f : matchedSuppliers) {
                    results.addAll(commandeDAO.obtenirCommandesParFournisseur(f.getId()));
                }

                Platform.runLater(() -> {
                    supplierCache.clear();
                    supplierCache.putAll(supMap);
                    productCache.clear();
                    productCache.putAll(prodMap);
                    allOrders.setAll(results);
                    pendingOrders.setAll(results.stream().filter(c -> !c.isRecu()).toList());
                    orderLines.clear();
                    statusLabel.setText("Search completed: " + results.size() + " orders");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Search failed: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void onOrderSelected(Commande selected) {
        if (selected == null) {
            orderLines.clear();
            markReceivedButton.setDisable(true);
            removeLineButton.setDisable(true);
            return;
        }

        markReceivedButton.setDisable(selected.isRecu());
        statusLabel.setText("Loading lines...");
        Thread t = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                LigneCommandeDAO ligneDAO = new LigneCommandeDAO(conn);
                ProduitDAO produitDAO = new ProduitDAO(conn);
                List<LigneCommande> lines = ligneDAO.obtenirLignesCommande(selected.getId());

                Map<Integer, Produit> prodMap = new HashMap<>(productCache);
                List<Produit> produits = produitDAO.obtenirTousProduits();
                for (Produit p : produits) {
                    prodMap.put(p.getId(), p);
                }

                Platform.runLater(() -> {
                    productCache.clear();
                    productCache.putAll(prodMap);
                    orderLines.setAll(lines);
                    removeLineButton.setDisable(lines.isEmpty());
                    statusLabel.setText("Order ready");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Failed to load lines: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void openNewOrderDialog() {
        Thread loadSuppliers = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                FournisseurDAO fournisseurDAO = new FournisseurDAO(conn);
                List<Fournisseur> suppliers = fournisseurDAO.obtenirTousFournisseurs();

                Platform.runLater(() -> showOrderDialogWithSuppliers(suppliers));
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Failed to load suppliers: " + e.getMessage()));
            }
        });
        loadSuppliers.setDaemon(true);
        loadSuppliers.start();
    }

    private void showOrderDialogWithSuppliers(List<Fournisseur> suppliers) {
        Dialog<Commande> dialog = new Dialog<>();
        dialog.setTitle("New Order");
        dialog.setHeaderText("Select supplier and ETA");

        ComboBox<Fournisseur> supplierCombo = new ComboBox<>();
        supplierCombo.getItems().addAll(suppliers);
        supplierCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Fournisseur item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getPrenom() + " " + item.getNom());
            }
        });
        supplierCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Fournisseur item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select supplier" : item.getPrenom() + " " + item.getNom());
            }
        });
        if (!suppliers.isEmpty()) {
            supplierCombo.getSelectionModel().selectFirst();
        }

        TextField etaField = new TextField("7");
        etaField.setPromptText("ETA in days");

        VBox content = new VBox(10, new Label("Supplier"), supplierCombo, new Label("ETA (days)"), etaField);
        content.setPadding(new javafx.geometry.Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Fournisseur selected = supplierCombo.getValue();
                if (selected == null) {
                    AlertHelper.showWarning("No supplier", "Please select a supplier");
                    return null;
                }
                try {
                    int eta = Integer.parseInt(etaField.getText().trim());
                    if (eta <= 0) throw new NumberFormatException();
                    return new Commande(selected.getId(), LocalDate.now(), eta);
                } catch (NumberFormatException ex) {
                    AlertHelper.showError("Validation Error", "ETA must be a positive number");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::createOrder);
    }

    private void createOrder(Commande commande) {
        statusLabel.setText("Creating order...");
        Thread t = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                CommandeDAO commandeDAO = new CommandeDAO(conn);
                int id = commandeDAO.ajouterCommande(commande);
                if (id > 0) {
                    Commande saved = new Commande(id, commande.getIdFournisseur(), commande.getDateCommande(),
                            commande.getPeriodeReception(), false, null);
                    Platform.runLater(() -> {
                        pendingOrders.add(saved);
                        allOrders.add(saved);
                        statusLabel.setText("Order created");
                    });
                } else {
                    Platform.runLater(() -> AlertHelper.showError("Error", "Failed to create order"));
                }
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Failed to create order: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void addOrderLineDialog() {
        Commande selected = getSelectedOrder();
        if (selected == null) {
            AlertHelper.showWarning("No selection", "Please select an order first");
            return;
        }

        Thread loadProducts = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                ProduitDAO produitDAO = new ProduitDAO(conn);
                List<Produit> products = produitDAO.obtenirTousProduits();
                Platform.runLater(() -> showAddLineDialog(selected, products));
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Failed to load products: " + e.getMessage()));
            }
        });
        loadProducts.setDaemon(true);
        loadProducts.start();
    }

    private void showAddLineDialog(Commande commande, List<Produit> products) {
        Dialog<LigneCommande> dialog = new Dialog<>();
        dialog.setTitle("Add Order Line");
        dialog.setHeaderText("Order #" + commande.getId());

        ComboBox<Produit> productCombo = new ComboBox<>();
        productCombo.getItems().addAll(products);
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
        if (!products.isEmpty()) {
            productCombo.getSelectionModel().selectFirst();
        }

        TextField qtyField = new TextField();
        qtyField.setPromptText("Quantity");
        TextField priceField = new TextField();
        priceField.setPromptText("Buy price");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Product"), productCombo);
        grid.addRow(1, new Label("Quantity"), qtyField);
        grid.addRow(2, new Label("Buy Price"), priceField);
        grid.setPadding(new javafx.geometry.Insets(10));

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    int qty = Integer.parseInt(qtyField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());
                    Produit produit = productCombo.getValue();
                    if (produit == null) {
                        AlertHelper.showWarning("No product", "Please select a product");
                        return null;
                    }
                    if (qty <= 0 || price <= 0) throw new NumberFormatException();
                    return new LigneCommande(commande.getId(), produit.getId(), qty, price);
                } catch (NumberFormatException ex) {
                    AlertHelper.showError("Validation Error", "Quantity and price must be positive numbers");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::saveOrderLine);
    }

    private void saveOrderLine(LigneCommande ligne) {
        if (ligne == null) {
            return;
        }
        statusLabel.setText("Saving line...");
        Thread t = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                LigneCommandeDAO dao = new LigneCommandeDAO(conn);
                dao.ajouterLigneCommande(ligne);
                Platform.runLater(() -> {
                    orderLines.add(ligne);
                    removeLineButton.setDisable(orderLines.isEmpty());
                    statusLabel.setText("Line added");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Failed to add line: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void removeSelectedLine() {
        Commande order = getSelectedOrder();
        LigneCommande selectedLine = linesTable.getSelectionModel().getSelectedItem();
        if (order == null || selectedLine == null) {
            AlertHelper.showWarning("No selection", "Select a line to remove");
            return;
        }

        Thread t = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                LigneCommandeDAO dao = new LigneCommandeDAO(conn);
                dao.supprimerLigneCommande(order.getId(), selectedLine.getIdProduit());
                Platform.runLater(() -> {
                    orderLines.remove(selectedLine);
                    removeLineButton.setDisable(orderLines.isEmpty());
                    statusLabel.setText("Line removed");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Failed to remove line: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void markOrderReceived() {
        Commande selected = getSelectedOrder();
        if (selected == null) {
            AlertHelper.showWarning("No selection", "Please select an order");
            return;
        }

        Thread t = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                CommandeDAO dao = new CommandeDAO(conn);
                dao.marquerCommandeRecue(selected.getId());
                Platform.runLater(() -> {
                    statusLabel.setText("Order marked received");
                    loadOrders();
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Failed to mark received: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private Commande getSelectedOrder() {
        Commande sel = pendingTable.getSelectionModel().getSelectedItem();
        if (sel != null) return sel;
        return allTable.getSelectionModel().getSelectedItem();
    }

    private String getSupplierName(int supplierId) {
        Fournisseur f = supplierCache.get(supplierId);
        if (f != null) {
            return f.getPrenom() + " " + f.getNom();
        }
        return "Supplier #" + supplierId;
    }

    private String getProductName(int productId) {
        Produit p = productCache.get(productId);
        if (p != null) {
            return p.getNom();
        }
        return "Product #" + productId;
    }

    private void goBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showDashboardScene();
    }
}
