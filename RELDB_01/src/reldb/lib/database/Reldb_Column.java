package reldb.lib.database;

import java.util.ArrayList;
import java.util.List;
import reldb.lib.migration.Filter;
import reldb.lib.sql.Reldb_Types;

/**
 * Eine Klasse, die wie eine Tabellenspalte fungiert
 *
 * @author s6fake
 */
public class Reldb_Column extends Reldb_DataContainer {

    private boolean isPrimaryKey = false;
    private boolean isForeignKey = false;

    /**
     * Die Tabelle, in der sich die Spalte befindet
     */
    private final Reldb_Table parentTable;
    /**
     * Variable für den Fremdschlüssel
     */
    private String refTableName = null, refColumnName = null;
    @Deprecated
    private int foreignKeySequence = 0;

    /**
     * Ein Array, welches die Filter auf diese Spalte verwaltet
     */
    private List<Filter> filters = new ArrayList<>();
    /**
     * Gibt an, ob die Spalte für den Export ausgewählt wurde
     */
    private boolean selected = true;

    /**
     * Erzeugt eine neue Spalte
     *
     * @param database Die Datenbank, in der sich die Spalte befindet
     * @param table Die Tabelle, in der sich die Spalte befindet
     * @param name Name der Spalte
     * @param type java.sql.Type der der Daten in der Spalte
     * @param typeName Name des Types, wie er als String aus der Datenbank
     * ausgelesen wird
     * @param size Größe des Datentypes
     * @param nullable Lässt die Spalte Null-Werte zu?
     * @param autoincrement Selbsterklärend
     */
    public Reldb_Column(Reldb_Database database, Reldb_Table table, String name, int type, String typeName, int size, boolean nullable, boolean autoincrement) {
        this.database = database;
        this.parentTable = table;
        this.COLUMN_NAME = name;
        this.DATA_TYPE = type;
        this.TYPE_NAME = typeName;
        this.COLUMN_SIZE = size;
        this.NULLABLE = nullable;
        this.AUTOINCREMENT = autoincrement;
    }

    @Deprecated
    public Reldb_Column(Reldb_Database database, Reldb_Table table, String name, int type, int size, boolean nullable, boolean autoincrement) {
        this.database = database;
        this.parentTable = table;
        this.COLUMN_NAME = name;
        this.DATA_TYPE = type;
        this.COLUMN_SIZE = size;
        this.NULLABLE = nullable;
        this.AUTOINCREMENT = autoincrement;
    }

    /**
     * Erzeugt eine Spalte, welche nur Strings beinhaltet. Diese Spalte ist
     * unabhängig von einer Datenbank Der Typ ist automatisch VARCHAR
     *
     * @param table Tabelle in der sich die Spalte befindet
     * @param name Name der Spalte
     */
    public Reldb_Column(Reldb_Table table, String name) {
        this.database = null;
        this.parentTable = table;
        this.COLUMN_NAME = name;
        this.DATA_TYPE = 12;
        this.TYPE_NAME = "VARCHAR";
        this.COLUMN_SIZE = 0;
        this.NULLABLE = false;
        this.AUTOINCREMENT = false;
    }

    /**
     * Fügt einen Fremdschlüssel zu Spalte hinzu
     *
     * @param refTableName Der Name der Tabelle, auf die der Schlüssel
     * referenziert
     * @param refColumnName Der Name der Spalte, auf die der Schlüssel
     * referenziert
     * @param foreignKeySequence Deprecated / Not Used
     */
    public void addForeignKey(String refTableName, String refColumnName, int foreignKeySequence) {
        isForeignKey = true;
        this.refColumnName = refColumnName;
        this.foreignKeySequence = foreignKeySequence;
        this.refTableName = refTableName;
    }

    /**
     * Gibt Information zur Spalte aus
     *
     * @return
     */
    public String printInfo() {
        String ref = "";
        if (isIsForeignKey()) {
            ref = " ref: " + getRefTableName() + "." + getRefColumnName();
        }

        return getCOLUMN_NAME() + "\n(" + TYPE_NAME + ") " + "\njava.sql.Type: " + Reldb_Types.typeMappings.get(DATA_TYPE) + "(" + COLUMN_SIZE + ")" + "\n" + ref + "\nAutoincrement: " + AUTOINCREMENT + " Unique: " + UNIQUE;
    }

    /**
     * Gibt den Spaltennamen aus
     *
     * @return
     */
    @Override
    public String toString() {
        return getCOLUMN_NAME();
    }

