package reldb.lib.migration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import reldb.lib.database.Reldb_Column;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Row;
import reldb.lib.database.Reldb_Table;
import reldb.lib.sql.Reldb_Statement;
import reldb.lib.sql.sql_expr;
import reldb.ui.dialogs.ProgressDialogController;

/**
 *
 * @author s6fake
 */
public class Reldb_DataMover extends Thread {

    private static final Logger log = Logger.getLogger(Reldb_DataMover.class.getName());

    private final static int MAX_ROW = -1;
    private final static int fetchSize = 20;

    public static Progress progress_current, progress_total;
    public static String progress_string = "Erstelle Tabelle ";

    private static ResultSet fetchedResults;

    private final int ThreadID;
    private final Reldb_Database source;
    private final List<Reldb_Table> sourceTables;
    private final List<Reldb_Row> invalidRows = new ArrayList<>();
    private final Reldb_Database destination;

    @Deprecated
    public Reldb_DataMover(Reldb_DatabasePattern source, Reldb_Database destination, ProgressDialogController dialog) {
        this(source, destination);
        dialog.initalizeChangeListener();

    }

    @Deprecated
    public Reldb_DataMover(Reldb_Database source, Reldb_Database destination) {
        this.source = source;
        this.sourceTables = source.getSelectedTables();
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
        progress_total = new Progress(sourceTables.size() + 1);
        progress_current = new Progress(1);
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
        if (!invalidRows.isEmpty()) {
            System.out.println("Fehlerhafte Einträge:");
            for (Reldb_Row row : invalidRows) {
                row.print();
            }
        }
    }

    public void copy() {
        dropAll();
        createAllTables();
        //createAllForeignKeys();
        progress_total.increaseCurrentProgress();
        progress_string = "Kopiere Datensatz ";
        copyAllData2();

        createAllForeignKeys();
    }

    @Deprecated
    public void copy(Reldb_Table source, Reldb_Database destinationDatabase) {
        Reldb_Statement statement = new Reldb_Statement(destinationDatabase.getConnection());

        statement.execute("DROP TABLE " + source.getTableName());
        statement.close();
        statement = new Reldb_Statement(destinationDatabase.getConnection());

        String command = sql_expr.createTable(source, destinationDatabase.getDatabaseType());
        statement.execute(command);
        statement.close();
    }

    @Deprecated
    private List<Reldb_Row> getData(Reldb_Table source) {
        List<Reldb_Row> rows = new ArrayList<>();
        Reldb_Statement statement = new Reldb_Statement(source.getDatabase().getConnection());
        ResultSet results = statement.executeQuery(sql_expr.selectAllFrom(source), fetchSize); // Im ResultSet landen die Datensätze
        try {
            int rowCount = MAX_ROW;
            while (rowCount > 0 && results.next()) {
                rows.add(new Reldb_Row(source, results));
                rowCount--;
            }
            results.close();
        } catch (SQLException ex) {
            log.warning(ex.toString());
        }
        statement.close();
        return rows;
    }

    @Deprecated
    private ResultSet getDataResultSet(Reldb_Table source, Reldb_Statement statement) {
        statement = new Reldb_Statement(source.getDatabase().getConnection());
        return statement.executeQuery(sql_expr.selectAllFrom(source), fetchSize);
    }

    @Deprecated
    private void insertData(Reldb_Table source, Reldb_Database destinationDatabase, ResultSet data) {
        try {
            int rowCount = MAX_ROW;
            while (rowCount > 0 && data.next()) {
                //rows.add(new Reldb_Row(source, data));
                rowCount--;
            }

        } catch (SQLException ex) {
            log.warning(ex.toString());
        }
    }

    private void createAllForeignKeys() {
        sourceTables.stream().forEach((table) -> {
            createForeignKeys(table, destination);
        });
    }

    private void createForeignKeys(Reldb_Table source, Reldb_Database destinationDatabase) {
        for (Reldb_Column column : source.getSelectedColumns()) {
            if (column.isIsForeignKey()) {
                Reldb_Statement statement = new Reldb_Statement(destinationDatabase.getConnection());
                String command = "ALTER TABLE " + source.getTableName() + " ADD " + column.getForeignKeyConstructorString(destinationDatabase.getDatabaseType());
                statement.execute(command);
                //System.out.println(command);
                statement.close();
            }
        }
    }

