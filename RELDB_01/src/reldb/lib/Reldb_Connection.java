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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s6fake
 */
public class Reldb_Connection {

    private static final Logger log = Logger.getLogger(Reldb_Connection.class.getName());
    private static Reldb_Connection Instance = null;
    private Connection connection = null;
    private String databaseName = null;

    private Reldb_Connection() {
        log.setLevel(Level.FINEST);                                               //Macht Singelton überhaupt Sinn? :D
    }

    public static Reldb_Connection getInstance() {
        if (Instance == null) {
            Instance = new Reldb_Connection();
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
            databaseName = result.getDatabaseProductName();
        } catch (SQLException e) {
            System.err.println(e);
        }
        return result;
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

    public boolean EstablishConnection(String url, String user, String pass) {
        if (connection != null) {
            System.err.println("Connection not null!");
            return false;
        }
        try {
            connection = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            System.err.println(e);
            return false;
        }
        log.log(Level.INFO, "Verbindung mit {0} hergestellt.", url);
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

}
