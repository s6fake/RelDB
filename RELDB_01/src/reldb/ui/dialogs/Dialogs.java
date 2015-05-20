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
import reldb.lib.Reldb_Connection;
import reldb.ui.RELDB_01;

/**
 *
 * @author s6fake
 */
public class Dialogs {
    
    public static void loginDialog(RELDB_01 parent){
        
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
            controller.setParent(parent);
            
            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException ex) {
            System.out.println("Fehler beim Starten des Login-Dialogs.");
            System.exit(1);
        }
    }
    
        public static void newConnectionDialog(RELDB_01 parent){
        
        try {
           Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Dialogs.class.getResource("NewConnectionDialog.fxml"));
            
            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Neue Verbindung erstellen");
            
            // Controller initialisieren
            NewConnectionDialogController controller = loader.<NewConnectionDialogController>getController();
            controller.setStage(stage);
            controller.setParent(parent);
            
            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }
    
    public static void executeDialog(RELDB_01 parent, Reldb_Connection connection){
                try {
           Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Dialogs.class.getResource("SQLDialog.fxml"));
            
            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("SQL");
            
            // Controller initialisieren
            SQLDialogController controller = loader.<SQLDialogController>getController();
            controller.setStage(stage);
            controller.setParent(parent);
            controller.initalize(connection);
            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException ex) {
            System.out.println("Fehler beim Starten des SQL-Dialogs.");
            System.exit(1);
        }
    }
    
}
