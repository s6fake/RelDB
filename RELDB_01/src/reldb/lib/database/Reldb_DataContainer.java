package reldb.lib.database;

/**
 *
 * @author s6fake
 */
public class Reldb_DataContainer {
    
    private Object data = null;
    private int dataType;                   // DatenTyp im java.sql.Types Format. Unterscheidet sich vom Typformat von Oracle und Postgres
    private Object databaseModel = null;    //TODO Oracle / Postgres
    
    public Reldb_DataContainer(Object data, int dataType) {
        this.data = data;
        this.dataType = dataType;
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
