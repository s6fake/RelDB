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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s6fake
 */
public class ConnectionManager {

    private static final Logger log = Logger.getLogger(ConnectionManager.class.getName());

    private static ConnectionManager Instance = null;

    private Connection connection = null;

    private ConnectionManager() {
        log.setLevel(Level.FINEST);                                               //Macht Singelton überhaupt Sinn? :D
    }

    public static ConnectionManager getInstance() {
        if (Instance == null) {
            Instance = new ConnectionManager();
        }
        return Instance;
    }

    /**
     * NullPointer Exception!
     *
     * @return
     */
    public DatabaseMetaData getMetadata() {
        DatabaseMetaData result = null;
        try {
            result = connection.getMetaData();
        } catch (SQLException e) {
            System.err.println(e);
        }
        return result;
    }

    public void EstablishConnection(String url, String user, String pass) {
        if (connection != null) {
            System.err.println("Connection not null!");
            return;
        }
        try {
            connection = DriverManager.getConnection(url, user, pass);// "imdbuser", "kemperSS15");
        } catch (SQLException e) {
            System.err.println(e);
            return;
        }
        log.log(Level.INFO, "Verbindung mit {0} hergestellt.", url);
    }

    public void CloseConnection() {

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println(e);
                return;
            }
            log.info("Verbindung mit  geschlossen");
        }
    }

}
