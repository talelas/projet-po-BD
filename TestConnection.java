import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/pharmacydb";
        String username = "root";
        String password = "140406"; // Replace with your MySQL password if you have one
        
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Connection successful!");
            
            // Test query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Employe");
            
            System.out.println("\n--- Employees in Database ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("idEmploye") + 
                                 " | Login: " + rs.getString("login") + 
                                 " | Name: " + rs.getString("nom"));
            }
            
            conn.close();
            System.out.println("\n✓ Test completed successfully!");
        } catch (Exception e) {
            System.out.println("✗ Connection failed!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
