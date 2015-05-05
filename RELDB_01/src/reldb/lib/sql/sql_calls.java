/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.lib.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import reldb.lib.ConnectionManager;

/**
 *
 * @author s6fake
 */
public class sql_calls {

    private static final Logger log = Logger.getLogger(sql_calls.class.getName());
    
    public static String CatalogSeparator = null;
    
    private Statement statement = null;

    public sql_calls(Statement st) {
        statement = st;
    }

    public void closeStatement() {
        try {
            statement.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }
    
    public ResultSet getTables()
    {
        ResultSet results = null;
        try {
            statement.executeQuery("SELECT * FROM PostgreSQL"+CatalogSeparator+"company_name;");
            //statement.executeQuery("SELECT * FROM table_schem;");
        }
            catch (SQLException e)
            {
                System.err.println(e);
            }
        return results;
    }
    
}
