package reldb.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import reldb.lib.database.Reldb_Column;
import reldb.lib.database.Reldb_Table;
import reldb.lib.migration.Filter;

/**
 *
 * @author keil
 */
public class FilterTab extends Tab {

    // Das TabPane, in dem die Filter eingefügt werden
    private TabPane tabPane = new TabPane();
    private Map<Reldb_Column, List<Filter>> filterList = new HashMap<Reldb_Column, List<Filter>>();
    private Map<Reldb_Column, Filter_displayContainerController> filterContainerList = new HashMap<Reldb_Column, Filter_displayContainerController>();
    
    
    /**
     * Erzeugt einen Tab mit einem TabPane für Filter
     *
     * @param table
     */
    public FilterTab(Reldb_Table table) {
        super(table.getTableName());
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

    public void addFilter(Reldb_Column column, Filter filter) {
        Filter_displayContainerController controller;
        if (filterContainerList.containsKey(column)) {
            controller = filterContainerList.get(column);
        } else {
            controller = newFilterTab(column);
        }
        controller.addFilter(filter);
        column.addFilter(filter);

    }
}
