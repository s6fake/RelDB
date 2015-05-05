/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.ui.dialogs;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author s6fake
 */
public class Dialogs {
    
    public static Stage loginDialog(){
        try {
           Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Dialogs.class.getResource("LoginDialog.fxml"));
            
            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Login");
            
            // Controller initialisieren
            LoginDialogController controller = loader.<LoginDialogController>getController();
            controller.setStage(stage);
            
            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException ex) {
            System.out.println("Fehler beim Starten des Login-Dialogs.");
            System.exit(1);
        }
        return null;
    }
    
}
