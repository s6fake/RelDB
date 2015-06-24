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

    private String titleCommand = "SELECT t.title, t.production_year, k.kind FROM #title t JOIN #kind_type k ON t.kind_id = k.id";
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
        //if (connection.getDatabaseType() == Reldb_Database.DATABASETYPE.ORACLE) {
            titleCommand = titleCommand.replace("#", "IMDB.");
            personCommand = personCommand.replace("#", "IMDB.");
        /*} else {//if (connection.getDatabaseType() == Reldb_Database.DATABASETYPE.POSTGRESQL) {
            titleCommand = titleCommand.replace("#", "");
            personCommand = personCommand.replace("#", "");
        }*/
            accordion.expandedPaneProperty().set(pane_simpleSearch);
    }

    @FXML
    private void on_simpleSearch(ActionEvent event) {
        if (!textField_keywords.getText().isEmpty()) {
           
            String titleCondition = convertInput("t.title");
            String nameCondition = convertInput("n.name");
            Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection());
            int titleCount = statement.selectCount("IMDB.title t", titleCondition);
            System.out.println("Count: " + titleCount);
            ResultSet titleResults = statement.executeQuery(titleCommand + " " + titleCondition);
            parent.newResultTab(titleResults, titleCount);
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
            result = result +  " upper(" + colString + ") = '" + input + "'";
        }

        return result;
    }
}
