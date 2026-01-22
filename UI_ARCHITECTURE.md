# Pharmacy Management System - JavaFX UI Architecture

## Overview
A modular JavaFX desktop application with role-based access (Employee/Admin), organized as:
- **Login Screen** → **Main Dashboard** → **Module Screens** → **Dialogs/Reports**

---

## 1. Application Structure (MVC Pattern)

### Folder Organization
```
ui/
├── controllers/          # FXML Controllers + business logic
│   ├── LoginController.java
│   ├── DashboardController.java
│   ├── ProductController.java
│   ├── ClientController.java
│   ├── SupplierController.java
│   ├── OrderController.java
│   ├── SalesController.java
│   ├── ReportsController.java
│   └── SettingsController.java
├── views/               # FXML files
│   ├── login.fxml
│   ├── dashboard.fxml
│   ├── product.fxml
│   ├── client.fxml
│   ├── supplier.fxml
│   ├── order.fxml
│   ├── sales.fxml
│   ├── reports.fxml
│   └── settings.fxml
├── styles/              # CSS stylesheets
│   └── styles.css
├── utils/               # Helpers
│   ├── SceneManager.java    # Scene switching
│   ├── TableHelper.java     # Table binding
│   └── AlertHelper.java     # Dialogs
└── App.java            # Main application entry
```

### Core Application Class
```java
public class App extends Application {
    private static Stage primaryStage;
    private static SceneManager sceneManager;
    
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        sceneManager = new SceneManager(stage);
        sceneManager.showLoginScene();
        stage.setTitle("Pharmacy Management System");
        stage.show();
    }
}
```

---

## 2. Navigation Flow

```
┌─────────────────┐
│  LOGIN SCREEN   │
│  (username/pwd) │
└────────┬────────┘
         │ ✓ Auth success
         ▼
┌─────────────────────────────────┐
│     MAIN DASHBOARD              │
│  (Navigation Hub + Quick Info)  │
│                                 │
│  ┌─────┬────────┬────────┬─┐   │
│  │ Prod│ Client │Supplier│📋  │
│  ├─────┼────────┼────────┼─┤   │
│  │Order│ Sales  │Reports │⚙│   │
│  └─────┴────────┴────────┴─┘   │
└────────┬────────────────────────┘
         │
    ┌────┴─────────────────────┬─────────┐
    ▼                           ▼         ▼
 [Products]             [Clients]      [Orders]
 [Suppliers]            [Sales]        [Reports]
```

---

## 3. Screen-by-Screen Breakdown

### **SCREEN 1: Login Screen**
**Purpose:** Employee authentication

**Components:**
- Logo/Title
- Username TextField
- Password PasswordField
- Login Button
- Remember Me Checkbox (optional)
- Status Label (error messages)
- Exit Button

**Data Binding:**
- Connect to `EmployeDAO.login(username, password)`
- On success: Store logged-in employee, navigate to Dashboard
- On failure: Show error message

**UI Layout:**
```
┌─────────────────────────┐
│   PHARMACY SYSTEM       │
│  ┌─────────────────┐    │
│  │  USERNAME       │    │
│  ├─────────────────┤    │
│  │  PASSWORD       │    │
│  ├─────────────────┤    │
│  │ ☑ Remember Me   │    │
│  ├─────────────────┤    │
│  │    [LOGIN]      │    │
│  └─────────────────┘    │
│   [Exit Application]    │
└─────────────────────────┘
```

---

### **SCREEN 2: Main Dashboard**
**Purpose:** Central navigation hub + quick status info

**Components:**
- **Top Bar:** Employee name, timestamp, Logout button
- **Sidebar Navigation:** 6 buttons for major modules
- **Quick Stats Panel:**
  - Total revenue (today)
  - Critical stock count
  - Pending orders count
  - Recent sales count
- **Quick Access Cards:** Show 5 most recently viewed items

**Data Binding:**
- Load stats from ReportingDAO, CommandeDAO, ProduitDAO, VenteDAO
- Update on focus/refresh button
- Auto-refresh every 30 seconds (optional)

**Navigation Buttons:**
1. **Products** → ProductController.java
2. **Clients** → ClientController.java
3. **Suppliers** → SupplierController.java
4. **Orders** → OrderController.java
5. **Sales** → SalesController.java
6. **Reports** (Admin only) → ReportsController.java

