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
    private List<Reldb_Column> columns = new ArrayList<>(); //Liste alle Spalten

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
            result = metaData.getColumns(null, null, tableName, null); //Metadaten der Spalten holen
            while (result.next()) {
                String columnName = result.getString(4);
                int columnType = result.getInt(5);
                String columnTypeName = result.getString(6);
                int columSize = result.getInt(7);
                Reldb_Column newCol = new Reldb_Column(columnName, columnType, columnTypeName, columSize); // Neue Reldb_Column erstellen
                columns.add(newCol);    //Spalte in die Spaltenliste hinzufügen
            }
        } catch (SQLException ex) {
            Logger.getLogger(Reldb_Table.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return tableName;
    }

    /**
     * Gibt die Tabelle und alle Spalten aus
     */
    public void print() {
        String result = "";
        for (Reldb_Column iterator : columns) {
            result = result.concat(iterator.toString() + " ");
        }
        System.out.println("|" + tableName + "|" + result);
    }

}
