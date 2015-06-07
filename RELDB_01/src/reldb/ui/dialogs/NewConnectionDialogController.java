package reldb.ui.dialogs;

import java.net.URL;
import java.util.ResourceBundle;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;
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

    private Reldb_Connection connection = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choicebox_type.setItems(observableArrayList("PostgreSQL", "Oracle"));
    }

    /**
     * Funktion muss aufgerufen werden, wenn eine Verbindung bearbeitet werden
     * soll
     *
     * @param connection Die Verbindung die geändert werden soll.
     */
    public void setConnection(Reldb_Connection connection) {
        this.connection = connection;
        name_field.setText(connection.getConnectionName());
        user_field.setText(connection.getUserName());
        choicebox_type.getSelectionModel().select(connection.getDatabaseType());
        url_field.setText(connection.getAdress());
        port_field.setText(Integer.toString(connection.getPort()));
        database_field.setText(connection.getDatabaseID());
    }

    private void disableButtons() {
        button_login.setDisable(true);
        button_create.setDisable(true);
        user_field.setDisable(true);
        password_field.setDisable(true);
    }

    @FXML
    private void doLogin(MouseEvent event) {
        if (!createNewConnection2()) {
            return;
        }
        disableButtons();
        stage.close();
        parent.logIn(user_field.getText(), password_field.getText(), connection);
    }

    public void setParent(RELDB_01 parent) {
        this.parent = parent;
    }

    @FXML
    private void createNewConnection(MouseEvent event) {
        if (!createNewConnection2()) {
            return;
        }
        disableButtons();
        stage.close();
    }

    private boolean createNewConnection2() {
        if (createUrl() == null) {
            JOptionPane.showMessageDialog(null, "Die Angegebene URL ist ungültig", "Verbindung kann nicht erstellt werden", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (name_field.getText() == null) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie einen Namen für die Verbindung an", "Verbindung kann nicht erstellt werden", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (connection != null) {
            parent.removeConnection(connection);
            connection.changeSettings(createUrl(), name_field.getText(), url_field.getText(), database_field.getText(), Integer.parseInt(port_field.getText()), choicebox_type.getSelectionModel().getSelectedIndex(), user_field.getText());
        } else {
            connection = new Reldb_Connection(createUrl(), name_field.getText(), url_field.getText(), database_field.getText(), Integer.parseInt(port_field.getText()), choicebox_type.getSelectionModel().getSelectedIndex(), user_field.getText());
        }
        parent.addConnectionToTreeView(connection);
        return true;
    }

    private String createUrl() {
        String jdbc, result;
        char separator = '/';
        // Prüfen ob alle Felder ausgefüllt wurden
        if (url_field.getText() == null || port_field.getText() == null || database_field.getText() == null) {
            return null;
        }

        if (choicebox_type.getSelectionModel().getSelectedIndex() == 1) {
            jdbc = "jdbc:oracle:thin:@";
            separator = ':';
        } else {
            jdbc = "jdbc:postgresql://";
        }                                                                       //jdbc:oracle:thin:@host:1521:dbvm02
        result = jdbc + url_field.getText() + ":" + port_field.getText() + separator + database_field.getText();    //  jdbc:postgresql://dbvm01.iai.uni-bonn.de:5432/imdb
        return result;
    }
}
