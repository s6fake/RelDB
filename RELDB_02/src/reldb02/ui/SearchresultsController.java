package reldb02.ui;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import reldb.lib.database.Reldb_Row;
import reldb.lib.database.Reldb_Table;
import reldb02.library.LendMovieCell;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class SearchresultsController implements Initializable {

    private static final Logger log = Logger.getLogger(SearchresultsController.class.getName());

    @FXML
    private Tab tab_titles;
    @FXML
    private TableView<Reldb_Row> table_titles;
    TableColumn<Reldb_Row, String> titleCol = new TableColumn<>("Title");
    TableColumn<Reldb_Row, String> yearCol = new TableColumn<>("Year");
    TableColumn<Reldb_Row, String> kindCol = new TableColumn<>("Kind");
    TableColumn<Reldb_Row, String> episodeOfCol = new TableColumn<>("Epidsode of");
    TableColumn<Reldb_Row, Boolean> actionCol = new TableColumn<>("");
    Reldb_Table titleTable;

    @FXML
    private Tab tab_persons;
    @FXML
    private TableView<Reldb_Row> table_persons;
    TableColumn<Reldb_Row, String> nameCol = new TableColumn<>("Name");
    TableColumn<Reldb_Row, String> birthCol = new TableColumn<>("Birthday");
    TableColumn<Reldb_Row, String> genderCol = new TableColumn<>("Gender");
    Reldb_Table personTable;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeTitleTable();
        initializePersonTable();
    }

    private void initializeTitleTable() {
        titleCol.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("title"));
        yearCol.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("production_year"));
        kindCol.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("kind"));
        episodeOfCol.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("episode_of"));
        actionCol.setCellValueFactory((CellDataFeatures<Reldb_Row, Boolean> p) -> new SimpleBooleanProperty(p.getValue().get("available").getValueSafe().equalsIgnoreCase("true")));

        actionCol.setSortable(false);

        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        /*actionCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Reldb_Row, Boolean>, ObservableValue<Boolean>>() {
         @Override
         public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Reldb_Row, Boolean> features) {
         return new SimpleBooleanProperty(features.getValue() != null);
         }
         });*/
        // create a cell value factory with an add button for each row in the table.
        actionCol.setCellFactory((TableColumn<Reldb_Row, Boolean> p) -> new LendMovieCell(table_titles, "Lend"));

        // Tabelle für die Ergebnisse erstellen
        String[] titleCols = {"id", "title", "production_year", "kind", "episode_of", "available"};
        titleTable = new Reldb_Table("Title", titleCols);

        table_titles.getColumns().setAll(actionCol, titleCol, yearCol, kindCol, episodeOfCol);
    }

    private void initializePersonTable() {
        nameCol.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("name"));
        birthCol.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("birthday"));
        genderCol.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("gender"));

        // Tabelle für die Ergebnisse erstellen
        String[] personCols = {"id", "name", "birthday", "gender"};
        personTable = new Reldb_Table("Person", personCols);

        table_persons.getColumns().setAll(nameCol, birthCol, genderCol);
    }

    void initialize(ResultSet resultsTitle, ResultSet resultsPerson) {
        titleTable.addRows(resultsTitle);
        table_titles.getItems().addAll(titleTable.getRows());
        if (resultsPerson != null) {
        personTable.addRows(resultsPerson);
        table_persons.getItems().addAll(personTable.getRows());
        } else {
            tab_persons.setDisable(true);
        }
    }

    @Deprecated
    void initializeArray(String[] titles, ResultSet resultSet, int titleCount) {
        /* if (titleCount == 0) {
         tab_titles.setDisable(true);
         return;
         }
         String[][] dataArray = convertTo2dArray(titles, resultSet, titleCount);
         ObservableList<String[]> data = FXCollections.observableArrayList();
         data.addAll(Arrays.asList(dataArray));
         data.remove(0);//remove titles from data
         for (int i = 0; i < titles.length; i++) {
         TableColumn tc = new TableColumn(dataArray[0][i]);

         final int colNo = i;
         tc.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
         @Override
         public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
         return new SimpleStringProperty((p.getValue()[colNo]));
         }
         });
         //tc.setPrefWidth(90);
         table_titles.getColumns().add(tc);
         }
         table_titles.setItems(data);*/
    }

    @Deprecated
    private String[][] convertTo2dArray(String[] titles, ResultSet results, int size) {
        String[][] rows;
        rows = new String[size + 1][titles.length];
        int colCount = titles.length;
        rows[0] = titles;

        int rowCount = 1;
        try {
            while (results.next()) {
                String[] row = new String[colCount];
                for (int i = 0; i < colCount; i++) {
                    row[i] = (results.getString(i + 1));
                    System.out.print(results.getString(i + 1) + " ");
                }
                rows[rowCount] = row;
                rowCount++;
                System.out.println();
            }
        } catch (SQLException ex) {
            log.log(Level.WARNING, ex.getMessage());
        } finally {
            try {
                results.close();
            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
        return rows;

    }

    private static class Reldb_TableImpl extends Reldb_Table {

        public Reldb_TableImpl(String tableName, String[] columns) {
            super(tableName, columns);
        }
    }
}
