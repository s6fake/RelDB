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

    public static enum DATABASETYPE {

        UNKNOWN, POSTGRESQL, ORACLE
    };

    private static final Logger log = Logger.getLogger(Reldb_Database.class.getName());
    protected Reldb_Database.DATABASETYPE databaseType;
    private DatabaseMetaData metaData;
    private String databaseName, version, catalogSeparator;
    private Reldb_Connection connection;
    protected List<Reldb_Schema> schemaList = new ArrayList<>();
    private List<Reldb_Table> tableList = new ArrayList<>();

    private boolean tableListFilled = false;

    protected Reldb_Database() {
        this.databaseType = DATABASETYPE.UNKNOWN;
        this.connection = null;
        this.metaData = null;
    }

    protected Reldb_Database(Reldb_Database reference) {
        if (reference == null) {
            this.databaseType = DATABASETYPE.UNKNOWN;
            this.connection = null;
            this.metaData = null;
        } else {
            this.databaseType = reference.getDatabaseType();
            this.metaData = reference.getMetaData();
            this.connection = reference.getConnection();
            this.schemaList = reference.getSchemaList();
        }
    }
    
    public Reldb_Database(Reldb_Connection connection) {
        if (connection == null) {
            throw new NullPointerException("Connection must not be null");
        }
        this.connection = connection;
        connection.setDatabase(this);

        this.metaData = connection.getMetadata();
        setInformation(metaData);
        if (databaseName.equalsIgnoreCase("PostgreSQL")) {
            databaseType = DATABASETYPE.POSTGRESQL;
        } else if (databaseName.equalsIgnoreCase("Oracle")) {
            databaseType = DATABASETYPE.ORACLE;
        } else {
            databaseType = DATABASETYPE.UNKNOWN;
        }
        createSchemaList(metaData);
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
        Reldb_Schema currentSchema;  // Aktuelles Schema, in das die Tabelle eingefügt wird
        ResultSet resultSet = null;
        try {
            resultSet = metaData.getSchemas();
            while (resultSet.next()) {
                currentSchema = new Reldb_Schema(this, resultSet.getString("TABLE_SCHEM"), resultSet.getString("TABLE_CATALOG"));
                getSchemaList().add(currentSchema);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Reldb_Database.class.getName()).log(Level.SEVERE, null, ex.getMessage());
        } finally {
            try {
                resultSet.close();
            } catch (SQLException ex) {
                Logger.getLogger(Reldb_Database.class.getName()).log(Level.SEVERE, null, ex.getMessage());
            }
        }
    }   
    
    /**
     * Gibt die Datenbank und alle Tabellen aus
     */
    public void print() {
        System.out.println(getDatabaseName() + " " + version);
        for (Reldb_Schema schem : getSchemaList()) {
            schem.print();
        }
    }

    
    /**
     * @param schemaList the schemaList to set
     */
    protected void setSchemaList(List<Reldb_Schema> schemaList) {
        this.schemaList = schemaList;
    }

    /**
     * @param tableList the tableList to set
     */
    protected void setTableList(List<Reldb_Table> tableList) {
        tableListFilled = true;
        this.tableList = tableList;
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
    Reldb_Schema getSchemaByName(String schemaName) {
        for (Reldb_Schema schem : getSchemaList()) {
            if (schem.getSchemaName().equals(schemaName)) {
                return schem;
            }
        }
        log.warning("Schema " + schemaName + " existiert nicht!");
        return null;
    }

    @Override
    public String toString() {
        return "Product: " + databaseName + "\n" + "Version: " + version + "\n" + "CatalogSeparator: " + catalogSeparator + "\n dbm: " + getDatabaseType().name();
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

    void addTable(Reldb_Table table) {
        tableList.add(table);
    }

    public Reldb_Table getTableByName(String tableName) {
        for (Reldb_Table table : getTables()) {
            if (table.getTableName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }

    /**
     * @return the metaData
     */
    public DatabaseMetaData getMetaData() {
        return metaData;
    }

    /**
     * @return the databaseType
     */
    public DATABASETYPE getDatabaseType() {
        return databaseType;
    }

    /**
     * @return the tableList
     */
    public List<Reldb_Table> getTables() {
        if (!tableListFilled) {
            for (Reldb_Schema schema : getSchemaList()) {
                schema.createTableList();
            }
            tableListFilled = true;
        }
        return tableList;
    }

}
