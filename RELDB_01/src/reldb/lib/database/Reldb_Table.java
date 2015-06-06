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

    private final String TABLE_TYPE;
    private final String TABLE_CAT;
    private final String TABLE_SCHEM;
    private final String TABLE_NAME; //Name der Tabelle
    private List<Reldb_Column> columns = new ArrayList<>(); //Liste aller Spalten
    private List<Reldb_Column> primaryKeys = new ArrayList<>(); // Liste aller Primärschlüssel

    /**
     * Erstellt einen neue Tabelle
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
        createColumns(metaData);
    }

    /**
     * Überträgt alle Spalten aus den Metadaten in die interne Spaltenliste
     *
     * @param metaData
     */
    private void createColumns(DatabaseMetaData metaData) {
        ResultSet result;
        try {
            result = metaData.getColumns(null, null, getTableName(), null); //Metadaten der Spalten holen
            while (result.next()) {
                String columnName = result.getString(4);
                int columnType = result.getInt(5);
                String columnTypeName = result.getString(6);
                int columSize = result.getInt(7);
                Reldb_Column newCol = new Reldb_Column(columnName, columnType, columnTypeName, columSize); // Neue Reldb_Column erstellen
                getColumns().add(newCol);    //Spalte in die Spaltenliste hinzufügen
            }
        } catch (SQLException ex) {
            Logger.getLogger(Reldb_Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createPrimaryKeys(DatabaseMetaData metaData) {
        ResultSet result;
        try {
            result = metaData.getPrimaryKeys(null, null, getTableName());
            while (result.next()) {
                primaryKeys.add(getColumnByName(result.getString(4)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Reldb_Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Reldb_Column getColumnByName(String columnName) {
        for (Reldb_Column colIterator : columns) {
            if (colIterator.getName().equals(columnName)) {
                return colIterator;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "TYPE: " + TABLE_TYPE + " CAT: " + TABLE_CAT + " SCHEM: " + TABLE_SCHEM + " NAME: " + TABLE_NAME;
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
