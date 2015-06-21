package reldb.ui.dialogs;

import javafx.stage.Stage;


/**
 *
 * @author s6fake
 */
public abstract class CustomDialog {

    protected Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