**UI Layout:**
```
┌──────────────────────────────────────────┐
│ PHARMACY | User: Fatima | 14:32  [Logout]│
├──────────────────────────────────────────┤
│ ┌──────────┐ │  ┌────────────────────┐   │
│ │ Products │ │  │  CA Today: 523 TND │   │
│ │ Clients  │ │  │  Critical Stock: 3 │   │
│ │ Suppliers│ │  │  Pending Orders: 5 │   │
│ │ Orders   │ │  │  Sales Today: 2    │   │
│ │ Sales    │ │  └────────────────────┘   │
│ │ Reports  │ │                            │
│ └──────────┘ │  [Last 5 actions/items]   │
│              │                            │
└──────────────────────────────────────────┘
```

---

### **SCREEN 3: Product Management**
**Purpose:** CRUD products, view stock status

**Components:**
- **Search Bar:** By name/type/brand
- **Table:** All products with columns:
  - ID | Name | Brand | Type | Quantity | Min | Status (OK/Critical/Alert) | TVA | Price | Actions
- **Buttons:**
  - [Add Product] → Dialog
  - [Edit] (row context menu)
  - [Delete] → Confirmation dialog
  - [Refresh]
- **Filter Dropdown:** Show All / Critical Stock / By Type
- **Status Color Coding:** Green (OK) / Yellow (Alert) / Red (Critical)

**Data Binding:**
- Populate table from `ProduitDAO.obtenirTousProduits()`
- Search filters applied locally + DB query
- Double-click row = edit dialog
- Right-click = context menu (edit/delete)

**Add/Edit Dialog:**
```
┌──────────────────────────┐
│ ADD/EDIT PRODUCT         │
├──────────────────────────┤
│ Product Name: [_______]  │
│ Brand: [_______]         │
│ Type: [Dropdown]         │
│ Quantity: [___]          │
│ Min Stock: [___]         │
│ Price: [___]             │
│ TVA (%): [___]           │
├──────────────────────────┤
│   [SAVE]    [CANCEL]     │
└──────────────────────────┘
```

**UI Layout:**
```
┌────────────────────────────────────────────────┐
│ Products │ [Search____] [▼ Filter]             │
├────────────────────────────────────────────────┤
│ ID│Name│Brand│Type│Qty│Min│Status│Price│ Actions│
├────────────────────────────────────────────────┤
│ 1 │Para│Doli│Anal│280│10│ ✓ OK │3.99│ Edit Del│
│ 2 │Amox│Amox│Anti│25 │5 │ ⚠ ALT│4.00│ Edit Del│
│ 3 │Vit │Redo│Vita│100│20│ ✓ OK │1.50│ Edit Del│
│   │    │    │    │   │  │      │    │        │
├────────────────────────────────────────────────┤
│ [+ Add]  [🚨 Critical Stock]  [🔄 Refresh]    │
└────────────────────────────────────────────────┘
```

**Critical Stock Button:**
- Shows modal with only critical stock items (qty <= minimum)
- Displays warning icon and color
- Quick actions to receive order or adjust stock

---

### **SCREEN 4: Client Management**
**Purpose:** CRUD clients, view purchase history

**Components:**
- **Search Bar:** By name/email/phone
- **Table:** All clients with columns:
  - ID | Name | Email | Phone | Address | Total Purchases | Last Purchase | Actions
- **Buttons:**
  - [Add Client] → Dialog
  - [Edit] (row context menu)
  - [Delete] → Confirmation dialog
  - [View History] → Show sales by this client
- **Sorting:** By name, purchases, last purchase date

**Data Binding:**
- Populate table from `ClientDAO.obtenirTousClients()`
- Calculate total purchases from `VenteDAO.obtenirVentesParClient(idClient)`
- Search filters applied locally

**Add/Edit Dialog:**
```
┌────────────────────────────┐
│ ADD/EDIT CLIENT            │
├────────────────────────────┤
│ Name: [________________]   │
│ Email: [________________]  │
│ Phone: [________________]  │
│ Address: [________________]│
├────────────────────────────┤
│   [SAVE]    [CANCEL]       │
└────────────────────────────┘
```

