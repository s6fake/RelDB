package reldb.lib.sql;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s6fake
 */
public abstract class Reldb_Types {
    
    public static Map<Integer, String> typeMappings = getAllTypeNames();
    
    /**
     * Funktion ordnet jedem java.sql.Type den dazugehörigen Namen zu
     * @return 
     */
    private static Map<Integer, String> getAllTypeNames() {
    Map<Integer, String> result = new HashMap<Integer, String>();

    for (Field field : java.sql.Types.class.getFields()) {
        try {
            result.put((Integer)field.get(null), field.getName());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Reldb_Types.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Reldb_Types.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    return result;
}
    
}
