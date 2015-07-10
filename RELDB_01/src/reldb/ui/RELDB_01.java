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
import reldb.lib.database.Reldb_DataContainer;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Row;
import reldb.lib.database.Reldb_Table;
import reldb.lib.migration.Reldb_DataMover;
import reldb.lib.sql.Reldb_Statement;
import reldb.ui.dialogs.Dialogs;

/**
 *
 * @author s6fake
 */
public class RELDB_01 extends Application implements IMainClass {

    private Stage stage;
    private static final String url = "jdbc:postgresql://dbvm01.iai.uni-bonn.de:5432/imdb";
    private MainController controller;
    private Reldb_Connection currentConnection, destinationDatabaseConnection;

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

            // Scene aufbauen
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("RELDB");

            // Controller initialisieren
            controller = loader.<MainController>getController();
            controller.setParent(this);

            controller.getMenuBar().setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            controller.getMenuBar().setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });

            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException e) {

            System.out.println("Fehler beim Starten der Application\n" + e);
            System.exit(1);
        }
    }

    @Override
    public void logIn(String user, String password, Reldb_Connection connection) {
        if (connection == null) {
            return;
        }
        if (currentConnection != null) {
            currentConnection.CloseConnection();
        }
        controller.label_1.setText("Verbinde mit " + connection.getConnectionName());
        if (connection.connect(user, password)) {
            controller.label_1.setText(url);
            controller.setTreeRoot(new Reldb_Database(connection));
        } else {
            controller.label_1.setText("Verbindung fehlgeschlagen");
        }
        currentConnection = connection;
    }
    @Override
    public void startExport(String user, String password, Reldb_Connection connection) {
        if (connection == null) {
            return;
        }
        
        if (connection.equals(currentConnection)) {
            return;
        }
        
        if (destinationDatabaseConnection != null) {
            destinationDatabaseConnection.CloseConnection();
        }

        if (connection.connect(user, password)) {
           Reldb_DataMover izzy = new Reldb_DataMover(currentConnection.getDatabase().getSelectedTables(), new Reldb_Database(connection));
           izzy.start();
           Dialogs.newProgressDialog();
        } else {
            Dialogs.newEditExportDialog(this, connection);
        }
        destinationDatabaseConnection = connection;
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
     * @param db
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
        exit(0);
    }

    public static void exit(int i) {
        Reldb_Connection.closeAllConnections(); //Alle Verbindungen schließen
        System.exit(i);
    }
    /*
     ToDo:

    
     */
}
