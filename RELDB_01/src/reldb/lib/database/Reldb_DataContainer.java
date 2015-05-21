package reldb.lib.database;

/**
 *
 * @author s6fake
 */
public class Reldb_DataContainer {
    
    private Object data = null;
    private int dataType;
    private Object databaseModel = null;    //TODO ORacle / Postgres
    
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
