package reldb02.ui;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import reldb.ui.IMainClass;
import reldb.ui.dialogs.Dialogs;
import reldb02.library.Library;

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
    @FXML
    private MenuItem menuItem_new_customer;
    @FXML
    private MenuItem menuItem_show_customers;
    @FXML
    private MenuItem menuItem_new_search;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menuItem_new_customer.disableProperty().bind(RELDB_02.isConnected());
        menuItem_show_customers.disableProperty().bind(RELDB_02.isConnected());
        menuItem_new_search.disableProperty().bind(RELDB_02.isConnected().not());
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

    public void newResultTab(String keywords, ResultSet titleResults, ResultSet personResults, ResultSet characterResults) {
        try {
            FXMLLoader loader = new FXMLLoader(RELDB_02.class.getResource("searchresults.fxml"));
            Node node = loader.load();
            SearchresultsController controller = loader.<SearchresultsController>getController();

            controller.initialize(titleResults, personResults, characterResults);
            resultControllers.add(controller);

            Tab newTab = new Tab("Results for: \"" + keywords + "\"", node);
            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);
        } catch (IOException ex) {
            log.log(Level.WARNING, ex.getMessage());
        }
    }
    
    public void loggedIn() {
        menuItem_new_customer.setDisable(false);
        menuItem_show_customers.setDisable(false);
    }

    @FXML
    private void on_quit(ActionEvent event) {
        RELDB_02.exit(0);
    }

    @FXML
    private void on_new_customer(ActionEvent event) {
    }

    @FXML
    private void on_show_customers(ActionEvent event) {
        Library.getInstance().showInterface();
    }
}
