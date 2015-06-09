package reldb.lib.database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s6fake
 */
public class Reldb_Schema {

    private static final Logger log = Logger.getLogger(Reldb_Schema.class.getName());

    private final String SCHEMA_NAME, CATALOG_NAME;
    private final DatabaseMetaData metaData;
    private final List<Reldb_Table> tableList;    //Liste aller im Tabellen im Schema

    public Reldb_Schema(DatabaseMetaData metaData, String schemaName, String catalogName) {
        this.metaData = metaData;
        this.SCHEMA_NAME = schemaName;
        this.CATALOG_NAME = catalogName;
        tableList = new ArrayList<>();
    }

    public boolean addTable(Reldb_Table table) {
        if (table == null || getTableList() == null) {
            return false;
        }
        return getTableList().add(table);
    }

    public void createTableList() {
        if (!tableList.isEmpty()) {
            return;
        }
        ResultSet resultSet = null;
        try {
            String[] tables = {"TABLE"};
            resultSet = metaData.getTables(null, SCHEMA_NAME, null, tables);
            while (resultSet.next()) {
                Reldb_Table newTable = new Reldb_Table(resultSet.getString("TABLE_TYPE"), resultSet.getString("TABLE_CAT"), SCHEMA_NAME, resultSet.getString("TABLE_NAME"), metaData);
                tableList.add(newTable);
            }
        } catch (SQLException ex) {
            log.warning(ex.getMessage());
        } finally {
            try {
                resultSet.close();
            } catch (SQLException ex) {
                log.warning(ex.getMessage());
            }
        }
    }

    /**
     * deprecated
     */
    public void setForeignKeys(DatabaseMetaData metaData) {
        try {
            ResultSet result = metaData.getExportedKeys(null, SCHEMA_NAME, null);

            while (result.next()) {

                Reldb_Table primaryTable = getTableByName(result.getString("PKTABLE_NAME"));                                // Tabelle auf die der Fremdschlüssel zeigt
                Reldb_Column primaryColumn = primaryTable.getColumnByName(result.getString("PKCOLUMN_NAME"));               // Spalte, auf die der Fremdschlüssel zeigt
                Reldb_Table tableWithForeignKey = getTableByName(result.getString("FKTABLE_NAME"));                         // Tabelle mit dem aktuelle betrachtetem Fremdschlüssel
                Reldb_Column foreignKeyColumn = tableWithForeignKey.getColumnByName(result.getString("FKCOLUMN_NAME"));     // Spalte, mit Fremdschlüssel
                foreignKeyColumn.addForeignKey(primaryTable.getTableName(), primaryColumn.getName(), result.getInt("KEY_SEQ"));

            }
        } catch (SQLException ex) {
            Logger.getLogger(Reldb_Table.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void print() {
        System.out.println(SCHEMA_NAME);
        for (Reldb_Table table : getTableList()) {
            table.print();
        }
    }

    @Override
    public String toString() {
        return SCHEMA_NAME;
    }

    /**
     * @return the schemaName
     */
    public String getSchemaName() {
        return SCHEMA_NAME;
    }

    /**
     * @return the tableList
     */
    public List<Reldb_Table> getTableList() {
        return tableList;
    }

    public Reldb_Table getTableByName(String name) {
        for (Reldb_Table iterator : tableList) {
            if (iterator.getTableName().equals(name)) {
                return iterator;
            }
        }
        log.warning("Table " + name + " ist nicht in Schema " + SCHEMA_NAME + " vorhanden!");
        return null;
    }
}
