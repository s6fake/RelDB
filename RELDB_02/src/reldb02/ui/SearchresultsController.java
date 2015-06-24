/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb02.ui;

import com.sun.javafx.collections.ElementObservableListDecorator;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import reldb.StringClass;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class SearchresultsController implements Initializable {

    private static final Logger log = Logger.getLogger(SearchresultsController.class.getName());

    @FXML
    private Tab tab_titles;
    @FXML
    private TableView table_titles;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    void initialize(ResultSet resultsTitle) {

        List<String> rows = new ArrayList();
        try {
            while (resultsTitle.next()) {
                for (int i = 1; i <= 3; i++) {
                    rows.add(resultsTitle.getString(i));
                    System.out.print(resultsTitle.getString(i) + " ");
                }
                System.out.println();
                //row.add(resultsTitle.getString("year"));
                //row.add(resultsTitle.getString("kind_id"));

            }
        } catch (SQLException ex) {
            log.log(Level.WARNING, ex.getMessage());
        } finally {
            try {
                resultsTitle.close();
            } catch (SQLException ex) {
                log.log(Level.WARNING, ex.getMessage());
            }
        }

        //table_titles.setItems(FXCollections.observableList(rows));
        //table_titles.getItems().addAll(titledata);
    }

    void initializeArray(String[] titles, ResultSet resultSet, int titleCount) {
        if (titleCount == 0) {
            tab_titles.setDisable(true);
            return;
        }
        String[][] dataArray = convertTo2dArray(titles, resultSet, titleCount);
        ObservableList<String[]> data = FXCollections.observableArrayList();
        data.addAll(Arrays.asList(dataArray));
        data.remove(0);//remove titles from data
        for (int i = 0; i < titles.length; i++) {
            TableColumn tc = new TableColumn(dataArray[0][i]);

            final int colNo = i;
            tc.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
                    return new SimpleStringProperty((p.getValue()[colNo]));
                }
            });
            //tc.setPrefWidth(90);
            table_titles.getColumns().add(tc);
        }
        table_titles.setItems(data);
    }

    private String[][] convertTo2dArray(String[] titles, ResultSet results, int size) {
        String[][] rows;
        rows = new String[size + 1][titles.length];
        int colCount = titles.length;
        rows[0] = titles;

        int rowCount = 1;
        try {
            while (results.next()) {
                String[] row = new String[colCount];
                for (int i = 0; i < colCount; i++) {
                    row[i] = (results.getString(i + 1));
                    System.out.print(results.getString(i + 1) + " ");
                }
                rows[rowCount] = row;
                rowCount++;
                System.out.println();
                //row.add(resultsTitle.getString("year"));
                //row.add(resultsTitle.getString("kind_id"));

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
        /*
         String[][] array = new String[colCount][rows.size()];
         for (int i = 0; i < colCount - 1; i++) {
         for (int j = 0; j < rows.size() - 1 ; j++) {
         array[j][i] = rows.get(j)[i];
         }
         }
         */
        return rows;
    }
}
