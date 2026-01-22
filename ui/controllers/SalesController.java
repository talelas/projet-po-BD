package ui.controllers;

import dao.ClientDAO;
import dao.LigneVenteDAO;
import dao.ProduitDAO;
import dao.VenteDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import models.Client;
import models.LigneVente;
import models.Produit;
import models.Vente;
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

public class SalesController {

    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Button backButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button newSaleButton;
    @FXML private Button confirmSaleButton;
    @FXML private Button addLineButton;
    @FXML private Button removeLineButton;

    @FXML private TableView<LigneVente> cartTable;
    @FXML private TableColumn<LigneVente, String> cartProdColumn;
    @FXML private TableColumn<LigneVente, Integer> cartQtyColumn;
    @FXML private TableColumn<LigneVente, Double> cartPriceColumn;
    @FXML private TableColumn<LigneVente, Double> cartTotalColumn;

    @FXML private TableView<Vente> historyTable;
    @FXML private TableColumn<Vente, Integer> histIdColumn;
    @FXML private TableColumn<Vente, String> histClientColumn;
    @FXML private TableColumn<Vente, LocalDate> histDateColumn;
    @FXML private TableColumn<Vente, Double> histTotalColumn;

    private final ObservableList<LigneVente> cartLines = FXCollections.observableArrayList();
    private final ObservableList<Vente> history = FXCollections.observableArrayList();
    private final Map<Integer, Produit> productCache = new HashMap<>();
    private final Map<Integer, String> clientNames = new HashMap<>();
    private final Map<Integer, Double> saleTotals = new HashMap<>();

    private Client currentClient;

    @FXML
    public void initialize() {
        setupUserInfo();
        setupTables();
        setupButtons();
        loadHistory();
    }

    private void setupUserInfo() {
        if (SessionManager.isLoggedIn()) {
            welcomeLabel.setText("Welcome, " + SessionManager.getEmployeeName());
        }
    }

