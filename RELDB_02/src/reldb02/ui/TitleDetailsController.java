package reldb02.ui;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import reldb.StringClass;
import reldb.lib.sql.Reldb_Statement;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class TitleDetailsController implements Initializable {

    @FXML
    private Label label_details;
    @FXML
    private Label label_detailsSub;
    @FXML
    private Label label_tagline;
    @FXML
    private TableView<StringClass> tableView_runtime;
    @FXML
    private TableColumn<StringClass, String> column_tableView_runtime;
    @FXML
    private TextField txt_releaseDate;
    @FXML
    private TextField txt_rating;
    @FXML
    private TextField txt_budget;
    @FXML
    private TextArea textfield_plot;
    @FXML
    private TableView<String> tableView_books;
    @FXML
    private TableView<String> tableView_cast;

    private static final Logger log = Logger.getLogger(TitleDetailsController.class.getName());

    public static TitleDetailsController makeController() {
        TitleDetailsController controller = null;
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(TitleDetailsController.class.getResource("titleDetails.fxml"));

            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Title Details");

            // Controller initialisieren
            controller = loader.<TitleDetailsController>getController();
            //stage.initOwner(parent);

            // Hauptfenster anzeigen
            stage.show();
        } catch (IOException ex) {
            log.log(Level.SEVERE, ex.getMessage());
        }
        return controller;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void initialize(int title_id, String title) {
        Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection());

        try {
            String releaseDate = getReleaseDate(statement, title_id);
            String yearString = (releaseDate.isEmpty()) ? "" : "(" + releaseDate.split(" ")[releaseDate.split(" ").length - 1] + ")";
            label_details.setText("Details for \"" + title + "\"" + yearString);
            txt_releaseDate.setText(releaseDate);

            String certificate = getCertificate(statement, title_id);
            String certificateString = (certificate.isEmpty()) ? "" : "Rated for age " + certificate + " (Germany)";
            String generes = getGeneres(statement, title_id);
            String generesString = generes.isEmpty() ? "" : certificateString.isEmpty() ? generes : " | " + generes;
            String kind = getKind(statement, title_id);
            String kindString = kind.isEmpty() ? "" : generesString.isEmpty() ? kind : " | " + kind;
            label_detailsSub.setText(certificateString + generesString + kindString);

            List<String> tags = getTags(statement, title_id);
            label_tagline.setText(tags.isEmpty() ? "" : tags.get(new Random().nextInt(tags.size())));

            ObservableList<StringClass> runtimes = getRuntimes(statement, title_id);
            //column_tableView_runtime.setCellValueFactory(null);
            column_tableView_runtime.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getString()));
            tableView_runtime.setItems(runtimes);// getItems().setAll(runtimes);

            txt_rating.setText(getRating(statement, title_id));
            textfield_plot.setText(getFirstPlot(statement, title_id));
            
            txt_budget.setText(getBudget(statement, title_id));
            
        } catch (Exception ex) {
            log.log(Level.WARNING, ex.getMessage());
        }
        statement.close();
    }

    private String getReleaseDate(Reldb_Statement statement, int title_id) {
        String releaseDate = "";
        ResultSet release_dates = statement.executeQuery("SELECT mi.info AS INFO FROM IMDB.info_type it JOIN IMDB.movie_info mi ON mi.info_type_id = it.id WHERE it.ID = 16 AND mi.movie_id = " + title_id);

        try {
            while (release_dates.next()) {
                if (release_dates.getString(1).contains("Germany")) {
                    System.out.println(release_dates.getString(1));
                    releaseDate = release_dates.getString(1).split("Germany:")[release_dates.getString(1).split("Germany:").length - 1];
                    break;
                }
            }
        } catch (SQLException ex) {
            log.log(Level.WARNING, ex.getMessage());
        } finally {
            try {
                release_dates.close();
            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
        return releaseDate;
    }

    private String getCertificate(Reldb_Statement statement, int title_id) {
        String certificate = "";
        ResultSet certificateResults = statement.executeQuery("SELECT mi.info AS INFO FROM IMDB.info_type it JOIN IMDB.movie_info mi ON mi.info_type_id = it.id WHERE it.ID = 5 AND mi.movie_id = " + title_id);

        try {
            while (certificateResults.next()) {
                if (certificateResults.getString(1).contains("Germany")) {
                    System.out.println(certificateResults.getString(1));
                    certificate = certificateResults.getString(1).split("Germany:")[certificateResults.getString(1).split("Germany:").length - 1];
                    break;
                }
            }
        } catch (SQLException ex) {
            log.log(Level.WARNING, ex.getMessage());
        } finally {
            try {
                certificateResults.close();
            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
        return certificate;
    }

    private String getGeneres(Reldb_Statement statement, int title_id) {
        String generes = "";

        ResultSet generesResults = statement.executeQuery("SELECT mi.info AS INFO FROM IMDB.info_type it JOIN IMDB.movie_info mi ON mi.info_type_id = it.id WHERE it.ID = 3 AND mi.movie_id = " + title_id);
        try {
            while (generesResults.next()) {
                generes = generes + generesResults.getString(1) + " ";
            }

        } catch (SQLException ex) {
            log.log(Level.WARNING, ex.getMessage());
        } finally {
            try {
                generesResults.close();
            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
        generes = generes.substring(0, generes.length() - 1);
        return generes;
    }

    private String getKind(Reldb_Statement statement, int title_id) {
        String kind = "";
        ResultSet kindInfo = statement.executeQuery("SELECT kt.kind FROM IMDB.kind_type kt JOIN IMDB.title t ON t.kind_id = kt.id WHERE t.id = " + title_id);

        try {
            kindInfo.next();
            kind = kindInfo.getString(1);

        } catch (SQLException ex) {
            log.log(Level.WARNING, ex.getMessage());
        } finally {
            try {
                kindInfo.close();
            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
        return kind;
    }

    private List<String> getTags(Reldb_Statement statement, int title_id) {
        List<String> tags = new ArrayList();
        ResultSet tagsResult = statement.executeQuery("SELECT mi.info AS INFO FROM IMDB.info_type it JOIN IMDB.movie_info mi ON mi.info_type_id = it.id WHERE it.ID = 9 AND mi.movie_id = " + title_id);

        try {
            while (tagsResult.next()) {
                tags.add(tagsResult.getString(1));
            }
        } catch (SQLException ex) {
            log.log(Level.WARNING, ex.getMessage());
        } finally {
            try {
                tagsResult.close();
            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
        return tags;
    }

    private ObservableList<StringClass> getRuntimes(Reldb_Statement statement, int title_id) {
        ObservableList<StringClass> runtimes = FXCollections.observableArrayList();
        ResultSet results = statement.executeQuery("SELECT mi.info AS INFO, mi.note AS NOTE FROM IMDB.info_type it JOIN IMDB.movie_info mi ON mi.info_type_id = it.id WHERE it.ID = 1 AND mi.movie_id = " + title_id);

        try {
            while (results.next()) {
                String info = results.getString("INFO");
                String note = results.getString("NOTE") == null ? "(normal version)" : results.getString("NOTE");
                runtimes.add(new StringClass(info + " " + note));
            }
        } catch (SQLException ex) {
            log.log(Level.WARNING, ex.getMessage());
        } finally {
            try {
                results.close();
            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
        return runtimes;
    }

    private String getRating(Reldb_Statement statement, int title_id) {
        ResultSet results = statement.executeQuery("SELECT miia.info AS Rating, miib.votes AS Votes FROM IMDB.movie_info_idx miia JOIN IMDB.movie_info_idx miib ON MIIA.MOVIE_ID = MIIB.MOVIE_ID WHERE MIIA.INFO_TYPE_ID = 101 AND MIIB.INFO_TYPE_ID = 100 AND MIIA.movie_id = " + title_id);
        double rating = 0;
        long votes = 0;
        try {
            results.next();
            rating = results.getDouble("Rating");
            votes = results.getLong("Votes");

        } catch (SQLException ex) {
            log.log(Level.WARNING, ex.getMessage());
        } finally {
            try {
                results.close();
            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }

        return String.valueOf(rating) + " (average of " + votes + ")";
    }

    private String getFirstPlot(Reldb_Statement statement, int title_id) {
        String plot = "";
        ResultSet results = statement.executeQuery("SELECT mi.info AS INFO FROM IMDB.movie_info mi WHERE mi.info_type_id = 98 AND mi.movie_id = " + title_id);
        try {
            results.next();
            plot = results.getString(1);

        } catch (SQLException ex) {
            log.log(Level.WARNING, ex.getMessage());
        } finally {
            try {
                results.close();
            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
        return plot;
    }

    private String getBudget(Reldb_Statement statement, int title_id) {
        String budget = "";
        ResultSet results = statement.executeQuery("SELECT mi.info AS INFO FROM IMDB.movie_info mi WHERE mi.info_type_id = 105 AND mi.movie_id = " + title_id);
        try {
            results.next();
            budget = results.getString(1);

        } catch (SQLException ex) {
            log.log(Level.WARNING, ex.getMessage());
        } finally {
            try {
                results.close();
            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }
        return budget;
    }
}
