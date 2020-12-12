/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author lytw1
 */
public class WIG3003_GroupProject extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("--> Start: " + Thread.currentThread().getName());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setStage(stage);
        DatabaseHelper db = DatabaseHelper.getInstance();
        if(db.getConn() == null) {
            System.exit(1);
        }
        System.out.println(db.getConn());
        db.createTable();
        Scene scene = new Scene(root);
        stage.setOnShown((WindowEvent event) -> {
            System.out.println("setuplisterner");
            controller.setListener();
        });
        stage.setTitle("Photo Annotation Collection");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
