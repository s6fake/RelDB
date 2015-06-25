package reldb02.ui;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import reldb.lib.Reldb_Connection;
import reldb.lib.database.Reldb_Database;
import reldb.lib.sql.Reldb_Statement;
import reldb.ui.IMainClass;
import reldb.ui.RELDB_01;

/**
 *
 * @author s6fake
 */
public class RELDB_02 extends Application implements IMainClass {

    private static final Logger log = Logger.getLogger(RELDB_02.class.getName());

    private static Reldb_Connection connection;

    /**
     * @return the connection
     */
    public static Reldb_Connection getConnection() {
        return connection;
    }
    Search_mainController searchController;

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
            controller.setParent(this);
            this.searchController = controller;
            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException ex) {
            System.out.println("Fehler im Hauptfenster: " + ex.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void logIn(String user, String password, Reldb_Connection newConnection) {
        if (newConnection == null) {
            return;
        }
        if (getConnection() != null) {
            getConnection().CloseConnection();
        }

        if (newConnection.connect(user, password)) {

            if (newConnection.getDatabaseTypeID() != 1) {
               
                JOptionPane.showMessageDialog(null, "Only Oracle Databases are supported yet!",
                        "Inane warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            connection = newConnection;
            searchController.newSearchTab();

        } else {
            log.log(Level.WARNING, "Verbindung konnte nicht hergestellt werden!");
        }

    }

    @Override
    public void startExport(String user, String password, Reldb_Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeConnection(Reldb_Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        exit(0);
    }

    public static void exit(int i) {
        Reldb_Connection.closeAllConnections();
        System.exit(i);
    }
}
