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
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

/**
 *
 * @author lytw1
 */
public class MainController implements Initializable {
    
    @FXML
    private TextField ImagePathTF;
    @FXML
    private TextField ImageFilenameTF;
    @FXML
    private TextArea AnnotationTA;
    
    @FXML
    private StackPane ImageViewContainer; 
    @FXML
    private ImageView ImageIV;
    
    @FXML
    private Button ButtonUpload;
    
    @FXML
    private Button ButtonClear;
    
    @FXML
    private ScrollPane ScrollPane;
    @FXML
    private FlowPane ImageListContainer;
    
    private List<ImageModel> imageList;
    private File choosenImgFile = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imageList = DatabaseHelper.getInstance().readImages();
        setImages(imageList);
    }
    
    private void setImages(List<ImageModel> imageList) {
        double width = ImageListContainer.getWidth();
        double height = ImageListContainer.getHeight();
        System.out.println(width);
        ImageListContainer.getChildren().clear();
        for(ImageModel image: imageList) {
            ImageListContainer.getChildren().add(new ImageItemController(image, width/5, height/3));
        }
    }
    
    @FXML
    private void uploadImage() {
        FileChooser.ExtensionFilter fileExtensionFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        FileChooser fileChooser =  new FileChooser();
        fileChooser.getExtensionFilters().add(fileExtensionFilter);
        fileChooser.setTitle("Choose Image File");
        choosenImgFile = fileChooser.showOpenDialog(null);
        if(choosenImgFile != null) {
            setImageIV();
        }
    }
    
    private void setImageIV() {
        BufferedImage choosenImg = null;
        double containerWidth = ImageViewContainer.getWidth();
        double containerHeight = ImageViewContainer.getHeight();
        try {
            choosenImg = ImageIO.read(choosenImgFile);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(choosenImg != null) {
            double imageWidth = choosenImg.getWidth();
            double imageHeight = choosenImg.getHeight();
            if(imageWidth <= containerWidth && imageHeight <= containerHeight) {
                setImage(choosenImgFile);
            } else if (imageWidth > containerWidth && imageHeight > containerHeight) {
                ImageIV.fitWidthProperty().bind(ImageViewContainer.widthProperty());
                ImageIV.fitHeightProperty().bind(ImageViewContainer.heightProperty());
                setImage(choosenImgFile);
            } else if (imageWidth > containerWidth && imageHeight < containerHeight) {
                ImageIV.fitWidthProperty().bind(ImageViewContainer.widthProperty());
                setImage(choosenImgFile);
            } else if (imageWidth < containerWidth && imageHeight > containerHeight) {
                ImageIV.fitHeightProperty().bind(ImageViewContainer.heightProperty());
                setImage(choosenImgFile);
            }
        }
        
    }
    
    private void setImage(File imgFile) {
        URL url;
        try {
            url = imgFile.toURI().toURL();
            ImagePathTF.setText(imgFile.getAbsolutePath());
            ImageFilenameTF.setText(imgFile.getName());
            ImageIV.setImage(new Image(url.toExternalForm()));
            ButtonClear.setVisible(true);
            AnnotationTA.requestFocus();
        } catch (MalformedURLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void clearImage() {
        choosenImgFile = null;
        ImagePathTF.setText("");
        ImageFilenameTF.setText("");
        ImageIV.setImage(null);
        ButtonClear.setVisible(false);
        ButtonUpload.requestFocus();
    }
    
    @FXML
    private void saveImage() {
        if(choosenImgFile == null) {
            return;
        }
        int isAnnotated = 0;
        if(!AnnotationTA.getText().isEmpty()) {
            isAnnotated = 1;
        }
        ImageModel image = new ImageModel(choosenImgFile.getName(), choosenImgFile, AnnotationTA.getText(), isAnnotated);
        DatabaseHelper.getInstance().createImage(image);
        imageList = DatabaseHelper.getInstance().readImages();
        setImages(imageList);
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
    
    @FXML
    private void removeImage() {
        
    }
}
