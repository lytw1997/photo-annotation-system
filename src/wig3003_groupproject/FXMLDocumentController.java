/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author lytw1
 */
public class FXMLDocumentController implements Initializable {
    
    private File choosenImgFile = null;
    private ImageView imageIV;
    
    @FXML
    private TextField imagePathTF;
    
    @FXML
    private HBox imageContainer;
    
    @FXML
    private TextArea annotationTA;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imageIV = new ImageView();
        imageIV.fitWidthProperty().bind(imageContainer.widthProperty());
        imageIV.fitHeightProperty().bind(imageContainer.heightProperty());
        imageContainer.getChildren().add(imageIV);
    }    
    
    public void setupStage(Stage stage) {
        
    }
    
    @FXML
    private void chooseImgFile(ActionEvent event) {
        FileChooser.ExtensionFilter fileExtensionFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        FileChooser fileChooser =  new FileChooser();
        fileChooser.getExtensionFilters().add(fileExtensionFilter);
        fileChooser.setTitle("Choose Image File");
        choosenImgFile = fileChooser.showOpenDialog(null);
        if(choosenImgFile != null) {
            setImageView();
        }
    }
    
    private void setImageView() {
        URL url;
        try {
            url = choosenImgFile.toURI().toURL();
            imagePathTF.setText(choosenImgFile.getAbsolutePath());
            imageIV.setImage(new Image(url.toExternalForm()));
        } catch (MalformedURLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void saveImgFile(ActionEvent event) {
        if(choosenImgFile == null) {
            return;
        }
        int isAnnotated = 0;
        if(!annotationTA.getText().isEmpty()) {
            isAnnotated = 1;
        }
        byte[] imageBytes = fileToByteArray(choosenImgFile);
        System.out.print(imageBytes);
        ImageModel image = new ImageModel(choosenImgFile.getName(), imageBytes, annotationTA.getText(), isAnnotated);
        DatabaseHelper.getInstance().insertData(image);
    }
    
    private byte[] fileToByteArray(File imgFile) {
        try {
            BufferedImage bImage = ImageIO.read(imgFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            return bos.toByteArray();
        }catch (IOException e) {
            return null;
        }
    }
}
