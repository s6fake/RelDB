package reldb.ui.dialogs;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import reldb.lib.Reldb_Connection;
import reldb.lib.database.Reldb_Column;
import reldb.ui.IMainClass;
import reldb.ui.MainController;
import reldb.ui.RELDB_01;

/**
 *
 * @author s6fake
 */
public class Dialogs {

    public static void loginDialog(IMainClass parent, Reldb_Connection connection) {

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
            controller.setConnection(connection);

            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException ex) {
            System.out.println("Fehler beim Starten des Login-Dialogs.");
            System.exit(1);
        }
    }

    public static void newExportDialog(RELDB_01 parent) {
        NewConnectionDialogController controller = newConnectionDialog(parent);
        controller.setIsExportConnection(true);
    }

    public static void newConnectionDialog(IMainClass parent, String url, String dbName, int port, int dbTypeID) {
        NewConnectionDialogController controller = newConnectionDialog(parent);
        controller.initializeDialog(url, dbName, port, dbTypeID);

    }

    public static NewConnectionDialogController newConnectionDialog(IMainClass parent) {
        NewConnectionDialogController controller = null;
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Dialogs.class.getResource("NewConnectionDialog.fxml"));

            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Neue Verbindung erstellen");

            // Controller initialisieren
            controller = loader.<NewConnectionDialogController>getController();
            controller.setStage(stage);
            controller.setParent(parent);

            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        return controller;
    }

    public static void newEditExportDialog(RELDB_01 parent, Reldb_Connection connection) {
        NewConnectionDialogController controller = newEditConnectionDialog(parent, connection);
        controller.setIsExportConnection(true);
    }

    /**
     * Einen Dialog zum Bearbeiten einer Verbindung erstellen
     *
     * @param parent
     * @param connection
     * @return
     */
    public static NewConnectionDialogController newEditConnectionDialog(RELDB_01 parent, Reldb_Connection connection) {
        NewConnectionDialogController controller = null;
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Dialogs.class.getResource("NewConnectionDialog.fxml"));

            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Verbindung bearbeiten");

            // Controller initialisieren
            controller = loader.<NewConnectionDialogController>getController();
            controller.setStage(stage);
            controller.setParent(parent);
            controller.initializeDialog(connection);
            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        return controller;
    }

    public static void newSQLDialog(RELDB_01 parent, Reldb_Connection connection) {
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

    public static void newSQLDialog(RELDB_01 parent, Reldb_Connection connection, String command) {
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
            controller.setDefaultCommand(command);
            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException ex) {
            System.out.println("Fehler beim Starten des SQL-Dialogs.");
            System.exit(1);
        }
    }

    public static void newProgressDialog() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Dialogs.class.getResource("ProgressDialog.fxml"));

            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Kopiere Daten");

            // Controller initialisieren
            ProgressDialogController controller = loader.<ProgressDialogController>getController();
            controller.setStage(stage);
            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException ex) {
            System.out.println("Fehler beim Starten des Progress-Dialogs.");

        }
    }

    public static void newFilterDialog(Reldb_Column column, boolean isNotFirstFilter, MainController parent) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Dialogs.class.getResource("newFilterDialog.fxml"));

            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Neuer Filter");

            // Controller initialisieren
            NewFilterDialogController controller = loader.<NewFilterDialogController>getController();
            controller.setStage(stage);
            controller.setColumn(column);
            controller.setFirstElementVisibility(isNotFirstFilter);
            controller.setParent(parent);
            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException ex) {
            System.out.println("Fehler beim Starten des Filter-Dialogs.");
            System.out.println(ex.getMessage());

        }
    }

}
