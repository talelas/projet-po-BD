package ui.utils;

import models.Employe;

/**
 * Manages the current logged-in employee session
 */
public class SessionManager {
    private static Employe loggedInEmployee;
    
    public static void setEmployee(Employe emp) {
        loggedInEmployee = emp;
    }
    
    public static Employe getEmployee() {
        return loggedInEmployee;
    }
    
    public static int getEmployeeId() {
        return loggedInEmployee != null ? loggedInEmployee.getId() : -1;
    }
    
    public static String getEmployeeName() {
        return loggedInEmployee != null ? 
            loggedInEmployee.getNom() + " " + loggedInEmployee.getPrenom() : "Unknown";
    }
    
    public static boolean isLoggedIn() {
        return loggedInEmployee != null;
    }
    
    public static boolean isAdmin() {
        // Admin user has ID = 1
        return loggedInEmployee != null && loggedInEmployee.getId() == 1;
    }
    
    public static void logout() {
        loggedInEmployee = null;
    }
}
