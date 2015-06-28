package reldb02.ui;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import reldb.ui.IMainClass;
import reldb.ui.dialogs.Dialogs;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class Search_mainController implements Initializable {

    private static final Logger log = Logger.getLogger(Search_mainController.class.getName());

    @FXML
    private TabPane tabPane;
    private IMainClass parent;

    private List<SearchresultsController> resultControllers = new LinkedList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setParent(IMainClass parent) {
        this.parent = parent;
    }

    @FXML
    private void login(ActionEvent event) {
        Dialogs.newConnectionDialog(parent, "dbvm02.iai.uni-bonn.de", "dbvm02", 1521, 1);
    }

    @FXML
    public void newSearchTab() {
        try {
            FXMLLoader loader = new FXMLLoader(RELDB_02.class.getResource("search.fxml"));
            Node node = loader.load();
            SearchController controller = loader.<SearchController>getController();
            controller.setParent(this);
            Tab newTab = new Tab("Search", node);
            tabPane.getTabs().add(newTab);

        } catch (IOException ex) {
            log.log(Level.WARNING, ex.getMessage());
        }
    }

    public void newResultTab(String keywords, ResultSet results) {
        try {
            FXMLLoader loader = new FXMLLoader(RELDB_02.class.getResource("searchresults.fxml"));
            Node node = loader.load();
            SearchresultsController controller = loader.<SearchresultsController>getController();

            controller.initialize(results);
            resultControllers.add(controller);

            Tab newTab = new Tab("Results for: \"" + keywords + "\"", node);
            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);
        } catch (IOException ex) {
            log.log(Level.WARNING, ex.getMessage());
        }
    }
}
