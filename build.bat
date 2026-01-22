@echo off
REM Build script for Pharmacy Management System with JavaFX

set JAVAFX_HOME=C:\javafx-sdk-25.0.2
set MYSQL_JAR=mysql-connector-j-9.5.0.jar
set CLASSPATH=classes;%MYSQL_JAR%

echo.
echo ========================================
echo Building Pharmacy Management System
echo ========================================
echo.

REM Compile backend classes
echo [1/4] Compiling backend classes (models, dao)...
javac -d classes -cp %CLASSPATH% models\*.java dao\*.java 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [✓] Backend compiled successfully
) else (
    echo [✗] Backend compilation failed
)

REM Copy UI resources (FXML, CSS, and application icons/images)
echo [2/4] Copying UI resources...
if not exist classes\ui\views mkdir classes\ui\views
if not exist classes\ui\styles mkdir classes\ui\styles
if not exist classes\resources\images mkdir classes\resources\images
copy /Y ui\views\*.fxml classes\ui\views\ >nul 2>&1
copy /Y ui\styles\*.css classes\ui\styles\ >nul 2>&1
copy /Y resources\images\*.png classes\resources\images\ >nul 2>&1
echo [✓] UI resources copied

REM Compile UI utility and helper classes
echo [3/4] Compiling UI utilities...
javac -d classes --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls,javafx.fxml -cp %CLASSPATH% ^
  ui\utils\SessionManager.java ^
  ui\utils\DatabaseManager.java ^
  ui\utils\AlertHelper.java ^
  ui\utils\SceneManager.java ^
  ui\utils\TableHelper.java

if %ERRORLEVEL% EQU 0 (
    echo [✓] UI utilities compiled successfully
) else (
    echo [✗] UI utilities compilation failed
    echo Error details:
    javac -d classes --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls,javafx.fxml -cp %CLASSPATH% ui\utils\*.java
    pause
    exit /b 1
)

REM Compile UI controllers
echo [4/4] Compiling UI controllers...
javac -d classes --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls,javafx.fxml -cp %CLASSPATH% ui\controllers\*.java

if %ERRORLEVEL% EQU 0 (
    echo [✓] UI controllers compiled successfully
) else (
    echo [✗] UI controllers compilation failed
    pause
    exit /b 1
)

REM Compile main App class
echo [5/5] Compiling main application class...
javac -d classes --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls,javafx.fxml -cp %CLASSPATH% App.java

if %ERRORLEVEL% EQU 0 (
    echo [✓] Main application compiled successfully
    echo.
    echo ========================================
    echo Build completed successfully!
    echo ========================================
    echo.
    echo To run the application:
    echo java --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls,javafx.fxml -cp "classes;%MYSQL_JAR%" App
    echo.
    pause
    exit /b 0
) else (
    echo [✗] Main application compilation failed
    pause
    exit /b 1
)
    exit /b 1
)
) else (
    echo [✗] Main application compilation failed
)

pause
