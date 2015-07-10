package reldb02.ui;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import reldb.StringClass;
import reldb.lib.database.Reldb_Row;
import reldb.lib.database.Reldb_Table;
import reldb.lib.sql.Reldb_Statement;
import reldb02.library.Library;

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
    private TableView<StringClass> tableView_books;
    @FXML
    private TableColumn<StringClass, String> column_tableView_books;
    @FXML
    private TableView<Reldb_Row> tableView_cast;
    @FXML
    private TableColumn<Reldb_Row, String> col_charName;
    @FXML
    private TableColumn<Reldb_Row, String> col_person;
    @FXML
    private TableColumn<Reldb_Row, String> col_roleType;
    @FXML
    private TableColumn<Reldb_Row, String> col_note;
    @FXML
    private TableView<Reldb_Row> tableView_companies;
    @FXML
    private TableColumn<Reldb_Row, String> col_company_name;
    @FXML
    private TableColumn<Reldb_Row, String> col_company_type;
    @FXML
    private TableColumn<Reldb_Row, String> col_company_note;
    @FXML
    private TableView<Reldb_Row> tableView_linked_movies;
    @FXML
    private TableColumn<Reldb_Row, String> col_linkType;
    @FXML
    private TableColumn<Reldb_Row, String> col_title;
    @FXML
    private TableColumn<Reldb_Row, String> col_year;
    @FXML
    private TableColumn<Reldb_Row, String> col_kind;
    @FXML
    private Button btn_plots;
    @FXML
    private Button btn_lend;

    private static final Logger log = Logger.getLogger(TitleDetailsController.class.getName());
    private int id;
    private String name;

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
        tableView_books.setPlaceholder(new Label("No Books"));
        tableView_runtime.setPlaceholder(new Label("No Runtimes"));
        tableView_companies.setPlaceholder(new Label("No Companies"));
        tableView_linked_movies.setPlaceholder(new Label("No Linked Movies"));
        tableView_cast.setPlaceholder(new Label("Nobody have ever been seen in this movie..."));

        column_tableView_books.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getString()));
        column_tableView_runtime.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getString()));

        col_charName.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("CHARACTER_NAME"));
        col_person.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("PERSON"));
        col_roleType.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("ROLE_TYPE"));
        col_note.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("NOTE"));

        col_company_name.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("COMPANY_NAME"));
        col_company_type.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("COMPANY_TYPE"));
        col_company_note.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("NOTE"));

        col_linkType.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("LINK_TYPE"));
        col_title.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("TITLE"));
        col_year.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("YEAR"));
        col_kind.setCellValueFactory((TableColumn.CellDataFeatures<Reldb_Row, String> p) -> p.getValue().get("KIND"));
        // Zum öffnen der Detailansicht
        tableView_linked_movies.setRowFactory(p -> {
            TableRow<Reldb_Row> row = new TableRow();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    TitleDetailsController.makeController().initialize(Integer.parseInt(row.getItem().get("ID").getValueSafe()), row.getItem().get("Title").getValueSafe());
                }
            });
            return row;
        });
    }

    public void initialize(int title_id, String title) {
        id = title_id;
        name = title;
        Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection());

        // try {
        String releaseDate = getReleaseDate(statement, title_id);
        String yearString = releaseDate.isEmpty() ? "" : "(" + releaseDate.split(" ")[releaseDate.split(" ").length - 1] + ")";
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
        tableView_runtime.setItems(runtimes);

        txt_rating.setText(getRating(statement, title_id));
        textfield_plot.setText(getFirstPlot(statement, title_id));
        if (textfield_plot.getText().isEmpty()) {
            btn_plots.setDisable(true);
        }

        txt_budget.setText(getBudget(statement, title_id));

        ObservableList<StringClass> books = getBooks(statement, title_id);

        tableView_books.setItems(books);

        tableView_cast.getItems().addAll(getCast(statement, title_id).getRows());

        tableView_linked_movies.getItems().addAll(getLinks(statement, title_id).getRows());

        tableView_companies.getItems().addAll(getCompanies(statement, title_id).getRows());

        btn_lend.setDisable(!checkAvailability(statement, title_id));

        /*
         } catch (Exception ex) {
         log.log(Level.WARNING, ex.getMessage());
         }*/
        statement.close();

        Pane header = (Pane) tableView_runtime.lookup("TableHeaderRow");
        header.setMaxHeight(0);
        header.setMinHeight(0);
        header.setPrefHeight(0);
        header.setVisible(false);

        header = (Pane) tableView_books.lookup("TableHeaderRow");
        header.setMaxHeight(0);
        header.setMinHeight(0);
        header.setPrefHeight(0);
        header.setVisible(false);

    }

    private String getReleaseDate(Reldb_Statement statement, int title_id) {
        String releaseDate = "";
        ResultSet release_dates = statement.executeQuery("SELECT mi.info AS INFO FROM IMDB.info_type it JOIN IMDB.movie_info mi ON mi.info_type_id = it.id WHERE it.ID = 16 AND mi.movie_id = " + title_id);

        try {
            while (release_dates.next()) {
                if (release_dates.getString(1).contains("Germany")) {
                    //System.out.println(release_dates.getString(1));
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
                    //System.out.println(certificateResults.getString(1));
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
        generes = generes.isEmpty() ? "" : generes.substring(0, generes.length() - 1);
        return generes;
    }

    private String getKind(Reldb_Statement statement, int title_id) {
        String kind = "";
        ResultSet kindInfo = statement.executeQuery("SELECT DECODE (kt.kind, 'episode', (SELECT CONCAT(CONCAT('episode of \"', t2.title),'\"') FROM IMDB.title t2 WHERE t2.id = t.episode_of_id), kt.kind) \"result\" \n"
                + "FROM IMDB.kind_type kt JOIN IMDB.title t ON t.kind_id = kt.id WHERE t.id = " + title_id);
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
            if (results.next()) {
                rating = results.getDouble("Rating");
                votes = results.getLong("Votes");
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

        return String.valueOf(rating) + " (average of " + votes + ")";
    }

    private String getFirstPlot(Reldb_Statement statement, int title_id) {
        String plot = "";
        ResultSet results = statement.executeQuery("SELECT mi.info AS INFO FROM IMDB.movie_info mi WHERE mi.info_type_id = 98 AND mi.movie_id = " + title_id);
        try {
            if (results.next()) {
                plot = results.getString(1);
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
        return plot;
    }

    private String getBudget(Reldb_Statement statement, int title_id) {
        String budget = "";
        ResultSet results = statement.executeQuery("SELECT mi.info AS INFO FROM IMDB.movie_info mi WHERE mi.info_type_id = 105 AND mi.movie_id = " + title_id);
        try {
            if (results.next()) {
                budget = results.getString(1);
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
        return budget;
    }

    private ObservableList<StringClass> getBooks(Reldb_Statement statement, int title_id) {
        ObservableList<StringClass> books = FXCollections.observableArrayList();
        ResultSet results = statement.executeQuery("SELECT mi.info AS INFO FROM IMDB.movie_info mi WHERE mi.info_type_id = 92 AND mi.movie_id = " + title_id);

        try {
            while (results.next()) {
                String info = results.getString("INFO");
                books.add(new StringClass(info));
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
        return books;
    }

    private Reldb_Table getCast(Reldb_Statement statement, int title_id) {
        String[] titleCols = {"ID", "CHARACTER_NAME", "PERSON", "ROLE_TYPE", "NOTE"};
        Reldb_Table castTable = new Reldb_Table("Cast", titleCols);
        ResultSet results = statement.executeQuery("SELECT ci.person_id AS ID, cn.name AS CHARACTER_NAME, n.name AS PERSON, rt.role AS ROLE_TYPE, ci.note AS NOTE\n"
                + "FROM IMDB.name n JOIN IMDB.cast_info ci ON ci.person_id = n.id \n"
                + "LEFT OUTER JOIN IMDB.char_name cn ON ci.person_role_id = cn.id \n"
                + "JOIN IMDB.role_type rt ON ci.role_id = rt.id\n"
                + "WHERE ci.movie_id = " + title_id + " ORDER BY ci.nr_order");
        castTable.addRowsAndClose(results);
        return castTable;
    }

    private Reldb_Table getLinks(Reldb_Statement statement, int title_id) {
        String[] titleCols = {"ID", "LINK_TYPE", "TITLE", "YEAR", "KIND"};
        Reldb_Table linkTable = new Reldb_Table("Links", titleCols);
        ResultSet results = statement.executeQuery("SELECT t.id AS ID, lt.link AS LINK_TYPE, t.title AS TITLE, t.production_year AS YEAR, kt.kind AS KIND \n"
                + "FROM IMDB.MOVIE_LINK ml JOIN IMDB.LINK_TYPE lt ON ml.link_type_id = lt.id \n"
                + "JOIN IMDB.title t ON ml.linked_movie_id = t.id \n"
                + "JOIN IMDB.kind_type kt ON t.kind_id = kt.id\n"
                + "WHERE ml.movie_id = " + title_id);
        linkTable.addRowsAndClose(results);
        return linkTable;
    }

    private Reldb_Table getCompanies(Reldb_Statement statement, int title_id) {
        String[] titleCols = {"COMPANY_NAME", "COMPANY_TYPE", "NOTE"};
        Reldb_Table companiesTable = new Reldb_Table("Companies", titleCols);
        ResultSet results = statement.executeQuery("SELECT cn.name AS COMPANY_NAME, ct.kind AS COMPANY_TYPE, mc.note AS NOTE\n"
                + "FROM IMDB.MOVIE_COMPANIES mc JOIN IMDB.COMPANY_NAME CN ON mc.company_id = cn.id\n"
                + "JOIN IMDB.COMPANY_TYPE ct ON mc.company_type_id = ct.id\n"
                + "WHERE mc.movie_id = " + title_id);
        companiesTable.addRowsAndClose(results);
        return companiesTable;
    }

    private boolean checkAvailability(Reldb_Statement statement, int title_id) {
        ResultSet results = statement.executeQuery("SELECT DISTINCT * FROM TITLE_RENT WHERE return_date IS NULL AND MOVIE_ID = " + title_id);
        try {
            if (results.next()) {
                results.close();
                return false;
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
        return true;
    }

    @FXML
    private void on_fullPlots(ActionEvent event) {
        PlotViewController.makeController().initialize(id, name);
    }

    @FXML
    private void on_lend(ActionEvent event) {
        Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection());
        btn_lend.setDisable(!checkAvailability(statement, id));
        if (!btn_lend.isDisable()) {
            String[] titleCols = {"id", "title", "production_year", "kind", "episode_of", "available"};
            Reldb_Table titleTable = new Reldb_Table("Title", titleCols);
            ResultSet results = statement.executeQuery("SELECT t.id, t.title, t.production_year, k.kind, t2.title AS episode_of, CASE WHEN (tr.movie_id > 0) THEN 'FALSE' ELSE 'TRUE' END AS available\n"
                    + "FROM IMDB.title t JOIN IMDB.kind_type k ON t.kind_id = k.id \n"
                    + "LEFT OUTER JOIN (SELECT DISTINCT movie_id FROM TITLE_RENT WHERE return_date IS NULL) tr ON tr.movie_id = t.id\n"
                    + "LEFT OUTER JOIN IMDB.title t2 ON t.episode_of_id = t2.id WHERE t.id = " + id);
            titleTable.addRowsAndClose(results);
            Reldb_Row selectedMovie = titleTable.getRows().get(0);

            Library.getInstance().showInterface(selectedMovie);
        }
        statement.close();
    }
}
