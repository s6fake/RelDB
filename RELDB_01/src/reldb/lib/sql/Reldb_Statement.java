/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.lib.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Supplier;
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
            log.log(Level.WARNING, e.getMessage());
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
            printError(e, sql_expr.getTableNames(connection.getDatabaseProductName()));
        }
        return results;
    }

    public void set(Object[] args) {
        PreparedStatement st = (PreparedStatement) statement;
        for (int i = 0; i < args.length; i++) {
            try {
                if (args[i] instanceof String) {
                    st.setString(i + 1, args[i].toString());
                } else if (args[i] instanceof Integer) {
                    st.setInt(i + 1, (Integer) args[i]);
                } else {
                    st.setObject(i + 1, args[i]);
                }
            } catch (SQLException ex) {
                log.warning(ex.getMessage());
            }
        }
        statement = st;
    }

    public boolean execute() {
        boolean result = false;
        PreparedStatement st = (PreparedStatement) statement;
        try {
            result = st.execute();
        } catch (SQLException ex) {
            printError(ex, st.toString());
        }
        return result;
    }

    public boolean execute(String command) {
        boolean result = false;
        try {
            result = statement.execute(command);
        } catch (SQLException e) {
            printError(e, command);
        }
        return result;
    }

    public SQLException executeUpdate(String command) {
        SQLException result = null;
        try {
            statement.executeUpdate(command);
        } catch (SQLException e) {
            result = e;
            printError(e, command);
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
            printError(e, command);
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
            printError(e, command);
        }
        return result;
    }

    private void printError(SQLException exception, String command) {
        if (exception.getErrorCode() == 2291 || exception.getErrorCode() == 904) {
            log.log(Level.CONFIG, exception.getMessage());
        } else {
            log.log(Level.SEVERE, exception.getMessage() + command + "\n");
        }
    }

}
