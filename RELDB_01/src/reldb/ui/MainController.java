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
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import reldb.ui.dialogs.Dialogs;

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
    private RELDB_01 parent;
    @FXML
    private TreeView<String> con_treeView;
    private TreeItem<String> treeConnRoot = new TreeItem<>("Connections");

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        con_treeView.setRoot(treeConnRoot);
    }

    private void open_connection(ActionEvent event) {
        parent.callLoginDialog();
    }

    public void setParent(RELDB_01 parent) {
        this.parent = parent;
    }
    
        public void addTreeItem(String str) {
        addTreeItem(treeConnRoot, str);
    }

    public void addTreeItem(TreeItem<String> tParent, String str) {
        TreeItem<String> item = new TreeItem<>(str);
        tParent.getChildren().add(item);
    }
    
    public TreeItem<String> getTreeItemByName(String name) {
        return getTreeItemByName(treeConnRoot, name);
    }

    public TreeItem<String> getTreeItemByName(TreeItem<String> tParent, String name) {
        for (TreeItem iterator : tParent.getChildren()) {
            if (((String) iterator.getValue()).equals(name)) {
                return iterator;
            }
        }
        return null;
    }

    @FXML
    private void make_sql_query(ActionEvent event) {
        parent.callSQLDialog();
    }

    @FXML
    private void menu_file_newConnection(ActionEvent event) {
        Dialogs.newConnectionDialog(parent);
    }
}
