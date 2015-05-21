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
import javax.swing.JOptionPane;
import reldb.lib.Reldb_TreeViewElement;
import reldb.lib.Reldb_Connection;
import reldb.lib.MetaDataManager;
import reldb.lib.Reldb_Database;
import reldb.lib.sql.Reldb_Statement;

/**
 *
 * @author s6fake
 */
public class RELDB_01 extends Application {

    private Stage stage;
    private static final String url = "jdbc:postgresql://dbvm01.iai.uni-bonn.de:5432/imdb";
    private static List<Reldb_Connection> connectionsListRef = Reldb_Connection.getConnections();   //Referenz zur Liste in Reldb_Connections mit allen Verbindungen
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
    }

    public void callSQLDialog() {
        // Einen SQL Dialog öffnen, mit der aktuell betracteten Connection
        //Funktioniert im moment nicht
        //Dialogs.executeDialog(this, selectetConnection);
    }

    public Reldb_Connection createConnection(String url, String name) {
        for (Reldb_Connection iterator : Reldb_Connection.getConnections()) {
            if (iterator.getConnectionName().equals(name)) {
                JOptionPane.showMessageDialog(null, "Es existiert bereits eine Verbindung mit dem Namen " + name, "Verbindung kann nicht erstellt werden", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            if (iterator.getUrl().equalsIgnoreCase(url)) {
                JOptionPane.showMessageDialog(null, "Es existiert bereits eine Verbindung zu " + url, "Verbindung kann nicht erstellt werden", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        Reldb_Connection newConnection = new Reldb_Connection(url, name);
        controller.addTreeItem(new Reldb_TreeViewElement(newConnection, name));    //Verbindung wird zur TreeView hinzugefügt

        return newConnection;
    }

    public void logIn(String user, String password, Reldb_Connection connection) {
        if (connection == null) {
            return;
        }
        if (connection.connect(user, password)) {
            mdManager = new MetaDataManager(connection.getMetadata());
            mdManager.printInfo(controller.textbox);
            controller.label_1.setText(url);
            updateTableNames(connection);
        }
    }

    //Achtung, wird fehlerhaft. Funktion muss ersetzt werden!!!
    public void updateTableNames(Reldb_Connection connection) {
        Reldb_Statement statement = new Reldb_Statement(connection);
        mdManager.updateTable_connection(controller, connection, statement.getTables());
        statement.close();
        Reldb_Database db = new Reldb_Database(connection);

    }

    public static void main(String[] args) {
        launch(args);
        Reldb_Connection.closeAllConnections(); //Alle Verbindungen schließen
    }

    /*
     ToDo:
     Statements den Verbindungen zuordnen? -> Dann auch beim beenden alle Statements schließen
    
     */
}
