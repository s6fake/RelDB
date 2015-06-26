package reldb02.library;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import reldb.lib.database.Reldb_Row;
import reldb.lib.database.Reldb_Table;
import reldb.lib.sql.Reldb_Statement;
import reldb02.ui.RELDB_02;

/**
 * FXML Controller class
 *
 * @author Fabo
 */
public class CustomerInterfaceController implements Initializable {
    @FXML
    private TableView<Reldb_Row> table_users;
    @FXML
    private TableColumn<Reldb_Row, String> col_users;
    @FXML
    private TableView<?> table_rent;
    @FXML
    private TableView<?> table_rating;
    private Stage stage;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void initialize(Reldb_Table userTable, Reldb_Table rentTable, Reldb_Table ratingTable) {
        col_users.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get(1));
        table_users.getItems().addAll(userTable.getRows());
    }

    @FXML
    private void on_addUser(ActionEvent event) {
        Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection());
       
        statement.executeUpdate("INSERT INTO customer (name) VALUES ('new User')");
        statement.close();
    }

    void setStage(Stage stage) {
        this.stage = stage;
    }
    
}
