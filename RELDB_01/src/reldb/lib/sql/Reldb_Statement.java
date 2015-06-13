/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.lib.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.logging.Logger;
import reldb.lib.Reldb_Connection;

/**
 *
 * @author s6fake
 */
public class Reldb_Statement {

    public static String LastCatalogSeparator = null;
    private static final Logger log = Logger.getLogger(Reldb_Statement.class.getName());

    private Statement statement = null;
    private Reldb_Connection connection;

    public Reldb_Statement(Reldb_Connection connection) {
        this.statement = connection.newStatement();
        this.connection = connection;
    }

    public void close() {
        try {
            statement.close();
        } catch (SQLException e) {
            log.warning(e.getMessage());
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
            //statement.executeQuery("SELECT * FROM table_schem;");
        } catch (SQLException e) {
            log.warning(e.getMessage() );
        }
        return results;
    }

    private void printWarnings() {
        try {
            SQLWarning warning = statement.getWarnings();
            while (warning != null) {
                String errStr = "Message: " + warning.getMessage();
                errStr = errStr + " SQLState: " + warning.getSQLState();
                errStr = errStr + " Vendor error code: "+ warning.getErrorCode();
                log.warning(errStr);
                warning = warning.getNextWarning();
            }
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
    }

    public boolean execute(String command) {
        boolean result = false;
        try {
            //statement.setFetchSize(fetch);
            result = statement.execute(command);
            //printWarnings();
            //log.info(command);

        } catch (SQLException e) {
            log.warning(e.getMessage() + "\n" + command);            
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
            log.warning(e.getMessage() + "\n" + command);    
            //printWarnings();
        }
        return results;
    }

    public ResultSet executeQuery(String command) {
        return Reldb_Statement.this.executeQuery(command, 0);
    }
}
