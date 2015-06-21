/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author s6fake
 */
public class Select_containerController implements Initializable {

    @FXML
    private GridPane gridpane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addElemenet().setVisbility(false);
    }

    @Deprecated
    public Select_elementController addElemenet() {
        Select_elementController controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("select_element.fxml"));
            Parent item = loader.load();
            int children = gridpane.getChildren().size();
            gridpane.add(item, 0, children);
            controller = loader.<Select_elementController>getController();
            //controller.setParent(this);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return controller;
    }

}
