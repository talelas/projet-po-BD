@echo off
setlocal

REM ========================================
REM Pharmacy Management System - Run Script
REM ========================================

echo.
echo ========================================
echo Running Pharmacy Management System
echo ========================================
echo.

REM Set JavaFX path (UPDATE THIS PATH to match your JavaFX installation)
set JAVAFX_PATH=C:\javafx-sdk-25.0.2\lib

REM Run the application
echo [*] Starting application...
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "classes;mysql-connector-j-9.5.0.jar" App

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [✓] Application closed successfully
) else (
    echo.
    echo [✗] Application exited with error code %ERRORLEVEL%
    echo.
    echo Common issues:
    echo - Check if MySQL server is running
    echo - Verify database credentials in DatabaseManager.java
    echo - Ensure JavaFX path is correct in this script
    pause
)

endlocal
