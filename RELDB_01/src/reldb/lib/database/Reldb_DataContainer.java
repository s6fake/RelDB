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
                return convertToOracle();
            default:
                return null;

        }
        return this;
    }

    private Object convertToOracle() {
        switch (dataType) {
            case (-7):     //BIT
                //ToDo
                break;
            case (-6):      //TINYINT
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

/*
 package java.sql;

 public class Types {

 public static final int BIT = -7;
 public static final int TINYINT = -6;
 public static final int SMALLINT = 5;
 public static final int INTEGER = 4;
 public static final int BIGINT = -5;
 public static final int FLOAT = 6;
 public static final int REAL = 7;
 public static final int DOUBLE = 8;
 public static final int NUMERIC = 2;
 public static final int DECIMAL = 3;
 public static final int CHAR = 1;
 public static final int VARCHAR = 12;
 public static final int LONGVARCHAR = -1;
 public static final int DATE = 91;
 public static final int TIME = 92;
 public static final int TIMESTAMP = 93;
 public static final int BINARY = -2;
 public static final int VARBINARY = -3;
 public static final int LONGVARBINARY = -4;
 public static final int NULL = 0;
 public static final int OTHER = 1111;
 public static final int JAVA_OBJECT = 2000;
 public static final int DISTINCT = 2001;
 public static final int STRUCT = 2002;
 public static final int ARRAY = 2003;
 public static final int BLOB = 2004;
 public static final int CLOB = 2005;
 public static final int REF = 2006;
 public static final int DATALINK = 70;
 public static final int BOOLEAN = 16;
 public static final int ROWID = -8;
 public static final int NCHAR = -15;
 public static final int NVARCHAR = -9;
 public static final int LONGNVARCHAR = -16;
 public static final int NCLOB = 2011;
 public static final int SQLXML = 2009;
 public static final int REF_CURSOR = 2012;
 public static final int TIME_WITH_TIMEZONE = 2013;
 public static final int TIMESTAMP_WITH_TIMEZONE = 2014;

 private Types() {
 // compiled code
 }
 }

 */
