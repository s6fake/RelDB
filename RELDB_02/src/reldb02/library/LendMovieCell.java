package reldb02.library;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
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
                button.setDisable(!checkAvailability(Integer.parseInt(selectedMovie.get("ID").getValue())));
                if (!button.isDisabled()) {
                    Library.getInstance().showInterface(selectedMovie);
                } else {
                    selectedMovie.getCellByColumn("available").setData("FALSE");
                    button.setTooltip(notAvailable);
                }
            }
        });
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            button.setDisable(!(getItem() != null ? getItem() : false));
            if (button.isDisable()) {
                button.setTooltip(notAvailable);
            } else
            {
                button.setTooltip(null);
            }
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(paddedButton);
        } else {
            setGraphic(null);
        }
    }

    private boolean checkAvailability(int title_id) {
        Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection());
        ResultSet results = statement.executeQuery("SELECT DISTINCT * FROM TITLE_RENT WHERE return_date IS NULL AND MOVIE_ID = " + title_id);
        try {
            if (results.next()) {
                results.close();
                statement.close();
                return false;
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                results.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }
        statement.close();
        return true;
    }
}
