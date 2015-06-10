package reldb.lib.migration;

import reldb.lib.database.Reldb_Column;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Table;
import reldb.lib.sql.Reldb_Statement;
import reldb.lib.sql.sql_expr;

/**
 *
 * @author s6fake
 */
public class Reldb_DataMover {
    
    
    public static void copy(Reldb_Database sourceDatabase, Reldb_Database destinationDatabase) {
        
    }
    
    public static void create_Table(Reldb_Database destination, Reldb_Table table_pattern) {
        Reldb_Statement statement = new Reldb_Statement(destination.getConnection());
        String command = sql_expr.createTable(table_pattern);
        statement.executeCommand(command);       
    }
}
