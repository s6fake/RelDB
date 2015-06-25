package reldb.ui;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import javax.swing.JOptionPane;
import reldb.lib.*;
import reldb.lib.database.*;
import reldb.lib.migration.Filter;
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
    private TreeView con_treeView;
    private Reldb_TreeItem treeConnRoot = new Reldb_TreeItem("No Connections");
    @FXML
    private ContextMenu treeView_ContextMenu;
    @FXML
    private MenuItem contextMenu_item_connect;
    @FXML
    private MenuItem contextMenu_item_close;

    @FXML
    private MenuItem contextMenu_item_delete;
    @FXML
    private MenuItem contextMenu_item_edit;
    @FXML
    private MenuItem contextMenu_item_query;
    @FXML
    private MenuItem contextMenu_item_export;

    @FXML
    private MenuBar menuBar;
    @FXML
    private Button btn_export;
    @FXML
    private MenuItem contextMenu_item_filter;
    @FXML
    private TabPane tabPane;

    private Map<Reldb_Column, List<Filter>> filterList = new HashMap<Reldb_Column, List<Filter>>();
    private Map<Reldb_Column, Filter_displayContainerController> filterContainerList = new HashMap<Reldb_Column, Filter_displayContainerController>();

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
                Reldb_TreeItem selectedItem = (Reldb_TreeItem) newValue;
                if (selectedItem.getValue() == null) {
                    return;
                }
                if (!selectedItem.discovered) {                  // Prüfen ob das Element schon einmal besucht wurde

                    con_treeView.setDisable(true);                          // TreeView deaktivieren, damit nicht mit unfertigen Daten gearbeitet wird
                    Collection<?> items = selectedItem.discover();     // Gegebenenfalls neue Kind-Elemente hinzufügen
                    if (items != null) {
                        addTreeItems(selectedItem, items);
                    }
                    con_treeView.setDisable(false);                         // TreeView wieder aktivieren
                }
                textbox.clear();
                textbox.insertText(0, selectedItem.printInfo());
            }
        });
    }

    public void setParent(RELDB_01 parent) {
        this.parent = parent;
    }

    public void setTreeRoot(Reldb_Database database) {
        treeConnRoot = new Reldb_TreeItem(database, database.getDatabaseName());
        con_treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
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
        Reldb_TreeItem connectionRoot = addTreeItem(new Reldb_TreeItem(connection, connection.getConnectionName()));// Neues Verbungs-Wurzelelement
        addTreeItem(connectionRoot, new Reldb_TreeItem(database, database.getDatabaseName()));   //Datenbank Element einfügen
    }

    public Reldb_TreeItem addTreeItem(Object item) {
        //updateExportMenu();
        return addTreeItem(treeConnRoot, item);
    }

    public void addTreeItems(Reldb_TreeItem tParent, Collection<?> items) {
        items.stream().forEach((item) -> {
            addTreeItem(tParent, item);
        });
    }

    public Reldb_TreeItem addTreeItem(Reldb_TreeItem tParent, Object item) {
        Reldb_TreeItem newTreeItem = new Reldb_TreeItem(item);
        ObservableList<Reldb_TreeItem> children = tParent.getChildren();

        // Change Listener hinzufügen
        newTreeItem.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            newTreeItem.selectStateChanged(new_val);
        });

        //VERBESSERN:
        for (Reldb_TreeItem iterator : children) {

            if (iterator.toString().equals(item.toString())) //Prüfen ob das es ein Element mit gleichem Namen schon gibt
            {
                return iterator;     //Wenn ja, kein neues anlegen
            }
        }

        tParent.add(newTreeItem);
        return newTreeItem;
    }

    public Reldb_TreeItem getTreeItemByName(String name) {
        return getTreeItemByName(treeConnRoot, name);
    }

    public Reldb_TreeItem getTreeItemByName(Reldb_TreeItem tParent, String name) {
        for (Object iterator : tParent.getChildren()) {
            if ((iterator.toString()).equals(name)) {
                return (Reldb_TreeItem) iterator;
            }
        }
        return null;
    }

    public void deleteConnectionFromTreeView(Reldb_TreeItem element) {
        deleteTreeItem(element);
        //updateExportMenu();
    }

    private void deleteTreeItem(Reldb_TreeItem element) {
        for (Object child : element.getChildren()) {
            deleteTreeItem((Reldb_TreeItem) child);
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
    private void contextMenu_connect(ActionEvent event) {
        Dialogs.newConnectionDialog(parent);
    }

    @FXML
    private void contextMenu_close(ActionEvent event) {
    }

    @FXML
    private void contextMenu_delete(ActionEvent event) {
        parent.removeConnection((Reldb_Connection) con_treeView.getSelectionModel().getSelectedItem());
    }

    /**
     * Setzt alle Einträge im Kontext Menü der Treeview zurück (Nur Add ist
     * aktiv)
     */
    private void setContextMenuToDefault() {
        contextMenu_item_connect.setDisable(false);
        contextMenu_item_close.setDisable(true);
        contextMenu_item_edit.setDisable(true);
        contextMenu_item_delete.setDisable(true);
        contextMenu_item_query.setDisable(true);
        contextMenu_item_export.setDisable(true);
        contextMenu_item_filter.setDisable(true);
    }

    @Deprecated
    private void setExportMenuVisibility(String selectedConnectionName) {
        /* for (MenuItem item : contextMenu_item_filter.getItems()) {
         item.setDisable(false);
         if (item.getText().equals(selectedConnectionName)) {
         item.setDisable(true);
         }
         }*/
    }

    /**
     * Kümmert sich darum, welche Elemente im Kontextmenü angezeigt werden.
     *
     * @param event
     */
    @FXML
    private void contextMenu_onShow(WindowEvent event) {
        setContextMenuToDefault();
        Reldb_TreeItem selectedItem;
        selectedItem = (Reldb_TreeItem) con_treeView.getSelectionModel().getSelectedItem();
        //String selectedConnection;
        if (selectedItem == null) {
            return;
        }
        Reldb_TreeItem element = selectedItem;
        if (element.getValue() == null) {
            return;
        }
        if ((element.getValue() instanceof Reldb_Connection)) {

            Reldb_Connection con = (Reldb_Connection) element.getValue();
            if (!con.isConnected()) {
                contextMenu_item_connect.setDisable(false);
            }
            contextMenu_item_close.setDisable(false);
            contextMenu_item_edit.setDisable(false);
            contextMenu_item_delete.setDisable(false);
            contextMenu_item_query.setDisable(false);
            return;
        }   // End instanceof Reldb_Connection

        if ((element.getValue() instanceof Reldb_Database)) {
            contextMenu_item_connect.setDisable(false);
            contextMenu_item_export.setDisable(false);
            return;
        }

        if ((element.getValue() instanceof Reldb_Table)) {
            //selectedConnection = ((Reldb_Table) (element.getValue())).getDatabase().getConnection().getConnectionName();
            contextMenu_item_export.setDisable(false);
            return;
        }

        if ((element.getValue() instanceof Reldb_Column)) {
            //selectedConnection = ((Reldb_Column) (element.getValue())).getDatabase().getConnection().getConnectionName();
            contextMenu_item_export.setDisable(false);
            contextMenu_item_filter.setDisable(false);
            return;
        }
    }

    @Deprecated
    private void updateExportMenu() {
        //contextMenu_item_filter.getItems().clear();
        for (Reldb_Connection connection : Reldb_Connection.getConnections()) {
            final MenuItem item = new MenuItem(connection.getConnectionName());

            item.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    Reldb_Connection selectedDestinationConnection = Reldb_Connection.getConnectionByName(item.getText());
                    Reldb_TreeItem selectedItem = (Reldb_TreeItem) con_treeView.getSelectionModel().getSelectedItem();
                    //Reldb_DatabasePattern collection = null;
                    Reldb_TreeItem item = selectedItem;
                    if (item.getValue() instanceof Reldb_Database) {
                        // collection = new Reldb_DatabasePattern((Reldb_Database) selectedItem.getValue().getItem());
                    } else if (item.getValue() instanceof Reldb_Table) {
                        //collection = new Reldb_DatabasePattern((Reldb_Table) selectedItem.getValue().getItem());
                    }

                    //Reldb_DataMover.copy(selectedItem.getValue().getItem(), selectedDestinationConnection.getDatabase());
                    //Dialogs.newSQLDialog(parent, selectedDestinationConnection);
                }
            });

            //  contextMenu_item_filter.getItems().add(item);
        }
    }

    @FXML
    private void contextMenu_edit(ActionEvent event) {
        Reldb_TreeItem selectedItem = (Reldb_TreeItem) con_treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        Object element = selectedItem.getValue();
        if (element == null) {
            return;
        }
        Dialogs.newEditConnectionDialog(parent, (Reldb_Connection) element);
    }

    @FXML
    private void contextMenu_query(ActionEvent event) {
        Dialogs.newSQLDialog(parent, (Reldb_Connection) con_treeView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void on_quit(ActionEvent event) {
        Reldb_Connection.closeAllConnections();
        System.exit(0);
    }

    /**
     * @return the menuBar
     */
    public MenuBar getMenuBar() {
        return menuBar;
    }

    @FXML
    private void on_export(ActionEvent event) {

        int dialogResult = JOptionPane.showConfirmDialog(null, "Alle ausgewählten Daten exportieren?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {

            Dialogs.newExportDialog(parent);
        }
        /*
         Reldb_TreeItem selectedItem = (Reldb_TreeItem) con_treeView.getSelectionModel().getSelectedItem();
         if (selectedItem.getValue() instanceof Reldb_Table) {
         System.out.println(sql_expr.selectFromWConditions((Reldb_Table) selectedItem.getValue()));
         }
         */
    }

    /**
     * Einen neuen FilterDialog für die ausgewähle Spalte aufrufen.
     *
     * @param event
     */
    @FXML
    private void make_new_filter(ActionEvent event) {
        Reldb_TreeItem selectedItem = (Reldb_TreeItem) con_treeView.getSelectionModel().getSelectedItem();
        if (selectedItem.getValue() instanceof Reldb_Column) {
            Reldb_Column selectedColumn = (Reldb_Column) (selectedItem.getValue());
            if (selectedColumn.isFiltered()) {
                Dialogs.newFilterDialog(selectedColumn, true, this);
            } else {
                Dialogs.newFilterDialog(selectedColumn, false, this);
            }
        }
    }

    /**
     * Erzeugt einen neuen Tab, mit leerem DisplayContainer
     *
     * @param column Die Spalte, für die der Filter angelegt werden soll.
     * @return Den neuen Display Container. Nicht verlieren!
     */
    private Filter_displayContainerController newFilterTab(Reldb_Column column) {
        Filter_displayContainerController controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(RELDB_01.class.getResource("filter_displayContainer.fxml"));
            Node node = loader.load();
            controller = loader.<Filter_displayContainerController>getController();
            controller.setColumn(column);
            filterContainerList.put(column, controller);
            Tab newTab = new Tab(column.getName(), node);
            tabPane.getTabs().add(newTab);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return controller;
    }

    public void addNewFilter(Reldb_Column column, Filter filter) {
        Filter_displayContainerController controller;
        if (filterContainerList.containsKey(column)) {
            controller = filterContainerList.get(column);
        } else {
            controller = newFilterTab(column);
        }
        controller.addFilter(filter);
        column.addFilter(filter);
    }

    void remove(Filter_displayElementController aThis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
