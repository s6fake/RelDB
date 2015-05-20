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
public class StatementManager {
    
    public static String LastCatalogSeparator = null;
    private static final Logger log = Logger.getLogger(StatementManager.class.getName());
    
    private Statement statement = null;
    private String CatalogSeparator = null;
    
    public StatementManager(Statement statement) {
        this.statement = statement;
        this.CatalogSeparator = LastCatalogSeparator;
    }
    
    public StatementManager(Statement statement, String CatalogSeparator) {
        this.statement = statement;
        this.CatalogSeparator = CatalogSeparator;
    }
    
    public void closeStatement() {
        try {
            statement.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }
    
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
    
    public ResultSet executeCommand(String command) {
        ResultSet results = null;
        try {
            results = statement.executeQuery(command);
            log.info(command);
        } catch (SQLException e) {
            System.err.println(e);
        }
        return results;        
        
    }

    public void close() {
        try {
            statement.close();
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
    }
}
