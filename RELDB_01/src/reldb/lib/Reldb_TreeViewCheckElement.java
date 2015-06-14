
package reldb.lib;

import java.util.List;
import javafx.scene.control.CheckBox;

/**
 *
 * @author s6fake
 */
public class Reldb_TreeViewCheckElement extends CheckBox implements IReldb_TreeViewElement{

    private final Reldb_TreeViewElement reference;
    
    public Reldb_TreeViewCheckElement(Object item, String displayName) {
        super(item.toString());
        this.reference = new Reldb_TreeViewElement(item, displayName);
        setText(displayName);
    }

    public Reldb_TreeViewCheckElement(Object item) {
        super(item.toString());
        this.reference = new Reldb_TreeViewElement(item);
        setText(reference.getDisplayName());
    }



    @Override
    public String toString() {
        return reference.toString();
    }

    /**
     * @return the item
     */
    @Override
    public Object getItem() {
        return reference.getItem();
    }

    /**
     * @return the displayName
     */
    @Override
    public String getDisplayName() {
        return reference.getDisplayName();
    }

    @Override
    public List<?> discover() {
        return reference.discover();
    }

    /**
     * @return the discovered
     */
    @Override
    public boolean isDiscovered() {
        return reference.isDiscovered();
    }

}
