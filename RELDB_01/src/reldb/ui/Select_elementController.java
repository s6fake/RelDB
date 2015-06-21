package reldb.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import reldb.lib.database.Reldb_Column;
import reldb.ui.dialogs.NewFilterDialogController;
import reldb.lib.migration.Filter;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class Select_elementController implements Initializable {

    private NewFilterDialogController parent;
    @FXML
    private ChoiceBox<String> choiceBox_con;
    @FXML
    private ChoiceBox<String> choiceBox_op;
    @FXML
    private TextField text_restriction;
    @FXML
    private Button btn_add_remove;

    private String lastValue = "";        // Der Wert der im TextField steht.
    private boolean valid = false;     // Gibt an, ob der Wert hinzugefügt wurde.
    private boolean firstItem = false;      // Wichtig für die Verknüpfung
    private Node node;

    private Reldb_Column column;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        text_restriction.setPromptText("Enter a value...");
        text_restriction.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                //Nothing to do here..
            } else {
                if (!validate()) {
                    text_restriction.setText(lastValue);
                }
            }
        });

        choiceBox_con.setItems(observableArrayList(Filter.items_con));
        choiceBox_con.getSelectionModel().selectFirst();

        choiceBox_op.setItems(observableArrayList(Filter.items_op));
        choiceBox_op.getSelectionModel().selectFirst();
    }

    public void setParent(NewFilterDialogController controller) {
        this.parent = controller;
    }

    @FXML
    private void on_add_remove(ActionEvent event) {
        if (isValid()) {      // Muss also wieder entfernt werden.
            parent.removeConstraint(this, node);
            return;
        }
        if (validate()) {
            parent.addConstraint();
            btn_add_remove.setText("Remove");
            valid = true;
        }
    }

    private boolean validate() {
        if (text_restriction.getText().isEmpty()) {
            return false;
        }
        lastValue = text_restriction.getText();
        return true;
    }

    public void setVisbility(boolean visible) {
        choiceBox_con.setVisible(visible);
        firstItem = !visible;
    }

    public void setNode(Node root) {
        this.node = root;
    }

    public void setColumn(Reldb_Column column) {
        this.column = column;
    }

    public Filter getFilter() {
        if (text_restriction.getText().isEmpty()) {
            return null;
        }
        int con = -1;
        if (!firstItem) {
            con = choiceBox_con.getSelectionModel().getSelectedIndex();
        }
        return new Filter(con, choiceBox_op.getSelectionModel().getSelectedIndex(), text_restriction.getText(), column.getName());
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }
}
