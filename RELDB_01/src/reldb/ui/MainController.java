/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class MainController implements Initializable {

    @FXML
    private TextArea textbox;
    @FXML
    private Label label_1;
    @FXML
    private ProgressBar pgbar;
    private RELDB_01 parent;
    @FXML
    private MenuItem edit_sql;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }

    @FXML
    private void open_connection(ActionEvent event) {
        parent.callLoginDialog();
    }

    @FXML
    private void close_connection(ActionEvent event) {
    }

    public void setParent(RELDB_01 parent) {
        this.parent = parent;
    }

    @FXML
    private void make_sql_query(ActionEvent event) {
        parent.callSQLDialog();
    }
}
