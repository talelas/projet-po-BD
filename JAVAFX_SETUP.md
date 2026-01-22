# JavaFX Setup Guide

## Prerequisites

The Pharmacy Management System UI requires JavaFX 21 to run. Follow these steps to set up your environment:

## Option 1: Using Pre-compiled JavaFX SDK (Recommended)

### Step 1: Download JavaFX SDK

1. Go to [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/)
2. Download JavaFX SDK 21 for **Windows (x64)** or your platform
3. Extract to a location, e.g., `C:\javafx-sdk-21`

### Step 2: Update build.bat

Edit `build.bat` and ensure this line matches your JavaFX location:
```batch
set JAVAFX_HOME=C:\javafx-sdk-21
```

### Step 3: Run Build Script

```batch
cd "c:\Users\HP\unbreaking news\version2\projet-po-BD"
build.bat
```

### Step 4: Run the Application

```batch
java --module-path C:\javafx-sdk-21\lib --add-modules javafx.controls,javafx.fxml ^
  -cp classes;mysql-connector-j-9.5.0.jar App
```

## Option 2: Maven Setup (Advanced)

If you prefer Maven for dependency management:

1. Create `pom.xml` in project root:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.pharmacy</groupId>
    <artifactId>pharmacy-management</artifactId>
    <version>1.0</version>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <javafx.version>21</javafx.version>
    </properties>

    <dependencies>
        <!-- JavaFX -->
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
        </dependency>

        <!-- MySQL JDBC -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>9.5.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <mainClass>App</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

2. Build with Maven:
```bash
mvn clean compile
```

3. Run with Maven:
```bash
mvn javafx:run
```

## Troubleshooting

### Error: "package javafx.* does not exist"
- **Cause:** JavaFX not in classpath
- **Solution:** Download JavaFX SDK and update `JAVAFX_HOME` in build.bat

### Error: "Module javafx.controls not found"
- **Cause:** Module path not specified correctly
- **Solution:** Use `--module-path` and `--add-modules` flags when running

### Error: "Cannot find symbol: class Stage"
- **Cause:** JavaFX not properly imported
- **Solution:** Ensure you're using Java 21 or later (`java -version`)

## Verify Installation

Run this command to verify JavaFX is accessible:

```batch
java --list-modules | findstr javafx
```

You should see output like:
```
javafx.base
javafx.controls
javafx.fxml
javafx.graphics
...
```

## Next Steps

Once JavaFX is properly set up:

1. Run `build.bat` to compile the project
2. Run the application using the command provided in Step 4
3. Use credentials from the database to log in

**Default test credentials:**
- Username: `admin`
- Password: (see database setup)

Or:
- Username: `falami`
- Password: `pass123`
