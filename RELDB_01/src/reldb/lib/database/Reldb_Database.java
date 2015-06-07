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
    private List<Reldb_Schema> schemaList;

    public Reldb_Database(Reldb_Connection connection) {
        if (connection == null) {
            throw new NullPointerException("Connection must not be null");
        }
        this.connection = connection;

        DatabaseMetaData metaData = connection.getMetadata();
        setInformation(metaData);
        //createTableList(metaData);
        createSchemaList(metaData);
        setForeignKeys(metaData);
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

    private void createSchemaList(DatabaseMetaData metaData) {
        setSchemaList(new ArrayList<>());
        String lastSchemaName = "";         // Name des zuletzt erstelltem Schema
        String currentSchemaName;      // Name des Schema der aktuell betrachteten Tabelle
        Reldb_Schema currentSchema = null;  // Aktuelles Schema, in das die Tabelle eingefügt wird
        ResultSet result;
        try {
            String[] types = {"TABLE"};
            String schema = null;//"public";
            result = metaData.getTables(null, schema, null, types);
            while (result.next()) {
                currentSchemaName = result.getString(2);                        // Schema Name auslesen
                if (!currentSchemaName.equals(lastSchemaName)) // Prüfen ob wir eine Tabelle aus einem anderem Schema haben
                {
                    currentSchema = getSchemaByName(currentSchemaName);         // Falls das Schema schon angelegt wurde, hole es
                    if (currentSchema == null) {
                        currentSchema = new Reldb_Schema(currentSchemaName);    // Ansonsten erstelle ein neues Schema
                        schemaList.add(currentSchema);
                    }
                }
                Reldb_Table newTable = new Reldb_Table(result.getString(4), result.getString(1), result.getString(2), result.getString(3), metaData);
                currentSchema.addTable(newTable);                               // Tabelle in das Schema einfügen
            }
        } catch (SQLException ex) {
            Logger.getLogger(Reldb_Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    private void setForeignKeys(DatabaseMetaData metaData) {
        for (Reldb_Schema schema : schemaList) {
            schema.setForeignKeys(metaData);
        }
        
    }
    
    /**
     * Gibt die Datenbank und alle Tabellen aus
     */
    public void print() {
        System.out.println(getDatabaseName() + " " + version);
        for (Reldb_Schema schem : schemaList) {
            schem.print();
        }
        /*
         getTableList().stream().forEach((iterator) -> {
         iterator.print();
         });
         */
    }

    /**
     * @return the connection
     */
    public Reldb_Connection getConnection() {
        return connection;
    }

    /**
     * Gibt das Schema mit dem gesuchten Namen zurück, ansonsten null
     *
     * @param schemaName
     * @return
     */
    public Reldb_Schema getSchemaByName(String schemaName) {
        for (Reldb_Schema schem : schemaList) {
            if (schem.getSchemaName().equals(schemaName)) {
                return schem;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Product: " + databaseName + "\n" + "Version: " + version + "\n" + "CatalogSeparator: " + catalogSeparator;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * @return the schemaList
     */
    public List<Reldb_Schema> getSchemaList() {
        return schemaList;
    }

    /**
     * @param schemaList the schemaList to set
     */
    public void setSchemaList(List<Reldb_Schema> schemaList) {
        this.schemaList = schemaList;
    }

    public Reldb_Table getTableByName(String tableName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
