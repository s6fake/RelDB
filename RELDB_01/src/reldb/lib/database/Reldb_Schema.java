package reldb.lib.database;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author s6fake
 */
public class Reldb_Schema {

    private static final Logger log = Logger.getLogger(Reldb_Schema.class.getName());

    private final String schemaName;
    private List<Reldb_Table> tableList;    //Liste aller im Tabellen im Schema

    public Reldb_Schema(String schemaName) {
        this.schemaName = schemaName;
        tableList = new ArrayList<>();
    }

    public boolean addTable(Reldb_Table table) {
        if (table == null || getTableList() == null) {
            return false;
        }
        return getTableList().add(table);
    }

    public void print() {
        System.out.println(schemaName);
        for (Reldb_Table table : getTableList()) {
            table.print();
        }
    }

    @Override
    public String toString() {
        return schemaName;
    }

    /**
     * @return the schemaName
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * @return the tableList
     */
    public List<Reldb_Table> getTableList() {
        return tableList;
    }
}
