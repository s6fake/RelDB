/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formtester;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class TestController implements Initializable {
    @FXML
    private TreeView<String> con_treeView;
    @FXML
    private TextArea textbox;
    @FXML
    private Label label_1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TreeItem<String> rootItem = new TreeItem<String>("Connections");
            TreeItem<String> item = new TreeItem<String>("imdb");
            rootItem.getChildren().add(item);
        con_treeView.setRoot(rootItem);
    }    

    @FXML
    private void open_connection(ActionEvent event) {
    }

    @FXML
    private void make_sql_query(ActionEvent event) {
    }
    
}
