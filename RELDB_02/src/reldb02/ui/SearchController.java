package reldb02.ui;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import reldb.lib.Reldb_Connection;
import reldb.lib.database.Reldb_Database;
import reldb.lib.sql.Reldb_Statement;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class SearchController implements Initializable {

    @FXML
    private ToggleGroup match_group;
    @FXML
    private CheckBox cb_filterYear;
    @FXML
    private CheckBox cb_filterRating;
    @FXML
    private CheckBox cb_filterVoting;
    @FXML
    private CheckBox cb_filterBudget;
    @FXML
    private CheckBox cb_filterRuntime;
    @FXML
    private TextField textField_keywords;
    @FXML
    private RadioButton rb_MatchAll;
    @FXML
    private RadioButton rb_MatchAny;
    @FXML
    private RadioButton rb_MatchExact;
    private Search_mainController parent;

    private String titleCommand = "SELECT t.id, t.title, t.production_year, k.kind, t2.title AS episode_of, CASE WHEN (tr.movie_id > 0) THEN 'FALSE' ELSE 'TRUE' END AS available\n"
            + "FROM IMDB.title t JOIN IMDB.kind_type k ON t.kind_id = k.id \n"
            + "LEFT OUTER JOIN (SELECT DISTINCT movie_id FROM TITLE_RENT WHERE return_date IS NULL) tr ON tr.movie_id = t.id\n"
            + "LEFT OUTER JOIN IMDB.title t2 ON t.episode_of_id = t2.id";
    //"SELECT t.id, t.title, t.production_year, k.kind FROM #title t JOIN #kind_type k ON t.kind_id = k.id";
    private String personCommand = "SELECT n.name FROM #name n";

    private Reldb_Connection connection;
    @FXML
    private Accordion accordion;
    @FXML
    private TitledPane pane_simpleSearch;
    @FXML
    private TitledPane pane_extendedSearch;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connection = RELDB_02.getConnection();
        accordion.expandedPaneProperty().set(pane_simpleSearch);
    }

    @FXML
    private void on_simpleSearch(ActionEvent event) {
        if (!textField_keywords.getText().isEmpty()) {

            String titleCondition = convertInput("t.title");
            String nameCondition = convertInput("n.name");
            Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection());

            ResultSet titleResults = statement.executeQuery(titleCommand + " " + titleCondition);
            parent.newResultTab(textField_keywords.getText(), titleResults);
            statement.close();
            //System.out.println(titleCondition);
        }
    }

    public void setParent(Search_mainController parent) {
        this.parent = parent;
    }

    /**
     * " und ' müssen noch gefiltert werden!
     *
     * @return
     * @deprecated
     */
    String convertInput(String colString) {
        String result = "WHERE";
        String input = textField_keywords.getText();

        if (rb_MatchAll.isSelected()) {
            input = input.toUpperCase();

            //ToDo: Zeichen Rausfiltern
            String[] keyWords = input.split(" ");
            for (String str : keyWords) {
                result = result + " upper(" + colString + ") LIKE '%" + str + "%' AND";
            }
            result = result.substring(0, result.length() - 4);
        } else if (rb_MatchAny.isSelected()) {
            input = input.toUpperCase();

            //ToDo: Zeichen Rausfiltern
            String[] keyWords = input.split(" ");
            for (String str : keyWords) {
                result = result + " upper(" + colString + ") LIKE '%" + str + "%' OR";
            }
            result = result.substring(0, result.length() - 3);
        } else if (rb_MatchExact.isSelected()) {
            input = input.toUpperCase();
            result = result + " upper(" + colString + ") = '" + input + "'";
        }

        return result;
    }

    @FXML
    private void text_on_keyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (accordion.getExpandedPane() == pane_simpleSearch) {
                on_simpleSearch(null);
            }
        }
    }
}
