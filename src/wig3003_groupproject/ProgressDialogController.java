/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author lytw1
 */
//  ProgressDialogController class to show progress indicator in an AnchorPane
public class ProgressDialogController extends AnchorPane{

    //  Store dialog window in a local variable
    private final Stage progressDialogStage;
    
    //  Construtor for ProgressDialogController
    public ProgressDialogController(Stage stage) throws IOException {
        progressDialogStage = new Stage();
        
        //  Setup parent window for progess dialog window
        progressDialogStage.initOwner(stage);
        
        //  Set the window background to transparent
        progressDialogStage.initStyle(StageStyle.TRANSPARENT);
        
        //  Make the modal window block parent window event
        progressDialogStage.initModality(Modality.APPLICATION_MODAL);
        
        //  Load the progress dialog fxml file and set the controller to the loader
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ProgressDialog.fxml"));
        fxmlLoader.setRoot(ProgressDialogController.this);
        fxmlLoader.setController(ProgressDialogController.this);
        Parent progressDialogRoot = fxmlLoader.load();
        
        //  Set the interface to scene
        Scene scene = new Scene(progressDialogRoot);
        scene.setFill(Color.TRANSPARENT);
        progressDialogStage.setResizable(false);
        
        //  Set the scene to window
        progressDialogStage.setScene(scene);
    }
    
    //  Show the progress dialog and position it to the center of the parent window
    public void showProgressDialog(double containerX, double containerY, double containerWidth, double containerHeight) {
        progressDialogStage.show();
        progressDialogStage.setX(containerX + containerWidth/2 - progressDialogStage.getWidth()/2 + 10);
        progressDialogStage.setY(containerY + containerHeight/2 - progressDialogStage.getHeight()/2);
    }
    
    //  Close the progress dialog window
    public void hideProgressDialog() {
        progressDialogStage.close();
    }
}
