package reldb.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import reldb.lib.migration.Filter;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class Filter_displayElementController implements Initializable {

    MainController parent;

    @FXML
    private Label label;
    private Filter filter;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void on_remove(ActionEvent event) {
        //parent.remove(this);
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
        label.setText(filter.toString());
    }


    void setParent(Filter_displayContainerController aThis) {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
