/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.ui;

import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import reldb.lib.Reldb_Connection;
import reldb.lib.MetaDataManager;
import reldb.lib.sql.StatementManager;
import reldb.ui.dialogs.Dialogs;

/**
 *
 * @author s6fake
 */
public class RELDB_01 extends Application {

    private Stage stage;
    private static final String url = "jdbc:postgresql://dbvm01.iai.uni-bonn.de:5432/imdb";
    private static List<Reldb_Connection> connectionsListRef = Reldb_Connection.getConnections();   //Referenz zur Liste mit allen Verbindungen
    private static Reldb_Connection selectetConnection = null;  //Aktuell betrachtete Connection
    private MetaDataManager mdManager;

    private MainController controller;

    @Override
    public void start(Stage primaryStage) {

        try {
            this.stage = primaryStage;

            // FXML für Frame laden
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));

            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("RELDB");

            // Controller initialisieren
            controller = loader.<MainController>getController();
            controller.setParent(this);
            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException e) {

            System.out.println("Fehler beim Starten der Application\n" + e);
            System.exit(1);
        }
        //logIn("x","y");
    }

    public void callLoginDialog() {
        Dialogs.loginDialog(this);
    }

    public void callSQLDialog() {
        // Einen SQL Dialog öffnen, mit der aktuell betracteten Connection
        Dialogs.executeDialog(this, selectetConnection);
    }

    public void createConnection(String url, String name) {
        selectetConnection = new Reldb_Connection(url, name);
        controller.addTreeItem(name);
    }

    public void logIn(String user, String password) {
        if (selectetConnection == null) {
            return;
        }
        if (selectetConnection.connect(user, password)) {
            mdManager = new MetaDataManager(selectetConnection.getMetadata());
            mdManager.printInfo(controller.textbox);
            controller.label_1.setText(url);
            updateTableNames();
        }
    }

    public void updateTable(Reldb_Connection connection) {

    }

    //Achtung, wird fehlerhaft. Funktion muss ersetzt werden!!!
    public void updateTableNames() {
        StatementManager statement = new StatementManager(selectetConnection.newStatement());
        mdManager.updateTable_connection(controller, selectetConnection, statement.executeCommand("select table_name from information_schema.tables where table_schema = 'public'"));
        statement.close();
    }

    public static void main(String[] args) {
        //selectetConnection = new Reldb_Connection(url, "IMDB");
        launch(args);

        Reldb_Connection.closeAllConnections(); //Alle Verbindungen schließen
    }

    /*
     ToDo:
     Statements den Verbindungen zuordnen? -> Dann auch beim beenden alle Statements schließen
    
     */
}
