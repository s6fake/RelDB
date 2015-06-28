package reldb02.library;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import reldb.lib.database.Reldb_Row;

/**
 *
 * @author Fabo
 */
public class LendMovieCell extends ButtonCell {

    private final Tooltip notAvailable = new Tooltip("This movie is currently not available");

    public LendMovieCell(TableView<Reldb_Row> table, String text) {
        super(table, text);

        //button.visibleProperty().bind(new SimpleBooleanProperty(getItem() != null ? getItem() : false));
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
}
