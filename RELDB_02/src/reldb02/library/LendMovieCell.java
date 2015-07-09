package reldb02.library;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import reldb.lib.database.Reldb_Row;
import reldb.lib.sql.Reldb_Statement;
import reldb02.ui.RELDB_02;

/**
 *
 * @author Fabo
 */
public class LendMovieCell extends ButtonCell {

    private final Tooltip notAvailable = new Tooltip("This movie is currently not available");

    public LendMovieCell(TableView<Reldb_Row> table, String text) {
        super(table, text);

        hackTooltipStartTiming(notAvailable);
        
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                table.getSelectionModel().select(getIndex());
                Reldb_Row selectedMovie = table.getSelectionModel().getSelectedItem();
                Library.getInstance().showInterface(selectedMovie);
            }
        });
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            button.setDisable(!(getItem() != null ? getItem() : false));
            if (button.isDisable()) {
                this.setTooltip(notAvailable);
            }
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(paddedButton);
        } else {
            setGraphic(null);
        }
    }
    
    private void checkAvailability(Reldb_Row selectedMovie) {
        Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection());
        
        statement.close();
    }
}
