package reldb.ui.dialogs;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import reldb.lib.migration.Reldb_DataMover;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class ProgressDialogController extends CustomDialog implements Initializable {

    @FXML
    private Label label_1;
    @FXML
    private Label label_2;
    @FXML
    private ProgressBar pgbar1;
    @FXML
    private ProgressBar pgbar2;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initalizeChangeListener();
    }

    public void initalizeChangeListener() {
        Reldb_DataMover.progress_current.getPercentProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal,
                    Object newVal) {
                updateProgress();
            }
        });
        Reldb_DataMover.progress_total.getPercentProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal,
                    Object newVal) {
                updateProgress();
            }
        });
    }

    @FXML
    private void on_cancel(ActionEvent event) {
    }

    public synchronized void updateProgress() {
        //String str = "bibabubu";
        //label_1.textProperty().set(str);
       
        //label_1.setText(Reldb_DataMover.progress_string + Reldb_DataMover.progress_current.getCurrent() + " von " + Reldb_DataMover.progress_current.getTotal());
        pgbar1.setProgress(Reldb_DataMover.progress_current.getPercentProperty().doubleValue());
        //label_2.setText("Gesamtfortschritt: " + (Reldb_DataMover.progress_total.getPercent()*100) + "%");
        pgbar2.setProgress(Reldb_DataMover.progress_total.getPercentProperty().doubleValue());
    }

}
