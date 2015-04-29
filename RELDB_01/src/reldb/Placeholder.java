/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb;

import reldb.lib.*;

/**
 *
 * @author sqrt
 */
public class Placeholder {

    private static final String url = "jdbc:postgresql://dbvm01.iai.uni-bonn.de:5432/imdb";

    public static void main(String arg1, String arg2) {
        if (arg1 != null && arg2 != null) {
            ConnectionManager connection = ConnectionManager.getInstance();
            connection.EstablishConnection(url, arg1, arg2);
            MetaDataManager mdManager = new MetaDataManager(connection.getMetadata());
            mdManager.printInfo();

            connection.CloseConnection();
        }
    }
}
