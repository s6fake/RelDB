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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import reldb.Reldb_TreeViewElement;
import reldb.lib.Reldb_Connection;
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
    private TreeView<Reldb_TreeViewElement> con_treeView;
    private TreeItem<Reldb_TreeViewElement> treeConnRoot = new TreeItem<>(new Reldb_TreeViewElement(null, "Connections"));
    @FXML
    private ContextMenu treeView_ContextMenu;
    @FXML
    private MenuItem contextMenu_item_connect;
    @FXML
    private MenuItem contextMenu_item_close;
    @FXML
    private MenuItem contextMenu_item_add;
    @FXML
    private MenuItem contextMenu_item_delete;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        con_treeView.setRoot(treeConnRoot);
    }

    public void setParent(RELDB_01 parent) {
        this.parent = parent;
    }

    public void addTreeItem(Reldb_TreeViewElement item) {
        addTreeItem(treeConnRoot, item);
    }

    public void addTreeItem(TreeItem<Reldb_TreeViewElement> tParent, Reldb_TreeViewElement item) {
        TreeItem<Reldb_TreeViewElement> newTreeItem = new TreeItem<>(item);
        tParent.getChildren().add(newTreeItem);
    }

    public TreeItem<Reldb_TreeViewElement> getTreeItemByName(String name) {
        return getTreeItemByName(treeConnRoot, name);
    }

    public TreeItem<Reldb_TreeViewElement> getTreeItemByName(TreeItem<Reldb_TreeViewElement> tParent, String name) {
        for (TreeItem<Reldb_TreeViewElement> iterator : tParent.getChildren()) {
            if ((iterator.getValue().toString()).equals(name)) {
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

    @FXML
    private void onTreeView_MouseClicked(MouseEvent event) {

    }

    @FXML
    private void contextMenu_connect(ActionEvent event) {
        //contextMenu_item_connect ist nur aktiv, wenn das ausgewählte Item eine Verbindung ist
        Dialogs.loginDialog(parent, (Reldb_Connection)con_treeView.getSelectionModel().getSelectedItem().getValue().getItem());
    }

    @FXML
    private void contextMenu_close(ActionEvent event) {
    }

    @FXML
    private void contextMenu_delete(ActionEvent event) {
    }

    private void setContextMenuToDefault() {
        contextMenu_item_connect.setDisable(true);
        contextMenu_item_add.setDisable(false);
        contextMenu_item_close.setDisable(true);
        contextMenu_item_delete.setDisable(true);
    }

    @FXML
    private void contextMenu_onShow(WindowEvent event) {
        setContextMenuToDefault();
        TreeItem<Reldb_TreeViewElement> selectedItem = con_treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        Reldb_TreeViewElement element = selectedItem.getValue();
        if (element.getItem() == null) {
            return;
        }
        if ((element.getItem() instanceof Reldb_Connection)) {

            Reldb_Connection con = (Reldb_Connection) element.getItem();
            if (!con.isConnected()) {
                contextMenu_item_connect.setDisable(false);
            }
            contextMenu_item_close.setDisable(false);
            contextMenu_item_delete.setDisable(false);
        }
    }

    @FXML
    private void contextMenu_add(ActionEvent event) {
        Dialogs.newConnectionDialog(parent);
    }

}
