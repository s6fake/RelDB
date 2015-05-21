package reldb.lib;

/**
 *
 * @author s6fake
 */
public class Reldb_TreeViewElement {
    
    private Object item;
    private String displayName;
    
    public Reldb_TreeViewElement(Object item, String displayName) {
        this.item = item;
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
    
    /**
     * @return the item
     */
    public Object getItem() {
        return item;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }
    
    
}
