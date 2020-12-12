/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
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
//  ToastController class to show toast message in a HBox
public class ToastController extends HBox {
    
    //  Get the ToastController instance
    public static ToastController getInstance(Stage stage) {
        return new ToastController(stage);
    };
    
    @FXML
    private HBox ToastContainer;
    @FXML
    private ImageView ToastIV;
    @FXML
    private Label ToastLB;
    
    //  Store main window, toast window and scene in local variables
    private Stage parentStage;
    private Stage toastStage;
    private Parent toastRoot;
    
    //  Constructor for ToastController
    public ToastController(Stage stage) {
        parentStage = stage;
        toastStage = new Stage();
        
        //  Setup parent window for toast window
        toastStage.initOwner(stage);
        
        //  Set the window background to transparent
        toastStage.initStyle(StageStyle.TRANSPARENT);
        
        //  Load the toast fxml file and set the controller to the loader
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Toast.fxml"));
        fxmlLoader.setRoot(ToastController.this);
        fxmlLoader.setController(ToastController.this);
        try {
            toastRoot = fxmlLoader.load();
            
            //  Set the interface to transparent
            toastRoot.setOpacity(0);
        } catch (IOException ex) {
            Logger.getLogger(ToastController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //  Display the toast with message
    public void showToast(String type, String message) {
        String imagePath;
        
        //  Set the style and icon to display on toast
        switch (type) {
            case MainController.ERROR:
                ToastContainer.setStyle("-fx-background-color: #FF0000; -fx-background-radius: 5");
                imagePath = "resources/error.png";
                ToastLB.setTextFill(Color.WHITE);
                break;
            case MainController.WARNING:
                ToastContainer.setStyle("-fx-background-color: #FFFF00; -fx-background-radius: 5");
                imagePath = "resources/warning.png";
                break;
            default:
                ToastContainer.setStyle("-fx-background-color: #00FF00; -fx-background-radius: 5");
                imagePath = "resources/check.png";
                break;
        }
        try {
            //  Set the image to the toast imageView
            ToastIV.setImage(new Image(getClass().getClassLoader().getResource(imagePath).toURI().toString()));
        } catch (URISyntaxException ex) {
            Logger.getLogger(ToastController.class.getName()).log(Level.SEVERE, null, ex);
        }
        ToastLB.setText(message);
        
        // Set the scene to the window and show it
        Scene scene = new Scene(this.toastRoot);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setResizable(false);
        toastStage.setScene(scene);
        toastStage.show();
        
        //  Position the window to bottom center
        toastStage.setX(parentStage.getX() + parentStage.getWidth()/2 - toastStage.getWidth()/2);
        toastStage.setY(parentStage.getY() + parentStage.getHeight() - toastStage.getHeight() - 20);
        
        /*  
            The codes below refer to https://stackoverflow.com/questions/26792812/android-toast-equivalent-in-javafx 
            Answer from alcoolis
        */
        //  Declare timeline to store fadeIn keyframe
        Timeline fadeInTimeline = new Timeline();
        
        //  Declare fadeIn keyframe with duration 300ms from opacity 0 to 1
        KeyFrame fadeInKey = new KeyFrame(Duration.millis(300), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 1)); 
        
        //  Add the fadeIn keyframe to the timeline
        fadeInTimeline.getKeyFrames().add(fadeInKey);
        
        //  Add onFinish listener to play fadeOut animation
        fadeInTimeline.setOnFinished((ActionEvent event) -> {
            
            //  Play fadeOut animation using new thread
            new Thread(){
                @Override
                public void run() {
                    try {
                        System.out.println("--> Toast: " + Thread.currentThread().getName());
                        //  Delay fadeOut by 1000ms
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ToastController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    //  Declare timeline to store fadeOut keyframe
                    Timeline fadeOutTimeline = new Timeline();
                    
                    //  Declare fadeOut keyframe with duration 800ms from opacity 1 to 0
                    KeyFrame fadeOutKey = new KeyFrame(Duration.millis(800), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0));
                    
                    //  Add the fadeOut keyframe to the timeline
                    fadeOutTimeline.getKeyFrames().add(fadeOutKey);
                    
                    //  Add onFinish listener to close the toast window
                    fadeOutTimeline.setOnFinished((ActionEvent event1) -> {
                        toastStage.close();
                    });
                    
                    //  Play the fadeOut animation
                    fadeOutTimeline.play();
                }
            }.start();
        });
        
        //  Play the fadeIn animation
        fadeInTimeline.play();
    }
}
