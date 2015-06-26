package reldb.lib.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author s6fake
 */
public class Reldb_Row {

    private static final Logger log = Logger.getLogger(Reldb_Row.class.getName());

    private Reldb_Table table;
    private Reldb_DataContainer[] cells;

    /**
     * 
     * @param table
     * @param objects Enthält genau eine Reihe an Daten
     */
    public Reldb_Row(Reldb_Table table, ResultSet objects) {
        this.table = table;
        this.cells = new Reldb_DataContainer[table.getSelectedColumns().size()];
        insertObjects(objects);
    }

    private void insertObjects(ResultSet objects) {
        String colName = "";
        Object object = null;
        int i = 0;
        try {
            int c = getCells().length;
            List<Reldb_Column> columns = getTable().getSelectedColumns();
            
            for (i = 0; i < c; i++) {
                colName = columns.get(i).getCOLUMN_NAME();
                object = objects.getObject(colName);
                cells[i] = new Reldb_DataContainer(object, colName);
                //System.out.println("Cells[" + i + "] = " + objects.getObject(colName) + " " + colName);
            }
        } catch (SQLException e) {
            System.err.println(e.toString() + " " + "Cells[" + i + "] = " + object + " " + colName);
        }
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < getCells().length; i++) {
            result = result + getCells()[i].toSaveString() + " ";
        }
        return result;
    }

    public void print() {
        System.out.println(getTable().getTableName());
        System.out.println(this.toString());
    }

    /**
     * @return the cells
     */
    public Reldb_DataContainer[] getCells() {
        return cells;
    }

    public Reldb_DataContainer getCellByColumn(String columnName) {
        for (Reldb_DataContainer container : cells) {
            if (container.getCOLUMN_NAME().equalsIgnoreCase(columnName)) {
                return container;
            }
        }
        String[] str = {columnName, getTable().getTableName()};
        log.log(Level.INFO, "Zelle {0} in Tabelle {1} nicht gefunden!", str);
        return null;
    }

    /**
     * @return the table
     */
    public Reldb_Table getTable() {
        return table;
    }

    /**
     * @return the database
     */
    public Reldb_Database getDatabase() {
        return table.getDatabase();
    }
    
    public StringProperty get(int i) {
        if (!(i < cells.length)) {
            return new SimpleStringProperty("");
        }
        return new SimpleStringProperty(cells[i].toString());
    }
    
        public StringProperty get(String name) {
         
            return new SimpleStringProperty("Not supported yet");
    }

}