    private void setupTables() {
        cartProdColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            getProductName(cell.getValue().getIdProduit())
        ));
        cartQtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        cartPriceColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnite"));
        cartTotalColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(
            cell.getValue().getQuantite() * cell.getValue().getPrixUnite()
        ).asObject());
        cartTable.setItems(cartLines);

        histIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        histClientColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            getClientName(cell.getValue().getIdClient())
        ));
        histDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateFacture"));
        histTotalColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(
            saleTotals.getOrDefault(cell.getValue().getId(), 0.0)
        ).asObject());
        historyTable.setItems(history);

        cartTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) ->
            removeLineButton.setDisable(newSel == null)
        );
    }

    private void setupButtons() {
        backButton.setOnAction(e -> goBack());
        searchButton.setOnAction(e -> searchByClient());
        refreshButton.setOnAction(e -> loadHistory());
        newSaleButton.setOnAction(e -> startNewSale());
        confirmSaleButton.setOnAction(e -> confirmSale());
        addLineButton.setOnAction(e -> addLineDialog());
        removeLineButton.setOnAction(e -> removeSelectedLine());
        confirmSaleButton.setDisable(true);
        removeLineButton.setDisable(true);
    }

    private void loadHistory() {
        statusLabel.setText("Loading sales...");
        Thread t = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                VenteDAO venteDAO = new VenteDAO(conn);
                ClientDAO clientDAO = new ClientDAO(conn);
                LigneVenteDAO ligneVenteDAO = new LigneVenteDAO(conn);
                ProduitDAO produitDAO = new ProduitDAO(conn);

                List<Client> clients = clientDAO.obtenirTousClients();
                Map<Integer, String> clientMap = new HashMap<>();
                for (Client c : clients) {
                    clientMap.put(c.getId(), c.getNom());
                }

                List<Produit> produits = produitDAO.obtenirTousProduits();
                Map<Integer, Produit> prodMap = new HashMap<>();
                for (Produit p : produits) {
                    prodMap.put(p.getId(), p);
                }

                List<Vente> ventes = venteDAO.obtenirToutesVentes();
                Map<Integer, Double> totals = new HashMap<>();
                for (Vente v : ventes) {
                    double total = 0;
                    List<LigneVente> lines = ligneVenteDAO.obtenirLignesVente(v.getId());
                    for (LigneVente l : lines) {
                        total += l.getQuantite() * l.getPrixUnite();
                    }
                    totals.put(v.getId(), total);
                }

                Platform.runLater(() -> {
                    clientNames.clear();
                    clientNames.putAll(clientMap);
                    productCache.clear();
                    productCache.putAll(prodMap);
                    saleTotals.clear();
                    saleTotals.putAll(totals);
                    history.setAll(ventes);
                    statusLabel.setText("Sales loaded");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Failed to load sales: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void searchByClient() {
        String keyword = searchField.getText() != null ? searchField.getText().trim() : "";
        if (keyword.isEmpty()) {
            loadHistory();
            return;
        }

        statusLabel.setText("Searching...");
        Thread t = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                VenteDAO venteDAO = new VenteDAO(conn);
                ClientDAO clientDAO = new ClientDAO(conn);
                LigneVenteDAO ligneVenteDAO = new LigneVenteDAO(conn);

                List<Client> clients = clientDAO.obtenirClientParNom(keyword);
                Map<Integer, String> clientMap = new HashMap<>();
                for (Client c : clients) {
                    clientMap.put(c.getId(), c.getNom());
                }

                List<Vente> ventes = new ArrayList<>();
                for (Client c : clients) {
                    ventes.addAll(venteDAO.obtenirVentesParClient(c.getId()));
                }

                Map<Integer, Double> totals = new HashMap<>();
                for (Vente v : ventes) {
                    double total = 0;
                    List<LigneVente> lines = ligneVenteDAO.obtenirLignesVente(v.getId());
                    for (LigneVente l : lines) {
                        total += l.getQuantite() * l.getPrixUnite();
                    }
                    totals.put(v.getId(), total);
                }

                Platform.runLater(() -> {
                    clientNames.clear();
                    clientNames.putAll(clientMap);
                    saleTotals.clear();
                    saleTotals.putAll(totals);
                    history.setAll(ventes);
                    statusLabel.setText("Search completed: " + ventes.size() + " sales");
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Search failed: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void startNewSale() {
        statusLabel.setText("Loading clients...");
        Thread t = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                ClientDAO clientDAO = new ClientDAO(conn);
                List<Client> clients = clientDAO.obtenirTousClients();
                Platform.runLater(() -> showNewSaleDialog(clients));
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Failed to load clients: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void showNewSaleDialog(List<Client> clients) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("New Sale");
        dialog.setHeaderText("Select client for the sale");

        ComboBox<Client> clientCombo = new ComboBox<>();
        clientCombo.getItems().addAll(clients);
        clientCombo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });
        clientCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select client" : item.getNom());
            }
        });
        if (!clients.isEmpty()) {
            clientCombo.getSelectionModel().selectFirst();
        }

        dialog.getDialogPane().setContent(clientCombo);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return clientCombo.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(client -> {
            currentClient = client;
            cartLines.clear();
            confirmSaleButton.setDisable(false);
            removeLineButton.setDisable(true);
            statusLabel.setText("Sale started for " + client.getNom());
        });
    }

    private void addLineDialog() {
        if (currentClient == null) {
            AlertHelper.showWarning("No sale", "Start a sale first");
            return;
        }

        Thread t = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                ProduitDAO produitDAO = new ProduitDAO(conn);
                List<Produit> produits = produitDAO.obtenirTousProduits();
                Platform.runLater(() -> showAddLineDialog(produits));
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Failed to load products: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void showAddLineDialog(List<Produit> produits) {
        Dialog<LigneVente> dialog = new Dialog<>();
        dialog.setTitle("Add Sale Line");
        dialog.setHeaderText("Choose product and quantity");

        ComboBox<Produit> productCombo = new ComboBox<>();
        productCombo.getItems().addAll(produits);
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
        if (!produits.isEmpty()) {
            productCombo.getSelectionModel().selectFirst();
        }

        TextField qtyField = new TextField();
        qtyField.setPromptText("Quantity");
        TextField priceField = new TextField();
        priceField.setPromptText("Unit price");

        // Auto-populate price when product is selected
        productCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                priceField.setText(String.valueOf(newVal.getPrix()));
            }
        });
        // Set initial price if a product is already selected
        if (productCombo.getValue() != null) {
            priceField.setText(String.valueOf(productCombo.getValue().getPrix()));
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Product"), productCombo);
        grid.addRow(1, new Label("Quantity"), qtyField);
        grid.addRow(2, new Label("Unit Price"), priceField);
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
                    return new LigneVente(0, produit.getId(), qty, price);
                } catch (NumberFormatException ex) {
                    AlertHelper.showError("Validation Error", "Quantity and price must be positive numbers");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(line -> {
            cartLines.add(line);
            removeLineButton.setDisable(cartLines.isEmpty());
            statusLabel.setText("Line added");
        });
    }

    private void removeSelectedLine() {
        LigneVente selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("No selection", "Select a line to remove");
            return;
        }
        cartLines.remove(selected);
        removeLineButton.setDisable(cartLines.isEmpty());
        statusLabel.setText("Line removed");
    }

    private void confirmSale() {
        if (currentClient == null) {
            AlertHelper.showWarning("No sale", "Start a sale first");
            return;
        }
        if (cartLines.isEmpty()) {
            AlertHelper.showWarning("Empty cart", "Add at least one line");
            return;
        }

        statusLabel.setText("Saving sale...");
        Thread t = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                VenteDAO venteDAO = new VenteDAO(conn);
                LigneVenteDAO ligneVenteDAO = new LigneVenteDAO(conn);
                int venteId = venteDAO.ajouterVente(new Vente(currentClient.getId(), LocalDate.now()));
                if (venteId > 0) {
                    for (LigneVente line : cartLines) {
                        LigneVente toSave = new LigneVente(venteId, line.getIdProduit(), line.getQuantite(), line.getPrixUnite());
                        ligneVenteDAO.ajouterLigneVente(toSave);
                    }
                }
                Platform.runLater(() -> {
                    cartLines.clear();
                    currentClient = null;
                    confirmSaleButton.setDisable(true);
                    removeLineButton.setDisable(true);
                    statusLabel.setText("Sale recorded");
                    loadHistory();
                });
            } catch (SQLException e) {
                Platform.runLater(() -> AlertHelper.showError("Database Error", "Failed to save sale: " + e.getMessage()));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private String getProductName(int productId) {
        Produit p = productCache.get(productId);
        return p != null ? p.getNom() : "Product #" + productId;
    }

    private String getClientName(int clientId) {
        return clientNames.getOrDefault(clientId, "Client #" + clientId);
    }

    private void goBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showDashboardScene();
    }
}
