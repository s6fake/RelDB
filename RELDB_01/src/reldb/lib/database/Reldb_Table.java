package reldb.lib.database;

import java.util.List;

/**
 *
 * @author s6fake
 */
public class Reldb_Table {
    private String tableName;
    private final int itemCount;

    private List<Reldb_DataContainer> columns;
    
    public Reldb_Table(String name, int itemCount){
        this.tableName = name;
        this.itemCount = itemCount;
    }
    
    public void addColumn(Reldb_DataContainer col) {
        columns.add(col);
    }
    
    @Override
    public String toString() {
        return tableName;
    }
    
    
}
