package reldb.lib.migration;

import java.util.ArrayList;
import java.util.List;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Schema;
import reldb.lib.database.Reldb_Table;

/**
 *
 * @author s6fake
 */
public class Reldb_DatabasePattern extends Reldb_Database {

    private List<Reldb_Table> tableList;
    
    public Reldb_DatabasePattern(Reldb_Database reference) {
        super(reference);
    }

    public Reldb_DatabasePattern(Reldb_Schema schemaPattern) {        
        super.databaseType = schemaPattern.getDatabase().getDatabaseType();        
        super.schemaList.add(schemaPattern);
        tableList = super.getTables();
        System.out.println("Schema 1");
    }

    public Reldb_DatabasePattern(Reldb_Table schemaPattern) {
        super.databaseType = schemaPattern.getDatabase().getDatabaseType();
        List<Reldb_Table> list = new ArrayList<>();
        list.add(schemaPattern);
        super.setTableList(list);
    }

    public Reldb_DatabasePattern(List<?> schemaPattern) {
        //super(schemaPattern);
        System.out.println("Schema Liste: " + schemaPattern.size());
    }
    
    @Override
    public List<Reldb_Table> getTables() {
        return tableList;
    }

}
