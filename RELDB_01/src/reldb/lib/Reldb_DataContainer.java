/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.lib;

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
