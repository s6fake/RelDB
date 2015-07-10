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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;
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

    @FXML
    private Tab tab_characters;
    @FXML
    private TableView<Reldb_Row> table_characters;
     TableColumn<Reldb_Row, String> charNameCol = new TableColumn<>("Character Name");
    TableColumn<Reldb_Row, String> personCol = new TableColumn<>("Person");
    TableColumn<Reldb_Row, String> movieCol = new TableColumn<>("Title");   
    TableColumn<Reldb_Row, String> yearCol_character = new TableColumn<>("Year");
    Reldb_Table characterTable;
    @FXML
    private Tab tab_companies;

    @FXML
    private TableView<?> table_companies;

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

        actionCol.setCellFactory((TableColumn<Reldb_Row, Boolean> p) -> new LendMovieCell(table_titles, "Lend"));

        // Tabelle für die Ergebnisse erstellen
        String[] titleCols = {"id", "title", "production_year", "kind", "episode_of", "available"};
        titleTable = new Reldb_Table("Title", titleCols);

        table_titles.getColumns().setAll(actionCol, titleCol, yearCol, kindCol, episodeOfCol);
        // Zum öffnen der Detailansicht
        table_titles.setRowFactory(p -> {
            TableRow<Reldb_Row> row = new TableRow();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    try {
                        TitleDetailsController.makeController().initialize(Integer.parseInt(row.getItem().get("ID").getValueSafe()), row.getItem().get("title").getValueSafe());
                    } catch (NullPointerException ex) {
                        log.log(Level.SEVERE, ex.getMessage());
                    }
                }
            });
            return row;
        });
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

    private void initializeCharacterTable() {
        charNameCol.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("char_name"));
        personCol.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("name"));
        movieCol.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("title"));
        yearCol_character.setCellValueFactory((CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("year"));
        
        // Tabelle für die Ergebnisse erstellen
        String[] characterCols = {"id", "char_name", "name", "title", "year"};
        characterTable = new Reldb_Table("Characters", characterCols);
        table_characters.getColumns().setAll(charNameCol, personCol, movieCol, yearCol_character);
    }
    
    void initialize(ResultSet resultsTitle, ResultSet resultsPerson, ResultSet resultsCharacters) {
        if (resultsTitle != null) {
            titleTable.addRowsAndClose(resultsTitle);
            table_titles.getItems().addAll(titleTable.getRows());
        }
        if (resultsTitle == null || titleTable.getRows().isEmpty()) {
            tab_titles.setDisable(true);
        }
        
        if (resultsPerson != null) {
            personTable.addRowsAndClose(resultsPerson);
            table_persons.getItems().addAll(personTable.getRows());
        }
        if (resultsPerson == null || personTable.getRows().isEmpty()) {
            tab_persons.setDisable(true);
        }
        
        if (resultsCharacters != null) {
            characterTable.addRowsAndClose(resultsCharacters);
            table_characters.getItems().addAll(characterTable.getRows());
        }
        if (resultsCharacters == null || characterTable.getRows().isEmpty()) {
            tab_characters.setDisable(true);
        }
    }
}
