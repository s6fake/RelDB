package reldb02.library;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import reldb.ui.dialogs.CustomDialog;

/**
 * FXML Controller class
 *
 * @author Fabo
 */
public class NewCustomerController extends CustomDialog implements Initializable {

    @FXML
    private TextField txt_name;
    @FXML
    private TextField txt_birth;
    @FXML
    private Button btn_ok;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btn_ok.disableProperty().bind(txt_name.textProperty().isEqualTo(""));
    }

    @FXML
    private void on_ok(ActionEvent event) {
        Object[] data = {txt_name.getText(), txt_birth.getText(), null, null, null};
        Library.getInstance().addCustomer(data);
        stage.close();
    }

    @FXML
    private void on_cancel(ActionEvent event) {
        stage.close();
    }

}
