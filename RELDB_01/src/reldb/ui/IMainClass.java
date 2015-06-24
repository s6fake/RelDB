package reldb.ui;

import reldb.lib.Reldb_Connection;

/**
 *
 * @author s6fake
 */
public interface IMainClass {
    
   
    public void logIn(String user, String password, Reldb_Connection connection);
    public void startExport(String user, String password, Reldb_Connection connection);
    public void removeConnection(Reldb_Connection connection);
}
