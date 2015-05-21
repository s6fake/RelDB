package reldb.lib;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import reldb.lib.sql.StatementManager;
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

    public void printInfo() {

        ResultSet tables;
        ResultSet catalogs, schemas;
        ResultSetMetaData rsmd;
        int counter;
        if (metaData == null) {
            log.warning("No metaData input");
            return;
        }
        try {
            System.out.println("Database: " + metaData.getDatabaseProductName());
            System.out.println("Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Catalog Seperator: " + metaData.getCatalogSeparator());
            StatementManager.LastCatalogSeparator = metaData.getCatalogSeparator();

            System.out.println("Table MetaData:");
            tables = metaData.getTables(null, null, null, null);
            rsmd = tables.getMetaData();
            counter = rsmd.getColumnCount();
            for (int i = 1; i < counter; i++) {
                System.out.println(rsmd.getColumnName(i));
            }
        } catch (SQLException e) {
            System.err.println(e);
        }

        try {
            System.out.println("Schemas:");
            schemas = metaData.getSchemas();
            rsmd = schemas.getMetaData();
            counter = rsmd.getColumnCount();
            for (int i = 1; i < counter; i++) {
                System.out.println(rsmd.getColumnName(i));
            }
        } catch (SQLException e) {
            System.err.println(e);
        }

        try {
            System.out.println("Catalogs:");
            catalogs = metaData.getCatalogs();
            rsmd = catalogs.getMetaData();
            counter = rsmd.getColumnCount();
            for (int i = 1; i < counter; i++) {
                System.out.println(rsmd.getColumnName(i));
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
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
    public void updateTable_connection(MainController controller, Reldb_Connection connection, ResultSet results) {
        if (results == null) {
            log.warning("Kein Resultset zum Ausgeben oder Liste uninitalisiert!");
            return;
        }

        String connectionName = connection.getConnectionName();
        String dataBaseName = connection.getDatabaseName();

        TreeItem<Reldb_TreeViewElement> root = controller.getTreeItemByName(connectionName);
        if (root == null) {
            controller.addTreeItem(new Reldb_TreeViewElement(null,connectionName));
            root = controller.getTreeItemByName(connectionName);
        }

        TreeItem<Reldb_TreeViewElement> database = controller.getTreeItemByName(root, dataBaseName);
        if (database == null) {
            controller.addTreeItem(root, new Reldb_TreeViewElement(null, dataBaseName));
            database = controller.getTreeItemByName(root, dataBaseName);
        }

        try {
            while (results.next()) {
                System.out.println(results.getString(1));
                controller.addTreeItem(database, new Reldb_TreeViewElement(new Reldb_Table(results.getString(1), 0), results.getString(1)));
            }

        } catch (SQLException e) {
            System.err.println(e);
        }
    }

}
