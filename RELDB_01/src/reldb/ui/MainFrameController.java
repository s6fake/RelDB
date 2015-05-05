/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import reldb.ui.dialogs.CustomDialog;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class MainFrameController implements Initializable {
    private RELDB_01 parent;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btn_connect_pressed(MouseEvent event) {        
        parent.callLoginDialog();
    }

    @FXML
    private void btn_close_pressed(MouseEvent event) {
    }
    
    public void setParent(RELDB_01 parent)
    {
        this.parent = parent;
    }
}
