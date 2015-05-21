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
import reldb.lib.Reldb_Connection;
import reldb.lib.MetaDataManager;
import reldb.lib.sql.Reldb_Statement;
import reldb.ui.RELDB_01;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class SQLDialogController extends CustomDialog implements Initializable {

    @FXML
    public TextArea txt_area;
    @FXML
    private Button ok_btn;
    @FXML
    private Button cancel_btn;
    private RELDB_01 parent;

    private Reldb_Connection connection;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void initalize(Reldb_Connection connection) {
        this.connection = connection;
    }

    @FXML
    private void execute(MouseEvent event) {
        Reldb_Statement statement = new Reldb_Statement(connection);
        MetaDataManager.printResultset(statement.executeCommand(connection, txt_area.getText()));
        statement.close();
    }

    @FXML
    private void closeDialog(MouseEvent event) {
        stage.close();
    }

    public void setParent(RELDB_01 parent) {
        this.parent = parent;
    }

}
