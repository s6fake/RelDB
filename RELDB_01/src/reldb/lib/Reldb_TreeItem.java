package reldb.lib;

import java.util.Collection;
import java.util.List;
import javafx.scene.control.CheckBoxTreeItem;
import reldb.lib.database.Reldb_Column;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Table;

/**
 *
 * @author s6fake
 */
public class Reldb_TreeItem extends CheckBoxTreeItem {

    public boolean discovered = false;
    private String displayName = null;

    public Reldb_TreeItem(Object value) {
        super(value);
    }

    public Reldb_TreeItem(Object value, String displayName) {
        super(value);
        this.displayName = displayName;
    }

    /**
     * Gucken obs Funktioniert.
     *
     * @param value
     * @param selected
     */
    public Reldb_TreeItem(Object value, boolean selected) {
        super(value, null, selected);
    }    
    
    /**
     * Funktion für den ChangeListener. Setzt das im Element befindliche Objekt.
     * @param newValue 
     */
    public void selectStateChanged(boolean newValue) {
        if (getValue() instanceof Reldb_Table) {
            Reldb_Table table = (Reldb_Table) getValue();
            table.setSelected(newValue);
        }

        if (getValue() instanceof Reldb_Column) {
            Reldb_Column column = (Reldb_Column) getValue();
            column.setSelected(newValue);
        }
    }

    public Collection<?> discover() {
        if (!discovered) {
            discovered = true;
            if (getValue() instanceof Reldb_Database) {
                Reldb_Database database_item = (Reldb_Database) getValue();
                return database_item.getTables();
            }
            if (getValue() instanceof Reldb_Table) {
                Reldb_Table table_item = (Reldb_Table) getValue();
                //table_item.createColumns();
                //table_item.createPrimaryKeys();
                //table_item.createForeignKeys();
                return table_item.getColumns();
            }
        }
        return null;
    }

    public void add(Reldb_TreeItem item) {
        if (isSelected()) {
            item.setSelected(true);
        }
        getChildren().add(item);

    }

    public String printInfo() {
        Object item = getValue();
        if (item instanceof Reldb_Database) {
            return ((Reldb_Database) item).printInfo();
        }
        if (item instanceof Reldb_Table) {
            return ((Reldb_Table) item).printInfo();
        }
        if (item instanceof Reldb_Column) {
            return ((Reldb_Column) item).printInfo();
        }
        return super.toString();
    }
}
