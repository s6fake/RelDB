package reldb02.library;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import reldb.lib.Reldb_Connection;
import reldb.lib.database.Reldb_Column;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Row;
import reldb.lib.database.Reldb_Table;
import reldb.lib.sql.Reldb_Statement;
import reldb.lib.sql.sql_expr;
import reldb02.ui.RELDB_02;

/**
 *
 * @author Fabo
 */
public class Library {

    private static final Logger log = Logger.getLogger(Library.class.getName());

    //Singleton Instanz
    private static Library library = null;
    private final Reldb_Connection connection;
    private final Reldb_Table customer, title_rating, title_rent;

    private CustomerInterfaceController interfaceController;
    private Reldb_Row selectedMovie;

    private Library() {
        connection = RELDB_02.getConnection();
        selectedMovie = null;
        customer = new Reldb_Table("customer");
        title_rating = new Reldb_Table("title_rating");
        title_rent = new Reldb_Table("title_rent");
        initializeTables();
        getData();
        initializeInterface();
    }

    /**
     * Erzeugt alle Tabellen für die Videothek
     */
    private void initializeTables() {
        Reldb_Column c_id = new Reldb_Column(connection.getDatabase(), customer, "id", 4, "", 0, false, true);
        customer.addPrimaryKeyColumn(c_id);
        Reldb_Column c_name = new Reldb_Column(connection.getDatabase(), customer, "name", 12, "", 45, false, false);
        customer.addColumn(c_name);
        Reldb_Column c_birthdate = new Reldb_Column(connection.getDatabase(), customer, "birthdate", 91, "", 0, true, false);
        customer.addColumn(c_birthdate);
        Reldb_Column c_street = new Reldb_Column(connection.getDatabase(), customer, "street", 12, "", 50, true, false);
        customer.addColumn(c_street);
        Reldb_Column c_city = new Reldb_Column(connection.getDatabase(), customer, "city", 12, "", 50, true, false);
        customer.addColumn(c_city);
        Reldb_Column c_postcode = new Reldb_Column(connection.getDatabase(), customer, "postcode", 1, "", 5, true, false);
        customer.addColumn(c_postcode);

        Reldb_Statement statement = new Reldb_Statement(connection);
        // Grant Access
        //statement.execute("GRANT REFERENCES ON 'IMDB.");
        //statement.execute("DROP TABLE customer");
        statement.close();
        statement = new Reldb_Statement(connection);
        statement.execute(sql_expr.createTable(customer, Reldb_Database.DATABASETYPE.ORACLE));

        Reldb_Column re_id = new Reldb_Column(connection.getDatabase(), getTitle_rent(), "id", 4, "", 0, false, true);
        getTitle_rent().addPrimaryKeyColumn(re_id);
        Reldb_Column re_customer_id = new Reldb_Column(connection.getDatabase(), getTitle_rent(), "customer_id", 4, "", 0, false, false);
        re_customer_id.addForeignKey("customer", "id", 0);
        getTitle_rent().addColumn(re_customer_id);
        Reldb_Column re_movie_id = new Reldb_Column(connection.getDatabase(), getTitle_rent(), "movie_id", 4, "", 0, false, false);
        getTitle_rent().addColumn(re_movie_id);
        Reldb_Column re_rent_date = new Reldb_Column(connection.getDatabase(), getTitle_rent(), "rent_date", 91, "", 0, false, false);
        getTitle_rent().addColumn(re_rent_date);
        Reldb_Column re_return_date = new Reldb_Column(connection.getDatabase(), getTitle_rent(), "return_date", 91, "", 0, true, false);
        getTitle_rent().addColumn(re_return_date);
        Reldb_Column re_note = new Reldb_Column(connection.getDatabase(), getTitle_rent(), "note", 12, "", 255, true, false);
        getTitle_rent().addColumn(re_note);

        // statement.execute("DROP TABLE title_rent");
        // statement.close();
        // statement = new Reldb_Statement(connection);
        statement.execute(sql_expr.createTable(getTitle_rent(), Reldb_Database.DATABASETYPE.ORACLE));
        statement.execute("ALTER TABLE title_rent ADD " + re_customer_id.getForeignKeyConstructorString(Reldb_Database.DATABASETYPE.ORACLE));

        Reldb_Column ra_id = new Reldb_Column(connection.getDatabase(), getTitle_rating(), "id", 4, "", 0, false, true);
        getTitle_rating().addPrimaryKeyColumn(ra_id);
        Reldb_Column ra_customer_id = new Reldb_Column(connection.getDatabase(), getTitle_rating(), "customer_id", 4, "", 0, false, false);
        ra_customer_id.addForeignKey("customer", "id", 0);
        getTitle_rating().addColumn(ra_customer_id);
        Reldb_Column ra_movie_id = new Reldb_Column(connection.getDatabase(), getTitle_rating(), "movie_id", 4, "", 0, false, false);
        getTitle_rating().addColumn(ra_movie_id);
        Reldb_Column ra_rating = new Reldb_Column(connection.getDatabase(), getTitle_rating(), "rating", 4, "", 0, false, false);
        getTitle_rating().addColumn(ra_rating);
        Reldb_Column ra_note = new Reldb_Column(connection.getDatabase(), getTitle_rating(), "note", 12, "", 255, true, false);
        getTitle_rating().addColumn(ra_note);

        //statement.execute("DROP TABLE title_rating");
        //  statement.close();
        // statement = new Reldb_Statement(connection);
        statement.execute(sql_expr.createTable(getTitle_rating(), Reldb_Database.DATABASETYPE.ORACLE));
        statement.execute("ALTER TABLE title_rating ADD " + ra_customer_id.getForeignKeyConstructorString(Reldb_Database.DATABASETYPE.ORACLE));

        statement.close();

    }

