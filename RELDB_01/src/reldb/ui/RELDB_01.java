/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.ui;

import reldb.ui.dialogs.LoginDialogController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import reldb.Placeholder;
import reldb.lib.ConnectionManager;
import reldb.lib.MetaDataManager;
import reldb.ui.dialogs.Dialogs;

/**
 *
 * @author s6fake
 */
public class RELDB_01 extends Application {

    private Stage stage;
    private static final String url = "jdbc:postgresql://dbvm01.iai.uni-bonn.de:5432/imdb";
    private static ConnectionManager connection;
    private MetaDataManager mdManager;

    @Override
    public void start(Stage primaryStage) {

        try {
            this.stage = primaryStage;

            // FXML für Frame laden
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainFrame.fxml"));

            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("RELDB");

            // Controller initialisieren
            MainFrameController controller = loader.<MainFrameController>getController();
            controller.setParent(this);
            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException e) {
            System.out.println("Fehler beim Starten der Application");
            System.exit(1);
        }
    }
    
    public void callLoginDialog() {
        Dialogs.loginDialog(this);
    }

    public void logIn(String user, String password) {        
        connection.EstablishConnection(url, user, password);
        mdManager = new MetaDataManager(connection.getMetadata());
        mdManager.printInfo();
    }

    public static void main(String[] args) {
        connection = ConnectionManager.getInstance();
        launch(args);

        connection.CloseConnection();
    }

}
