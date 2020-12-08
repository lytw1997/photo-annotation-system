/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    private Button ButtonSave;
    @FXML
    private Button ButtonRemove;
    
    @FXML
    private TextField SearchImageTF;
    
    @FXML
    private FlowPane ImageListContainer;
    @FXML
    private GridPane ParentPane;
    
    private Stage stage;
    private boolean isEditing;
    private List<ImageModel> imageList;
    private File choosenImgFile = null;
    private ImageModel choosenImg = null;
    private double imageWidth = 0;
    private double imageHeight = 0;
    
    static final String SUCCESS = "success";
    static final String ERROR = "error";
    static final String WARNING = "warning"; 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.imageList = DatabaseHelper.getInstance().readImages();
        } catch (SQLException | IOException ex) {
            ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.isEditing = false;
        SearchImageTF.textProperty().addListener((observable, oldValue, newValue) -> {
            filterImages(newValue);
        });
        ParentPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double changedWidth = newValue.doubleValue()/2 - 20;
                if(imageWidth >= changedWidth) {
                    ImageIV.fitWidthProperty().unbind();
                    ImageIV.setFitWidth(changedWidth);
                }
            }
        });
        ParentPane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double changedHeight = newValue.doubleValue() * 0.55;
                if(imageHeight >= changedHeight){
                    ImageIV.fitHeightProperty().unbind();
                    ImageIV.setFitHeight(changedHeight);
                }
            }
            
        });
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    private void filterImages(String filter) {
        if(filter.isEmpty()) {
            setImages();
        }
        List<ImageModel> filteredImageList = imageList.stream().filter(image -> image.getFileName().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
        setImages(filteredImageList);
    }
    
    public void setImages() {
        double width = ImageListContainer.getWidth() - 70;
        double height = ImageListContainer.getHeight();
        ImageListContainer.getChildren().clear();
        for(ImageModel image: imageList) {
            ImageListContainer.getChildren().add(new ImageItemController(image, width/5, height/3, this));
        }
    }
    
    public void setImages(List<ImageModel> filteredImageList) {
        double width = ImageListContainer.getWidth() - 70;
        double height = ImageListContainer.getHeight();
        ImageListContainer.getChildren().clear();
        for(ImageModel image: filteredImageList) {
            ImageListContainer.getChildren().add(new ImageItemController(image, width/5, height/3, this));
        }
    }
    
    public void SelectImage(ImageModel image) {
        this.isEditing = true;
        this.choosenImg = image;
        setImageIV();
    }
    
    @FXML
    private void uploadImage() {
        FileChooser.ExtensionFilter fileExtensionFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        FileChooser fileChooser =  new FileChooser();
        fileChooser.getExtensionFilters().add(fileExtensionFilter);
        fileChooser.setTitle("Choose Image File");
        choosenImgFile = fileChooser.showOpenDialog(null);
        if(choosenImgFile != null) {
            isEditing = false;
            ButtonSave.setText("Save");
            ButtonRemove.setDisable(true);
            setImageIV();
        }
    }
    
    private void setupImageViewContainer() {
        ImageIV.fitWidthProperty().unbind();
        ImageIV.fitHeightProperty().unbind();
        ImageIV.setFitWidth(0);
        ImageIV.setFitHeight(0);
    }
    
    private void setImageIV() {
        setupImageViewContainer();
        if(!isEditing) {
            BufferedImage choosenBuffImg = null;
            try {
                choosenBuffImg = ImageIO.read(choosenImgFile);
            } catch (IOException ex) {
                ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
            imageWidth = choosenBuffImg.getWidth();
            imageHeight = choosenBuffImg.getHeight();
        } else {
            imageWidth = choosenImg.getImage().getWidth();
            imageHeight = choosenImg.getImage().getHeight();
        }
        setupImageViewProperty();
    }
    
    private void setupImageViewProperty() {
        double containerWidth = ImageViewContainer.getWidth();
        double containerHeight = ImageViewContainer.getHeight();
        if (imageWidth > containerWidth && imageHeight > containerHeight) {
            ImageIV.fitWidthProperty().bind(ImageViewContainer.widthProperty());
            ImageIV.fitHeightProperty().bind(ImageViewContainer.heightProperty());
        } else if (imageWidth > containerWidth && imageHeight < containerHeight) {
            ImageIV.fitWidthProperty().bind(ImageViewContainer.widthProperty());
        } else if (imageWidth < containerWidth && imageHeight > containerHeight) {
            ImageIV.fitHeightProperty().bind(ImageViewContainer.heightProperty());
        }
        setImageContent();
    }
    
    private void setImageContent() {
        if(!isEditing) {
            URL url;
            try {
                url = choosenImgFile.toURI().toURL();
                ImagePathTF.setText(choosenImgFile.getAbsolutePath());
                ImageFilenameTF.setText(choosenImgFile.getName());
                ImageIV.setImage(new Image(url.toExternalForm()));
                AnnotationTA.requestFocus();
            } catch (MalformedURLException ex) {
                ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            ImageFilenameTF.setText(choosenImg.getFileName());
            ImageIV.setImage(SwingFXUtils.toFXImage(choosenImg.getImage(), null));
            AnnotationTA.setText(choosenImg.getAnnotation());
            ButtonSave.setText("Update");
            ButtonRemove.setDisable(false);
        }
        ButtonClear.setVisible(true);
    }
    
    @FXML
    private void clearImage() {
        isEditing = false;
        choosenImgFile = null;
        choosenImg = null;
        ImagePathTF.setText("");
        ImageFilenameTF.setText("");
        ImageIV.setImage(null);
        ButtonClear.setVisible(false);
        AnnotationTA.setText("");
        ButtonSave.setText("Save");
        ButtonRemove.setDisable(true);
        ButtonUpload.requestFocus();
    }
    
    @FXML
    private void saveImage() {
        if(ImageFilenameTF.getText().isEmpty()) {
            ToastController.getInstance(stage).showToast(WARNING, "Filename required.");
            return;
        }
        if(!isEditing) {
            if(choosenImgFile == null) {
                ToastController.getInstance(stage).showToast(WARNING, "Image file required.");
                return;
            }
            int isAnnotated = 0;
            if(!AnnotationTA.getText().isEmpty()) {
                isAnnotated = 1;
            }
            ImageModel image = new ImageModel(ImageFilenameTF.getText(), choosenImgFile, AnnotationTA.getText(), isAnnotated);
            try {
                DatabaseHelper.getInstance().createImage(image);
                ToastController.getInstance(stage).showToast(SUCCESS, "Created successfully.");
            } catch (SQLException | FileNotFoundException ex) {
                ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            if(choosenImg == null) {
                ToastController.getInstance(stage).showToast(ERROR, "Something went wrong.");
                return;
            }
            int isAnnotated = 0;
            if(!AnnotationTA.getText().isEmpty()) {
                isAnnotated = 1;
            }
            ImageModel image = new ImageModel(choosenImg.getId(), ImageFilenameTF.getText(), choosenImg.getImage(), AnnotationTA.getText(), isAnnotated);
            try {
                DatabaseHelper.getInstance().updateImage(image);
                ToastController.getInstance(stage).showToast(SUCCESS, "Updated successfully.");
            } catch (SQLException ex) {
                ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        updateImageList();
    }
    
    @FXML
    private void removeImage() {
        if(!isEditing || choosenImg == null) {
            ToastController.getInstance(stage).showToast(ERROR, "Something went wrong.");
            return;
        }
        try {
            DatabaseHelper.getInstance().deleteImage(choosenImg.getId());
            ToastController.getInstance(stage).showToast(SUCCESS, "Deleted successfully.");
        } catch (SQLException ex) {
            ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        updateImageList();
    }
    
    private void updateImageList() {
        try {
            imageList = DatabaseHelper.getInstance().readImages();
        } catch (SQLException | IOException ex) {
            ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        setImages();
        clearImage();
    }
}
