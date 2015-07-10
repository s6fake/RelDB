package reldb.lib;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import reldb.lib.database.Reldb_Database;

/**
 * Diese Klasse kapselt eine java.sql.Connection, verwaltet diese und kümmert
 * sich um die Fehlerbehandlung. Zudem werden alle geöffneten Verbindungen in
 * einer Liste verwaltet.
 *
 * @author s6fake
 */
public class Reldb_Connection {

    private static final Logger log = Logger.getLogger(Reldb_Connection.class.getName());

    // Liste aller erstellten Verbindungen
    private static List<Reldb_Connection> connections = new ArrayList<>();

    private Connection connection = null;
    private DatabaseMetaData metaData;
    // Name der Datenbank, ausgelesen aus den MetaDaten
    private String databaseProductName, version, catalogSeparator;
    private String url = null;
    // Name der Verbindung wie sie in der UI angezeigt wird
    private String connectionName = "Neue Verbindung";
    // Die  dieser Verbindung zuzuordnende Datenbank
    private Reldb_Database database;

    //  Optionale Felder, damit man die Verbindung im Nachhinein bearbeiten kann
    private String adress = "";
    private String databaseID = "";
    private String userName = "";
    private int port = 0;
    private int databaseType = 0;   // 0: Postgresql, 1: Oracle

    /**
     * Erzeugt eine neue Verbindung
     *
     * @param url Die url zur Datenbank. Muss die Informationen zum jdbc
     * Treiber, etc. enthalten. not null
     * @param connectionName Ein Name für die Verbindung
     */
    @Deprecated
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

    /**
     * Erzeugt eine neue Verbdingung
     *
     * @param url URL zur Datenbank. Es müssen bereits alle Informationen über
     * den JDBC Treiber, Datenbanknamen, Port, etc. enthalten sein
     * @param connectionName Ein Optionaler Name für die Verbindung
     * @param adress Die reine Adresse. Wird benötigt um die Verbindung evtl.
     * später nochmals zu bearbeiten
     * @param databaseID Der Name der Datenbank. Wird benötigt um die Verbindung
     * evtl. später nochmals zu bearbeiten
     * @param port Der Port der Datenbankverbindung. Wird benötigt um die
     * Verbindung evtl. später nochmals zu bearbeiten
     * @param databaseType Der Typ der Datenbank (ORACLE oder POSTGRES). Wird
     * benötigt um die Verbindung evtl. später nochmals zu bearbeiten
     * @param userName Der Benutzername. Wird benötigt um die Verbindung evtl.
     * später nochmals zu bearbeiten
     */
    public Reldb_Connection(String url, String connectionName, String adress,
            String databaseID, int port, int databaseType, String userName) {
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
     * Liest die Informationen der Datenbank aus den Metadaten aus. Wird
     * aufgerufen, sobald sich mit der Datenbank verbunden wird.
     *
     * @param metaData Die MetaDaten
     */
    private void setInformation(DatabaseMetaData metaData) {
        try {
            databaseProductName = metaData.getDatabaseProductName();
            version = metaData.getDatabaseProductVersion();
            catalogSeparator = metaData.getCatalogSeparator();
        } catch (SQLException e) {
            log.warning(e.toString());
        }
    }

    @Deprecated
    //Rausschmeißen
    public void changeSettings(String url, String connectionName, String adress, 
            String databaseID, int port, int databaseType, String userName) {
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
     * @param counter Zählt die rekursiven Funktionsaufrufe, default 0
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

    /**
     * Getter für die Metadaten
     *
     * @return
     */
    public DatabaseMetaData getMetadata() {
        return metaData;
    }

    /**
     * Erzeugt ein neues java.sql.Statement auf der Verbindung
     *
     * @return Das neue Statement. Bei Misserfolg null
     */
    public Statement newStatement() {
        Statement result = null;
        try {
            result = connection.createStatement();
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
        return result;
    }

    /**
     * Erzeugt ein neues java.sql.PreparedStatement auf der Verbindung
     *
     * @return Das neue Statement. Bei Misserfolg null
     */
    public PreparedStatement newPreparedStatement(String stmt) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(stmt);
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
        return statement;
    }

    /**
     * Stellt die Verbindung zur Datenbank her. Anschließend werden die
     * Metadaten ausgelesen. Zudem wird autocommit auf false gesetzt
     *
     * @param user Der Benutzername
     * @param pass Das Password
     * @return True bei Erfolg, False bei Misserfolg
     */
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

    /**
     * Ändert den Wert von Autocommit auf der Verbindung Die Verbindung muss
     * bereits hergestellt sein
     *
     * @param autoCommit
     */
    private void setAutoCommit(boolean autoCommit) {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException ex) {
            Logger.getLogger(Reldb_Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Versucht die Verbindung zu schließen
     */
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

    /**
     * Schließt alle Verbindungen
     */
    public static void closeAllConnections() {
        for (Reldb_Connection iterator : connections) {
            try {
                iterator.connection.close();
                log.info("Verbindung mit " + iterator.connectionName + " geschlossen");

            } catch (SQLException e) {
                System.err.println(e);
                continue;
            }
            log.warning("Verbindung mit " + iterator.connectionName + "konnte nicht geschlossen werden!");
        }
        connections.clear();
    }

    /**
     * Prüft ob die Verbindung eingerichtet wurde
     *
     * @return true if connection != null
     */
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public String toString() {
        return connectionName + "\n" + url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Reldb_Connection) {
            Reldb_Connection other = (Reldb_Connection) o;
            if (other.url.equals(url)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return Den Namen der Verbindung
     */
    public String getConnectionName() {
        return connectionName;
    }

    /**
     * @return Name der Datenbank
     */
    public String getDatabaseProductName() {
        return databaseProductName;
    }

    /**
     * Gibt eine Liste aller Verbindungen zurück
     *
     * @return
     */
    public static List<Reldb_Connection> getConnections() {
        return connections;
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
    public int getDatabaseTypeID() {
        return databaseType;
    }

    /**
     * Gibt den Datenbanktyp als Reldb_Database.DATABASETYPE zurück
     *
     * @return
     */
    public Reldb_Database.DATABASETYPE getDatabaseType() {
        if (database == null) {
            return Reldb_Database.DATABASETYPE.UNKNOWN;
        }
        return database.getDatabaseType();
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return the database
     */
    public Reldb_Database getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(Reldb_Database database) {
        this.database = database;
    }

}
