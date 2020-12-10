/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class ProgressDialogController extends AnchorPane{

    private Stage parentStage;
    private Stage progressDialogStage;
    private Parent root;
    private Scene scene;
    
    public ProgressDialogController(Stage stage) {
        parentStage = stage;
        progressDialogStage = new Stage();
        progressDialogStage.initOwner(stage);
        progressDialogStage.initStyle(StageStyle.TRANSPARENT);
        progressDialogStage.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ProgressDialog.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            root = fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(ProgressDialogController.class.getName()).log(Level.SEVERE, null, ex);
        }
        scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        progressDialogStage.setResizable(false);
        progressDialogStage.setScene(scene);
    }
    
    public void showProgressDialog(double containerX, double containerY, double containerWidth, double containerHeight) {
        progressDialogStage.show();
        progressDialogStage.setX(containerX + containerWidth/2 - progressDialogStage.getWidth()/2 + 10);
        progressDialogStage.setY(containerY + containerHeight/2 - progressDialogStage.getHeight()/2);
    }
    
    public Stage getDialogStage() {
        return progressDialogStage;
    }
    
    public void hideProgressDialog() {
        progressDialogStage.close();
    }
}
