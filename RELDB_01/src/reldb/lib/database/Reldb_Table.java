package reldb.lib.database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s6fake
 */
public class Reldb_Table {

    private static final Logger log = Logger.getLogger(Reldb_Table.class.getName());

    private final String TABLE_TYPE;
    private final String TABLE_CAT;
    private final String TABLE_SCHEM;
    private final String TABLE_NAME; //Name der Tabelle
    private final Reldb_Database database;
    private List<Reldb_Column> columns = new ArrayList<>(); //Liste aller Spalten
    private List<Reldb_Column> primaryKeys = new ArrayList<>(); // Liste aller Primärschlüssel

    private boolean listsFilled = false;

    /**
     * Erstellt einen neue Tabelle
     *
     * @param database Die Datenbank, der die Tabelle zugehörig ist
     * @param TABLE_TYPE
     * @param TABLE_CAT
     * @param TABLE_SCHEM Schema der Tabelle
     * @param TABLE_NAME Name der Tabelle
     */
    public Reldb_Table(Reldb_Database database, String TABLE_TYPE, String TABLE_CAT, String TABLE_SCHEM, String TABLE_NAME) {
        this.TABLE_TYPE = TABLE_TYPE;
        this.TABLE_CAT = TABLE_CAT;
        this.TABLE_SCHEM = TABLE_SCHEM;
        this.TABLE_NAME = TABLE_NAME;
        this.database = database;
        //createColumns(metaData);
        //createPrimaryKeys(metaData);
    }

    void initalize() {
        if (!listsFilled) {
        createColumns();
        createPrimaryKeys();
        createForeignKeys();
        listsFilled = true;
        }
    }

    /**
     * Überträgt alle Spalten aus den Metadaten in die interne Spaltenliste
     */
    public void createColumns() {
        if (!columns.isEmpty()) {
            return;
        }
        ResultSet result = null;
        try {
            result = getMetaData().getColumns(null, TABLE_SCHEM, getTableName(), null); //Metadaten der Spalten holen
            while (result.next()) {
                String columnName = result.getString("COLUMN_NAME");
                int columnType = result.getInt("DATA_TYPE");
                String columnTypeName = result.getString("TYPE_NAME");
                int columSize = result.getInt("COLUMN_SIZE");
                boolean nullable = false;
                if (result.getString("IS_NULLABLE").equalsIgnoreCase("YES")) {
                    nullable = true;
                }
                boolean autoincrement = false;
                if (result.getString("IS_AUTOINCREMENT").equalsIgnoreCase("YES")) {
                    autoincrement = true;
                }
                Reldb_Column newCol = new Reldb_Column(getDatabase(), this, columnName, columnType, columnTypeName, columSize, nullable, autoincrement); // Neue Reldb_Column erstellen
                columns.add(newCol);    //Spalte in die Spaltenliste hinzufügen
                //log.info(TABLE_NAME + "." + columnName + " erstellt.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Reldb_Table.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                result.close();
            } catch (SQLException ex) {
                Logger.getLogger(Reldb_Table.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Funktioniert noch nicht so ganz
     */
    private void getUniqueColumns() {
        ResultSet resultSet = null;
        try {
            resultSet = getMetaData().getIndexInfo(TABLE_CAT, TABLE_SCHEM, TABLE_NAME, true, true);
            while (resultSet.next()) {
                if (resultSet.getBoolean("NON_UNIQUE")) {
                    Reldb_Column col = getColumnByName(resultSet.getString("COLUMN_NAME"));
                    col.setUNIQUE(true);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Reldb_Table.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                resultSet.close();
            } catch (SQLException ex) {
                Logger.getLogger(Reldb_Table.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void createPrimaryKeys() {
        if (!primaryKeys.isEmpty()) {
            return;
        }
        ResultSet result = null;
        try {
            result = getMetaData().getPrimaryKeys(null, TABLE_SCHEM, TABLE_NAME);

            while (result.next()) {
                Reldb_Column col = getColumnByName(result.getString("COLUMN_NAME"));
                if (col == null) {
                    log.warning("Column " + result.getString("TABLE_NAME") + " not in Table!");
                    int count = result.getMetaData().getColumnCount();
                    for (int i = 0; i < count; i++) {
                        System.out.println(result.getString(i + 1));
                    }

                    continue;
                }
                col.setIsPrimaryKey(true);
                primaryKeys.add(col);
            }
        } catch (SQLException ex) {
            log.warning(ex.getMessage());
            //Logger.getLogger(Reldb_Table.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } finally {
            try {
                result.close();
            } catch (SQLException ex) {
                log.warning(ex.getMessage());
            }
        }
    }

    public void createForeignKeys() {
        ResultSet result = null;
        try {
            result = getMetaData().getImportedKeys(null, TABLE_SCHEM, TABLE_NAME);

            while (result.next()) {
                //System.out.println(TABLE_NAME + " " + result.getString("FKCOLUMN_NAME") + " " + result.getString("PKTABLE_NAME") + " " + result.getString("PKCOLUMN_NAME"));
                Reldb_Column col = getColumnByName(result.getString("FKCOLUMN_NAME"));
                col.addForeignKey(result.getString("PKTABLE_NAME"), result.getString("PKCOLUMN_NAME"), result.getInt("KEY_SEQ"));

            }
        } catch (SQLException ex) {
            log.warning(ex.getMessage());
            //Logger.getLogger(Reldb_Table.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } finally {
            try {
                result.close();
            } catch (SQLException ex) {
                log.warning(ex.getMessage());
            }
        }
    }

    public Reldb_Column getColumnByName(String columnName) {
        for (Reldb_Column colIterator : columns) {
            if (colIterator.getName().equals(columnName)) {
                return colIterator;
            }
        }
        log.warning("Spalte " + columnName + " ist nicht in der Tabelle " + getTableName() + " vorhanden!");
        return null;
    }

    @Override
    public String toString() {
        String result = "";
        for (Reldb_Column col : primaryKeys) {
            result = result + col.getName() + " ";
        }
        return "TYPE: " + TABLE_TYPE + " CAT: " + TABLE_CAT + " SCHEM: " + TABLE_SCHEM + " NAME: " + TABLE_NAME + "\nPrimary Keys: " + result;

    }

    /**
     * Gibt die Tabelle und alle Spalten aus
     */
    public void print() {
        String result = "";
        for (Reldb_Column iterator : getColumns()) {
            result = result.concat(iterator.toString() + " ");
        }
        System.out.print("TYPE: " + TABLE_TYPE + "\nCAT: " + TABLE_CAT + "\nSCHEM: " + TABLE_SCHEM);
        System.out.println("|" + getTableName() + "|" + result);
    }

    /**
     * @return the TABLE_NAME
     */
    public String getTableName() {
        return TABLE_NAME;
    }

    /**
     * @return the columns
     */
    public List<Reldb_Column> getColumns() {
        if (!listsFilled) {
            this.initalize();
        }
        return columns;
    }

    /**
     * @return the metaData
     */
    public DatabaseMetaData getMetaData() {
        return getDatabase().getMetaData();
    }

    /**
     * @return the database
     */
    public Reldb_Database getDatabase() {
        return database;
    }

}