**History Popup:**
```
┌──────────────────────────────────┐
│ Sales History - Dupont           │
├──────────────────────────────────┤
│ Date │ Invoice │ Products │Total │
├──────────────────────────────────┤
│ 22/1 │ #0005   │ 2 items  │318   │
│ 19/1 │ #0001   │ 3 items  │420   │
└──────────────────────────────────┘
```

**UI Layout:**
```
┌────────────────────────────────────────┐
│ Clients │ [Search____] [▼ Filter]      │
├──────────────────────────────────────────┤
│ ID│Name│Email│Phone│Total│Last Purchase │
├──────────────────────────────────────────┤
│ 1 │Dupont│d@em│0123│954 TND│2026-01-22  │
│ 2 │Martin│m@em│0987│1353TND│2026-01-22  │
│   │      │    │    │       │            │
├──────────────────────────────────────────┤
│ [+ Add]  [📊 History]  [🔄 Refresh]    │
└────────────────────────────────────────┘
```

---

### **SCREEN 5: Supplier Management**
**Purpose:** CRUD suppliers, manage product-supplier links

**Components:**
- **Search Bar:** By name
- **Supplier Table:**
  - ID | Name | Prenom | Phone | Email | Total Products | Actions
- **Buttons:**
  - [Add Supplier] → Dialog
  - [Edit] (row context menu)
  - [Delete] → Confirmation dialog
  - [Link Products] → Opens product linking modal
- **Product Linking Modal:**
  - List all products with checkboxes
  - Show already-linked products as checked
  - [Save] applies changes

**Data Binding:**
- Populate table from `FournisseurDAO.obtenirTousFournisseurs()`
- Count products via `FournisseurDAO.obtenirProduitsDuFournisseur(id)`
- Link/unlink via `FournisseurDAO.lierProduitFournisseur()` / `delierProduitFournisseur()`

**Add/Edit Dialog:**
```
┌──────────────────────────┐
│ ADD/EDIT SUPPLIER        │
├──────────────────────────┤
│ Last Name: [_______]     │
│ First Name: [_______]    │
│ Phone: [_______]         │
│ Email: [_______]         │
├──────────────────────────┤
│   [SAVE]    [CANCEL]     │
└──────────────────────────┘
```

**Product Linking Modal:**
```
┌──────────────────────────────────┐
│ Link Products - Pharma Bernard   │
├──────────────────────────────────┤
│ ☑ Paracétamol 500mg             │
│ ☑ Amoxicilline 400mg            │
│ ☐ Vitamine C                    │
│ ☐ Ibuprofène                    │
├──────────────────────────────────┤
│   [SAVE]    [CANCEL]             │
└──────────────────────────────────┘
```

---

### **SCREEN 6: Order Management**
**Purpose:** Create orders, track reception, view history

**Components:**
- **Two Tabs:**
  1. **Open Orders Tab:**
     - Table: All pending orders (recu = FALSE)
     - Columns: ID | Supplier | Date | Period | Status | Actions
     - [Mark as Received] button → Updates stock automatically
     - [Cancel Order] button
  
  2. **Order History Tab:**
     - Table: All orders (received + pending)
     - Columns: ID | Supplier | Date | Period | Received | Actions
     - [View Details] button → Shows order lines with supplier info

- **Create Order Dialog:**
  - Select supplier (dropdown)
  - Add line items (product + quantity + purchase price)
  - Reception period (days)
  - [Create Order]

- **Order Details Modal:**
  - Show all line items
  - Show supplier info with phone
  - Show status + dates

**Data Binding:**
- Pending orders from `CommandeDAO.obtenirCommandesEnAttente()`
- All orders from `CommandeDAO.obtenirCommandesTrieesParDate()`
- Mark received calls `CommandeDAO.marquerCommandeRecue(id)` → auto-stock update
- View details loads via `CommandeDAO.afficherHistoriqueCommandesProduit()`

