package reldb.ui;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import reldb.lib.Reldb_TreeViewElement;
import reldb.lib.Reldb_Connection;
import reldb.lib.MetaDataManager;
import reldb.lib.database.Reldb_DataContainer;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Table;
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
    
/*
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
*/
    
    public void addConnectionToTreeView(Reldb_Connection newConnection) {
        controller.addConnectionToTreeView(new Reldb_TreeViewElement(newConnection, newConnection.getConnectionName()));  
    }
    
    public void logIn(String user, String password, Reldb_Connection connection) {
        if (connection == null) {
            return;
        }
            controller.label_1.setText("Verbinde mit " + connection.getConnectionName());
        if (connection.connect(user, password)) {
            mdManager = new MetaDataManager(connection.getMetadata());
            mdManager.printInfo(controller.textbox);
            controller.label_1.setText(url);
            updateTableNames(connection);
        }
            else
        {            
            controller.label_1.setText("Verbindung fehlgeschlagen");
        }
    }

    //Achtung, wird fehlerhaft. Funktion muss ersetzt werden!!!
    public void updateTableNames(Reldb_Connection connection) {
        Reldb_Database db = new Reldb_Database(connection);
        controller.addDatabaseToConnectionInTreeView(connection, db);
        testQuery(db);
    }
    
    /**
     * Schließt eine Verbindung und entfernt diese aus der TreeView
     * @param connection 
     */
    public void removeConnection(Reldb_Connection connection) {
        
        controller.deleteConnectionFromTreeView(controller.getTreeItemByName(connection.getConnectionName())); 
        connection.CloseConnection();
    }

    /**
     * nur zum testen, liest 5 Datensätze aus der Datenbank aus
     *
     * @param connection
     */
    public void testQuery(Reldb_Database db) {
        String tableName = "title";
        Reldb_Connection connection = db.getConnection();
        Reldb_Statement statement = new Reldb_Statement(connection);
        Reldb_Table testTable = db.getTableByName(tableName);
        int fetchSize = 10;
        List<Reldb_DataContainer> testData = new ArrayList<>();

        
            ResultSet results = statement.executeQuery("SELECT * FROM " + tableName, fetchSize); // Im ResultSet landen die Datensätze
            try {
                int columnCount = testTable.getColumns().size();
                while (fetchSize >= 0 && results.next()) {
                    
                    for (int j = 1; j <= columnCount; j++) {
                        testData.add(new Reldb_DataContainer(results.getObject(j)));
                        //testData.add(new Reldb_DataContainer(results.getObject(j), testTable.getColumns().get(1).getType()));   //DataContainer Objekte erstellen
                    }
                    //System.out.println("SELECT * FROM title:" + results.getObject(2).toString());
                    fetchSize--;
                }
                results.close();
            } catch (SQLException ex) {
                Logger.getLogger(RELDB_01.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        statement.close();

        testData.stream().forEach((data) -> {
            System.out.print(data.toString() + " ");
        });
        System.out.print("\n");
    }

    public static void main(String[] args) {
        launch(args);
        Reldb_Connection.closeAllConnections(); //Alle Verbindungen schließen
    }

    /*
     ToDo:

    
     */
}
