/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.ui.dialogs;

import javafx.stage.Stage;
import reldb.ui.RELDB_01;

/**
 *
 * @author s6fake
 */
public abstract class CustomDialog {
    

    protected Stage stage;
    

    
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }
}
