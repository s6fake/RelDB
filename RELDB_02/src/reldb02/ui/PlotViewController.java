package reldb02.ui;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import reldb.lib.Reldb_TreeItem;
import reldb.lib.database.Reldb_Row;
import reldb.lib.database.Reldb_Table;
import reldb.lib.sql.Reldb_Statement;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class PlotViewController implements Initializable {

    @FXML
    private TableView<Reldb_Row> tableView;
    @FXML
    private TableColumn<Reldb_Row, String> col_note;
    @FXML
    private TableColumn<Reldb_Row, String> col_info;
    @FXML
    private TextArea textfield;
    @FXML
    private Label label;

    private static final Logger log = Logger.getLogger(PlotViewController.class.getName());

    public static PlotViewController makeController() {
        PlotViewController controller = null;
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(PlotViewController.class.getResource("plotView.fxml"));

            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Plots");

            // Controller initialisieren
            controller = loader.<PlotViewController>getController();
            //stage.initOwner(parent);

            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException ex) {
            log.log(Level.SEVERE, ex.getMessage());
        }
        return controller;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        col_info.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("INFO"));
        col_note.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("NOTE"));

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                textfield.setText(newVal.get("INFO").getValue());
            }
        });
    }

    public void initialize(int title_id, String title) {
        label.setText("Plots for \"" + title + "\"");
        Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection());
        tableView.getItems().addAll(getPlots(statement, title_id).getRows());

        statement.close();
        
        Pane header = (Pane) tableView.lookup("TableHeaderRow");
        header.setMaxHeight(0);
        header.setMinHeight(0);
        header.setPrefHeight(0);
        header.setVisible(false);
    }

    private Reldb_Table getPlots(Reldb_Statement statement, int title_id) {
        String[] titleCols = {"INFO", "NOTE"};
        Reldb_Table plotsTable = new Reldb_Table("Plots", titleCols);
        ResultSet results = statement.executeQuery("SELECT TO_CHAR(mi.info) AS INFO, mi.note AS NOTE FROM IMDB.movie_info mi WHERE mi.info_type_id = 98 AND mi.movie_id = " + title_id);
        plotsTable.addRowsAndClose(results);
        return plotsTable;
    }

}
