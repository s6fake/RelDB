package reldb.lib.database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private List<Reldb_Row> rows = new ArrayList<>();       // Für Aufgabe 2
    
    private boolean listsFilled = false;
    private boolean selected = false;       // Wurde die Tabelle für den Export ausgewählt?

    /**
     * Erstellt einen neue Tabelle
     *
     * @param database Die Datenbank, der die Tabelle zugehörig ist
     * @param TABLE_TYPE
     * @param TABLE_CAT
     * @param TABLE_SCHEM Schema der Tabelle
     * @param TABLE_NAME Name der Tabelle
     */
    protected Reldb_Table(Reldb_Database database, String TABLE_TYPE, String TABLE_CAT, String TABLE_SCHEM, String TABLE_NAME) {
        this.TABLE_TYPE = TABLE_TYPE;
        this.TABLE_CAT = TABLE_CAT;
        this.TABLE_SCHEM = TABLE_SCHEM;
        this.TABLE_NAME = TABLE_NAME;
        this.database = database;
        //createColumns(metaData);
        //createPrimaryKeys(metaData);
    }

    /**
     * Erstellt eine neue Tabelle, unabhängig von einer Datenbank
     *
     * @param tableName
     * @param columns
     */
    public Reldb_Table(String tableName, Reldb_Column[] columns) {
        this(tableName);
        this.columns.addAll(Arrays.asList(columns));
        listsFilled = true;
    }

    /**
     * Erstellt eine neue Tabelle, unabhängig von einer Datenbank
     *
     * @param tableName
     * @param columns
     */
    public Reldb_Table(String tableName, String[] columns) {
        this(tableName);
        for (int c = 0; c < columns.length; c++) {
            this.columns.add(new Reldb_Column(this, columns[c]));
        }
        listsFilled = true;
    }

    private Reldb_Table(String tableName) {
        this.TABLE_TYPE = "TABLE";
        this.TABLE_NAME = tableName;
        this.TABLE_CAT = null;
        this.TABLE_SCHEM = null;
        this.database = null;
    }

    void initalize() {
        if (!listsFilled) {
            listsFilled = true;
            createColumns();
            createPrimaryKeys();
            createForeignKeys();

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
    
    public void addRows(ResultSet rowData) {
        try {
            while(rowData.next()) {
                rows.add(new Reldb_Row(this, rowData));
            }
        } catch (SQLException ex) {
            log.log(Level.INFO, ex.getMessage());
        }
    }
    

    public Reldb_Column getColumnByName(String columnName) {
        for (Reldb_Column colIterator : getColumns()) {
            if (colIterator.getName().equalsIgnoreCase(columnName)) {
                return colIterator;
            }
        }
        log.warning("Spalte " + columnName + " ist nicht in der Tabelle " + getTableName() + " vorhanden!");
        return null;
    }

    public String printInfo() {
        String result = "";
        for (Reldb_Column col : primaryKeys) {
            result = result + col.getName() + " ";
        }
        return "TYPE: " + TABLE_TYPE + " CAT: " + TABLE_CAT + " SCHEM: " + TABLE_SCHEM + " NAME: " + TABLE_NAME + "\nPrimary Keys: " + result;
    }

    @Override
    public String toString() {
        return TABLE_NAME;
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
     *
     * @return Die Spalten, die zuvor ausgewählt wurden
     */
    public List<Reldb_Column> getSelectedColumns() {
        List<Reldb_Column> list = new ArrayList();
        for (Reldb_Column col : getColumns()) {
            if (col.isSelected()) {
                list.add(col);
            }
        }
        return list;
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

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
