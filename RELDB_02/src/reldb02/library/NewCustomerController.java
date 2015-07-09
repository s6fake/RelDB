package reldb02.library;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
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
    private Button btn_ok;
    @FXML
    private TextField txt_street;
    @FXML
    private TextField txt_city;
    @FXML
    private TextField txt_postcode;
    @FXML
    private DatePicker dp_birthdate;

    /**
     * Pseudo Konstruktor
     *
     * @param parent
     * @return
     */
    public static NewCustomerController customerDialog(Window parent) {
        NewCustomerController controller = null;
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(NewCustomerController.class.getResource("newCustomer.fxml"));

            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("New Customer");

            // Controller initialisieren
            controller = loader.<NewCustomerController>getController();
            controller.setStage(stage);
            stage.initOwner(parent);

            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return controller;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btn_ok.disableProperty().bind(txt_name.textProperty().isEqualTo(""));
    }

    @FXML
    private void on_ok(ActionEvent event) {
        if (!checkValues()) {
            return;
        }
        java.sql.Date date = null;
        if (dp_birthdate.getValue() != null) {
            date = new java.sql.Date(dp_birthdate.getValue().toEpochDay() * 86400000);
        }

        Object[] data = {txt_name.getText(), date, txt_street.getText(), txt_city.getText(), (txt_postcode.getText() != null && !txt_postcode.getText().isEmpty()) ? Integer.decode(txt_postcode.getText()) : null};
        Library.getInstance().addCustomer(data);
        stage.close();
    }

    private boolean checkValues() {
        if (txt_postcode.getText() != null && !txt_postcode.getText().isEmpty()) {
            if (txt_postcode.getText().length() > 5) {
                return false;
            }
        }

        return true;
    }

    @FXML
    private void on_cancel(ActionEvent event) {
        stage.close();
    }

}
