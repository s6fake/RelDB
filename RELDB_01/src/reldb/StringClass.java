package reldb;

/**
 * Eine Hilfsklasse, um z.B. eine TableView mit einfachen Strings zu füllen
 * @author s6fake
 */
public class StringClass {
    
    private String string;
    
    /**
     * Erzeugt ein neues StringClass Object. Der String wird Kopiert.
     * @param str 
     */
    public StringClass(String str)
    {
        string = new String(str);
    }
    
    /**
     * Gibt den String aus
     * @return the string
     */
    @Override
    public String toString() {
        return string;
    }  
}
