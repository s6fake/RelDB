package reldb.lib.database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import reldb.lib.Reldb_Connection;

/**
 *
 * @author s6fake
 */
public class Reldb_Database {

    private static final Logger log = Logger.getLogger(Reldb_Database.class.getName());

    private String databaseName, version, catalogSeparator;
    private Reldb_Connection connection;
    private List<Reldb_Table> tableList;

    public Reldb_Database(Reldb_Connection connection) {
        if (connection == null) {
            throw new NullPointerException("Connection must not be null");
        }
        this.connection = connection;

        DatabaseMetaData metaData = connection.getMetadata();
        setInformation(metaData);
        createTableList(metaData);
    }

    /**
     * Setzt die Datenbankinformationen
     *
     * @param metaData Metadaten der Verbindung
     */
    private void setInformation(DatabaseMetaData metaData) {
        try {
            databaseName = metaData.getDatabaseProductName();
            version = metaData.getDatabaseProductVersion();
            catalogSeparator = metaData.getCatalogSeparator();
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
    }

    /**
     * Erstellt eine Liste aller public Tables
     * @param metaData
     */
    private void createTableList(DatabaseMetaData metaData) {
        setTableList(new ArrayList<>());
        //Reldb_Statement tableStatement = new Reldb_Statement(connection);
        ResultSet result;
        try {
            String[] types = {"TABLE"};
            result = metaData.getTables(null, "public", null, types);
            while (result.next()) {
                Reldb_Table newTable = new Reldb_Table(result.getString(3), metaData);
                getTableList().add(newTable);
                //System.out.println(result.getString(1) + " " + result.getString(2) + " " + result.getString(3)+ " " + result.getString(4) + " " + result.getString(5));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Reldb_Database.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Gibt die Datenbank und alle Tabellen aus
     */
    public void print() {
        System.out.println(getDatabaseName() +" " + version);
        for (Reldb_Table iterator : getTableList()) {
            iterator.print();
        }
    }

    /**
     * @return the connection
     */
    public Reldb_Connection getConnection() {
        return connection;
    }
    
    @Override
    public String toString() {
        return "Product: " + databaseName+"\n" + "Version: " + version + "\n" + "CatalogSeparator: " + catalogSeparator;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * @return the tableList
     */
    public List<Reldb_Table> getTableList() {
        return tableList;
    }

    /**
     * @param tableList the tableList to set
     */
    public void setTableList(List<Reldb_Table> tableList) {
        this.tableList = tableList;
    }

}
