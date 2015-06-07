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
    private String databaseProductName, version, catalogSeparator;                    // Name der Datenbank, ausgelesen aus den MetaDaten
    private String url = null;
    private String connectionName = "Neue Verbindung";                                  // Name der Verbindung wie sie in der UI angezeigt wird

    //  Optionale Felder, damit man die Verbindung im Nachhinein bearbeiten kann
    private String adress = "";
    private String databaseID = "";
    private String userName = "";
    private int port = 0;
    private int databaseType = 0;   // 0: Postgresql, 1: Oracle

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

    public Reldb_Connection(String url, String connectionName, String adress, String databaseID, int port, int databaseType, String userName) {
        if (url == null) {
            throw new NullPointerException("url must not be null!");
        }
        this.url = url;
        this.adress = adress;
        this.databaseID = databaseID;
        this.port = port;
        this.databaseType = databaseType;
        this.userName = userName;
        if (connectionName != null) {
            this.connectionName = connectionName;
        }
        addConnection(this, 0);
    }

    private void setInformation(DatabaseMetaData metaData) {
        try {
            databaseProductName = metaData.getDatabaseProductName();
            version = metaData.getDatabaseProductVersion();
            catalogSeparator = metaData.getCatalogSeparator();
        } catch (SQLException e) {
            log.warning(e.toString());
        }
    }

    public void changeSettings(String url, String connectionName, String adress, String databaseID, int port, int databaseType, String userName) {
        if (url == null) {
            throw new NullPointerException("url must not be null!");
        }
        this.url = url;
        this.adress = adress;
        this.databaseID = databaseID;
        this.port = port;
        this.databaseType = databaseType;
        this.userName = userName;
        if (connectionName != null) {
            this.connectionName = connectionName;
        }
        addConnection(this, 0);
    }

    /**
     * Nimmt eine neue Verbindung in die Liste aller Verbindungen auf.
     * Exisitiert schon eine Verbingung mit gleichem Namen, wird die neue
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
                    return addConnection(newConn, counter + 1);
                }
            } else if (iterator.connectionName.equals(newConn.connectionName + "(" + counter + ")")) {
                return addConnection(newConn, counter + 1);
            }

        }
        if (counter != 0) {
            newConn.connectionName = newConn.connectionName.concat("(" + counter + ")");
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
        setAutoCommit(false);
        return true;
    }

    public static boolean checkIfConnectionExists(String url, String name) {
        for (Reldb_Connection iterator : getConnections()) {
            /*
             if (iterator.getConnectionName().equals(name)) {
             JOptionPane.showMessageDialog(null, "Es existiert bereits eine Verbindung mit dem Namen " + name, "Verbindung kann nicht erstellt werden", JOptionPane.ERROR_MESSAGE);
             return false;
             } */
            if (iterator.getUrl().equalsIgnoreCase(url)) {
                //JOptionPane.showMessageDialog(null, "Es existiert bereits eine Verbindung zu " + url, "Verbindung kann nicht erstellt werden", JOptionPane.ERROR_MESSAGE);
                return true;
            }
        }

        return false;
    }

    private void setAutoCommit(boolean autoCommit) {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException ex) {
            Logger.getLogger(Reldb_Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void CloseConnection() {
        connections.remove(this);
        if (connection != null) {
            try {
                connection.close();
                log.info("Verbindung mit " + connectionName + " geschlossen");
                System.out.println(connections.remove(this));
            } catch (SQLException e) {
                System.err.println(e);
                return;
            }
        }
        log.warning("Verbindung mit " + connectionName + "konnte nicht geschlossen werden!");
    }

    public static void closeAllConnections() {
        connections.stream().forEach((iterator) -> {
            iterator.CloseConnection();
        });
    }

    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public String toString() {
        return connectionName + "\n" + url;
    }

    public String getConnectionName() {
        return connectionName;
    }

    /**
     * @return Name der Datenbank
     */
    public String getDatabaseProductName() {
        return databaseProductName;
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

    /**
     * @return the adress
     */
    public String getAdress() {
        return adress;
    }

    /**
     * @return the databaseID
     */
    public String getDatabaseID() {
        return databaseID;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the databaseType
     */
    public int getDatabaseType() {
        return databaseType;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

}
