/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.lib.sql;

import reldb.lib.database.Reldb_Column;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Schema;
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

    /**
     * Nur zum testen
     *
     */
    private static String convert(String typeName, int type, int size) {
        String dataString = "";
        if (type != 12) {   //Varchar
            dataString = Reldb_Types.typeMappings.get(type);
            return dataString;
        }
        if (typeName.equalsIgnoreCase("text")) {
            dataString = "CLOB";
            return dataString;
        }
        if (size == 1) {
            dataString = "CHAR";
            return dataString;
        }
        if (size > 400) {
            dataString = "LONG VARCHAR";
            return dataString;
        }
        dataString = "VARCHAR (" + size + ")";
        return dataString;
    }

    /**
     * Erzeug einen CREATE TABLE Befehl auf Grundlage der übergebenen Tabelle.
     *
     * @param pattern Die Tabelle die als Vorlage dienen soll. not null.
     * @return
     */
    public static String createTable(Reldb_Table pattern) {
        return createTable(pattern, pattern.getDatabase().getDatabaseType());
    }

    /**
     * Erzeug einen CREATE TABLE Befehl auf Grundlage der übergebenen Tabelle
     * und passt diese ggf. an ein anderes Format an.
     *
     * @param pattern Die Tabelle die als Vorlage dienen soll.
     * @param dbType Das Datenbankformat, in der die Tabelle eingefügt werden
     * soll
     * @return
     */
    public static String createTable(Reldb_Table pattern, Reldb_Database.DATABASETYPE dbType) {
        String command = "CREATE TABLE " + pattern.getTableName() + " (";
        for (Reldb_Column column : pattern.getColumns()) {
            //String typeStr = convert(column.getTypeName(), column.getType(), column.getSize());
            //String type = Reldb_Types.typeMappings.get(column.getType());       // Type ins richtige Format kovertieren
            command = command + "\n" + column.getConstructorString(dbType) + ",";
        }
        command = command.substring(0, command.length() - 1);                   // Letztes Komma wieder entfernen
        command = command + "\n)";
        System.out.println(command);
        return command;
    }
}