    /**
     * Erzeugt einen String, wie er benötigt wird um diese Spalte in einer
     * Tabelle in einer Datenbank zu erstellen
     *
     * @param dbModel Oracle oder Postgres, zur Zeit ist nur ORACLE unterstützt
     * @return
     */
    @Override
    public String getConstructorString(Reldb_Database.DATABASETYPE dbModel) {
        String typeStr = super.getConstructorString(dbModel);
        String constraints = "";
        String nullable = "";

        if (dbModel == Reldb_Database.DATABASETYPE.ORACLE) {
            if (isPrimaryKey) {
                constraints = constraints + ",\nCONSTRAINT " + getTable().getTableName() + "_PK PRIMARY KEY (" + getCOLUMN_NAME() + ")";
            }

            if (!NULLABLE) {
                nullable = nullable + " NOT NULL";
            }
            if (AUTOINCREMENT) {
                nullable = " GENERATED BY DEFAULT ON NULL AS IDENTITY";
            }
            return getCOLUMN_NAME() + " " + typeStr + nullable + constraints;
        }
        return getCOLUMN_NAME() + " " + constraints + " " + typeStr;
    }

    /**
     * Kürzt einen String, wenn er '_' enthält auf eine Zeichenkette, bestehend
     * aus den Anfangsbuchstaben. Wird benötigt, damit Constraint Namen nicht
     * die zulässige Länge überschreiten
     *
     * @param str Der zu kürzende String
     * @return
     */
    private String shorten(String str) {
        if (str.contains("_")) {
            String result = "";
            String[] segments = str.split("_");
            result = "";
            for (int i = 0; i < segments.length - 1; i++) {
                if (!segments[i].equals("")) {
                    result = result + segments[i].charAt(0);
                }
            }
            result = result + segments[segments.length - 1];
            return result;
        }
        return str;
    }

    /**
     * Erzeugt einen Bezeichner für den Fremdschlüssel
     *
     * @return Einen möglichst kurzen, aber eindutigen Namen
     */
    public String getForeignKeyConstraintName() {
        String constraint = "";
        if (isForeignKey) {
            constraint = getTable().getTableName() + "_FK_"
                    + shorten(getCOLUMN_NAME()) + "_"
                    + shorten(getRefColumnName());
        }
        return constraint;
    }

    /**
     * Erzeigt eine Zeichenkette, mit der der Fremschlüssel in einer Datenbank
     * eingefügt werden kann
     *
     * @param dbModel Das Modell der Datenbank in welcher der Schlüssel
     * eingefügt werden soll
     * @return "" wenn die Spalte kein Fremdschlüssel ist
     */
    public String getForeignKeyConstructorString(Reldb_Database.DATABASETYPE dbModel) {
        String constraints = "";
        if (dbModel == Reldb_Database.DATABASETYPE.ORACLE) {
            if (isIsForeignKey()) {
                constraints = constraints + "CONSTRAINT " + getForeignKeyConstraintName();
                constraints = constraints
                        + " FOREIGN KEY (" + getCOLUMN_NAME() + ") REFERENCES "
                        + getRefTableName() + "(" + getRefColumnName() + ")";
            }
        }
        return constraints;
    }

    /**
     * Wenn die Daten in der Spalte gefiltert werden sollen, kann mit dieser
     * Funktion ein entsprechender String erstellt werden
     *
     * @param dbModel Das Datenbankmodell, für welches der String erstellt
     * werden soll
     * @return
     */
    public String getConditionString(Reldb_Database.DATABASETYPE dbModel) {
        String condition = "";
        if (dbModel == Reldb_Database.DATABASETYPE.ORACLE) {
            for (Filter filter : filters) {
                condition = condition + filter.toString() + " ";
            }
            condition = condition.substring(0, condition.length() - 1);
        }
        return condition;
    }

    /**
     * @return the name
     */
    public String getName() {
        return getCOLUMN_NAME();
    }

    /**
     * @return the type
     */
    public int getType() {
        return DATA_TYPE;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return TYPE_NAME;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return COLUMN_SIZE;
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

    /**
     * @return the NULLABLE
     */
    public boolean isNullable() {
        return NULLABLE;
    }

    /**
     * @return the isForeignKey
     */
    public boolean isIsForeignKey() {
        return isForeignKey;
    }

    /**
     * @return the parentTable
     */
    public Reldb_Table getTable() {
        return parentTable;
    }

    /**
     * Einen weiteren Filter zur Spalte hinzufügen
     *
     * @param filter
     */
    public void addFilter(Filter filter) {
        filters.add(filter);

        System.out.println("Filter hinzugefügt " + filters.get(0).toString());
    }

    public List<Filter> getFilter() {
        return filters;
    }

    /**
     * @return the filtered
     */
    public boolean isFiltered() {
        if (filters.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the refTableName
     */
    public String getRefTableName() {
        return refTableName;
    }

    /**
     * @return the refColumnName
     */
    public String getRefColumnName() {
        return refColumnName;
    }
}
