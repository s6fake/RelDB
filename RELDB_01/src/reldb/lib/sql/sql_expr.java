/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.lib.sql;

import reldb.lib.database.Reldb_Column;
import reldb.lib.database.Reldb_Table;

/**
 * SELECT table_name FROM information_schema.tables WHERE table_schema='public'
 * AND table_type='BASE TABLE';
*
 */
/**
 *
 * @author s6fake
 */
public class sql_expr {

    public static String getTableNames(String database) {
        if (database.equalsIgnoreCase("PostgreSQL")) {
            return "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';";
        }
        return null;
    }

    public static String createTable(Reldb_Table pattern) {
        String command = "CREATE TABLE " + pattern.getTableName() + " (\n";
        for (Reldb_Column column : pattern.getColumns()) {
            command = command + column.getName() + " " + column.getTypeName() + ",\n";
        }
        command = command + ")";
        System.out.println(command);
        return command;
    }
}