**Create Order Dialog:**
```
┌──────────────────────────────────┐
│ CREATE ORDER                     │
├──────────────────────────────────┤
│ Supplier: [▼ Pharma Bernard]     │
│ Reception Period: [7] days       │
│                                  │
│ Line Items:                      │
│ ┌──────────────────────────────┐ │
│ │ Product │ Qty │ Price/Unit  │ │
│ ├──────────────────────────────┤ │
│ │ Paracét │ 50  │ 12.0 TND    │ │
│ │ Amoxi   │ 30  │ 18.0 TND    │ │
│ │ [Add]   │     │             │ │
│ └──────────────────────────────┘ │
├──────────────────────────────────┤
│   [CREATE]    [CANCEL]           │
└──────────────────────────────────┘
```

**UI Layout:**
```
┌───────────────────────────────────────┐
│ Orders │ [Open]  [History]  [+ Create]│
├──────────────────────────────────────┤
│ ID│Supplier│Date│Period│Status│Actions│
├──────────────────────────────────────┤
│ 13│Pharma │17/1│7d   │⏳Wait│✓Receive│
│ 14│Medic  │19/1│5d   │⏳Wait│ Cancel │
│ 15│Pharma │22/1│10d  │⏳Wait│Details │
│   │       │    │     │      │        │
├──────────────────────────────────────┤
│ [🔄 Refresh]  [📋 Details]           │
└───────────────────────────────────────┘
```

---

### **SCREEN 7: Sales Management**
**Purpose:** Create sales invoices, track sales history, print invoices

**Components:**
- **Two Tabs:**
  1. **Create Sale Tab:**
     - Select client (dropdown)
     - Add line items (product + quantity + unit price)
     - Show running total
     - [Create Invoice] button
  
  2. **Sales History Tab:**
     - Table: All sales (most recent first)
     - Columns: ID | Client | Date | Total | Items | Actions
     - Search/filter by date range or client
     - [View Invoice] button → Displays formatted invoice
     - [Print] button → Sends to printer

**Data Binding:**
- Clients from `ClientDAO.obtenirTousClients()`
- Products from `ProduitDAO.obtenirTousProduits()` (available stock)
- Create sale calls `VenteDAO.ajouterVente()` + `LigneVenteDAO.ajouterLigneVente()` (auto-decrements stock)
- View invoice calls `VenteDAO.afficherFacture(id)`

**Create Sale Dialog:**
```
┌──────────────────────────────────┐
│ CREATE INVOICE                   │
├──────────────────────────────────┤
│ Client: [▼ Martin]               │
│                                  │
│ Line Items:                      │
│ ┌──────────────────────────────┐ │
│ │ Product │ Qty │ Price/Unit  │ │
│ ├──────────────────────────────┤ │
│ │ Paracét │ 10  │ 18.6 TND    │ │
│ │ Amoxi   │ 5   │ 26.4 TND    │ │
│ │ [Add]   │     │             │ │
│ └──────────────────────────────┘ │
│                                  │
│ TOTAL: 318.00 TND                │
├──────────────────────────────────┤
│   [CREATE]    [CANCEL]           │
└──────────────────────────────────┘
```

**Invoice View:**
```
╔════════════════════════════════════╗
║    FACTURE DE VENTE #0005      ║
╚════════════════════════════════════╝
Date: 2026-01-22
Client: Dupont
Téléphone: 0123456789
┌────────────────────────────────────┐
│ PRODUITS
├────────────────────────────────────┤
│ Paracétamol         10 × 18,60 = 186,00
│ Amoxicilline         5 × 26,40 = 132,00
├────────────────────────────────────┤
│ TOTAL: 318,00 TND
└────────────────────────────────────┘

[PRINT]  [EXPORT PDF]  [CLOSE]
```

**UI Layout:**
```
┌───────────────────────────────────────┐
│ Sales │ [Create]  [History]           │
├──────────────────────────────────────┤
│ ID│Client│Date│Total │Items│Actions  │
├──────────────────────────────────────┤
│ 5 │Dupont│22/1│318TND│2   │📄View   │
│ 6 │Martin│22/1│451TND│2   │🖨Print  │
│ 1 │Dupont│19/1│420TND│3   │Details  │
│   │      │    │      │    │         │
├──────────────────────────────────────┤
│ [Date Range ▼]  [Search]  [Refresh]  │
└───────────────────────────────────────┘
```

---

### **SCREEN 8: Reports & Analytics** (Admin Only)
**Purpose:** View financial reports, supplier metrics, stock analysis

**Components:**
- **Four Report Tabs:**

