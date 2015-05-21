/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.lib.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            results = statement.executeQuery(sql_expr.getTableNames(connection.getDatabaseName()));
            //statement.executeQuery("SELECT * FROM table_schem;");
        } catch (SQLException e) {
            log.warning(e.getMessage());
        } 
        return results;
    }
    /*
     public ResultSet getTables(String database) {
     ResultSet results = null;
     try {
     results = statement.executeQuery("SELECT * FROM PostgreSQL" + CatalogSeparator + "company_name;");
     //statement.executeQuery("SELECT * FROM table_schem;");
     } catch (SQLException e) {
     System.err.println(e);
     }
     return results;
     }
     */

    public ResultSet executeCommand(String command) {       
        ResultSet results = null;
        try {
            results = statement.executeQuery(command);
            log.info(command);
        } catch (SQLException e) {
            log.warning(e.getMessage());
        } 
        return results;

    }
}
