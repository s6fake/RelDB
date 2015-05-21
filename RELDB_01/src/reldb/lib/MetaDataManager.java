package reldb.lib;

import reldb.lib.database.Reldb_Table;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import reldb.ui.MainController;

/**
 *
 * @author s6fake
 */
public class MetaDataManager {

    private static final Logger log = Logger.getLogger(MetaDataManager.class.getName());

    private DatabaseMetaData metaData = null;

    public MetaDataManager(DatabaseMetaData metaData) {
        if (metaData == null) {
            log.info("Where's my food?");
        }
        this.metaData = metaData;

    }

    public void printInfo(TextArea txtArea) {
        if (metaData == null) {
            log.warning("No metaData input");
            return;
        }
        try {
            txtArea.clear();
            txtArea.insertText(0, "Database: " + metaData.getDatabaseProductName() + "\n");
            txtArea.insertText(txtArea.getLength(), "Version: " + metaData.getDatabaseProductVersion() + "\n");
            txtArea.insertText(txtArea.getLength(), "Catalog Seperator: " + metaData.getCatalogSeparator());
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public static void printResultset(ResultSet results) {
        ResultSetMetaData rsmd;

        int counter;
        if (results == null) {
            log.warning("Kein Resultset zum Ausgeben");
            return;
        }
        try {
            rsmd = results.getMetaData();
            counter = rsmd.getColumnCount();
            for (int i = 1; i < counter; i++) {
                System.out.println(rsmd.getColumnName(i));
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        System.out.println("Zweiter Block");
        try {
            while (results.next()) {
                System.out.println(results.getString(1));
            }
        } catch (SQLException e) {
            System.err.println(e);
        }

    }
    
}
