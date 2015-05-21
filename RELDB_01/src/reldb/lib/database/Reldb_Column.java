package reldb.lib.database;

/**
 *
 * @author s6fake
 */
public class Reldb_Column {

    private final String name;      //Name der Spalte
    private final String typeName;  //Name des Datentyps
    private final int type;         //Datentyp der Spalte als java.sql.Types
    private final int size;         //Größe der Spalte

    public Reldb_Column(String name, int type, String typeName, int size) {
        this.name = name;
        this.type = type;
        this.typeName = typeName;
        this.size = size;
    }

    @Override
    public String toString() {
        return name + "\n(" + typeName + ") " +"\njava.sql.Type:"+ type;
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
}
