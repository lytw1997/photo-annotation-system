/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author lytw1
 */
public class ToastController extends HBox {
    
    public static ToastController getInstance(Stage stage) {
        return new ToastController(stage);
    };
    
    @FXML
    private HBox ToastContainer;
    @FXML
    private ImageView ToastIV;
    @FXML
    private Label ToastLB;
    
    private Stage parentStage;
    private Stage toastStage;
    private Parent root;
    
    public ToastController(Stage stage) {
        parentStage = stage;
        toastStage=new Stage();
        toastStage.initOwner(stage);
        toastStage.initStyle(StageStyle.TRANSPARENT);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Toast.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            root = fxmlLoader.load();
            root.setOpacity(0);
        } catch (IOException ex) {
            Logger.getLogger(ToastController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void showToast(String type, String message) {
        File file;
        switch (type) {
            case MainController.ERROR:
                ToastContainer.setStyle("-fx-background-color: #FF0000; -fx-background-radius: 5");
                file = new File("src/assests/error.png");
                ToastLB.setTextFill(Color.WHITE);
                break;
            case MainController.WARNING:
                ToastContainer.setStyle("-fx-background-color: #FFFF00; -fx-background-radius: 5");
                file = new File("src/assests/warning.png");
                break;
            default:
                ToastContainer.setStyle("-fx-background-color: #00FF00; -fx-background-radius: 5");
                file = new File("src/assests/check.png");
                break;
        }
        ToastIV.setImage(new Image(file.toURI().toString()));
        ToastLB.setText(message);
        Scene scene = new Scene(this.root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setResizable(false);
        toastStage.setScene(scene);
        toastStage.show();
        toastStage.setX(parentStage.getX() + parentStage.getWidth()/2 - toastStage.getWidth()/2);
        toastStage.setY(parentStage.getY() + parentStage.getHeight() - toastStage.getHeight() - 20);
        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey = new KeyFrame(Duration.millis(300), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 1)); 
        fadeInTimeline.getKeyFrames().add(fadeInKey);
        fadeInTimeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new Thread(){
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ToastController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Timeline fadeOutTimeline = new Timeline();
                        KeyFrame fadeOutKey = new KeyFrame(Duration.millis(800), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0)); 
                        fadeOutTimeline.getKeyFrames().add(fadeOutKey);   
                        fadeOutTimeline.setOnFinished(new EventHandler<ActionEvent>() {
                            public void handle(ActionEvent event) {
                                toastStage.close();
                            }
                        }); 
                        fadeOutTimeline.play();
                    }
                }.start();
            }
           
        });
        fadeInTimeline.play();
    }
}
