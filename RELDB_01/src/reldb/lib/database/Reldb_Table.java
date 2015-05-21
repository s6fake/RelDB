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

    private final String tableName; //Name der Tabelle
    private List<Reldb_Column> columns = new ArrayList<>(); //Liste aller Spalten
    private List<Reldb_Column> primaryKeys = new ArrayList<>(); // Liste aller Primärschlüssel

    /**
     * Erstellt einen neue Tabelle
     *
     * @param name Name der Tabelle
     * @param metaData Metadaten, mit der die Tabelle gefüllt werden soll.
     */
    public Reldb_Table(String name, DatabaseMetaData metaData) {
        this.tableName = name;
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
        return getTableName();
    }

    /**
     * Gibt die Tabelle und alle Spalten aus
     */
    public void print() {
        String result = "";
        for (Reldb_Column iterator : getColumns()) {
            result = result.concat(iterator.toString() + " ");
        }
        System.out.println("|" + getTableName() + "|" + result);
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return the columns
     */
    public List<Reldb_Column> getColumns() {
        return columns;
    }

}
