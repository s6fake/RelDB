package reldb.lib.database;

/**
 *
 * @author s6fake
 */
public class Reldb_Column {
    
    private final String name;  //Name der Spalte
    private final int type;     //Datentyp der Spalte
    
    public Reldb_Column(String name, int type) {
        this.name = name;
        this.type = type;
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
}
