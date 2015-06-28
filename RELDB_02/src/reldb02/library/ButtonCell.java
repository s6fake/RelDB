package reldb02.library;

import java.lang.reflect.Field;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import reldb.lib.database.Reldb_Row;

/**
 *
 * @author Fabo
 */
public abstract class ButtonCell extends TableCell<Reldb_Row, Boolean> {

    protected final Button button;
    protected final StackPane paddedButton = new StackPane();
    protected final TableView<?> table;

    /**
     * @Author Stackoverflow
     * @param tooltip 
     */
        public static void hackTooltipStartTiming(Tooltip tooltip) {
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ButtonCell(final TableView<?> table, String text) {
        button = new Button(text);
        paddedButton.setPadding(new Insets(3));
        paddedButton.getChildren().add(button);
        this.table = table;
    }

    /**
     * Button in der Tabelle anzeigen
     *
     * @param item
     * @param empty
     */
    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(paddedButton);
        } else {
            setGraphic(null);
        }
    }

}
