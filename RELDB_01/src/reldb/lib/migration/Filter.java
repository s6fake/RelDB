package reldb.lib.migration;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author s6fake
 */
public class Filter {

    public final static String[] items_op = {"=", "NOT", "<", ">", "<=", ">=", "CONTAINS", "LIKE"};
    public final static String[] items_con = {"AND", "OR"};

    private int conjunction = -1;
    private int operation = -1;
    private String value = null, column = null;
    private Filter[] bundle = null;

    public Filter(int con_index, int op_index, String value, String column) {
        conjunction = con_index;
        operation = op_index;
        this.value = new String(value);
        this.column = column;
    }

    public Filter(List<Filter> bundle) {
        this.bundle = new Filter[bundle.size()];
        this.bundle = bundle.<Filter>toArray(this.bundle);

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

    public Filter[] getFilters() {
        if (bundle == null) {
            Filter[] list = {this};
            return list;
        } else {
            return bundle;
        }
    }

    @Override
    public String toString() {
        String str = "";
        if (bundle != null) {
            for (Filter item : bundle) {
                str = str + item.toString() + "\n";
            }
            str = str.substring(0, str.length() - 1);
            return str;
        }

        if (value == null) {
            return "";
        }

        if (conjunction != -1) {
            str = items_con[conjunction] + " ";
        }
        str = str + "(" + column + " " + items_op[operation] + " " + value + ")";
        return str;
    }
}
