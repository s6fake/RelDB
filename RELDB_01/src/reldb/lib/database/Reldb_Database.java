package reldb.lib.database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
        createTableList(metaData);
    }

    private void createTableList(DatabaseMetaData metaData) {
        if (!tableList.isEmpty()) {
            return;
        }
        ResultSet resultSet = null;
        try {
            String[] tables = {"TABLE"};
            String SCHEMA_NAME = "public";
            resultSet = metaData.getTables(null, SCHEMA_NAME, null, tables);
            while (resultSet.next()) {
                Reldb_Table newTable = new Reldb_Table(this,
                        resultSet.getString("TABLE_TYPE"),
                        resultSet.getString("TABLE_CAT"),
                        SCHEMA_NAME,
                        resultSet.getString("TABLE_NAME"));
                tableList.add(newTable);
            }
        } catch (SQLException ex) {
            log.warning(ex.getMessage());
        } finally {
            try {
                resultSet.close();
            } catch (SQLException ex) {
                log.warning(ex.getMessage());
            }
        }
        tableListFilled = true;
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
     * Gibt die Datenbank und alle Tabellen aus
     */
    public void print() {
        System.out.println(getDatabaseName() + " " + version);
        for (Reldb_Table table : tableList) {
            table.print();
        }
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

    public String printInfo() {
        return "Product: " + databaseName + "\n" + "Version: " + version + "\n"
                + "CatalogSeparator: " + catalogSeparator + "\n dbm: "
                + getDatabaseType().name();
    }

    @Override
    public String toString() {
        return databaseName;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
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
        return tableList;
    }

    public List<Reldb_Table> getSelectedTables() {
        List<Reldb_Table> list = new ArrayList<>();
        for (Reldb_Table table : getTables()) {
            if (table.isSelected()) {
                list.add(table);
            }
        }
        return list;
    }

}
