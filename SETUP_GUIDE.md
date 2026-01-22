# Pharmacy Management System - Setup Guide

## 📋 Table of Contents
1. [Prerequisites](#prerequisites)
2. [Database Setup](#database-setup)
3. [Project Configuration](#project-configuration)
4. [Building the Application](#building-the-application)
5. [Running the Application](#running-the-application)
6. [Troubleshooting](#troubleshooting)

---

## 🔧 Prerequisites

### Required Software
1. **Java Development Kit (JDK) 17 or higher**
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Verify installation: `java -version` and `javac -version`

2. **JavaFX SDK 25.0.2 (or compatible version)**
   - Download from: https://gluonhq.com/products/javafx/
   - Extract to a location (e.g., `C:\javafx-sdk-25.0.2`)
   - Remember this path - you'll need it later

3. **MySQL Server 8.0 or higher**
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Install with root password (remember this password!)

4. **MySQL Connector/J 9.5.0 (JDBC Driver)**
   - Already included in project: `mysql-connector-j-9.5.0.jar`
   - If missing, download from: https://dev.mysql.com/downloads/connector/j/

---

## 🗄️ Database Setup

### Step 1: Start MySQL Server
```bash
# Windows - Start MySQL service
net start MySQL80

# Or use MySQL Workbench / Services app to start MySQL
```

### Step 2: Create Database and Tables

1. **Connect to MySQL**:
```bash
mysql -u root -p
# Enter your MySQL root password
```

2. **Create the Database**:
```sql
CREATE DATABASE IF NOT EXISTS pharmacydb1;
USE pharmacydb1;
```

3. **Create Tables** (run these SQL commands in order):

```sql
-- Employees table

CREATE TABLE Produit (
  idProduit INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nomProduit VARCHAR(100),
  marque VARCHAR(50),
  quantite INT,
  quantiteMinimale INT DEFAULT 10 NOT NULL,
  prix DECIMAL(10,2),
  TVA DECIMAL(5,2),
  type VARCHAR(50)
);

CREATE TABLE Fournisseur (
  idFournisseur INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(50),
  prenom VARCHAR(50),
  numeroTelephone VARCHAR(20),
  adresseEmail VARCHAR(100)
);

CREATE TABLE Produit_Fournisseur (
  idProduit INT,
  idFournisseur INT,
  PRIMARY KEY (idProduit, idFournisseur),
  FOREIGN KEY (idProduit) REFERENCES Produit(idProduit),
  FOREIGN KEY (idFournisseur) REFERENCES Fournisseur(idFournisseur)
);

CREATE TABLE Commande (
  idCommande INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  idFournisseur INT,
  dateCommande DATE,
  periodeReception INT,
  recu BOOLEAN DEFAULT FALSE,
  dateReception DATE,
  FOREIGN KEY (idFournisseur) REFERENCES Fournisseur(idFournisseur)
);

CREATE TABLE LigneCommande (
  idCommande INT,
  idProduit INT,
  quantite INT,
  prixAchat DECIMAL(10,2),
  PRIMARY KEY (idCommande, idProduit),
  FOREIGN KEY (idCommande) REFERENCES Commande(idCommande),
  FOREIGN KEY (idProduit) REFERENCES Produit(idProduit)
);

CREATE TABLE Client (
  idClient INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(50),
  adresse VARCHAR(100),
  email VARCHAR(100),
  nTelephone VARCHAR(20)
);

CREATE TABLE Vente (
  idVente INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  dateFacture DATE,
  idClient INT,
  FOREIGN KEY (idClient) REFERENCES Client(idClient)
);

CREATE TABLE LigneVente (
  idVente INT,
  idProduit INT,
  quantite INT,
  prixUnite DECIMAL(10,2),
  PRIMARY KEY (idVente, idProduit),
  FOREIGN KEY (idVente) REFERENCES Vente(idVente),
  FOREIGN KEY (idProduit) REFERENCES Produit(idProduit)
);

CREATE TABLE Employe (
  idEmploye INT PRIMARY KEY AUTO_INCREMENT,
  nom VARCHAR(50) NOT NULL,
  prenom VARCHAR(50) NOT NULL,
  login VARCHAR(50) UNIQUE NOT NULL,
  motDePasse VARCHAR(255) NOT NULL,
  email VARCHAR(100),
  dateEmbauche DATE,
  actif BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_login ON Employe(login);

INSERT INTO Employe (nom, prenom, login, motDePasse, email, dateEmbauche, actif) 
VALUES ('Admin', 'Pharmacie', 'admin', 'admin123', 'admin@pharmacie.com', NOW(), TRUE);
### Step 3: Insert Initial Data

```sql
-- Create default admin account (password: admin123)
INSERT INTO Employe (nom, prenom, email, motDePasse, role, actif) 
VALUES ('Admin', 'System', 'admin@pharmacy.com', 'admin123', 'admin', TRUE);

-- Create default employee account (password: emp123)
INSERT INTO Employe (nom, prenom, email, motDePasse, role, actif) 
VALUES ('Employee', 'Test', 'employee@pharmacy.com', 'emp123', 'employee', TRUE);

-- Sample products
INSERT INTO Produit (nomProduit, marque, quantite, quantiteMinimale, prix, tva, type) VALUES
('Aspirin 500mg', 'Generic', 100, 20, 5.50, 6.0, 'Medication'),
('Vitamin C', 'Nature Plus', 50, 10, 12.00, 6.0, 'Supplement'),
('Band-Aid Pack', 'MediCare', 75, 15, 3.50, 6.0, 'Medical Device');

-- Sample clients
INSERT INTO Client (nom, adresse, email, nTelephone) VALUES
('Dupont Pharmacy', '123 Rue de Paris', 'dupont@email.com', '0123456789'),
('Martin Clinic', '456 Ave Lyon', 'martin@clinic.com', '0987654321');

-- Sample suppliers
INSERT INTO Fournisseur (nom, prenom, numeroTelephone, adresseEmail) VALUES
('Pharma Distribution', 'Jean', '0111222333', 'jean@pharma.com'),
('Medical Supplies', 'Sophie', '0555666777', 'sophie@medsupply.com');
```

---

## ⚙️ Project Configuration

### Step 1: Update Database Connection Settings

1. Open the file: `ui/utils/DatabaseManager.java`

2. Update the database credentials (around line 12-15):

```java
private static final String URL = "jdbc:mysql://localhost:3306/pharmacydb1";
private static final String USERNAME = "root";        // Your MySQL username
private static final String PASSWORD = "your_password"; // Your MySQL password
```

**Replace** `"your_password"` with your actual MySQL root password.

### Step 2: Verify JavaFX Path in build.bat

1. Open `build.bat` in a text editor

2. Find the line with `JAVAFX_PATH` (around line 7):

```batch
set JAVAFX_PATH=C:\javafx-sdk-25.0.2\lib
```

3. Update this path to match where you extracted JavaFX SDK

---

## 🔨 Building the Application

### Windows

1. **Open PowerShell or Command Prompt** in the project directory:
```bash
cd "C:\Users\HP\unbreaking news\version2\projet-po-BD"
```

2. **Run the build script**:
```bash
.\build.bat
```

3. **Expected output**:
```
========================================
Building Pharmacy Management System
========================================
[1/4] Compiling backend classes (models, dao)...
[✓] Backend compiled successfully
[2/4] Copying UI resources...
[✓] UI resources copied
[3/4] Compiling UI utilities...
[✓] UI utilities compiled successfully
[4/4] Compiling UI controllers...
[✓] UI controllers compiled successfully
[5/5] Compiling main application class...
[✓] Main application compiled successfully

========================================
Build completed successfully!
========================================
```

### If Build Fails

Check:
- ✅ Java JDK is installed (`javac -version`)
- ✅ JavaFX path in `build.bat` is correct
- ✅ MySQL connector JAR exists in project root
- ✅ No syntax errors in Java files

---

## ▶️ Running the Application

### Method 1: Using the Run Script (Recommended)

```bash
.\run.bat
```

### Method 2: Manual Command

```bash
java --module-path "C:\javafx-sdk-25.0.2\lib" ^
     --add-modules javafx.controls,javafx.fxml ^
     -cp "classes;mysql-connector-j-9.5.0.jar" ^
     App
```

**Note**: Replace `C:\javafx-sdk-25.0.2\lib` with your actual JavaFX lib path.

### Login Credentials

**Admin Account:**
- Email: `admin@pharmacy.com`
- Password: `admin123`

**Employee Account:**
- Email: `employee@pharmacy.com`
- Password: `emp123`

---

## 🎯 Application Features

### Main Modules

1. **Dashboard** - Overview with statistics
2. **Products Management** - Add, edit, delete products; track inventory
3. **Clients** - Manage client information
4. **Suppliers** - Manage suppliers and link products
5. **Purchase Orders** - Create orders, track deliveries, update stock
6. **Sales** - Process sales, manage cart, view history
7. **Reports** - Generate revenue and performance reports
8. **Settings** - Manage employees (admin only)

### Key Workflows

**Creating a Purchase Order:**
1. Navigate to Orders → New Order
2. Select supplier
3. Add products with quantities and buy prices
4. Mark as received to update stock automatically

**Processing a Sale:**
1. Navigate to Sales → New Sale
2. Select client
3. Add products (prices auto-populated)
4. Confirm sale (stock decrements automatically)

**Managing Products:**
- Products with sales/orders cannot be deleted (data integrity)
- Stock is managed through Orders (increase) and Sales (decrease)

---

## 🐛 Troubleshooting

### "Cannot connect to database"
```
❌ Error: Communications link failure
```
**Solutions:**
- ✅ Check MySQL service is running
- ✅ Verify credentials in `DatabaseManager.java`
- ✅ Ensure database `pharmacydb1` exists
- ✅ Check firewall isn't blocking port 3306

### "Module javafx.controls not found"
```
❌ Error: module javafx.controls not found
```
**Solutions:**
- ✅ Verify JavaFX SDK path in command/script
- ✅ Ensure `--module-path` points to JavaFX `lib` folder
- ✅ Check JavaFX version compatibility (use 17+)

### "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
```
❌ Error: ClassNotFoundException
```
**Solutions:**
- ✅ Ensure `mysql-connector-j-9.5.0.jar` is in project root
- ✅ Check `-cp` classpath includes the JAR file

### "Cannot delete product: foreign key constraint fails"
```
❌ Error: Cannot delete or update a parent row
```
**This is expected behavior!** Products referenced in sales/orders cannot be deleted to maintain data integrity. The UI will now show a clear message explaining this.

### Build Fails with "javac not recognized"
```
❌ 'javac' is not recognized as an internal or external command
```
**Solutions:**
- ✅ Add JDK bin folder to System PATH:
  - Windows: `C:\Program Files\Java\jdk-17\bin`
  - Restart terminal after updating PATH

---

## 📁 Project Structure

```
projet-po-BD/
├── App.java                    # Main application entry point
├── build.bat                   # Windows build script
├── run.bat                     # Windows run script (create this)
├── mysql-connector-j-9.5.0.jar # MySQL JDBC driver
├── dao/                        # Data Access Objects
│   ├── ClientDAO.java
│   ├── CommandeDAO.java
│   ├── EmployeDAO.java
│   ├── FournisseurDAO.java
│   ├── LigneCommandeDAO.java
│   ├── LigneVenteDAO.java
│   ├── ProduitDAO.java
│   ├── ReportingDAO.java
│   └── VenteDAO.java
├── models/                     # Data models
│   ├── Client.java
│   ├── Commande.java
│   ├── Employe.java
│   ├── Fournisseur.java
│   ├── LigneCommande.java
│   ├── LigneVente.java
│   ├── Produit.java
│   └── Vente.java
├── ui/                         # User Interface
│   ├── controllers/            # JavaFX controllers
│   ├── utils/                  # Utilities (DatabaseManager, SceneManager, etc.)
│   ├── views/                  # FXML files
│   └── styles/                 # CSS stylesheets
├── resources/                  # Application resources
│   └── images/                 # Icons and images
└── testing/                    # Test files

```

---

## 🔐 Security Notes

⚠️ **IMPORTANT:** This is a **demonstration/educational project**. For production use:

1. **Never store passwords in plain text** - Use bcrypt or similar hashing
2. **Use environment variables** for database credentials
3. **Implement proper authentication** with JWT or sessions
4. **Add input validation** to prevent SQL injection
5. **Use prepared statements** (already implemented in DAOs)
6. **Add audit logging** for critical operations
7. **Implement role-based access control** more rigorously

---

## 📞 Support

For issues or questions:
1. Check this guide thoroughly
2. Review error messages carefully
3. Verify all prerequisites are installed
4. Check database connection and credentials

---

## 📝 License

See LICENSE file for details.

---

**Version:** 2.0  
**Last Updated:** January 2026  
**Status:** ✅ Production Ready