    private void getData() {
        Reldb_Statement statement = new Reldb_Statement(connection);
        customer.addRows(statement.executeQuery("SELECT * FROM customer"));
        title_rating.addRows(statement.executeQuery("SELECT * FROM title_rating"));
        title_rent.addRows(statement.executeQuery("SELECT * FROM title_rent"));
        statement.close();
    }

    private void initializeInterface() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(Library.class.getResource("CustomerInterface.fxml"));

            // Scene aufbauen
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Interface");

            // Controller initialisieren
            interfaceController = loader.<CustomerInterfaceController>getController();
            interfaceController.setStage(stage);
            interfaceController.initialize(customer, title_rent, title_rating);

        } catch (IOException ex) {
            System.err.println("Fehler beim Starten des Kunden Interfaces" + ex.getMessage());
        }
    }

    /**
     * Öffnet das Interface mit der Aussicht, den Ausgewählten Film, an eine
     * Person zu verleihen.
     *
     * @param selectedMovie
     */
    public void showInterface(Reldb_Row selectedMovie) {
        this.selectedMovie = selectedMovie;
        interfaceController.setSelectedMovie(selectedMovie);
        interfaceController.show();
    }

    /**
     * Öffnet das Interface, ohne das ein Film ausgeliehen werden kann
     */
    public void showInterface() {
        this.selectedMovie = null;
        interfaceController.setSelectedMovie(null);
        interfaceController.show();
    }

    /**
     * Einen Film an eine Person ausleihen
     *
     * @param person
     * @return True, wenn der Film ausgeliehen wurde, false sonst.
     */
    public boolean giveMovieTo(Reldb_Row person) {
        String title = selectedMovie.get("title").getValueSafe();
        String name = person.get("name").getValueSafe();
        int dialogResult = JOptionPane.showConfirmDialog(null, "Give " + title + " to " + name + "?", "Question", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            String customer_id = person.get("id").getValueSafe();
            String movie_id = selectedMovie.get("id").getValueSafe();
            Reldb_Statement statement = new Reldb_Statement(connection);
            statement.execute("INSERT INTO title_rent (customer_id, movie_id, rent_date) VALUES (" + customer_id + ", " + movie_id + ", SYSDATE)");
            statement.close();
            interfaceController.pause();
            selectedMovie.getCellByColumn("available").setData("FALSE");
            close();
            return true;
        }
        return false;
    }

    public boolean returnMovie(Reldb_Row movie) {
        String rent_id = movie.get("id").getValueSafe();
        String title = movie.get("movie_name").getValueSafe();
        int dialogResult = JOptionPane.showConfirmDialog(null, "Return " + title + " ?", "Question", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection());
            String command = "UPDATE title_rent SET return_date = SYSDATE WHERE id = " + rent_id;
            System.out.println(command);
            System.out.println(movie.toString());
            statement.executeUpdate(command);
            statement.close();
            return true;
        }
        return false;
    }

    /**
     * Versucht die ID der Ausleihe in der Datenbank zu ermitteln
     *
     * @param customer_id
     * @param rentDate
     * @param returnDate
     * @return
     */
    public int getRentID(String customer_id, java.sql.Date rentDate, java.sql.Date returnDate) {
        int ID = -1;
        Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection(), "SELECT id FROM title_rent WHERE customer_id = " + customer_id + "AND rent_date = ? AND return_date = ?");
        Object[] args = {rentDate, returnDate};
        statement.set(args);
        ResultSet results = statement.executeQuery();

        try {
            results.next();
            ID = results.getInt(1);
        } catch (SQLException ex) {
            log.log(Level.WARNING, ex.getMessage());
        }

        statement.close();
        return ID;
    }

    /**
     * Erzeugt ggf. die Library und gibt diese zurück
     *
     * @return Die Library-Instanz
     */
    public static Library getInstance() {
        if (library == null) {
            library = new Library();
        }
        return library;
    }

    private static void close() {
        try {
            library.interfaceController.pause();
        } catch (Exception e) {
            log.log(Level.INFO, e.getMessage());
        }
        library = null;
    }

    public void addCustomer(Object[] data) {
        Reldb_Statement statement = new Reldb_Statement(RELDB_02.getConnection(), "INSERT INTO customer (name, birthdate, street, city, postcode) VALUES (?, ?, ?, ?, ?)");
        statement.set(data);
        statement.execute();
        statement.close();

        Reldb_Row selectedMovie_old = selectedMovie;

        close();
        Library.getInstance().showInterface(selectedMovie_old);

    }

    /**
     * @return the customers
     */
    public Reldb_Table getCustomer() {
        return customer;
    }

    /**
     * @return the title_rating
     */
    public Reldb_Table getTitle_rating() {
        return title_rating;
    }

    /**
     * @return the title_rent
     */
    public Reldb_Table getTitle_rent() {
        return title_rent;
    }
}
