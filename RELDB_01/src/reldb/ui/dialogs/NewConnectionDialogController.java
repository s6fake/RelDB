/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.ui.dialogs;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import reldb.lib.Reldb_Connection;
import reldb.ui.RELDB_01;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class NewConnectionDialogController extends CustomDialog implements Initializable {

    @FXML
    private TextField user_field;
    @FXML
    private PasswordField password_field;
    @FXML
    private TextField url_field;

    private RELDB_01 parent;
    @FXML
    private TextField name_field;
    @FXML
    private TextField port_field;
    @FXML
    private TextField database_field;
    @FXML
    private Button button_create;
    @FXML
    private Button button_login;
    @FXML
    private ChoiceBox choicebox_type;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choicebox_type.setItems(observableArrayList("PostgreSQL", "Oracle"));
    }

    @FXML
    private void doLogin(MouseEvent event) {
        Reldb_Connection newConnection = parent.createConnection(createUrl(), name_field.getText());
        if (newConnection == null)
                return;
        button_login.setDisable(true);
        user_field.setDisable(true);
        password_field.setDisable(true);

        stage.close();
        parent.logIn(user_field.getText(), password_field.getText(), newConnection);
    }

    public void setParent(RELDB_01 parent) {
        this.parent = parent;
    }

    @FXML
    private void createNewConnection(MouseEvent event) {
        if (parent.createConnection(createUrl(), name_field.getText()) == null)
            return;
        stage.close();

    }

    private String createUrl() {
        String jdbc = null;
        String result = null;
        if (choicebox_type.getSelectionModel().getSelectedIndex() == 1) {
            jdbc = "jdbc:oracle";
        } else {
            jdbc = "jdbc:postgresql";
        }
        result = jdbc + "://" + url_field.getText() + ":" + port_field.getText() + "/" + database_field.getText();    //  jdbc:postgresql://dbvm01.iai.uni-bonn.de:5432/imdb
        return result;
    }
}
