/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.ui;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import reldb.StringClass;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class MainController implements Initializable {

    @FXML
    public TextArea textbox;
    @FXML
    public Label label_1;
    @FXML
    private ProgressBar pgbar;
    private RELDB_01 parent;
    @FXML
    private MenuItem edit_sql;
    @FXML
    public TableView<StringClass> tables_view;
    public TableColumn<StringClass, String> tables_column;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       tables_view.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

    public void setTableNameRef(ObservableList<?> list){
        tables_column.setCellValueFactory(new PropertyValueFactory<StringClass, String>("string")); 
        tables_view.setItems((ObservableList<StringClass>) list);
        
        tables_view.getColumns().setAll(tables_column);

    }
    
    @FXML
    private void make_sql_query(ActionEvent event) {
        parent.callSQLDialog();
    }
}
