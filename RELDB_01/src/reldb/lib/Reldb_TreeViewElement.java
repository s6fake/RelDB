package reldb.lib;


import java.util.List;
import reldb.lib.database.Reldb_Column;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Schema;
import reldb.lib.database.Reldb_Table;

/**
 *
 * @author s6fake
 */
public class Reldb_TreeViewElement implements IReldb_TreeViewElement {

    private final Object item;
    private String displayName;
    public boolean discovered = false;

    public Reldb_TreeViewElement(Object item, String displayName) {
        this.item = item;
        this.displayName = displayName;
    }

    public Reldb_TreeViewElement(Object item) {
        this(item, "Vashta Nerada");
        if (item instanceof Reldb_Table) {
            displayName = ((Reldb_Table) item).getTableName();
        }
        if (item instanceof Reldb_Schema) {
            displayName = ((Reldb_Schema) item).getSchemaName();
        }
        if (item instanceof Reldb_Column) {
            displayName = ((Reldb_Column) item).getName();
        }
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * @return the item
     */
    public Object getItem() {
        return item;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    public List<?> discover() {
        if (!discovered) {
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
    public boolean isDiscovered() {
        return discovered;
    }

}
