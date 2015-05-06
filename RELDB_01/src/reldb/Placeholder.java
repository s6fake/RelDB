/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import reldb.lib.*;
import reldb.lib.sql.StatementManager;

/**
 *
 * @author sqrt
 */
public class Placeholder {

    private static final String url = "jdbc:postgresql://dbvm01.iai.uni-bonn.de:5432/imdb";

    public static ConnectionManager connection;
    
    public static void main(String arg1, String arg2) {
        if (arg1 != null && arg2 != null) {
            connection = ConnectionManager.getInstance();
            connection.EstablishConnection(url, arg1, arg2);
            
            MetaDataManager mdManager = new MetaDataManager(connection.getMetadata());
            mdManager.printInfo();

            Statement st = connection.newStatement();
            StatementManager dings = new StatementManager(st);
            ResultSet rs = dings.getTables();
            ResultSetMetaData rsmd;
            int counter = 0;
            if (rs != null) {
                try {
                    rsmd = rs.getMetaData();
                    counter = rsmd.getColumnCount();
                    for (int i = 1; i < counter; i++) {
                        System.out.println(rsmd.getColumnName(i));
                    }
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }

            
        }
    }
    
    public static void close()
    {
        connection.CloseConnection();
    }
}
