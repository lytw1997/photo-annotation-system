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
//  Main class to create JavaFX application
public class WIG3003_GroupProject extends Application {
    
    //  Entry point to start JavaFX application
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("--> Start: " + Thread.currentThread().getName());
        
        //  FXMLLoader to load the fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root = loader.load();
        
        //  Get the main.fxml controller
        MainController controller = loader.getController();
        
        //  Pass the stage object into controller
        controller.setStage(stage);
        
        //  Instantiate DatabaseHelper
        DatabaseHelper db = DatabaseHelper.getInstance();
        
        //  Exit the program if no connection
        if(db.getConn() == null) {
            System.exit(1);
        }
        System.out.println(db.getConn());
        db.createTable();
        
        //  Setup the scene with main interface
        Scene scene = new Scene(root);
        
        //  Setup listener when the window is shown
        stage.setOnShown((WindowEvent event) -> {
            System.out.println("setuplisterner");
            controller.setListener();
        });
        
        //  Setup the window title and scene
        stage.setTitle("Photo Annotation Collection");
        stage.setScene(scene);
        
        //  Show the window
        stage.show();
    }

    //  Launch the JavaFX application
    public static void main(String[] args) {
        launch(args);
    }
}
