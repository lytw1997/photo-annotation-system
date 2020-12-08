/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

import javafx.application.Application;
import javafx.event.EventHandler;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setStage(stage);
        DatabaseHelper db = DatabaseHelper.getInstance();
        if(db.getConn() == null) {
            System.exit(1);
        }
        System.out.println(db.getConn());
        db.createTable();
        stage.setOnShown(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                controller.setImages();
            }
        });
        Scene scene = new Scene(root);
        stage.setTitle("Photo Annotation Collection");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
