package reldb.lib.migration;

import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Schema;
import reldb.lib.database.Reldb_Table;
import reldb.lib.sql.Reldb_Statement;
import reldb.lib.sql.sql_expr;

/**
 *
 * @author s6fake
 */
public class Reldb_DataMover {

    public static void copy(Object object, Reldb_Database destinationDatabase) {
        if (object instanceof Reldb_Database) {
            copy((Reldb_Database) object, destinationDatabase);
            return;
        }
        if (object instanceof Reldb_Table) {
            copy((Reldb_Table) object, destinationDatabase);
            return;
        }
        if (object instanceof Reldb_Schema) {
            copy((Reldb_Schema) object, destinationDatabase);
            return;
        }
        
        return;
    }

    public static void copy(Reldb_Database sourceDatabase, Reldb_Database destinationDatabase) {
        for (Reldb_Schema schema : sourceDatabase.getSchemaList()) {
            copy(schema, destinationDatabase);
        }
    }

    public static void copy(Reldb_Schema source, Reldb_Database destinationDatabase) {
        for (Reldb_Table table : source.getTableList()) {
            copy(table, destinationDatabase);
        }
    }

    public static void copy(Reldb_Table source, Reldb_Database destinationDatabase) {
        Reldb_Statement statement = new Reldb_Statement(destinationDatabase.getConnection());
        String command = sql_expr.createTable(source, destinationDatabase.getDatabaseType());
        statement.executeCommand(command);
        statement.close();
    }

    public static void create_Table(Reldb_Database destination, Reldb_Table table_pattern) {
        Reldb_Statement statement = new Reldb_Statement(destination.getConnection());
        String command = sql_expr.createTable(table_pattern);
        statement.executeCommand(command);
    }
}
