package reldb.ui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import reldb.lib.IReldb_TreeViewElement;
import reldb.lib.Reldb_TreeViewElement;
import reldb.lib.Reldb_Connection;
import reldb.lib.Reldb_TreeViewCheckElement;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Table;
import reldb.lib.migration.Reldb_DataMover;
import reldb.ui.dialogs.Dialogs;
import reldb.lib.sql.sql_expr;

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
    private TreeView<IReldb_TreeViewElement> con_treeView;
    private TreeItem<IReldb_TreeViewElement> treeConnRoot = new TreeItem(new Reldb_TreeViewElement("", "No Connections"));
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
    @FXML
    private MenuItem contextMenu_item_edit;
    @FXML
    private MenuItem contextMenu_item_query;
    @FXML
    private MenuItem contextMenu_item_export;
    @FXML
    private Menu contextMenu_item_exportMenu;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        con_treeView.setRoot(treeConnRoot);

        /**
         * Event Handler zur TreeView hinzufügen
         */
        con_treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<IReldb_TreeViewElement> selectedItem = (TreeItem<IReldb_TreeViewElement>) newValue;
                if (selectedItem.getValue() == null) {
                    return;
                }
                if (!selectedItem.getValue().isDiscovered()) {                  // Prüfen ob das Element schon einmal besucht wurde

                    con_treeView.setDisable(true);                          // TreeView deaktivieren, damit nicht mit unfertigen Daten gearbeitet wird
                    List<?> items = selectedItem.getValue().discover();     // Gegebenenfalls neue Kind-Elemente hinzufügen
                    if (items != null) {
                        addTreeItems(selectedItem, items);
                    }
                    con_treeView.setDisable(false);                         // TreeView wieder aktivieren
                }
                textbox.clear();
                textbox.insertText(0, selectedItem.getValue().getItem().toString());
            }
        });
    }

    public void setParent(RELDB_01 parent) {
        this.parent = parent;
    }

    public void setTreeRoot(Reldb_Database database) {
        treeConnRoot = new TreeItem(new Reldb_TreeViewElement(database, database.getDatabaseName()));
        //treeConnRoot 
        con_treeView.setRoot(treeConnRoot);
    }

    /**
     * Fügt eine Datenbank in die TreeView ein
     *
     * @param connection Die Verbindung, zu der die Datenbank eingefügt werden
     * soll.
     * @param database Die Datenbank die eingefügt werden soll
     */
    @Deprecated
    public void addDatabaseToConnectionInTreeView(Reldb_Connection connection, Reldb_Database database) {
        TreeItem<IReldb_TreeViewElement> connectionRoot = addTreeItem(new Reldb_TreeViewElement(connection, connection.getConnectionName()));// Neues Verbungs-Wurzelelement
        addTreeItem(connectionRoot, new Reldb_TreeViewElement(database, database.getDatabaseName()));   //Datenbank Element einfügen
    }

    public TreeItem<IReldb_TreeViewElement> addConnectionToTreeView(IReldb_TreeViewElement item) {
        return addTreeItem(treeConnRoot, item);
    }

    public TreeItem<IReldb_TreeViewElement> addTreeItem(IReldb_TreeViewElement item) {
        updateExportMenu();
        return addTreeItem(treeConnRoot, item);
    }

    public void addTreeItems(TreeItem<IReldb_TreeViewElement> tParent, List<?> items) {
        items.stream().forEach((item) -> {
            addTreeItem(tParent, new Reldb_TreeViewCheckElement(item));
        });
    }

    public TreeItem<IReldb_TreeViewElement> addTreeItem(TreeItem<IReldb_TreeViewElement> tParent, IReldb_TreeViewElement item) {
        TreeItem<IReldb_TreeViewElement> newTreeItem = new TreeItem<>(item);
        ObservableList<TreeItem<IReldb_TreeViewElement>> children = tParent.getChildren();

        //VERBESSERN:
        for (TreeItem<IReldb_TreeViewElement> iterator : children) {

            if (iterator.getValue().getDisplayName().equals(item.getDisplayName())) //Prüfen ob das es ein Element mit gleichem Namen schon gibt
            {
                return iterator;     //Wenn ja, kein neues anlegen
            }
        }

        tParent.getChildren().add(newTreeItem);
        return newTreeItem;
    }

    public TreeItem<IReldb_TreeViewElement> getTreeItemByName(String name) {
        return getTreeItemByName(treeConnRoot, name);
    }

    public TreeItem<IReldb_TreeViewElement> getTreeItemByName(TreeItem<IReldb_TreeViewElement> tParent, String name) {
        for (TreeItem<IReldb_TreeViewElement> iterator : tParent.getChildren()) {
            if ((iterator.getValue().toString()).equals(name)) {
                return iterator;
            }
        }
        return null;
    }

    public void deleteConnectionFromTreeView(TreeItem<IReldb_TreeViewElement> element) {
        deleteTreeItem(element);
        updateExportMenu();
    }

    private void deleteTreeItem(TreeItem<IReldb_TreeViewElement> element) {
        for (TreeItem<IReldb_TreeViewElement> child : element.getChildren()) {
            deleteTreeItem(child);
        }
        if (element.getParent() != null) {
            element.getParent().getChildren().remove(element);
        }
        element = null;
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
    @Deprecated
    private void onTreeView_MouseClicked(MouseEvent event) {
        // Kann weg...
    }

    @FXML
    private void contextMenu_connect(ActionEvent event
    ) {
        //contextMenu_item_connect ist nur aktiv, wenn das ausgewählte Item eine Verbindung ist
        Dialogs.loginDialog(parent, (Reldb_Connection) con_treeView.getSelectionModel().getSelectedItem().getValue().getItem());
    }

    @FXML
    private void contextMenu_close(ActionEvent event) {
    }

    @FXML
    private void contextMenu_delete(ActionEvent event) {
        parent.removeConnection((Reldb_Connection) con_treeView.getSelectionModel().getSelectedItem().getValue().getItem());
    }

    /**
     * Setzt alle Einträge im Kontext Menü der Treeview zurück (Nur Add ist
     * aktiv)
     */
    private void setContextMenuToDefault() {
        contextMenu_item_connect.setDisable(true);
        contextMenu_item_add.setDisable(false);
        contextMenu_item_close.setDisable(true);
        contextMenu_item_edit.setDisable(true);
        contextMenu_item_delete.setDisable(true);
        contextMenu_item_query.setDisable(true);
        contextMenu_item_export.setDisable(true);
        contextMenu_item_exportMenu.setDisable(true);
    }

    private void setExportMenuVisibility(String selectedConnectionName) {
        for (MenuItem item : contextMenu_item_exportMenu.getItems()) {
            item.setDisable(false);
            if (item.getText().equals(selectedConnectionName)) {
                item.setDisable(true);
            }
        }
    }

    @FXML
    private void contextMenu_onShow(WindowEvent event) {
        setContextMenuToDefault();
        TreeItem<IReldb_TreeViewElement> selectedItem = con_treeView.getSelectionModel().getSelectedItem();
        String selectedConnection;
        if (selectedItem == null) {
            return;
        }
        IReldb_TreeViewElement element = selectedItem.getValue();
        if (element.getItem() == null) {
            return;
        }
        if ((element.getItem() instanceof Reldb_Connection)) {

            Reldb_Connection con = (Reldb_Connection) element.getItem();
            if (!con.isConnected()) {
                contextMenu_item_connect.setDisable(false);
            }
            contextMenu_item_close.setDisable(false);
            contextMenu_item_edit.setDisable(false);
            contextMenu_item_delete.setDisable(false);
            contextMenu_item_query.setDisable(false);
            return;
        }   // End instanceof Reldb_Connection

        if ((element.getItem() instanceof Reldb_Database)) {
            selectedConnection = ((Reldb_Database) (element.getItem())).getConnection().getConnectionName();
            setExportMenuVisibility(selectedConnection);

            contextMenu_item_exportMenu.setDisable(false);
            return;
        }

        if ((element.getItem() instanceof Reldb_Table)) {
            selectedConnection = ((Reldb_Table) (element.getItem())).getDatabase().getConnection().getConnectionName();
            setExportMenuVisibility(selectedConnection);
            contextMenu_item_export.setDisable(false);
            contextMenu_item_exportMenu.setDisable(false);
            return;
        }

    }

    private void updateExportMenu() {
        contextMenu_item_exportMenu.getItems().clear();
        for (Reldb_Connection connection : Reldb_Connection.getConnections()) {
            final MenuItem item = new MenuItem(connection.getConnectionName());

            item.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    Reldb_Connection selectedDestinationConnection = Reldb_Connection.getConnectionByName(item.getText());
                    TreeItem<IReldb_TreeViewElement> selectedItem = con_treeView.getSelectionModel().getSelectedItem();
                    //Reldb_DatabasePattern collection = null;
                    IReldb_TreeViewElement item = selectedItem.getValue();
                    if (item.getItem() instanceof Reldb_Database) {
                        // collection = new Reldb_DatabasePattern((Reldb_Database) selectedItem.getValue().getItem());
                    } else if (item.getItem() instanceof Reldb_Table) {
                        //collection = new Reldb_DatabasePattern((Reldb_Table) selectedItem.getValue().getItem());
                    }

                    //Reldb_DataMover.copy(selectedItem.getValue().getItem(), selectedDestinationConnection.getDatabase());
                    //Dialogs.newSQLDialog(parent, selectedDestinationConnection);
                }
            });

            contextMenu_item_exportMenu.getItems().add(item);
        }
    }

    @FXML
    private void contextMenu_add(ActionEvent event) {
        Dialogs.newConnectionDialog(parent);
    }

    @FXML
    private void contextMenu_edit(ActionEvent event) {
        TreeItem<IReldb_TreeViewElement> selectedItem = con_treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        IReldb_TreeViewElement element = selectedItem.getValue();
        if (element.getItem() == null) {
            return;
        }
        Dialogs.newEditConnectionDialog(parent, (Reldb_Connection) element.getItem());
    }

    @FXML
    private void contextMenu_query(ActionEvent event) {
        Dialogs.newSQLDialog(parent, (Reldb_Connection) con_treeView.getSelectionModel().getSelectedItem().getValue().getItem());
    }

    @FXML
    private void contextMenu_export(ActionEvent event) {
        Reldb_Database db = (Reldb_Database) (con_treeView.getSelectionModel().getSelectedItem().getParent().getParent().getValue().getItem());
        Dialogs.newSQLDialog(parent, db.getConnection(), sql_expr.createTable((Reldb_Table) (con_treeView.getSelectionModel().getSelectedItem().getValue().getItem())));
    }

    @FXML
    private void contextMenu_exportMenu_onShow(Event event) {

    }

}
