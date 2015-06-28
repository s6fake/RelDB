package reldb02.library;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import reldb.lib.database.Reldb_Row;
import reldb.lib.database.Reldb_Table;
import reldb.lib.sql.Reldb_Statement;
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
    private TableView<?> table_rating;
    private Stage stage;

    private Reldb_Row selectedMovie = null;     // Film, der vielleicht ausgeliehen wird.

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        col_btn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Reldb_Row, Boolean>, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Reldb_Row, Boolean> features) {
                        return new SimpleBooleanProperty(features.getValue() != null);
                    }
                });

        // create a cell value factory with an add button for each row in the table.
        col_btn.setCellFactory(new Callback<TableColumn<Reldb_Row, Boolean>, TableCell<Reldb_Row, Boolean>>() {
            @Override
            public TableCell<Reldb_Row, Boolean> call(TableColumn<Reldb_Row, Boolean> personBooleanTableColumn
            ) {
                return new GiveMovieCell(tableView_users, "Give");
            }
        });
    }

    public void initialize(Reldb_Table userTable, Reldb_Table rentTable, Reldb_Table ratingTable) {
        col_users.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("name"));
        table_users = userTable;


        TableColumn<Reldb_Row, String> rCId = new TableColumn<>("Customer");
        rCId.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("customer_name"));
        TableColumn<Reldb_Row, String> rMId = new TableColumn<>("Movie");
        rMId.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("movie_name"));
        TableColumn<Reldb_Row, String> rdate = new TableColumn<>("Rent Date");
        rdate.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("rent_date"));
        TableColumn<Reldb_Row, String> retdate = new TableColumn<>("Return Date");
        retdate.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("return_date"));
        tableView_rent.getColumns().setAll(rCId, rMId, rdate, retdate);
        String[] rentColumns ={"customer_name", "movie_name","rent_date", "return_date"};
        table_rent = new Reldb_Table("title_rent", rentColumns);
        String command = "SELECT c.name AS customer_name, t.title AS movie_name, TO_CHAR(r.rent_date, 'DD.MM.YY') AS rent_date, TO_CHAR(r.RETURN_DATE, 'DD.MM.YY') AS RETURN_DATE FROM title_rent r JOIN IMDB.title t ON r.movie_id = t.id JOIN customer c ON r.customer_id = c.id";
        Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection());
        ResultSet results = statement.executeQuery(command);
        
        
        table_rent.addRows(results);
        //table_rent = rentTable;
    }

    @FXML
    private void on_addUser(ActionEvent event) {
        Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection());

        statement.executeUpdate("INSERT INTO customer (name) VALUES ('new User')");
        statement.close();
    }

    void show() {
        tableView_users.getItems().setAll(table_users.getRows());
        tableView_rent.getItems().setAll(table_rent.getRows());
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