    @Deprecated
    private void copyAllData() {
        sourceTables.stream().forEach((table) -> {
            setRowCounter(table);
            copyData(table, destination);
            progress_total.increaseCurrentProgress();
        });
    }

    private void copyAllData2() {
        sourceTables.stream().forEach((table) -> {
            setRowCounter(table);
            copyData2(table, destination);
            progress_total.increaseCurrentProgress();
        });
    }

    @Deprecated
    private void copyData(Reldb_Table source, Reldb_Database destinationDatabase) {
        Reldb_Statement statement;
        String valuesCmd, insertIntoCmd = sql_expr.insertIntoTable(source);
        List<Reldb_Row> data = getData(source);        // Daten holen
        if (data.isEmpty()) {
            return;
        }
        // Daten kopieren
        for (int i = 0; i < data.size() - 1; i++) {
            valuesCmd = sql_expr.values(data.get(i));
            statement = new Reldb_Statement(destinationDatabase.getConnection());
            statement.execute(insertIntoCmd + valuesCmd);
            progress_current.increaseCurrentProgress();
            //System.out.println(insertIntoCmd + valuesCmd);
            statement.close();
        }
    }

    private void copyData2(Reldb_Table source, Reldb_Database destinationDatabase) {
        String insertIntoCmd = sql_expr.insertIntoTable(source);

        Reldb_Row newRow;
        Reldb_Statement statement = new Reldb_Statement(source.getDatabase().getConnection());
        ResultSet results = statement.executeQuery(sql_expr.selectFromWConditions(source), fetchSize); // Im ResultSet landen die Datensätze
        try {
            int rowCount = MAX_ROW;
            while ((rowCount > 0 || MAX_ROW == -1) && results.next()) {
                newRow = new Reldb_Row(source, results);
                System.out.println("New RoW");
                if (!insert2(newRow, insertIntoCmd, destinationDatabase)) {
                    invalidRows.add(newRow);
                }
                rowCount--;
            }
            results.close();
        } catch (SQLException ex) {
            log.warning(ex.toString());
        } finally {
            try {
                results.close();
            } catch (SQLException ex) {
                log.warning(ex.getMessage());
            }
        }
        statement.close();

    }

    private boolean insert2(Reldb_Row data, String insertIntoCmd, Reldb_Database destinationDatabase) {
        if (data == null) {
            return false;
        }
        String valuesCmd = sql_expr.values(data);
        Reldb_Statement statement = new Reldb_Statement(destinationDatabase.getConnection());
        boolean result = statement.execute(insertIntoCmd + valuesCmd);

        
        progress_current.increaseCurrentProgress();        
        //System.out.println(destinationDatabase.getDatabaseName() + ": "+insertIntoCmd + valuesCmd);
        statement.close();
        return result;
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
        for (Reldb_Column column : source.getSelectedColumns()) {
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
            progress_current.increaseCurrentProgress();
        });
    }

    private void createTable(Reldb_Table table) {
        Reldb_Statement statement = new Reldb_Statement(destination.getConnection());
        String command = sql_expr.createTableWConditions(table, destination.getDatabaseType());
        statement.execute(command);
        statement.close();
    }


    /*
     Funktionen für die Statistik
     */
    private void setRowCounter(Reldb_Table source) {
        Reldb_Statement statement = new Reldb_Statement(source.getDatabase().getConnection());
        ResultSet result = statement.executeQuery(sql_expr.selectSelectedCount(source));
        int rowCount = 0;
        try {
            result.next();
            rowCount = result.getInt(1);
            System.out.println("COUNT(" + source.getTableName() + "): " + rowCount);
        } catch (SQLException ex) {
            log.warning(ex.getMessage());
        } finally {
            try {
                result.close();
            } catch (SQLException ex) {
                log.warning(ex.getMessage());
            }
        }
        statement.close();
        if (MAX_ROW == -1 || rowCount < MAX_ROW) {
            progress_current.reset(rowCount);
        } else {
            progress_current.reset(MAX_ROW);
        }
    }

}
        // 40774215
