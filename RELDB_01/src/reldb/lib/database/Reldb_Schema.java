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

    private final String schemaName;
    private List<Reldb_Table> tableList;    //Liste aller im Tabellen im Schema

    public Reldb_Schema(String schemaName) {
        this.schemaName = schemaName;
        tableList = new ArrayList<>();
    }

    public boolean addTable(Reldb_Table table) {
        if (table == null || getTableList() == null) {
            return false;
        }
        return getTableList().add(table);
    }

    public void setForeignKeys(DatabaseMetaData metaData) {        
            try {
                ResultSet result = metaData.getExportedKeys(null, schemaName, null);

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
        System.out.println(schemaName);
        for (Reldb_Table table : getTableList()) {
            table.print();
        }
    }

    @Override
    public String toString() {
        return schemaName;
    }

    /**
     * @return the schemaName
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * @return the tableList
     */
    public List<Reldb_Table> getTableList() {
        return tableList;
    }
    
    public Reldb_Table getTableByName(String name) {
        for (Reldb_Table iterator : tableList) {
            if (iterator.getTableName().equals(name))
                return iterator;
        }
        return null;
    }
}
