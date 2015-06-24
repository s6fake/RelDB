package reldb.ui.dialogs;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;
import reldb.lib.Reldb_Connection;
import reldb.ui.IMainClass;
import reldb.ui.RELDB_01;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class LoginDialogController extends CustomDialog implements Initializable {

    @FXML
    private TextField user_field;
    @FXML
    private PasswordField password_field;
    @FXML
    private Button login_button;
    private IMainClass parent;
    private Pair<String, String> container;
    private Reldb_Connection connection;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void doLogin(MouseEvent event) {
        stage.close();
        parent.logIn(user_field.getText(), password_field.getText(), connection);
    }

    public void setParent(IMainClass parent) {
        this.parent = parent;
    }
    
    public void setConnection(Reldb_Connection connection)
    {
        this.connection = connection;
    }
    public void setContainer(Pair<String, String> container) {
        this.container = container;
    }
}