#### **Tab 1: Revenue Report**
- **Inputs:**
  - Date Range Picker (from/to)
  - [Generate Report] button
- **Outputs:**
  - Total CA for period
  - CA breakdown by product (table + bar chart)
  - CA breakdown by client (table + pie chart)
  - Top 5 products by revenue

#### **Tab 2: Stock Status Report**
- **Inputs:**
  - Show all / critical only / low stock filter
- **Outputs:**
  - Critical stock list with details
  - Stock aging analysis
  - Stock value (qty × price)
  - Stock movement history

#### **Tab 3: Supplier Performance**
- **Inputs:**
  - Date range
  - [Analyze] button
- **Outputs:**
  - Supplier table with:
    - Total orders placed
    - Orders received
    - Reception rate (%)
    - Total spent
  - Sort by performance/spend
  - Top suppliers by volume

#### **Tab 4: Export/Print**
- Export all reports as PDF/CSV
- Generate monthly summary
- Print-friendly formats

**Data Binding:**
- All data from `ReportingDAO`
- Charts using JavaFX Chart APIs (BarChart, PieChart, LineChart)
- Real-time calculations on date range change

**UI Layout:**
```
┌─────────────────────────────────────┐
│ Reports │ [Revenue] [Stock] [Suppliers]│
├─────────────────────────────────────┤
│                                     │
│ Date From: [22/01/2026]             │
│ Date To:   [22/01/2026]             │
│ [GENERATE REPORT]                   │
│                                     │
│ ┌──────────────────────────────┐    │
│ │ Total CA: 2,307.60 TND       │    │
│ │ ┌────────────────────────┐   │    │
│ │ │  Bar Chart (CA by Prod)│   │    │
│ │ │                        │   │    │
│ │ └────────────────────────┘   │    │
│ │                              │    │
│ │ Product  │ Qty  │ CA        │    │
│ │ Paracét  │ 90   │ 1674 TND  │    │
│ │ Amoxi    │ 24   │ 633.6TND  │    │
│ └──────────────────────────────┘    │
│                                     │
│ [📊 Charts]  [📥 Export]  [🖨Print]  │
└─────────────────────────────────────┘
```

---

## 4. Common UI Components & Patterns

### **Dialog/Modal Templates:**
1. **Confirmation Dialog**
   ```
   Are you sure you want to delete [Item Name]?
   [YES]  [CANCEL]
   ```

2. **Success/Error Toast**
   ```
   ✓ Product updated successfully!
   ✗ Error: Stock insufficient!
   ```

3. **Loading Indicator**
   - Show spinner during DB queries (especially reports)

### **Table Features:**
- Column sorting (click header)
- Row selection (highlight)
- Context menu (right-click)
- Pagination (if >100 rows)
- Export to CSV button

### **Input Validation:**
- Real-time field validation
- Error messages below fields
- Disable save if validation fails

### **Search/Filter Pattern:**
- Live search (filter as user types)
- Filter dropdowns for categorical data
- Date range pickers for temporal data

---

## 5. Style & UX Guidelines

