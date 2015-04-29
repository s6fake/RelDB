/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reldb.ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import reldb.Placeholder;
import reldb.ui.dialogs.Alerts;

/**
 *
 * @author s6fake
 */
public class RELDB_01 extends Application {

    private Pair<String, String> loginData = null;

    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Test Connection");
        btn.setId("1");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (event.getSource().getClass() == Button.class) {
                    Button tButton = (Button) (event.getSource());
                    int btnID = 0;
                    try {
                        btnID = Integer.parseInt(tButton.getId());
                    } catch (NumberFormatException e) {
                        System.out.println(e);

                    }

                    switch (btnID) {
                        case 1:
                            //Alerts.Information("HEY DU!", null);
                            loginData = Alerts.UserPassword();
                            if (loginData != null) {
                                //TODO - Hier muss ein neuer Thread erzeugt werden
                            Placeholder.main(loginData.getKey(),loginData.getValue());
                            }
                            break;
                        default:
                            break;
                    }

                }

            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);

        // String input = Dialogs.showInputDialog(scene, "Please enter your name:", "Input Dialog", "title");
    }

}
