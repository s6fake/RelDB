package reldb.ui.dialogs;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import reldb.lib.database.Reldb_Column;
import reldb.lib.migration.Filter;
import reldb.ui.MainController;
import reldb.ui.RELDB_01;
import reldb.ui.Select_elementController;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class NewFilterDialogController extends CustomDialog implements Initializable {

    @FXML
    private GridPane gridpane;
    private Reldb_Column column;
    private final List<Select_elementController> elements = new ArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void on_cancel(ActionEvent event) {
        close();
    }

    @FXML
    private void on_ok(ActionEvent event) {
        createString();
        close();
    }

    private void createString() {
        String result = "";
        List<Filter> filters = getFilters();
        for (Filter izzy : filters) {
            result = result + izzy.toString() + " ";
        }
        result = result.substring(0, result.length() - 1);
        result = "(" + result + ")";
        System.out.println(result);
    }

    public void setColumn(Reldb_Column column) {
        this.column = column;
        addConstraint().setVisbility(false);
    }

    public Select_elementController addConstraint() {
        Select_elementController controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(RELDB_01.class.getResource("select_element.fxml"));
            Node root = loader.load();
            int children = gridpane.getChildren().size();
            gridpane.add(root, 0, children);
            controller = loader.<Select_elementController>getController();
            controller.setParent(this);
            controller.setNode(root);
            controller.setColumn(column);
            elements.add(controller);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return controller;
    }

    public boolean removeConstraint(Select_elementController item, Node node) {
        boolean remove = gridpane.getChildren().remove(node);
        if (remove) {
            elements.remove(item);
            Node[] children = new Node[gridpane.getChildren().size()];
            children = gridpane.getChildren().<Node>toArray(children);
            gridpane.getChildren().clear();
            for (int i = 0; i < children.length; i++) {
                gridpane.add(children[i], 0, i);
            }
        }
        return remove;
    }

    private void close() {
        stage.close();
    }

    public List<Filter> getFilters() {
        List<Filter> result = new ArrayList();
        for (Select_elementController iterator : elements) {
            if (iterator.isValid()) {
                result.add(iterator.getFilter());
            }
        }

        return result;
    }

}
