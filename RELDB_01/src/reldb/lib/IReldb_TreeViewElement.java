package reldb.lib;

import java.util.List;

/**
 *
 * @author s6fake
 */
@Deprecated
public interface IReldb_TreeViewElement {
    public Object getItem();
    public String getDisplayName();
    public List<?> discover();
    public boolean isDiscovered();
}
