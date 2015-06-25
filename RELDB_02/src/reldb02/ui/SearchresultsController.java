package reldb02.ui;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
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
    TableColumn<Reldb_Row, Boolean> actionCol = new TableColumn<>("");

    Reldb_Table titleTable;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titleCol.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get(0));
        yearCol.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get(1));
        kindCol.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get(2));

        actionCol.setSortable(false);

        // define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
        actionCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Reldb_Row, Boolean>, ObservableValue<Boolean>>() {
                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Reldb_Row, Boolean> features) {
                        return new SimpleBooleanProperty(features.getValue() != null);
                    }
                });

        // create a cell value factory with an add button for each row in the table.
        actionCol.setCellFactory(
                new Callback<TableColumn<Reldb_Row, Boolean>, TableCell<Reldb_Row, Boolean>>() {
                    @Override
                    public TableCell<Reldb_Row, Boolean> call(TableColumn<Reldb_Row, Boolean> personBooleanTableColumn
                    ) {
                        return new LendMovieCell(table_titles);
                    }
                });

        String[] titleCols = {"title", "production_year", "kind"};
        titleTable = new Reldb_Table("Title", titleCols);

        table_titles.getColumns().setAll(titleCol, yearCol, kindCol, actionCol);

    }

    void initialize(ResultSet resultsTitle) {
        titleTable.addRows(resultsTitle);
        table_titles.getItems().addAll(titleTable.getRows());
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
