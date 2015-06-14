package reldb.lib.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author s6fake
 */
public class Reldb_Row {
    
    private Reldb_Table table;
    private Reldb_DataContainer[] cells;
    

    public Reldb_Row(Reldb_Table table, ResultSet objects) {
        this.table = table;
        this.cells = new Reldb_DataContainer[table.getColumns().size()];
        insertObjects(objects);
    }
    
    private void insertObjects(ResultSet objects) {
        try {
            int c = getCells().length;
            List<Reldb_Column> columns = table.getColumns();
            for (int i = 0; i < c; i++) {
                String colName = columns.get(i).COLUMN_NAME;
                cells[i] = new Reldb_DataContainer(objects.getObject(colName));
            }
        }
        catch (SQLException e) {
            System.err.print(e.toString());
        }
    }
    
    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < getCells().length; i++) {
            result = result + getCells()[i] + " " ;
        }
        return result;
    }
    
    public void print() {
        System.out.println(table.getTableName());
        System.out.println(this.toString());
    }

    /**
     * @return the cells
     */
    public Reldb_DataContainer[] getCells() {
        return cells;
    }
}
