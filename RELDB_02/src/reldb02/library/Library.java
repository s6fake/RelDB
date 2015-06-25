package reldb02.library;

import reldb.lib.Reldb_Connection;
import reldb.lib.database.Reldb_Column;
import reldb.lib.database.Reldb_Database;
import reldb.lib.database.Reldb_Table;
import reldb.lib.sql.Reldb_Statement;
import reldb.lib.sql.sql_expr;
import reldb02.ui.RELDB_02;

/**
 *
 * @author Fabo
 */
public class Library {

    //Singleton Instanz
    private static Library library = null;
    private final Reldb_Connection connection;
    private final Reldb_Table customers;
    private final Reldb_Table title_rating;
    private final Reldb_Table title_rent;

    private Library() {
        connection = RELDB_02.getConnection();

        customers = new Reldb_Table("customer");
        title_rating = new Reldb_Table("title_rating");
        title_rent = new Reldb_Table("title_rent");
        initializeTables();
    }

    private void initializeTables() {
        Reldb_Column c_id = new Reldb_Column(connection.getDatabase(), customers, "id", 4, 0, false, true);
        customers.addPrimaryKeyColumn(c_id);
        Reldb_Column c_name = new Reldb_Column(connection.getDatabase(), customers, "name", 12, 45, false, false);
        customers.addColumn(c_name);
        Reldb_Column c_birthdate = new Reldb_Column(connection.getDatabase(), customers, "birthdate", 91, 0, true, false);
        customers.addColumn(c_birthdate);
        Reldb_Column c_street = new Reldb_Column(connection.getDatabase(), customers, "street", 12, 50, true, false);
        customers.addColumn(c_street);
        Reldb_Column c_city = new Reldb_Column(connection.getDatabase(), customers, "city", 12, 50, true, false);
        customers.addColumn(c_city);
        Reldb_Column c_postcode = new Reldb_Column(connection.getDatabase(), customers, "postcode", 1, 5, true, false);
        customers.addColumn(c_postcode);

        Reldb_Statement statement = new Reldb_Statement(connection);
        // Grant Access
        //statement.execute("GRANT REFERENCES ON 'IMDB.");
        statement.execute(sql_expr.createTable(customers, Reldb_Database.DATABASETYPE.ORACLE));

        Reldb_Column re_id = new Reldb_Column(connection.getDatabase(), title_rent, "id" , 4, 0 , false, true);
        title_rent.addPrimaryKeyColumn(re_id);
        Reldb_Column re_customer_id = new Reldb_Column(connection.getDatabase(), title_rent, "customer_id" , 4, 0 , false, false);
        re_customer_id.addForeignKey("customers", "id", 0);
        title_rent.addColumn(re_customer_id);
        Reldb_Column re_movie_id = new Reldb_Column(connection.getDatabase(), title_rent, "movie_id" , 4, 0 , false, false);
        title_rent.addColumn(re_movie_id);
        Reldb_Column re_rent_date = new Reldb_Column(connection.getDatabase(), title_rent, "rent_date" , 91, 0 , false, false);
        title_rent.addColumn(re_rent_date);
        Reldb_Column re_return_date = new Reldb_Column(connection.getDatabase(), title_rent, "return_date" , 91, 0 , true, false);
        title_rent.addColumn(re_return_date);
        Reldb_Column re_note = new Reldb_Column(connection.getDatabase(), title_rent, "note" , 12, 255 , true, false);
        title_rent.addColumn(re_note);
        
        statement.execute(sql_expr.createTable(title_rent, Reldb_Database.DATABASETYPE.ORACLE));
        
        Reldb_Column ra_id = new Reldb_Column(connection.getDatabase(), title_rating, "id", 4, 0, false, true);
        title_rating.addPrimaryKeyColumn(ra_id);
        Reldb_Column ra_customer_id = new Reldb_Column(connection.getDatabase(), title_rating, "customer_id", 4, 0, false, false);
        ra_customer_id.addForeignKey("customers", "id", 0);
        title_rating.addColumn(ra_customer_id);
        Reldb_Column ra_movie_id = new Reldb_Column(connection.getDatabase(), title_rating, "movie_id", 4, 0, false, false);
        title_rating.addColumn(ra_movie_id);
        Reldb_Column ra_rating = new Reldb_Column(connection.getDatabase(), title_rating, "rating", 4, 0, false, false);
        title_rating.addColumn(ra_rating);
        Reldb_Column ra_note = new Reldb_Column(connection.getDatabase(), title_rating, "note", 12, 255, true, false);
        title_rating.addColumn(ra_note);
        
        statement.execute(sql_expr.createTable(title_rating, Reldb_Database.DATABASETYPE.ORACLE));
        
        statement.close();

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

        library = null;
    }
}
