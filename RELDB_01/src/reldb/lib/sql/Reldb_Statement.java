/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.lib.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import reldb.lib.Reldb_Connection;

/**
 *
 * @author s6fake
 */
public class Reldb_Statement {

    public static String LastCatalogSeparator = null;
    private static final Logger log = Logger.getLogger(Reldb_Statement.class.getName());

    protected Statement statement = null;
    protected Reldb_Connection connection;

    public Reldb_Statement(Reldb_Connection connection) {
        this.statement = connection.newStatement();
        this.connection = connection;
    }

    /**
     * Creates a prepared Statement
     *
     * @param connection
     * @param command
     */
    public Reldb_Statement(Reldb_Connection connection, String command) {
        this.connection = connection;
        this.statement = connection.newPreparedStatement(command);
    }

    public void close() {
        try {
            statement.close();
        } catch (SQLException e) {
            printError(e);
        }
    }

    public static void closeStatement(Statement statement) {
        try {
            statement.close();
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
    }

    public ResultSet getTables() {
        if (connection == null) {
            throw new NullPointerException();
        }
        ResultSet results = null;
        try {
            results = statement.executeQuery(sql_expr.getTableNames(connection.getDatabaseProductName()));
        } catch (SQLException e) {
            printError(e);
        }
        return results;
    }

    public boolean execute(String command) {
        boolean result = false;
        try {
            result = statement.execute(command);
        } catch (SQLException e) {
            printError(e);
        }
        return result;
    }

    public SQLException executeUpdate(String command) {
        SQLException result = null;
        try {
            statement.executeUpdate(command);
        } catch (SQLException e) {
            result = e;
            printError(e);
        }
        return result;
    }

    public ResultSet executeQuery(String command, int fetch) {
        ResultSet results = null;
        try {
            statement.setFetchSize(fetch);
            results = statement.executeQuery(command);
            //printWarnings();            
        } catch (SQLException e) {
            printError(e);
        }
        return results;
    }

    public ResultSet executeQuery(String command) {
        return Reldb_Statement.this.executeQuery(command, 0);
    }

    public int selectCount(String table, String condition) {
        String command = "SELECT COUNT(*) FROM " + table + " " + condition;
        System.out.println(command);
        ResultSet results;
        int result = 0;
        try {
            results = statement.executeQuery(command);
            while (results.next()) {
                result = Integer.parseInt(results.getObject(1).toString());
            }
            results.close();
        } catch (SQLException e) {
            printError(e);
        }
        return result;
    }

    private void printError(SQLException exception) {
        if (exception.getErrorCode() == 2291 || exception.getErrorCode() == 904) {
            log.log(Level.CONFIG, exception.getMessage());
        } else {
            log.log(Level.SEVERE, exception.getMessage());
        }
    }

}
