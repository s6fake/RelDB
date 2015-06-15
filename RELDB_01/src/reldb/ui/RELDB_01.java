package reldb.ui;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import reldb.lib.Reldb_Connection;
import reldb.lib.MetaDataManager;
import reldb.lib.database.Reldb_DataContainer;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Row;
import reldb.lib.database.Reldb_Table;
import reldb.lib.sql.Reldb_Statement;

/**
 *
 * @author s6fake
 */
public class RELDB_01 extends Application {

    private Stage stage;
    private static final String url = "jdbc:postgresql://dbvm01.iai.uni-bonn.de:5432/imdb";
    private MetaDataManager mdManager;
    private MainController controller;
    private Reldb_Connection currentConnection;

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) {

        try {
            this.stage = primaryStage;

            // FXML für Frame laden
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = loader.load();
            // Stylesheet setzen
            setUserAgentStylesheet(STYLESHEET_CASPIAN);
            //remove window decoration
            primaryStage.initStyle(StageStyle.UNDECORATED);

            root.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    primaryStage.setX(event.getScreenX() - xOffset);
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
            });

            // Scene aufbauen
            Scene scene = new Scene(root);
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
    public void logIn(String user, String password, Reldb_Connection connection) {
        if (connection == null) {
            return;
        }
        if (currentConnection != null) {
            currentConnection.CloseConnection();
        }
        controller.label_1.setText("Verbinde mit " + connection.getConnectionName());
        if (connection.connect(user, password)) {
            mdManager = new MetaDataManager(connection.getMetadata());
            mdManager.printInfo(controller.textbox);
            controller.label_1.setText(url);
            updateTableNames(connection);
        } else {
            controller.label_1.setText("Verbindung fehlgeschlagen");
        }
        currentConnection = connection;
    }

    //Achtung, wird fehlerhaft. Funktion muss ersetzt werden!!!
    public void updateTableNames(Reldb_Connection connection) {
        Reldb_Database db = new Reldb_Database(connection);
        //controller.addDatabaseToConnectionInTreeView(connection, db);
        controller.setTreeRoot(db);
        //testQuery(db);
    }

    /**
     * Schließt eine Verbindung und entfernt diese aus der TreeView
     *
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
        List<Reldb_Row> testData2 = new ArrayList<>();

        ResultSet results = statement.executeQuery("SELECT * FROM " + tableName, fetchSize); // Im ResultSet landen die Datensätze
        try {
            int columnCount = testTable.getColumns().size();
            while (fetchSize > 0 && results.next()) {
                testData2.add(new Reldb_Row(testTable, results));
                //for (int j = 1; j <= columnCount; j++) {
                //  testData.add(new Reldb_DataContainer(results.getObject(j)));
                //testData.add(new Reldb_DataContainer(results.getObject(j), testTable.getColumns().get(1).getType()));   //DataContainer Objekte erstellen
                //}
                //System.out.println("SELECT * FROM title:" + results.getObject(2).toString());
                fetchSize--;
            }
            results.close();
        } catch (SQLException ex) {
            Logger.getLogger(RELDB_01.class.getName()).log(Level.SEVERE, null, ex);
        }

        statement.close();
        int tableSize = testTable.getColumns().size();
        for (int i = 0; i < testData2.size(); i++) {
            System.out.println(testData2.get(i).toString());
        }
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
