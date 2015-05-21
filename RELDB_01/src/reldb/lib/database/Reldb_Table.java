package reldb.lib.database;

import java.util.List;

/**
 *
 * @author s6fake
 */
public class Reldb_Table {
    private final String tableName;

    private List<Reldb_Column> columns;
    
    /**
     * Erstellt einen neue leere Tabelle
     * @param name Name der Tabelle
     */
    public Reldb_Table(String name){
        this.tableName = name;
    }
    
    public void addColumn(Reldb_Column col) {
        columns.add(col);
    }
    
    @Override
    public String toString() {
        return tableName;
    }
    
    
}
