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

    
    public String printInfo() {
        String ref = "";
        if (isIsForeignKey()) {
            ref = " ref: " + refTableName + "." + refColumnName;
        }

        return COLUMN_NAME + "\n(" + TYPE_NAME + ") " + "\njava.sql.Type: " + Reldb_Types.typeMappings.get(DATA_TYPE) + "(" + COLUMN_SIZE + ")" + "\n" + ref + "\nAutoincrement: " + AUTOINCREMENT + " Unique: " + UNIQUE;
    }

    @Override
    public String toString() {
        return COLUMN_NAME;
    }
    
    @Override
    public String getConstructorString(Reldb_Database.DATABASETYPE dbModel) {
        String typeStr = super.getConstructorString(dbModel);
        String constraints = "";
        String nullable = "";

        if (dbModel == Reldb_Database.DATABASETYPE.ORACLE) {
            if (isPrimaryKey) {
                constraints = constraints + ",\nCONSTRAINT " + getTable().getTableName() + "_PK PRIMARY KEY (" + COLUMN_NAME + ")";
            }

            if (!NULLABLE) {
                nullable = nullable + " NOT NULL";
            }
            return COLUMN_NAME + " " + typeStr + nullable + constraints;
        }
        return COLUMN_NAME + " " + constraints + " " + typeStr;
    }

    private String shorten(String str) {
        if (str.contains("_")) {
            String result = "";
            String[] segments = str.split("_");
            result = "";
            for (int i = 0; i < segments.length - 1; i++) {
                if (!segments[i].equals("")) {
                    result = result + segments[i].charAt(0);
                }
            }
            result = result + segments[segments.length - 1];
            return result;
        }
        return str;
    }
    
    public String getForeignKeyConstraintName() {
        String constraint = "";
        if (isForeignKey)
        {
            constraint = getTable().getTableName() + "_FK_" + shorten(COLUMN_NAME) + "_" + shorten(refColumnName);
        }
        return constraint;
    }

    public String getForeignKeyConstructorString(Reldb_Database.DATABASETYPE dbModel) {
        String constraints = "";
        if (dbModel == Reldb_Database.DATABASETYPE.ORACLE) {
            if (isIsForeignKey()) {
                constraints = constraints + "CONSTRAINT " + getForeignKeyConstraintName();
                constraints = constraints + " FOREIGN KEY (" + COLUMN_NAME + ") REFERENCES " + refTableName + "(" + refColumnName + ")";
            }
        }
        return constraints;
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

    /**
     * @return the isForeignKey
     */
    public boolean isIsForeignKey() {
        return isForeignKey;
    }

    /**
     * @return the parentTable
     */
    public Reldb_Table getTable() {
        return parentTable;
    }
}
