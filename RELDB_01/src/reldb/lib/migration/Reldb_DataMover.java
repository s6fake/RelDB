package reldb.lib.migration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import reldb.lib.database.Reldb_Column;
import reldb.lib.database.Reldb_DataContainer;
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

    private static Map<String, String[]> foreignKeys = new HashMap<>();

    private final int ThreadID;
    private final Reldb_Database source;
    private final List<Reldb_Table> sourceTables;
    private final List<Reldb_Table> createdTables;  // Referenzwert, welche Tabellen schon angelegt wurden.
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
        this.createdTables = new ArrayList<>(this.sourceTables.size() * 2);
        this.destination = destination;
        ThreadID = 0;
    }

    public Reldb_DataMover(List<Reldb_Table> tables, Reldb_Database destination) {
        if (tables == null) {
            //MEEP
        }
        this.source = tables.get(0).getDatabase();
        this.sourceTables = tables;
        this.createdTables = new ArrayList<>(this.sourceTables.size() * 2);
        this.destination = destination;
        progress_total = new Progress(sourceTables.size() + 1);
        progress_current = new Progress(1);
        ThreadID = 0;
    }

    private Reldb_DataMover(Reldb_DataMover parent) {
        this.source = parent.source;
        this.sourceTables = parent.sourceTables;
        this.createdTables = new ArrayList<>(this.sourceTables.size() * 2);
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
        createAllForeignKeys();

        progress_total.increaseCurrentProgress();
        progress_string = "Kopiere Datensatz ";
        copyAllData2();

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
            createAllForeignKeysForTable(table, destination);
        });
    }

    private void createAllForeignKeysForTable(Reldb_Table source, Reldb_Database destinationDatabase) {
        for (Reldb_Column column : source.getSelectedColumns()) {
            if (column.isIsForeignKey()) {
                createSingleForeignKey(column, destinationDatabase);
            }
        }
    }

    private boolean createSingleForeignKey(Reldb_Column column, Reldb_Database destinationDatabase) {
        Reldb_Statement statement = new Reldb_Statement(destinationDatabase.getConnection());
        String command = "ALTER TABLE " + column.getTable().getTableName() + " ADD " + column.getForeignKeyConstructorString(destinationDatabase.getDatabaseType());
        SQLException warnings = statement.executeUpdate(command);
        statement.close();
        if (warnings == null) {
            addForeignKeyToList(column.getForeignKeyConstraintName(), column.getTable().getTableName(), column.getName(), column.getRefTableName(), column.getRefColumnName());
            return true;
        } else {
            return handleErrorSpalteNichtda(warnings, column, destinationDatabase);
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
                //System.out.println("New RoW");
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
        SQLException warnings = statement.executeUpdate(insertIntoCmd + valuesCmd);
        if (warnings != null) {
            return handleErrorOderSo(warnings, data, insertIntoCmd, destinationDatabase);
        }
        progress_current.increaseCurrentProgress();
        //System.out.println(destinationDatabase.getDatabaseName() + ": "+insertIntoCmd + valuesCmd);
        statement.close();
        return true;
    }

    private void dropAll() {
        sourceTables.stream().forEach((table) -> {
            dropForeignKeys(table, destination);
        });

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
            createdTables.add(table);
            progress_current.increaseCurrentProgress();
        });
    }

    private boolean createTable(Reldb_Table table) {
        boolean result = false;
        Reldb_Statement statement = new Reldb_Statement(destination.getConnection());
        String command = sql_expr.createTableWConditions(table, destination.getDatabaseType());
        result = statement.execute(command);
        statement.close();
        return result;
    }

    private boolean addColumn(Reldb_Column column, Reldb_Table destinationTable, Reldb_Database destinationDatabase) {
        Reldb_Statement statement = new Reldb_Statement(destinationDatabase.getConnection());
        String command = "ALTER TABLE " + destinationTable.getTableName() + " ADD " + column.getConstructorString(destinationDatabase.getDatabaseType());
        SQLException warnings = statement.executeUpdate(command);
        statement.close();
        if (warnings == null) {
            return true;
        } else {
            return false;
        }
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

    private void addForeignKeyToList(String keyName, String srcTable, String scrColumn, String refTable, String refColumn) {
        String[] newValue = {srcTable, scrColumn, refTable, refColumn};
        if (foreignKeys.put(keyName.toUpperCase(), newValue) != null) {
            log.log(Level.INFO, "ForeignKey {0} wurde \u00fcberschrieben!", keyName);
        }
    }

    /**
     * ForeignKey aus der HashMap holen. Mit Fehler Info
     *
     * @param key
     * @return
     */
    private String[] getForeignKeyFromList(String key) {
        String[] result = foreignKeys.get(key.toUpperCase());
        if (result == null) {
            log.log(Level.WARNING, "ForeignKey {0} wurde nicht gefunden!", key.toUpperCase());
        }
        return result;
    }

    private boolean handleErrorSpalteNichtda(SQLException warnings, Reldb_Column column, Reldb_Database destinationDatabase) {
        if (warnings.getErrorCode() == 904) { // Ungültiger Bezeichner
             /*        Alle Daten zusammentragen       */
            Reldb_Database database = column.getDatabase();
            // Tabelle mit Fremdschlüssel holen
            Reldb_Table refTable = database.getTableByName(column.getRefTableName());
            // Tabelle für den Export markieren
            refTable.setSelected(true);
            // Spalte mit dem Fremdschlüssel holen
            Reldb_Column refColumn = refTable.getColumnByName(column.getRefColumnName());
            // Spalte für den Export markieren
            refColumn.setSelected(true);
            // Prüfen, ob die Tabelle bereits schon angelegt wurde
            if (!createdTables.contains(refTable)) {
                // Tabelle neu anlegen
                if (!createTable(refTable)) {
                    // Bei einem Fehler kann man jetzt auch nichts mehr machen...
                    return false;
                }
            } else {
                // Benutzerabfrage muss noch stattfinden!!
                addColumn(refColumn, refTable, destinationDatabase);
                /*
                log.log(Level.WARNING, "Drop Table {0}!", refTable.getTableName());
                dropTable(refTable);
                */
            }

            return createSingleForeignKey(column, destinationDatabase);
        }

        return false;
    }

    private boolean handleErrorOderSo(SQLException warnings, Reldb_Row data, String insertIntoCmd, Reldb_Database destinationDatabase) {

        if (warnings.getErrorCode() == 2291) {  // Integritäts-Constraint verletzt

            String constraint = getConstraintString(warnings.getMessage());
            String[] references = getForeignKeyFromList(constraint.toUpperCase());
            if (references == null) {
                return false;
            }
            /*        Alle Daten zusammentragen       */
            Reldb_Database database = data.getDatabase();
            // Tabelle mit Fremdschlüssel holen
            Reldb_Table refTable = database.getTableByName(references[2]);
            // Tabelle für den Export markieren
            refTable.setSelected(true);
            // Spalte mit dem Fremdschlüssel holen
            Reldb_Column refColumn = refTable.getColumnByName(references[3]);
            // Spalte für den Export markieren
            refColumn.setSelected(true);
            // Prüfen, ob die Tabelle bereits schon angelegt wurde
            if (!createdTables.contains(refTable)) {
                // Also wenn wir hier landen, dann haben wir echt ein Problem...
                log.log(Level.SEVERE, "Referenzierte Tabelle ist nicht vorhanden ({0})!", refTable.getTableName());
                return false;
            }
            // Fremdschlüssel Datensatz holen.
            Reldb_DataContainer cell = data.getCellByColumn(references[1]);

            Reldb_Row newRow;
            Reldb_Statement statement = new Reldb_Statement(database.getConnection());
            ResultSet results = statement.executeQuery(sql_expr.selectFrom(refTable, cell), fetchSize); // Im ResultSet landen die Datensätze
            try {
                if (results.next()) {
                    newRow = new Reldb_Row(refTable, results);
                    results.close();
                    statement.close();
                    return insert2(newRow, insertIntoCmd, destinationDatabase);
                }
            } catch (SQLException ex) {
                log.warning(ex.toString());
            } finally {
                try {
                    results.close();
                    statement.close();
                } catch (SQLException ex) {
                    log.warning(ex.getMessage());
                }
            }
        }
        return false;
    }

    /**
     * Sehr gewagte Funktion, die einem z.B. den ForeignKey Constraint String
     * liefert.
     *
     * @param errMessage
     * @return
     */
    private String getConstraintString(String errMessage) {
        String result = "xoxo";
        try {
            result = errMessage.split("(")[1];
            result = result.split(")")[0];
            if (result.contains(".")) {
                String results[] = result.split(".");
                result = results[results.length - 1];
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return result;
    }
}
        // 40774215
