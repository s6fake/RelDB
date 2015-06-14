package reldb.lib.migration;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author s6fake
 */
public class Progress {
   
    
    private SimpleIntegerProperty current, total;
    private SimpleDoubleProperty percent = new SimpleDoubleProperty();
    
    public Progress(int total) {
        this.current = new  SimpleIntegerProperty();
        this.total = new SimpleIntegerProperty();
        setTotalValue(total);
        setCurrentProgress(0);        
    }
    
    public void reset(int value) {
        setTotalValue(value);
        setCurrentProgress(0);        
    }
    
    public void increaseCurrentProgress() {
        setCurrentProgress(current.get() + 1);
    }
    
    private void setCurrentProgress(int value) {
        current.setValue(value);
        calcPercent();
    }
    
    public final void setTotalValue(int value) {
        total.setValue(value);
    }
    
    private void calcPercent() {
        percent.setValue(current.doubleValue()/total.doubleValue());
    }
    public SimpleDoubleProperty getPercentProperty() {
        return percent;
    }
    
    public double getPercent() {
        return percent.doubleValue();
    }
    
    public int getCurrent() {
        return current.get();
    }
    
    public int getTotal() {
        return total.get();
    }
}
