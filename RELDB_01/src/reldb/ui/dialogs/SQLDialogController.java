/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.ui.dialogs;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class SQLDialogController implements Initializable {
    @FXML
    private TextArea txt_area;
    @FXML
    private Button ok_btn;
    @FXML
    private Button cancel_btn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void execute(MouseEvent event) {
    }

    @FXML
    private void closeDialog(MouseEvent event) {
    }
    
}
