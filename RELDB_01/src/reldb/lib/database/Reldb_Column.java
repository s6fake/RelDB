package reldb.lib.database;

import reldb.lib.sql.Reldb_Types;

/**
 *
 * @author s6fake
 */
public class Reldb_Column extends Reldb_DataContainer {

    private boolean isPrimaryKey = false;
    private boolean isForeignKey = false;
    
    private final Reldb_Table parentTable;
    private String refTableName = null, refColumnName = null;
    private int foreignKeySequence = 0;
    

    public Reldb_Column(Reldb_Database database, Reldb_Table table, String name, int type, String typeName, int size, boolean nullable, boolean autoincrement) {
        this.database = database;
        this.parentTable = table;
        this.COLUMN_NAME = name;
        this.DATA_TYPE = type;
        this.TYPE_NAME = typeName;
        this.COLUMN_SIZE = size;
        this.NULLABLE = nullable;
        this.AUTOINCREMENT = autoincrement;
//super(database, typeName, size, typeName, size, nullable);
    }

    public void addForeignKey(String refTableName, String refColumnName, int foreignKeySequence) {
        isForeignKey = true;
        this.refColumnName = refColumnName;
        this.foreignKeySequence = foreignKeySequence;
        this.refTableName = refTableName;
    }

    @Override
    public String toString() {
        String ref = "";
        if (isForeignKey) {
            ref = " ref: " + refTableName + "." + refColumnName;
        }
        
        return COLUMN_NAME + "\n(" + TYPE_NAME + ") " + "\njava.sql.Type: " + Reldb_Types.typeMappings.get(DATA_TYPE) + "(" + COLUMN_SIZE + ")" + "\n" + ref +"\nAutoincrement: " + AUTOINCREMENT + " Unique: " + UNIQUE;
    }

    @Override
    public String getConstructorString(Reldb_Database.DATABASETYPE dbModel) {
        String typeStr = super.getConstructorString(dbModel);
        String constraints = "";
        String nullable = "";
        if (dbModel == Reldb_Database.DATABASETYPE.ORACLE) {
            if (isPrimaryKey) {
                constraints = constraints + ",\nCONSTRAINT "+ parentTable.getTableName() +"_PK PRIMARY KEY (" + COLUMN_NAME + ")";
            }
            if (!NULLABLE) {
                nullable = nullable + " NOT NULL";
            }
            return COLUMN_NAME + " " + typeStr + nullable + constraints;
        }
        return COLUMN_NAME + " " + constraints + " " + typeStr;
    }

    /**
     * @return the name
     */
    public String getName() {
        return COLUMN_NAME;
    }

    /**
     * @return the type
     */
    public int getType() {
        return DATA_TYPE;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return TYPE_NAME;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return COLUMN_SIZE;
    }

    /**
     * @return the isPrimaryKey
     */
    public boolean isIsPrimaryKey() {
        return isPrimaryKey;
    }

    /**
     * @param isPrimaryKey the isPrimaryKey to set
     */
    public void setIsPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    /**
     * @return the NULLABLE
     */
    public boolean isNullable() {
        return NULLABLE;
    }
}
