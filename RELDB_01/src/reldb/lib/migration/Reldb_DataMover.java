package reldb.lib.migration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import reldb.lib.database.Reldb_Column;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Row;
import reldb.lib.database.Reldb_Schema;
import reldb.lib.database.Reldb_Table;
import reldb.lib.sql.Reldb_Statement;
import reldb.lib.sql.sql_expr;

/**
 *
 * @author s6fake
 */
public class Reldb_DataMover extends Thread {

    private static final Logger log = Logger.getLogger(Reldb_DataMover.class.getName());

    private final static int MAX_ROW = 2;
    private static ResultSet fetchedResults;

    private final int ThreadID;
    private final Reldb_Database source;
    private final List<Reldb_Table> sourceTables;
    private final Reldb_Database destination;

    public Reldb_DataMover(Reldb_DatabasePattern source, Reldb_Database destination) {
        this.source = source;
        this.sourceTables = source.getTables();
        this.destination = destination;
        ThreadID = 0;
    }

    public Reldb_DataMover(List<Reldb_Table> tables, Reldb_Database destination) {
        if (tables == null) {
            //MEEP
        }
        this.source = tables.get(0).getDatabase();
        this.sourceTables = tables;
        this.destination = destination;
        ThreadID = 0;
    }

    private Reldb_DataMover(Reldb_DataMover parent) {
        this.source = parent.source;
        this.sourceTables = parent.sourceTables;
        this.destination = parent.destination;
        ThreadID = 1;
    }

    @Override
    public void run() {
        System.out.println("Thread " + ThreadID + " gestartet!");
        if (ThreadID == 0) {
            System.out.println("Tabellen zu kopieren: " + sourceTables.size());
            System.out.println("Zieldatenbank: " + destination.getDatabaseName());
            new Reldb_DataMover(this).start();
            copy();
        }
        System.out.println("Thread " + ThreadID + " ist fertig!");
    }

    public void copy() {

        dropAll();
        createAllTables();
        //createAllForeignKeys();
        copyAllData();

    }

    public void copy(Object object, Reldb_Database destinationDatabase) {
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

    public void copy(Reldb_Database sourceDatabase, Reldb_Database destinationDatabase) {
        for (Reldb_Schema schema : sourceDatabase.getSchemaList()) {
            copy(schema, destinationDatabase);
        }
    }

    public void copy(Reldb_Schema source, Reldb_Database destinationDatabase) {
        List<Reldb_Table> tables = source.getTableList();
        // DROP Foreignkeys
        for (Reldb_Table table : tables) {
            dropForeignKeys(table, destinationDatabase);
        }

        for (Reldb_Table table : tables) {
            copy(table, destinationDatabase);
        }

        for (Reldb_Table table : tables) {
            createForeignKeys(table, destinationDatabase);
        }

        //Daten einfügen
        for (Reldb_Table table : tables) {
            copyData(table, destinationDatabase);
        }

    }

    public void copy(Reldb_Table source, Reldb_Database destinationDatabase) {
        Reldb_Statement statement = new Reldb_Statement(destinationDatabase.getConnection());

        statement.execute("DROP TABLE " + source.getTableName());
        statement.close();
        statement = new Reldb_Statement(destinationDatabase.getConnection());

        String command = sql_expr.createTable(source, destinationDatabase.getDatabaseType());
        statement.execute(command);
        statement.close();
    }

    private void copyAllData() {
        sourceTables.stream().forEach((table) -> {
            copyData(table, destination);
        });
    }

    private void copyData(Reldb_Table source, Reldb_Database destinationDatabase) {
        Reldb_Statement statement;
        String valuesCmd, insertIntoCmd = sql_expr.insertIntoTable(source);
        List<Reldb_Row> data = getData(source);        // Daten holen
        if (data.isEmpty()) {
            return;
        }
        // Daten kopieren
        for (int i = 0; i < MAX_ROW; i++) {
            valuesCmd = sql_expr.values(data.get(i));
            statement = new Reldb_Statement(destinationDatabase.getConnection());
            statement.execute(insertIntoCmd + valuesCmd);
            //System.out.println(insertIntoCmd + valuesCmd);
            statement.close();
        }
    }

    private List<Reldb_Row> getData(Reldb_Table source) {
        List<Reldb_Row> rows = new ArrayList<>();
        Reldb_Statement statement = new Reldb_Statement(source.getDatabase().getConnection());
        ResultSet results = statement.executeQuery(sql_expr.selectAllFrom(source), MAX_ROW); // Im ResultSet landen die Datensätze
        try {
            int fetchSize = MAX_ROW;
            while (fetchSize > 0 && results.next()) {
                rows.add(new Reldb_Row(source, results));
                fetchSize--;
            }
            results.close();
        } catch (SQLException ex) {
            log.warning(ex.toString());
        }
        statement.close();
        return rows;
    }

    private void createAllForeignKeys() {
        sourceTables.stream().forEach((table) -> {
            createForeignKeys(table, destination);
        });
    }

    private void createForeignKeys(Reldb_Table source, Reldb_Database destinationDatabase) {
        for (Reldb_Column column : source.getColumns()) {
            if (column.isIsForeignKey()) {
                Reldb_Statement statement = new Reldb_Statement(destinationDatabase.getConnection());
                String command = "ALTER TABLE " + source.getTableName() + " ADD " + column.getForeignKeyConstructorString(destinationDatabase.getDatabaseType());
                statement.execute(command);
                //System.out.println(command);
                statement.close();
            }
        }
    }

    private void dropAll() {
        /*
         sourceTables.stream().forEach((table) -> {
         dropForeignKeys(table, destination);
         });
         */
        sourceTables.stream().forEach((table) -> {
            dropTable(table);
        });
    }

    private void dropTable(Reldb_Table table) {
        Reldb_Statement statement = new Reldb_Statement(destination.getConnection());
        statement.execute("DROP TABLE " + table.getTableName());
        statement.close();
    }

    private void dropForeignKeys(Reldb_Table source, Reldb_Database destinationDatabase) {
        for (Reldb_Column column : source.getColumns()) {
            if (column.isIsForeignKey()) {
                Reldb_Statement statement = new Reldb_Statement(destinationDatabase.getConnection());
                String command = "ALTER TABLE " + source.getTableName() + " DROP CONSTRAINT " + column.getForeignKeyConstraintName();
                statement.execute(command);
                //System.out.println(command);
                statement.close();
            }
        }
    }

    private void createAllTables() {
        sourceTables.stream().forEach((table) -> {
            createTable(table);
        });
    }

    private void createTable(Reldb_Table table) {
        Reldb_Statement statement = new Reldb_Statement(destination.getConnection());
        String command = sql_expr.createTable(table, destination.getDatabaseType());
        statement.execute(command);
        statement.close();
    }
}
