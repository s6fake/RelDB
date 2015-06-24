package reldb.ui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author s6fake
 */
public class RELDB_02 extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Search_mainController controller = null;
        try {
            stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("search_main.fxml"));

            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);            
            stage.setTitle("Search");

            // Controller initialisieren
            controller = loader.<Search_mainController>getController();
            
            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
