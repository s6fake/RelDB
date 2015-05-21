package reldb.lib;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
    }

    private void setInformation(DatabaseMetaData metaData) {
        try {
            databaseName = metaData.getDatabaseProductName();
            version = metaData.getDatabaseProductVersion();
            catalogSeparator = metaData.getCatalogSeparator();
        } catch (SQLException e) {
            log.warning(e.toString());
        }
    }
    
    private void createTableList(DatabaseMetaData metaData) {
        tableList = new ArrayList<>(); 
    }

    /**
     * @return the connection
     */
    public Reldb_Connection getConnection() {
        return connection;
    }

    
}
