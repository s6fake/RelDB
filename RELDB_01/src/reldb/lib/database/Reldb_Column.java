package reldb.lib.database;
import reldb.lib.sql.Reldb_Types;

/**
 *
 * @author s6fake
 */
public class Reldb_Column {

    private final String name;      //Name der Spalte
    private final String typeName;  //Name des Datentyps
    private final int type;         //Datentyp der Spalte als java.sql.Types
    private final int size;         //Größe der Spalte

    private boolean isPrimaryKey = false;
    private boolean isForeignKey = false;
    private String refTableName = null, refColumnName = null;
    private int foreignKeySequence = 0;
    
    public Reldb_Column(String name, int type, String typeName, int size) {
        this.name = name;
        this.type = type;
        this.typeName = typeName;
        this.size = size;
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
        return name + "\n(" + typeName + ") " +"\njava.sql.Type: " + Reldb_Types.typeMappings.get(type) + ref;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
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
}
