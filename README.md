# Pharmacy Management System

A comprehensive JavaFX-based desktop application for managing pharmacy operations including inventory, sales, orders, clients, and suppliers.

## 🚀 Quick Start

### Prerequisites
- Java JDK 17+
- JavaFX SDK 25.0.2+
- MySQL Server 8.0+
- MySQL Connector/J (included)

### Setup in 3 Steps

1. **Setup Database**
   ```bash
   # Connect to MySQL
   mysql -u root -p
   
   # Create database and run schema
   CREATE DATABASE pharmacydb1;
   USE pharmacydb1;
   # Then run all CREATE TABLE statements from SETUP_GUIDE.md
   ```

2. **Configure Connection**
   - Edit `ui/utils/DatabaseManager.java`
   - Update MySQL username/password (lines 12-15)

3. **Build & Run**
   ```bash
   .\build.bat
   .\run.bat
   ```

### Default Login
- **Admin:** admin@pharmacy.com / admin123
- **Employee:** employee@pharmacy.com / emp123

## 📖 Full Documentation

See [SETUP_GUIDE.md](SETUP_GUIDE.md) for:
- Complete step-by-step setup instructions
- Database schema with sample data
- Troubleshooting guide
- Security recommendations
- Feature documentation

## ✨ Features

- **Product Management** - Track inventory with low-stock alerts
- **Sales Processing** - Point-of-sale with automatic stock updates
- **Purchase Orders** - Manage supplier orders and stock receiving
- **Client & Supplier Management** - Maintain business relationships
- **Reporting** - Revenue analysis and supplier performance metrics
- **User Management** - Role-based access control (Admin/Employee)

## 🛡️ Data Integrity

- Foreign key constraints prevent orphaned records
- Products with sales/order history cannot be deleted (prevents data loss)
- Automatic stock updates on order receiving and sales
- Transaction-based operations ensure consistency

## 📁 Project Structure

```
projet-po-BD/
├── dao/              # Data Access Objects (database layer)
├── models/           # Business entity models
├── ui/
│   ├── controllers/  # JavaFX controllers
│   ├── views/        # FXML layout files
│   ├── styles/       # CSS stylesheets
│   └── utils/        # Utilities (DB, Scene, Session managers)
├── testing/          # Test classes
├── resources/        # Application resources (icons, images)
├── build.bat         # Build script
├── run.bat           # Run script
└── SETUP_GUIDE.md    # Comprehensive setup documentation
```

## 🔧 Technologies

- **JavaFX 25.0.2** - Modern UI framework
- **MySQL 8.0** - Relational database
- **JDBC** - Database connectivity
- **CSS** - Custom styling
- **Maven-compatible** structure

## 🎯 Key Workflows

### Creating Orders
1. Orders → New Order → Select Supplier
2. Add products with buy prices
3. Mark as received → Stock automatically updates

### Processing Sales  
1. Sales → New Sale → Select Client
2. Add products (price auto-filled from database)
3. Confirm → Stock automatically decrements

### Managing Inventory
- View all products with stock levels
- Edit prices and minimum thresholds
- Track critical stock items
- Stock updates via orders (increase) and sales (decrease)

## ⚠️ Important Notes

This is an **educational/demonstration project**. For production use:

- ✅ Implement password hashing (bcrypt)
- ✅ Use environment variables for credentials
- ✅ Add comprehensive input validation
- ✅ Implement audit logging
- ✅ Add data backup mechanisms
- ✅ Use HTTPS for any network communication

See SETUP_GUIDE.md for detailed security recommendations.

## 🐛 Common Issues & Solutions

**"Cannot connect to database"**
- Check MySQL service is running
- Verify credentials in DatabaseManager.java

**"Cannot delete product"**
- Products with sales/orders cannot be deleted (by design)
- This preserves historical data integrity

**"Module javafx.controls not found"**
- Update JavaFX path in build.bat and run.bat

See SETUP_GUIDE.md for complete troubleshooting guide.

## 📝 License

See LICENSE file for details.

## 👥 Contributors

Class project for POO/BD course.

---

**Version:** 2.0  
**Status:** ✅ Production Ready  
**Last Update:** January 2026
