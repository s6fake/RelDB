package reldb.lib.migration;

/**
 *
 * @author s6fake
 */
public class Filter {

    public final static String[] items_op = {"=", "NOT", "<", ">", "<=", ">=", "CONTAINS", "LIKE"};
    public final static String[] items_con = {"AND", "OR"};

    private int conjunction;
    private int operation;
    private String value;
    private final String column;

    public Filter(int con_index, int op_index, String value, String column) {
        System.out.println("Filter, " + value);
        conjunction = con_index;
        operation = op_index;
        this.value = new String(value);
        this.column = column;
        
    }

    public void reset(int con_index, int op_index, String value) {
        conjunction = con_index;
        setOperation(op_index);
        this.value = value;
    }

    /**
     * @return the conjunction
     */
    public int getConjunction() {
        return conjunction;
    }

    /**
     * @return the operation
     */
    public int getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(int operation) {
        this.operation = operation;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value == null) {
            return "";
        }

        String str = "";
        if (conjunction != -1) {
            str = items_con[conjunction] + " ";
        }
        str = str + column + " " + items_op[operation] + " " + value;
        return str;
    }
}
