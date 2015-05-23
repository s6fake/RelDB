package reldb.lib.database;

import java.util.logging.Logger;
import java.sql.Types;

/**
 *
 * @author s6fake
 */
public class Reldb_DataContainer {

    public static enum DATABASEMODEL {

        POSTGRESQL, ORACLE
    };
private static final Logger log = Logger.getLogger(Reldb_DataContainer.class.getName());
    private Object data = null;
    private final int dataType;  // DatenTyp im java.sql.Types Format. Unterscheidet sich vom Typformat von Oracle und Postgres

    public Reldb_DataContainer(Object data, int dataType) {
        this.data = data;
        this.dataType = dataType;
    }

    public Object convertTo(DATABASEMODEL dbm) {
        switch (dbm) {
            case POSTGRESQL:
                //ToDo
                break;
            case ORACLE:
                //ToDo
                break;
            default:
                return null;

        }
        return this;
    }
    
    

    @Override
    public String toString() {
        if (data == null) {
            return "(null)";
        }
        return data.toString();
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @return the dataType
     */
    public int getDataType() {
        return dataType;
    }
}
