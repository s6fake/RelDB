package reldb02.library;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javax.swing.JOptionPane;
import reldb.lib.database.Reldb_Row;

/**
 *
 * @author Fabo
 */
public class LendMovieCell extends TableCell<Reldb_Row, Boolean> {

    final Button addButton = new Button("Lend");
    final StackPane paddedButton = new StackPane();

    public LendMovieCell(final TableView table) {
        paddedButton.setPadding(new Insets(3));
        paddedButton.getChildren().add(addButton);

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                /*JOptionPane.showMessageDialog(null, "Du kannst diesen Film noch nicht ausleihen!\n Die Bibliothek wird aber erstellt.",
                        "Inane warning",
                        JOptionPane.WARNING_MESSAGE);*/
                Library.getInstance().showInterface();
            }
        });

    }
    /**
     * Button in der Tabelle anzeigen
     * @param item
     * @param empty 
     */
    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(paddedButton);
        } else {
            setGraphic(null);
        }
    }

}
