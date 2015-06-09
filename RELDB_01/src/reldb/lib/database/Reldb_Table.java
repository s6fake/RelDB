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
    private final DatabaseMetaData metaData;
    private List<Reldb_Column> columns = new ArrayList<>(); //Liste aller Spalten
    private List<Reldb_Column> primaryKeys = new ArrayList<>(); // Liste aller Primärschlüssel

    /**
     * Erstellt einen neue Tabelle
     *
     * @param TABLE_TYPE
     * @param TABLE_CAT
     * @param TABLE_SCHEM Schema der Tabelle
     * @param TABLE_NAME Name der Tabelle
     * @param metaData Metadaten, mit der die Tabelle gefüllt werden soll.
     */
    public Reldb_Table(String TABLE_TYPE, String TABLE_CAT, String TABLE_SCHEM, String TABLE_NAME, DatabaseMetaData metaData) {
        this.TABLE_TYPE = TABLE_TYPE;
        this.TABLE_CAT = TABLE_CAT;
        this.TABLE_SCHEM = TABLE_SCHEM;
        this.TABLE_NAME = TABLE_NAME;
        this.metaData = metaData;
        //createColumns(metaData);
        //createPrimaryKeys(metaData);
    }

    /**
     * Überträgt alle Spalten aus den Metadaten in die interne Spaltenliste
     *
     * @param metaData
     */
    public void createColumns() {
        if (!columns.isEmpty()) {
            return;
        }
        ResultSet result = null;
        try {
            result = metaData.getColumns(null, TABLE_SCHEM, getTableName(), null); //Metadaten der Spalten holen
            while (result.next()) {
                String columnName = result.getString(4);
                int columnType = result.getInt(5);
                String columnTypeName = result.getString(6);
                int columSize = result.getInt(7);
                Reldb_Column newCol = new Reldb_Column(columnName, columnType, columnTypeName, columSize); // Neue Reldb_Column erstellen
                //System.out.println(columnName + " " + columnType + " " + columnTypeName + " " + columSize);
                getColumns().add(newCol);    //Spalte in die Spaltenliste hinzufügen
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

    public void createPrimaryKeys() {
        if (!primaryKeys.isEmpty()) {
            return;
        }
        ResultSet result = null;
        try {
            result = metaData.getPrimaryKeys(null, TABLE_SCHEM, TABLE_NAME);

            while (result.next()) {
                Reldb_Column col = getColumnByName(result.getString("COLUMN_NAME"));
                if (col == null) {
                    log.warning("Column " + result.getString("TABLE_NAME") + " not in Table!");
                    int count =  result.getMetaData().getColumnCount();
                    for (int i = 0; i < count; i++) {
                        System.out.println(result.getString(i+1));
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
            result = metaData.getImportedKeys(null, TABLE_SCHEM, TABLE_NAME);

            while (result.next()) {
               // System.out.println(TABLE_NAME + " " +result.getString("FKCOLUMN_NAME") +" "+result.getString("PKTABLE_NAME") +" "+ result.getString("PKCOLUMN_NAME"));
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
        return columns;
    }

}