### **Color Scheme:**
- **Primary:** Pharmacy green (#0F7938 or #2E7D32)
- **Accent:** Medical blue (#1976D2)
- **Warning:** Amber/Orange (#FF9800)
- **Danger:** Red (#D32F2F)
- **Success:** Green (#4CAF50)
- **Background:** Light gray (#F5F5F5)

### **Typography:**
- **Headers:** 18px, bold
- **Labels:** 12px, regular
- **Body:** 11px, regular
- **Font:** System font (Segoe UI / Ubuntu / Helvetica)

### **Responsive Layout:**
- Use BorderPane for main containers
- VBox/HBox for nested layouts
- GridPane for forms
- ScrollPane for large tables/lists

### **Accessibility:**
- Keyboard shortcuts (Alt+P = Products, Alt+S = Sales, etc.)
- Tab navigation order
- Tooltips on buttons
- High contrast for critical alerts

---

## 6. State Management & Controllers

### **Session Manager (Singleton):**
```java
public class SessionManager {
    private static Employe loggedInEmployee;
    
    public static void setEmployee(Employe emp) { loggedInEmployee = emp; }
    public static Employe getEmployee() { return loggedInEmployee; }
    public static boolean isAdmin() { return loggedInEmployee.getId() == 1; }
    public static void logout() { loggedInEmployee = null; }
}
```

### **Database Connection Pool:**
```java
public class DatabaseManager {
    private static final HikariDataSource dataSource = createDataSource();
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
```

### **Controller Base Class (optional):**
```java
public abstract class BaseController {
    protected Employe currentUser;
    protected Connection dbConnection;
    
    @FXML
    public void initialize() {
        currentUser = SessionManager.getEmployee();
        dbConnection = DatabaseManager.getConnection();
        initializeUI();
    }
    
    protected abstract void initializeUI();
}
```

---

### **SCREEN 9: Settings** (Connected Users Only)
**Purpose:** Manage employees (add new users), accessible from Dashboard

**Components:**
- **Two Tabs:**
  
  1. **Manage Employees Tab:**
     - Table: All active employees with columns:
       - ID | Name | Login | Email | Hire Date | Status | Actions
     - [Add Employee] button → Opens Add Employee dialog
     - [Edit] (row context menu) → Edit employee details (admin only)
     - [Deactivate] button → Disables employee account
  
  2. **Change Password Tab:**
     - Current Password field
     - New Password field
     - Confirm Password field
     - [Change] button
     - Status message (success/error)

**Add Employee Dialog:**
```
┌──────────────────────────────┐
│ ADD NEW EMPLOYEE             │
├──────────────────────────────┤
│ Last Name: [_____________]   │
│ First Name: [_____________]  │
│ Login: [_____________]       │
│ Email: [_____________]       │
│ Password: [_____________]    │
│ Confirm Password: [_______]  │
│ Hire Date: [22/01/2026]      │
├──────────────────────────────┤
│   [CREATE]    [CANCEL]       │
└──────────────────────────────┘
```

**Data Binding:**
- Populate table from `EmployeDAO.obtenirTousEmployes()`
- Add employee via `EmployeDAO.ajouterEmploye()`
- Change password via `EmployeDAO.changerMotDePasse()`
- Deactivate via `EmployeDAO.desactiverEmploye()`

**UI Layout (Settings Window):**
```
┌────────────────────────────────────┐
│ Settings │ [Employees] [Password]  │
├────────────────────────────────────┤
│ ID│Name│Login│Email│Hire│Status   │
├────────────────────────────────────┤
│ 1 │Admin│admin│adm@│22/1│✓ Active │
│ 2 │Alami│falami│f@e│20/1│✓ Active │
│   │     │      │   │    │         │
├────────────────────────────────────┤
│ [+ Add Employee]  [🔄 Refresh]    │
└────────────────────────────────────┘
```

---

## 7. Implementation Roadmap

### **Phase 1: Foundation (1 day)**
- [ ] Set up JavaFX project structure
- [ ] Create SceneManager + navigation
- [ ] Build Login screen
- [ ] Build Dashboard screen
- [ ] Style with CSS

### **Phase 2: CRUD Screens (2 days)**
- [ ] Product Management screen
- [ ] Client Management screen
- [ ] Supplier Management screen
- [ ] Add/Edit dialogs for each

### **Phase 3: Transaction Screens (2 days)**
- [ ] Order Management screen
- [ ] Sales/Invoice screen
- [ ] Invoice printing/export

### **Phase 4: Reporting & Polish (1.5 days)**
- [ ] Reports screen with charts
- [ ] PDF export functionality
- [ ] Bug fixes & refinements
- [ ] Final testing & documentation

**Total Estimate:** 6.5 days of development

---

## 8. Key Libraries & Dependencies

```xml
<!-- In pom.xml or manually add JARs -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>21</version>
</dependency>

<!-- Optional: Charts, PDF export, etc. -->
<!-- itextpdf for PDF generation -->
<!-- jfreeswing/XChart for advanced charting (if needed) -->
```

---

## Summary

This architecture provides:
✅ Clear separation between login, navigation, CRUD, transactions, and reports  
✅ Consistent UX with modular screens  
✅ Database integration via existing DAOs  
✅ Role-based access (login filters admin features)  
✅ Scalable controller + FXML pattern  
✅ Real-time data binding to UI tables  

**Next Step:** Approve this architecture, then begin implementation starting with the Login screen.
