package reldb.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import reldb.lib.database.Reldb_Column;
import reldb.lib.migration.Filter;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class Filter_displayContainerController implements Initializable {

    private Reldb_Column column;
    @FXML
    private GridPane gridPane;
    private List<Filter_displayElementController> elements = new ArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setColumn(Reldb_Column column) {
        this.column = column;
    }

    public Reldb_Column getColumn() {
        return column;
    }

    public void addFilter(Filter filter) {
        addElement(filter);
    }

    private Filter_displayElementController addElement(Filter filter) {
        Filter_displayElementController controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("filter_displayElement.fxml"));
            Node root = loader.load();
            int children = gridPane.getChildren().size();
            gridPane.add(root, 0, children);
            controller = loader.<Filter_displayElementController>getController();
            controller.setParent(this);
            controller.setFilter(filter);
            //controller.setNode(root);

            elements.add(controller);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return controller;
    }

}
