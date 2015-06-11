package reldb.lib.migration;

import java.sql.ResultSet;
import reldb.lib.database.Reldb_Column;
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

    private final static int MAX_ROW = 10;
    private static ResultSet fetchedResults;
    
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

        // DROP Foreignkeys
        for (Reldb_Table table : source.getTableList()) {
            dropForeignKeys(table, destinationDatabase);
        }

        for (Reldb_Table table : source.getTableList()) {
            copy(table, destinationDatabase);
        }

        for (Reldb_Table table : source.getTableList()) {

            createForeignKeys(table, destinationDatabase);
        }
    }

    public static void copy(Reldb_Table source, Reldb_Database destinationDatabase) {
        Reldb_Statement statement = new Reldb_Statement(destinationDatabase.getConnection());

        statement.execute("DROP TABLE " + source.getTableName());
        statement.close();
        statement = new Reldb_Statement(destinationDatabase.getConnection());

        String command = sql_expr.createTable(source, destinationDatabase.getDatabaseType());
        statement.execute(command);
        statement.close();
    }

    private static void copyData() {
        
    }
    
    
    private static void createForeignKeys(Reldb_Table source, Reldb_Database destinationDatabase) {
        for (Reldb_Column column : source.getColumns()) {
            if (column.isIsForeignKey()) {
                Reldb_Statement statement = new Reldb_Statement(destinationDatabase.getConnection());
                String command = "ALTER TABLE " + source.getTableName() + " ADD " + column.getForeignKeyConstructorString(destinationDatabase.getDatabaseType());
                statement.execute(command);
                System.out.println(command);
                statement.close();
            }
        }
    }

    private static void dropForeignKeys(Reldb_Table source, Reldb_Database destinationDatabase) {
        for (Reldb_Column column : source.getColumns()) {
            if (column.isIsForeignKey()) {
                Reldb_Statement statement = new Reldb_Statement(destinationDatabase.getConnection());
                String command = "ALTER TABLE " + source.getTableName() + " DROP CONSTRAINT " + column.getForeignKeyConstraintName();
                statement.execute(command);
                System.out.println(command);
                statement.close();
            }
        }
    }

    public static void create_Table(Reldb_Database destination, Reldb_Table table_pattern) {
        Reldb_Statement statement = new Reldb_Statement(destination.getConnection());
        String command = sql_expr.createTable(table_pattern);
        statement.execute(command);
        statement.close();
    }
}
