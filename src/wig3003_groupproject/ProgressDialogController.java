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
public class ProgressDialogController extends AnchorPane{

    private final Stage parentStage;
    private final Stage progressDialogStage;
    private final Parent root;
    private final Scene scene;
    
    public ProgressDialogController(Stage stage) throws IOException {
        parentStage = stage;
        progressDialogStage = new Stage();
        progressDialogStage.initOwner(stage);
        progressDialogStage.initStyle(StageStyle.TRANSPARENT);
        progressDialogStage.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ProgressDialog.fxml"));
        fxmlLoader.setRoot(ProgressDialogController.this);
        fxmlLoader.setController(ProgressDialogController.this);
        root = fxmlLoader.load();
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
