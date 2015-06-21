package reldb.lib.database;

import java.util.logging.Logger;
import reldb.lib.database.Reldb_Database.DATABASETYPE;
import reldb.lib.sql.Reldb_Types;

/**
 *
 * @author s6fake
 */
public class Reldb_DataContainer {

    private static final Logger log = Logger.getLogger(Reldb_DataContainer.class.getName());

    protected String COLUMN_NAME = "";       //Name der Spalte
    protected String TYPE_NAME = "";         //Name des Datentyps
    protected int DATA_TYPE = 0;            //Datentyp als java.sql.Types.
    protected int COLUMN_SIZE = 0;          //Größe der Spalte
    protected boolean NULLABLE = true;         //Lässt die Spalte Nullwerte zu?
    protected boolean AUTOINCREMENT = false;
    protected boolean UNIQUE = false;
    protected Reldb_Database database = null;
    private final Object data;

    public Reldb_DataContainer() {
        this.data = null;
    }

    public Reldb_DataContainer(Object data) {
        this.data = data;
    }

    public Reldb_DataContainer(Reldb_Database database, String COLUMN_NAME, int DATA_TYPE, String TYPE_NAME, int COLUMN_SIZE, boolean NULLABLE, boolean AUTOINCREMENT) {
        this.database = database;
        this.COLUMN_NAME = COLUMN_NAME;
        this.DATA_TYPE = DATA_TYPE;
        this.TYPE_NAME = TYPE_NAME;
        this.COLUMN_SIZE = COLUMN_SIZE;
        this.NULLABLE = NULLABLE;
        this.AUTOINCREMENT = AUTOINCREMENT;
        this.data = null;
    }

    public Reldb_DataContainer(Reldb_Database database, String COLUMN_NAME, int DATA_TYPE, String TYPE_NAME, int COLUMN_SIZE, boolean NULLABLE, Object data) {
        this.database = database;
        this.COLUMN_NAME = COLUMN_NAME;
        this.DATA_TYPE = DATA_TYPE;
        this.TYPE_NAME = TYPE_NAME;
        this.COLUMN_SIZE = COLUMN_SIZE;
        this.NULLABLE = NULLABLE;
        this.data = data;
    }

    @Override
    public Reldb_DataContainer clone() {
        Reldb_DataContainer newObj = new Reldb_DataContainer(getDatabase(), COLUMN_NAME, DATA_TYPE, TYPE_NAME, COLUMN_SIZE, NULLABLE, data);
        return newObj;
    }

    public Object convertTo(DATABASETYPE dbm) {
        if (getDatabase().getDatabaseType() == dbm) {
            return this;
        }
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
        switch (DATA_TYPE) {
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
            return "NULL";
        }
        if (data instanceof String) {
            String str = data.toString();
            if (str.contains("'")) {
                String[] elements = str.split("'");
                str = "";
                for (int i = 0; i < elements.length; i ++) {
                    str = str + elements[i]+"''";
                }
                str = str.substring(str.length() - 2);
            }
            return "'"+str+"'";
        }
        return data.toString();
    }
    

    protected String getConstructorString(DATABASETYPE dbModel) {
        String string = "";
        if (dbModel == DATABASETYPE.ORACLE) {
            if (DATA_TYPE != 12) {          // Nur VARCHAR muss bearbeitet werden
                string = Reldb_Types.typeMappings.get(DATA_TYPE);
                return string;
            }
            if (Reldb_Types.typeMappings.get(DATA_TYPE).equalsIgnoreCase("text")) {
                string = "CLOB";
                return string;
            }
            if (COLUMN_SIZE == 1) {
                string = "CHAR";
                return string;
            }
            if (COLUMN_SIZE > 400) {
                string = "LONG VARCHAR";
                return string;
            }
            string = "VARCHAR (" + COLUMN_SIZE + ")";
            return string;
        }

        return string;
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
        return DATA_TYPE;
    }

    /**
     * @return the UNIQUE
     */
    public boolean isUNIQUE() {
        return UNIQUE;
    }

    /**
     * @param UNIQUE the UNIQUE to set
     */
    public void setUNIQUE(boolean UNIQUE) {
        this.UNIQUE = UNIQUE;
    }

    /**
     * @return the database
     */
    public Reldb_Database getDatabase() {
        return database;
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
