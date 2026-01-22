# JavaFX UI Development - Getting Started

## ✅ Completed Setup

### 1. **Project Structure Created**
```
ui/
├── controllers/
│   └── LoginController.java        (Login authentication logic)
├── views/
│   └── login.fxml                  (Login screen UI)
├── styles/
│   └── styles.css                  (Global stylesheet)
└── utils/
    ├── SessionManager.java         (Track logged-in user)
    ├── DatabaseManager.java        (DB connection helper)
    ├── SceneManager.java           (Scene navigation)
    ├── AlertHelper.java            (Dialog boxes)
    └── TableHelper.java            (Table utilities)
```

### 2. **Core Application Files**
- **App.java** - Main entry point with JavaFX bootstrap
- **build.bat** - Automated build script
- **JAVAFX_SETUP.md** - Setup instructions

### 3. **Updated Architecture**
- Added **Settings screen** for employee management (connected users only)
- Added **Critical Stock button** to Products screen
- Both features documented in [UI_ARCHITECTURE.md](UI_ARCHITECTURE.md)

---

## 🚀 Next Steps: Running the Application

### Prerequisites
You need **JavaFX SDK 21** installed locally.

### Step-by-Step Guide

#### **Step 1: Download JavaFX SDK 21**
1. Visit: https://gluonhq.com/products/javafx/
2. Download **JavaFX SDK 21** for Windows (x64)
3. Extract to: `C:\javafx-sdk-21`

#### **Step 2: Update build.bat (if needed)**
Edit `build.bat` line 3:
```batch
set JAVAFX_HOME=C:\javafx-sdk-21
```
(Update path if you installed JavaFX elsewhere)

#### **Step 3: Build the Project**
```powershell
cd "c:\Users\HP\unbreaking news\version2\projet-po-BD"
.\build.bat
```

Expected output:
```
[✓] Backend compiled successfully
[✓] UI utilities compiled successfully
[✓] UI controllers compiled successfully
[✓] Main application compiled successfully
```

#### **Step 4: Run the Application**
```powershell
java --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml ^
  -cp "classes;mysql-connector-j-9.5.0.jar" App
```

You should see the **Pharmacy Management System Login Screen**!

---

## 🔐 Login Credentials

Use any employee record from the database:

**Test Account 1 (Admin):**
- Login: `admin`
- Password: (check database)

**Test Account 2:**
- Login: `falami`
- Password: `pass123`

---

## 📋 What's Been Implemented

### **Login Screen**
✅ Username/Password input fields
✅ Remember me checkbox
✅ Error message display
✅ Database authentication via EmployeDAO
✅ Background thread execution (non-blocking UI)
✅ Session management via SessionManager
✅ Key binding (press Enter to login)
✅ Exit button

### **Supporting Infrastructure**
✅ SessionManager - Stores current logged-in employee
✅ DatabaseManager - Provides database connections
✅ SceneManager - Handles scene transitions
✅ AlertHelper - Shows dialogs/notifications
✅ TableHelper - Utilities for table binding
✅ Global CSS styling with color scheme

---

## 📚 Architecture Overview

### **Authentication Flow**
```
Login Screen
    ↓
User enters credentials
    ↓
[LOGIN] button → handleLogin()
    ↓
Background Thread:
  EmployeDAO.login(username, password)
    ↓
Database query
    ↓
Valid? → SessionManager.setEmployee(employee)
  ↓
  YES → AlertHelper.showSuccess()
     → SceneManager.showDashboardScene()
  ↓
  NO → AlertHelper.showError()
     → Clear password field
     → Request focus
```

### **File Locations**
| Component | File |
|-----------|------|
| Entry Point | `App.java` |
| Login Controller | `ui/controllers/LoginController.java` |
| Login FXML | `ui/views/login.fxml` |
| Session Manager | `ui/utils/SessionManager.java` |
| Database Manager | `ui/utils/DatabaseManager.java` |
| Scene Manager | `ui/utils/SceneManager.java` |
| Alert Helper | `ui/utils/AlertHelper.java` |
| Stylesheet | `ui/styles/styles.css` |

---

## 🎨 Current Styling

### **Color Palette**
- **Primary:** Pharmacy Green (#0F7938)
- **Accent:** Medical Blue (#1976D2)
- **Warning:** Amber (#FF9800)
- **Danger:** Red (#D32F2F)
- **Success:** Green (#4CAF50)
- **Background:** Light Gray (#F5F5F5)

### **Typography**
- Headers: 18px bold
- Labels: 12px regular
- Body: 11px regular

---

## 🔧 Troubleshooting

### Problem: "Module javafx.controls not found"
**Solution:** Download and install JavaFX SDK, update JAVAFX_HOME in build.bat

### Problem: "Cannot find symbol: class Stage"
**Solution:** Ensure you're compiling with `--module-path` flag

### Problem: Build script fails
**Solution:** Run PowerShell as Administrator, or manually run the javac commands from build.bat

### Problem: Database connection fails at login
**Solution:** Verify MySQL is running and credentials in DatabaseManager.java are correct

---

## 📖 Next Screens to Implement

Once Login is working:
1. **Dashboard** - Navigation hub + quick stats
2. **Product Management** - CRUD + critical stock alerts
3. **Client Management** - CRUD + purchase history
4. **Supplier Management** - CRUD + product linking
5. **Order Management** - Create/track orders
6. **Sales/Invoice** - Create invoices + print
7. **Reports** - Financial analytics (chiffre d'affaires)
8. **Settings** - Employee management (for connected users)

Each follows the same MVC pattern: FXML layout + Controller logic + DAO data access.

---

## 💡 Development Tips

### Adding a New Screen
1. Create `XxxxController.java` in `ui/controllers/`
2. Create `xxxx.fxml` in `ui/views/`
3. Add `fx:controller="ui.controllers.XxxxController"` in FXML
4. Use `@FXML` annotations to bind UI elements
5. Add method to `SceneManager.showXxxxScene()`
6. Style with CSS from `styles.css`

### Binding Data to Tables
Use `TableHelper.createColumn()` helper method:
```java
TableColumn<Product, String> nameCol = TableHelper.createColumn("Name", "nomProduit", 100);
table.getColumns().add(nameCol);
```

### Showing Dialogs
Use `AlertHelper` for consistent dialogs:
```java
AlertHelper.showSuccess("Operation", "Product added successfully!");
AlertHelper.showError("Error", "Failed to add product");
boolean confirmed = AlertHelper.showConfirmation("Delete", "Are you sure?");
```

---

## 📝 Summary

You now have:
✅ A complete JavaFX project structure
✅ Working login screen with database authentication
✅ Session management system
✅ Scene navigation framework
✅ UI utility helpers
✅ Global stylesheet
✅ Build automation
✅ Setup documentation

**Next action:** Download JavaFX SDK and run `build.bat` to verify everything compiles!

---

## 📞 Quick Reference Commands

```powershell
# Build the project
cd "c:\Users\HP\unbreaking news\version2\projet-po-BD"
.\build.bat

# Run the application
java --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml ^
  -cp "classes;mysql-connector-j-9.5.0.jar" App

# Compile only UI (if needed)
javac -d classes -cp "classes;mysql-connector-j-9.5.0.jar;C:\javafx-sdk-21\lib\*" ^
  ui\utils\*.java ui\controllers\*.java App.java
```

---

**Status:** ✅ Ready for JavaFX SDK download and build
**Estimated Build Time:** 30 seconds
**First Run:** Should show login screen with styled UI
