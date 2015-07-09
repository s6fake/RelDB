package reldb02.library;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import reldb.lib.database.Reldb_Row;

/**
 *
 * @author s6fake
 */
public class ReturnMovieCell extends ButtonCell {

    public ReturnMovieCell(TableView<Reldb_Row> table, String text) {
        super(table, text);
        
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                table.getSelectionModel().select(getIndex());
                Reldb_Row selectedMovie = table.getSelectionModel().getSelectedItem();
                if (Library.getInstance().returnMovie(selectedMovie)) {
                    setDisable(true);
                }
            }
        });
    }    
}
