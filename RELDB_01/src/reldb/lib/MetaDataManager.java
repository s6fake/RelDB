/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.lib;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import reldb.StringClass;
import reldb.lib.sql.StatementManager;

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

    public void updateTable(ObservableList<StringClass> tableNames, ResultSet results) {
        if (results == null) {
            log.warning("Kein Resultset zum Ausgeben oder Liste uninitalisiert!");
            return;
        }
        try {
            while (results.next()) {
                System.out.println(results.getString(1));
                tableNames.add(new StringClass(results.getString(1)));           
            }
            
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

}
