/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.lib;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s6fake
 */
public class Reldb_Connection {

    private static final Logger log = Logger.getLogger(Reldb_Connection.class.getName());

    private static List<Reldb_Connection> connections = new ArrayList<>();      // Liste aller erstellten Verbindungen

    private Connection connection = null;
    private DatabaseMetaData metaData;
    private String databaseName, version, catalogSeparator;                    // Name der Datenbank, ausgelesen aus den MetaDaten
    private String url = null;
    private String connectionName = "Unnamed";                                  // Name der Verbindung wie sie in der UI angezeigt wird

    /**
     * @param url Die url zur Datenbank, not null
     * @param connectionName Ein Anzeigename für die Verbindung
     */
    public Reldb_Connection(String url, String connectionName) {
        if (url == null) {
            throw new NullPointerException("url must not be null!");
        }
        this.url = url;
        if (connectionName != null) {
            this.connectionName = connectionName;
        }
        addConnection(this, 0);
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

    /**
     * Nimmt eine neue Verbindung in die Liste aller Verbindungen auf.
     * Exisitiert schon eine Verbingung mit gleichem Namen, wird die neu
     * Verbindung umbenannt.
     *
     * @param newConn Die Verbindung die hinzugefügt werden soll
     * @param counter Zählt die Funktionsaufrufe, default 0
     *
     * @return Gibt zurück ob die Verbindung erfolgreich in die Liste eingefügt
     * werden konnte.
     */
    private static boolean addConnection(Reldb_Connection newConn, int counter) {
        for (Reldb_Connection iterator : getConnections()) {
            if (counter == 0) {
                if (iterator.connectionName.equals(newConn.connectionName)) {
                    return addConnection(newConn, counter++);
                }
            } else if (iterator.connectionName.equals(newConn.connectionName + "(" + counter + ")")) {
                return addConnection(newConn, counter++);
            }
        }
        if (counter != 0) {
            newConn.databaseName = newConn.databaseName.concat("(" + counter + ")");
        }
        return getConnections().add(newConn);
    }

    public DatabaseMetaData getMetadata() {
        return metaData;
    }

    public Statement newStatement() {
        Statement result = null;
        try {
            result = connection.createStatement();
        } catch (SQLException e) {
            System.err.println(e);
        }
        return result;
    }

    public boolean connect(String user, String pass) {
        if (connection != null) {
            System.err.println("Connection not null!");
            return false;
        }
        try {
            connection = DriverManager.getConnection(getUrl(), user, pass);
        } catch (SQLException e) {
            System.err.println(e);
            return false;
        }
        log.log(Level.INFO, "Verbindung mit {0} hergestellt.", getUrl());

        try {
            metaData = connection.getMetaData();
        } catch (SQLException ex) {
            Logger.getLogger(Reldb_Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        setInformation(metaData);

        return true;
    }

    public void CloseConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println(e);
                return;
            }
            log.info("Verbindung mit " + databaseName + " geschlossen");
        }
    }

    public static void closeAllConnections() {
        for (Reldb_Connection iterator : connections) {
            iterator.CloseConnection();
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public String getConnectionName() {
        return connectionName;
    }

    /**
     * @return Name der Datenbank
     */
    public String getDatabaseName() {
        return databaseName;
    }

    public static List<Reldb_Connection> getConnections() {
        return connections;
    }

    /**
     * To Do: Implementieren!
     *
     * @return
     */
    public static Reldb_Connection getConnectionByName() {
        return null;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

}
