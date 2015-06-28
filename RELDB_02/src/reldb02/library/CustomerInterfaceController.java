package reldb02.library;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import reldb.lib.database.Reldb_Row;
import reldb.lib.database.Reldb_Table;
import reldb.lib.sql.Reldb_Statement;
import reldb.ui.dialogs.Dialogs;
import reldb02.ui.RELDB_02;

/**
 * FXML Controller class
 *
 * @author Fabo
 */
public class CustomerInterfaceController implements Initializable {

    @FXML
    private TableView<Reldb_Row> tableView_users;
    private Reldb_Table table_users;
    @FXML
    private TableColumn<Reldb_Row, String> col_users;
    @FXML
    private TableColumn<Reldb_Row, Boolean> col_btn;
    @FXML
    private TableView<Reldb_Row> tableView_rent;
    private Reldb_Table table_rent;
    @FXML
    private TableColumn<Reldb_Row, String> col_rent_CID;
    @FXML
    private TableColumn<Reldb_Row, String> col_rent_MID;
    @FXML
    private TableColumn<Reldb_Row, String> col_rent_rentDate;
    @FXML
    private TableColumn<Reldb_Row, String> col_rent_returnDate;
    @FXML
    private TableColumn<Reldb_Row, Boolean> col_rent_btn;
    @FXML
    private TableView<?> table_rating;
    private Stage stage;

    private Reldb_Row selectedMovie = null;     // Film, der vielleicht ausgeliehen wird.

    private ObservableList<Reldb_Row> rentData = FXCollections.observableArrayList();
    private SortedList<Reldb_Row> sortedData;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        col_btn.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, Boolean> features) -> new SimpleBooleanProperty(features.getValue() != null));

        // create a cell value factory with an add button for each row in the table.
        col_btn.setCellFactory((TableColumn<Reldb_Row, Boolean> personBooleanTableColumn) -> new GiveMovieCell(tableView_users, "Give"));
        col_rent_btn.setCellFactory((TableColumn<Reldb_Row, Boolean> personBooleanTableColumn) -> new GiveMovieCell(tableView_users, "Return"));

    }

    public void initialize(Reldb_Table userTable, Reldb_Table rentTable, Reldb_Table ratingTable) {
        rentData = FXCollections.observableArrayList();
        col_users.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("name"));
        table_users = userTable;

        col_rent_CID.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("customer_name"));
        col_rent_MID.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("movie_name"));
        col_rent_rentDate.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("rent_date"));
        col_rent_returnDate.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("return_date"));
        //tableView_rent.getColumns().setAll(col_rent_CID, col_rent_MID, col_rent_rentDate, col_rent_returnDate, col_rent_btn);

        String[] rentColumns = {"id", "customer_name", "movie_name", "rent_date", "return_date"};
        table_rent = new Reldb_Table("title_rent", rentColumns);
        String command = "SELECT c.id, c.name AS customer_name, t.title AS movie_name, TO_CHAR(r.rent_date, 'DD.MM.YY') AS rent_date, TO_CHAR(r.return_date, 'DD.MM.YY') AS return_date FROM title_rent r JOIN IMDB.title t ON r.movie_id = t.id JOIN customer c ON r.customer_id = c.id";
        Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection());
        ResultSet results = statement.executeQuery(command);

        table_rent.addRows(results);

        FilteredList<Reldb_Row> filteredData = new FilteredList<>(rentData, p -> true);

        tableView_users.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(row -> {
                if (newVal == null) {
                    return true;
                }
                return row.get("ID").toString().equalsIgnoreCase(newVal.get("ID").toString());
            });

        });

        sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tableView_rent.comparatorProperty());
        rentData.addAll(table_rent.getRows());
        //table_rent = rentTable;
    }

    @FXML
    private void on_addUser(ActionEvent event) {
        customerDialog(stage);
    }

    private void customerDialog(Window parent) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(CustomerInterfaceController.class.getResource("newCustomer.fxml"));

            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("new Customer");

            // Controller initialisieren
            NewCustomerController controller = loader.<NewCustomerController>getController();
            controller.setStage(stage);
            stage.initOwner(parent);

            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());

        }
    }

    void show() {
        tableView_users.getItems().setAll(table_users.getRows());
        tableView_rent.setItems(sortedData);
        stage.show();
    }

    void pause() {
        stage.close();
    }

    void setStage(Stage stage) {
        this.stage = stage;
    }

    void setSelectedMovie(Reldb_Row selectedMovie) {
        this.selectedMovie = selectedMovie;
    }
}
