# Modifying Database Connection - Manual Configuration

If you want to use an existing database (not create a new one), modify only this file:

## 📝 File to Edit

**`ui/utils/DatabaseManager.java`** (Lines 12-15)

### Current Configuration

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacydb1";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "140406";
```

### Modify These Three Lines:

| Parameter | What to Change | Example |
|-----------|----------------|---------|
| **DB_URL** | Database location and name | `jdbc:mysql://localhost:3306/your_database_name` |
| **DB_USER** | MySQL username | `jdbc:mysql://192.168.1.100:3306/pharmacy` |
| **DB_PASSWORD** | MySQL password | Change password to your actual MySQL password |

---

## 🔧 Examples

### Scenario 1: Different Database Name (Same Server)

**Current:**
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacydb1";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "140406";
```

**Change to:**
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/my_pharmacy_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "140406";
```

**What changed:** `pharmacydb1` → `my_pharmacy_db`

---

### Scenario 2: Different MySQL User

**Current:**
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacydb1";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "140406";
```

**Change to:**
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacydb1";
private static final String DB_USER = "pharmacy_user";
private static final String DB_PASSWORD = "userpassword123";
```

**What changed:** User credentials

---

### Scenario 3: Remote MySQL Server

**Current:**
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacydb1";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "140406";
```

**Change to:**
```java
private static final String DB_URL = "jdbc:mysql://192.168.1.100:3306/pharmacydb1";
private static final String DB_USER = "admin";
private static final String DB_PASSWORD = "remotepassword";
```

**What changed:** Host IP, user, and password

---

### Scenario 4: Docker Container

**Current:**
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacydb1";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "140406";
```

**Change to:**
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacydb1";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "rootpass";
```

**What changed:** Only the password (for Docker mysql:8.0.40 container with rootpass)

---

## ✅ URL Format Explanation

```
jdbc:mysql://[HOST]:[PORT]/[DATABASE]
             ↑     ↑         ↑
          localhost 3306   pharmacydb1
```

- **localhost** = MySQL running on your computer
- **3306** = Standard MySQL port
- **pharmacydb1** = Database name to connect to

### Common Hosts:
- `localhost` = Local machine
- `127.0.0.1` = Local machine (same as localhost)
- `192.168.1.100` = Remote server on local network
- `db.example.com` = Remote server by domain name

---

## 🔑 Steps to Modify

1. Open file: `ui/utils/DatabaseManager.java`
2. Locate lines 12-15
3. Update these three fields:
   - `DB_URL` - change database name or host
   - `DB_USER` - change username
   - `DB_PASSWORD` - change password
4. Save the file
5. Rebuild: `.\build.bat`
6. Run app: `java ... App`

---

## 🧪 Verification

After modifying the connection settings, test with this command:

```powershell
# In PowerShell, run:
$testConnection = @'
import mysql.connector

try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="140406",
        database="pharmacydb1"
    )
    print("✓ Connection successful")
    conn.close()
except Exception as e:
    print(f"✗ Connection failed: {e}")
'@
```

Or use this MySQL command to test:

```powershell
docker exec -it pharmacydb mysql -u root -p140406 -e "USE pharmacydb1; SHOW TABLES;"
```

---

## ⚠️ Important Notes

### Database Must Already Exist

The application connects to an **existing database**. You must:

1. ✅ Have MySQL running (local or remote)
2. ✅ Have the database already created (e.g., `CREATE DATABASE pharmacydb1;`)
3. ✅ Have all 9 tables created (schema must match)
4. ✅ Have correct username/password for that MySQL user

### If Tables Don't Exist

Run the SQL schema from README.md:

```sql
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

-- ... and 8 more CREATE TABLE statements ...
```

---

## 📋 Database Requirements Checklist

Before changing DatabaseManager.java, ensure:

- [ ] MySQL server is running (local or remote)
- [ ] Database exists with correct name
- [ ] All 9 tables exist with correct structure
- [ ] Username/password are correct
- [ ] MySQL user has permissions on that database
- [ ] Network connection to MySQL is available (if remote)

---

## 🔍 Common Mistakes

| Mistake | Fix |
|---------|-----|
| Wrong port (3305 instead of 3306) | Use correct port number, usually 3306 |
| Wrong database name | Use exact database name that exists |
| Wrong password | Use the actual MySQL password |
| Database doesn't exist | Create it: `CREATE DATABASE dbname;` |
| Tables don't exist | Run SQL schema creation statements |
| MySQL not running | Start MySQL service/container |
| Firewall blocking connection | Allow MySQL port in firewall |

---

## 🎯 Summary

**To modify database connection without creating a new database:**

1. **Only file to edit:** `ui/utils/DatabaseManager.java`
2. **Only lines to modify:** 12-15 (the three `private static final String` fields)
3. **What you can change:**
   - Database name (change `pharmacydb1` to your database)
   - Host (change `localhost` to your server address)
   - Username (change `root` to your MySQL user)
   - Password (change `140406` to your MySQL password)
4. **Prerequisites:**
   - MySQL must be running
   - Database must already exist
   - All 9 tables must be created
   - Username/password must be correct

---

**That's it! Everything else stays the same.**
