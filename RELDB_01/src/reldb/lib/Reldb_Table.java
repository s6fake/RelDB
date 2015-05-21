package reldb.lib;

/**
 *
 * @author s6fake
 */
public class Reldb_Table {
    private String tableName;
    private final int itemCount;

    public Reldb_Table(String name, int itemCount){
        this.tableName = name;
        this.itemCount = itemCount;
    }
    
    @Override
    public String toString() {
        return tableName;
    }
    
    
}
