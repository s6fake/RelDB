/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.ui;

import java.io.IOException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import reldb.StringClass;
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
    private static Reldb_Connection connection;
    private MetaDataManager mdManager;

    private MainController controller;
    ObservableList<StringClass> observableTableNames = FXCollections.observableArrayList();

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
            controller.setTableNameRef(observableTableNames);
            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException e) {
            System.out.println("Fehler beim Starten der Application");
            System.exit(1);
        }
        //logIn("x","y");
    }

    public void callLoginDialog() {
        Dialogs.loginDialog(this);
    }

    public void callSQLDialog() {
        Dialogs.executeDialog(this, connection);
    }

    public void logIn(String user, String password) {
        if (connection.EstablishConnection(url, user, password)) {
            mdManager = new MetaDataManager(connection.getMetadata());
            mdManager.printInfo(controller.textbox);
            controller.label_1.setText(url);
            updateTableNames();
          //  controller.tables_view.getColumns().setAll(controller.tables_column);
        }
    }
    
    public void updateTableNames() {
        StatementManager statement = new StatementManager(connection.newStatement());
        mdManager.updateTable(observableTableNames, statement.executeCommand("select table_name from information_schema.tables where table_schema = 'public'"));
        statement.close();
    }

    public static void main(String[] args) {
        connection = Reldb_Connection.getInstance();
        launch(args);

        connection.CloseConnection();
    }

}
