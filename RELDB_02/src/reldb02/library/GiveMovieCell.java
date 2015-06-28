package reldb02.library;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import reldb.lib.database.Reldb_Row;

/**
 *
 * @author Fabo
 */
public class GiveMovieCell extends ButtonCell {

    
    public GiveMovieCell(TableView<Reldb_Row> table, String text) {
        super(table, text);

        
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {                
                table.getSelectionModel().select(getIndex());
                Reldb_Row selectedPerson = table.getSelectionModel().getSelectedItem();
                Library.getInstance().giveMovieTo(selectedPerson);
                
            }
        });
    }

}
