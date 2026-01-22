package ui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import dao.ReportingDAO;
import dao.ReportingDAO.RevenueParProduit;
import dao.ReportingDAO.RevenueParClient;
import dao.ReportingDAO.PerformanceFournisseur;
import ui.utils.SessionManager;
import ui.utils.AlertHelper;
import ui.utils.SceneManager;
import ui.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReportsController {
    
    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button backButton;
    @FXML private Button generateButton;
    
    // Product Revenue Table
    @FXML private TableView<RevenueParProduit> productRevenueTable;
    @FXML private TableColumn<RevenueParProduit, Integer> prodIdColumn;
    @FXML private TableColumn<RevenueParProduit, String> prodNameColumn;
    @FXML private TableColumn<RevenueParProduit, Integer> prodQtyColumn;
    @FXML private TableColumn<RevenueParProduit, Double> prodRevenueColumn;
    
    // Client Revenue Table
    @FXML private TableView<RevenueParClient> clientRevenueTable;
    @FXML private TableColumn<RevenueParClient, Integer> clientIdColumn;
    @FXML private TableColumn<RevenueParClient, String> clientNameColumn;
    @FXML private TableColumn<RevenueParClient, Double> clientRevenueColumn;
    
    // Supplier Performance Table
    @FXML private TableView<PerformanceFournisseur> supplierPerfTable;
    @FXML private TableColumn<PerformanceFournisseur, Integer> suppIdColumn;
    @FXML private TableColumn<PerformanceFournisseur, String> suppNameColumn;
    @FXML private TableColumn<PerformanceFournisseur, Integer> suppOrdersColumn;
    @FXML private TableColumn<PerformanceFournisseur, Integer> suppReceivedColumn;
    @FXML private TableColumn<PerformanceFournisseur, Double> suppRateColumn;
    @FXML private TableColumn<PerformanceFournisseur, Double> suppTotalColumn;
    
    private ObservableList<RevenueParProduit> productRevenueList = FXCollections.observableArrayList();
    private ObservableList<RevenueParClient> clientRevenueList = FXCollections.observableArrayList();
    private ObservableList<PerformanceFournisseur> supplierPerfList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupUserInfo();
        setupTables();
        setupButtonHandlers();
        setupDefaultDates();
    }
    
    private void setupUserInfo() {
        if (SessionManager.isLoggedIn()) {
            welcomeLabel.setText("Welcome, " + SessionManager.getEmployeeName());
        }
    }
    
    private void setupTables() {
        // Product Revenue Table
        prodIdColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().idProduit).asObject());
        prodNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().nomProduit));
        prodQtyColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().quantiteVendue).asObject());
        prodRevenueColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().chiffreAffaires).asObject());
        productRevenueTable.setItems(productRevenueList);
        
        // Client Revenue Table
        clientIdColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().idClient).asObject());
        clientNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().nomClient));
        clientRevenueColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().chiffreAffaires).asObject());
        clientRevenueTable.setItems(clientRevenueList);
        
        // Supplier Performance Table
        suppIdColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().idFournisseur).asObject());
        suppNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().nom + " " + cellData.getValue().prenom));
        suppOrdersColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().totalCommandes).asObject());
        suppReceivedColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().commandesRecues).asObject());
        suppRateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().tauxReception * 100).asObject());
        suppTotalColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().montantTotalAchete).asObject());
        supplierPerfTable.setItems(supplierPerfList);
        
        // Format number columns
        prodRevenueColumn.setCellFactory(col -> new TableCell<RevenueParProduit, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f", item));
            }
        });
        
        clientRevenueColumn.setCellFactory(col -> new TableCell<RevenueParClient, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f", item));
            }
        });
        
        suppRateColumn.setCellFactory(col -> new TableCell<PerformanceFournisseur, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.1f%%", item));
            }
        });
        
        suppTotalColumn.setCellFactory(col -> new TableCell<PerformanceFournisseur, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f", item));
            }
        });
    }
    
    private void setupButtonHandlers() {
        backButton.setOnAction(e -> goBack());
        generateButton.setOnAction(e -> generateReport());
    }
    
    private void setupDefaultDates() {
        // Set default to last 30 days
        endDatePicker.setValue(LocalDate.now());
        startDatePicker.setValue(LocalDate.now().minusDays(30));
    }
    
    private void generateReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate == null || endDate == null) {
            AlertHelper.showWarning("Invalid Dates", "Please select both start and end dates");
            return;
        }
        
        if (startDate.isAfter(endDate)) {
            AlertHelper.showWarning("Invalid Dates", "Start date must be before end date");
            return;
        }
        
        statusLabel.setText("Generating reports...");
        generateButton.setDisable(true);
        
        Thread reportThread = new Thread(() -> {
            try (Connection conn = DatabaseManager.getConnection()) {
                ReportingDAO dao = new ReportingDAO(conn);
                
                // Get total revenue
                double totalRevenue = dao.getChiffreAffaires(startDate, endDate);
                
                // Get revenue by product
                List<RevenueParProduit> productRevenue = dao.getChiffreAffairesParProduit(startDate, endDate);
                
                // Get revenue by client
                List<RevenueParClient> clientRevenue = dao.getChiffreAffairesParClient(startDate, endDate);
                
                // Get supplier performance
                List<PerformanceFournisseur> supplierPerf = dao.getPerformanceFournisseurs(startDate, endDate);
                
                Platform.runLater(() -> {
                    totalRevenueLabel.setText(String.format("Total Revenue: %.2f TND", totalRevenue));
                    
                    productRevenueList.clear();
                    productRevenueList.addAll(productRevenue);
                    
                    clientRevenueList.clear();
                    clientRevenueList.addAll(clientRevenue);
                    
                    supplierPerfList.clear();
                    supplierPerfList.addAll(supplierPerf);
                    
                    statusLabel.setText("Report generated successfully - " + 
                                      productRevenue.size() + " products, " +
                                      clientRevenue.size() + " clients, " +
                                      supplierPerf.size() + " suppliers");
                    generateButton.setDisable(false);
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    AlertHelper.showError("Database Error", "Failed to generate report: " + e.getMessage());
                    statusLabel.setText("Error generating report");
                    generateButton.setDisable(false);
                });
            }
        });
        reportThread.setDaemon(true);
        reportThread.start();
    }
    
    private void goBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showDashboardScene();
    }
}
