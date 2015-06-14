
package reldb.lib;

import java.util.List;
import javafx.scene.control.CheckBox;
import reldb.lib.database.Reldb_Column;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Schema;
import reldb.lib.database.Reldb_Table;

/**
 *
 * @author s6fake
 */
public class Reldb_TreeViewCheckElement extends CheckBox implements IReldb_TreeViewElement{

    private final Object item;
    private String displayName;
    private boolean discovered = false;
    
    public Reldb_TreeViewCheckElement(Object item, String displayName) {
        super(item.toString());
        this.item = item;
        this.displayName = displayName;
        setText(displayName);
    }

    public Reldb_TreeViewCheckElement(Object item) {
        super(item.toString());
        this.item = item;
        this.displayName =  "Vashta Nerada";
        if (item instanceof Reldb_Table) {
            displayName = ((Reldb_Table) item).getTableName();
        }
        if (item instanceof Reldb_Schema) {
            displayName = ((Reldb_Schema) item).getSchemaName();
        }
        if (item instanceof Reldb_Column) {
            displayName = ((Reldb_Column) item).getName();
        }
        setText(displayName);
    }



    @Override
    public String toString() {
        return displayName;
    }

    /**
     * @return the item
     */
    @Override
    public Object getItem() {
        return item;
    }

    /**
     * @return the displayName
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public List<?> discover() {
        if (!isDiscovered()) {
            discovered = true;
            if (item instanceof Reldb_Database) {
                Reldb_Database database_item = (Reldb_Database) item;
                return database_item.getSchemaList();
            }
            if (item instanceof Reldb_Schema) {
                Reldb_Schema schema_item = (Reldb_Schema) item;
                return schema_item.getTableList();
            }
            if (item instanceof Reldb_Table) {
                Reldb_Table table_item = (Reldb_Table) item;
                //table_item.createColumns();
                //table_item.createPrimaryKeys();
                //table_item.createForeignKeys();
                return table_item.getColumns();
            }
        }

        return null;
    }

    /**
     * @return the discovered
     */
    @Override
    public boolean isDiscovered() {
        return discovered;
    }

}
