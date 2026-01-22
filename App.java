import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ui.utils.SceneManager;
import ui.utils.DatabaseManager;

/**
 * Main entry point for the Pharmacy Management System JavaFX application
 */
public class App extends Application {
    
    private static Stage primaryStage;
    private static SceneManager sceneManager;
    
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        sceneManager = new SceneManager(stage);
        
        // Configure primary stage
        primaryStage.setTitle("Pharmacy Management System");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.centerOnScreen();
        
        // Set application icon
        try {
            Image icon = new Image(getClass().getResourceAsStream("/resources/images/pharmacy-icon.png"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Could not load application icon: " + e.getMessage());
        }
        
        // Show login screen
        sceneManager.showLoginScene();
        
        // Show the stage
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        // Close database connection pool on application exit
        DatabaseManager.closePool();
        super.stop();
    }
    
    /**
     * Get the primary stage (for scene switching)
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Get the scene manager (for scene switching)
     */
    public static SceneManager getSceneManager() {
        return sceneManager;
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        launch(args);
    }
}
