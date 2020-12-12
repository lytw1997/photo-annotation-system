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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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
//  MainController class to handle main function of the application
public class MainController implements Initializable {
    
    @FXML
    private GridPane ParentPane;
    @FXML
    private TextField ImagePathTF;
    @FXML
    private TextField ImageFilenameTF;
    @FXML
    private StackPane ImageViewContainer; 
    @FXML
    private ImageView ImageIV;
    @FXML
    private TextArea AnnotationTA;
    
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
    private ChoiceBox DisplayModeCB;
    @FXML
    private CheckBox AnnotatedModeCB;
    
    @FXML
    private FlowPane ImageListContainer;
    
    /*  
        Local variables to store the window object, edit, display, and annotated mode, 
        filter string, imageList, image file, model, width and height
    */
    private Stage stage;
    private boolean isEditing;
    private String filter = "";
    private String displayMode;
    private String annotatedMode;
    private final ObservableList<ImageModel> imageList = FXCollections.observableArrayList();
    private File choosenImgFile = null;
    private ImageModel choosenImg = null;
    private double imageWidth = 0;
    private double imageHeight = 0;
    
    //  Static variable for toast type
    static final String SUCCESS = "success";
    static final String ERROR = "error";
    static final String WARNING = "warning"; 
    
    //  Static variable for image list display mode
    static final String DISPLAY_FN = "Filename";
    static final String DISPLAY_AT = "Annotation";
    
    //  Static variable for image list annotated mode
    private final String INDETERMINATE = "indeterminate";
    private final String CHECKED = "checked";
    private final String UNCHECKED = "unchecked";
    
    //  Local variable for progress dialog controller
    private ProgressDialogController pDController;
    
    //  Local variable for setImageThread
    Thread setImageThread;
    
    //  Initialize the main scene
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //  Setup the default variables
        isEditing = false;
        displayMode = DISPLAY_FN;
        annotatedMode = INDETERMINATE;
        try {
            //  Instantiate the progress dialog controller object
            pDController = new ProgressDialogController(stage);
        } catch (IOException ex) {
            //  Make sure toast shows in JavaFX application thread
            Platform.runLater(() -> {
                //  Show the error message toast
                ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
            });
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //  Store the stage object to the local variable
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    //  Setup the main interface listeners
    public void setListener() {
        //  Add onChange listener to search text field
        SearchImageTF.textProperty().addListener((observable, oldValue, newValue) -> {
            //  Store the changed text to the filter
            filter = newValue;
            
            //  Display the filtered image list
            filterImages(filter);
        });
        
        /*
            Make the image view resizable using parent pane width and height properties
            If the changed width and height is smaller than the image width and height, resize the image view
        */
        ParentPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            double changedWidth = newValue.doubleValue()/2 - 20;
            if(imageWidth >= changedWidth) {
                ImageIV.fitWidthProperty().unbind();
                ImageIV.setFitWidth(changedWidth);
            }
        });
        ParentPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            double changedHeight = newValue.doubleValue() * 0.55;
            if(imageHeight >= changedHeight){
                ImageIV.fitHeightProperty().unbind();
                ImageIV.setFitHeight(changedHeight);
            }
        });
        
        //  Add listener to the image list to update the UI
        imageList.addListener((ListChangeListener.Change<? extends ImageModel> change) -> {
            System.out.println("-->List Change: " + Thread.currentThread().getName());
            
            //  Make sure filterImages function runs in JavaFX application thread
            Platform.runLater(() -> {
                //  Display the filtered image list
                filterImages(filter);
            });
        });
        try {
            System.out.println("-->First Read: " + Thread.currentThread().getName());
            
            //  Retrieved images when the user start the application and add to imageList
            imageList.addAll(DatabaseHelper.getInstance().readImages());
        } catch (SQLException | IOException ex) {
            //  Show the error message toast
            ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //  Add listener to the display mode choice box
        DisplayModeCB.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //  Store the display mode in a local variable
                displayMode = newValue;
                
                //  Display the filtered image list
                filterImages(filter);
            }
        });
    }
    
    //  Change the annotated mode when the check box state changed
    @FXML
    private void ChangeAnnotatedMode() {
        //  Store the annotated mode in a local variable
        if(AnnotatedModeCB.isIndeterminate()) {
            annotatedMode = INDETERMINATE;
        } else if(AnnotatedModeCB.isSelected()) {
            annotatedMode = CHECKED;
        } else if(!AnnotatedModeCB.isSelected()) {
            annotatedMode = UNCHECKED;
        }
        //  Display the filtered image list
        filterImages(filter);
    }
    
    //  Filter the image list
    private void filterImages(String filter) {
        //  Create new empty imageList to store the filtered image list
        List<ImageModel> filteredImageList = new ArrayList<>();
        
        //  Filter the image list based on filter string, display, and annotated mode 
        if(displayMode.equals(DISPLAY_FN) && annotatedMode.equals(INDETERMINATE)) {
            filteredImageList = imageList.stream().filter(image -> image.getFileName().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
        } else if(displayMode.equals(DISPLAY_AT) && annotatedMode.equals(INDETERMINATE)) {
            filteredImageList = imageList.stream().filter(image -> image.getAnnotation().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
        } else if(displayMode.equals(DISPLAY_FN) && annotatedMode.equals(CHECKED)) {
            filteredImageList = imageList.stream().filter(image -> image.getFileName().toLowerCase().contains(filter.toLowerCase()) && !image.getAnnotation().isEmpty()).collect(Collectors.toList());
        } else if(displayMode.equals(DISPLAY_AT) && annotatedMode.equals(CHECKED)) {
            filteredImageList = imageList.stream().filter(image -> image.getAnnotation().toLowerCase().contains(filter.toLowerCase()) && !image.getAnnotation().isEmpty()).collect(Collectors.toList());
        } else if(displayMode.equals(DISPLAY_FN) && annotatedMode.equals(UNCHECKED)) {
            filteredImageList = imageList.stream().filter(image -> image.getFileName().toLowerCase().contains(filter.toLowerCase()) && image.getAnnotation().isEmpty()).collect(Collectors.toList());
        } else if(displayMode.equals(DISPLAY_AT) && annotatedMode.equals(UNCHECKED)) {
            filteredImageList = imageList.stream().filter(image -> image.getAnnotation().toLowerCase().contains(filter.toLowerCase()) && image.getAnnotation().isEmpty()).collect(Collectors.toList());
        }
        
        //  Display the images
        setImages(filteredImageList);
    }
    
    //  Display the filtered image
    public void setImages(List<ImageModel> filteredImageList) {
        System.out.println("--> Set Images: " + Thread.currentThread().getName());
        //  Get the available width used to show the images
        double width = ImageListContainer.getWidth() - 80;
        
        //  Set the image views in JavaFX application thread
        Platform.runLater(() -> {
            //  Clear the container's componets
            ImageListContainer.getChildren().clear();
            
            //  Loop through the image list and setup the image views in the container
            filteredImageList.stream().forEach((ImageModel image) -> {
                try {
                    ImageListContainer.getChildren().add(new ImageItemController(image, width/5, MainController.this, displayMode));
                } catch (IOException ex) {
                    //  Make sure the toast show in JavaFX application thread
                    Platform.runLater(() -> {
                        //  Show the error message toast
                        ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
                    });
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
    }
    
    /*  
        Store the image in local variable
        Change the edit mode
        Set the image view
    */
    public void SelectImage(ImageModel image) {
        this.isEditing = true;
        this.choosenImg = image;
        setImageIV();
    }
    
    //  Show the file chooser dialog when upload button click
    @FXML
    private void uploadImage() {
        //  Create file chooser object with extension filter
        FileChooser.ExtensionFilter fileExtensionFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        FileChooser fileChooser =  new FileChooser();
        fileChooser.getExtensionFilters().add(fileExtensionFilter);
        fileChooser.setTitle("Choose Image File");
        
        //  Store the choosen image file from file choooser object
        choosenImgFile = fileChooser.showOpenDialog(null);
        if(choosenImgFile != null) {
            //  Show the progress dialog
            pDController.showProgressDialog(stage.getX(), stage.getY(),ParentPane.getWidth(), ParentPane.getHeight());
            
            //  Create task to read the image file and set the image view from another thread
            Task<Void> setImageTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    System.out.println("--> Upload: " + Thread.currentThread().getName());
                    ImageIV.setImage(null);
                    isEditing = false;
                    ButtonSave.setText("Save");
                    ButtonRemove.setDisable(true);
                    setImageIV();
                    return null;
                }
            };
            setImageThread = new Thread(setImageTask);
            setImageThread.start();
            
            //  Close the progress dialog when the task success
            setImageTask.setOnSucceeded((WorkerStateEvent workerStateEvent) -> {
                setImageThread = null;
                pDController.hideProgressDialog();
                System.out.println("--> Upload Done: " + Thread.currentThread().getName());
                AnnotationTA.requestFocus();
            });
        }
    }
    
    /*  
        Unbind the image view width and height with parent container
        Set image view with default width and height
    */
    private void setupImageViewContainer() {
        ImageIV.fitWidthProperty().unbind();
        ImageIV.fitHeightProperty().unbind();
        ImageIV.setFitWidth(0);
        ImageIV.setFitHeight(0);
    }
    
    /*
        Read the image file and get the image width and height
    */
    private void setImageIV() {
        setupImageViewContainer();
        if(!isEditing) {
            try {
                //  Read the image file and store in buffered image object
                BufferedImage choosenBuffImg = ImageIO.read(choosenImgFile);
                
                //  Store the image width and height in local variables
                imageWidth = choosenBuffImg.getWidth();
                imageHeight = choosenBuffImg.getHeight();
            } catch (IOException ex) {
                //  Show the error message toast in JavaFX application thread
                Platform.runLater(() -> {
                    ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
                });
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } else {
            //  Store the image width and height in local variables
            imageWidth = choosenImg.getImage().getWidth();
            imageHeight = choosenImg.getImage().getHeight();
        }
        setupImageViewProperty();
    }
    
    //  Bind the imageView with the coontainer if its properties are greater than the container
    private void setupImageViewProperty() {
        //  Get the image view container width and height
        double containerWidth = ImageViewContainer.getWidth();
        double containerHeight = ImageViewContainer.getHeight();
        
        //  Bind the image view with the container
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
    
    //  Set the image, filename and annotation text field
    private void setImageContent() {
        if(!isEditing) {
            URL url;
            try {
                url = choosenImgFile.toURI().toURL();
                ImagePathTF.setText(choosenImgFile.getAbsolutePath());
                //  Set the image filename to the text field
                ImageFilenameTF.setText(choosenImgFile.getName().substring(0, choosenImgFile.getName().lastIndexOf('.')));
                ImageIV.setImage(new Image(url.toExternalForm()));
            } catch (MalformedURLException ex) {
                //  Show the error message toast in JavaFX application thread
                Platform.runLater(() -> {
                    ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
                });
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
    
    //  Clear the text fields and set the file and buffered image to null
    @FXML
    private void clearImage() {
        System.out.println("--> Clear: " + Thread.currentThread().getName());
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
    
    //  Insert the image into database when save button click
    @FXML
    private void saveImage() {
        if(ImageFilenameTF.getText().isEmpty()) {
            //  Show warning message toast if filename is empty
            ToastController.getInstance(stage).showToast(WARNING, "Filename required.");
            return;
        }
        if(!isEditing) {
            if(choosenImgFile == null) {
                //  Show warning message toast if choosen file is null
                ToastController.getInstance(stage).showToast(WARNING, "Image file required.");
                return;
            }
            int isAnnotated = 0;
            if(!AnnotationTA.getText().isEmpty()) {
                isAnnotated = 1;
            }
            //  Store the image content in the image model object
            ImageModel image = new ImageModel(ImageFilenameTF.getText(), choosenImgFile, AnnotationTA.getText(), isAnnotated);
            try {
                //  Execute the create operation
                DatabaseHelper.getInstance().createImage(image);
                //  Show success message toast if if the operation success
                ToastController.getInstance(stage).showToast(SUCCESS, "Created successfully.");
            } catch (SQLException | FileNotFoundException ex) {
                //  Show error message toast
                ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            if(choosenImg == null) {
                //  Show error message if the buffered image variable is null
                ToastController.getInstance(stage).showToast(ERROR, "Something went wrong.");
                return;
            }
            int isAnnotated = 0;
            if(!AnnotationTA.getText().isEmpty()) {
                isAnnotated = 1;
            }
            //  Store the image content in the image model object
            ImageModel image = new ImageModel(choosenImg.getId(), ImageFilenameTF.getText(), choosenImg.getImage(), AnnotationTA.getText(), isAnnotated);
            try {
                //  Execute the update operation
                DatabaseHelper.getInstance().updateImage(image);
                //  Show success message toast if if the operation success
                ToastController.getInstance(stage).showToast(SUCCESS, "Updated successfully.");
            } catch (SQLException ex) {
                //  Show error message toast
                ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //  Clear the image content and update the image list after the operation
        clearImage();
        updateImageList();
    }
    
    //  Delete the image from the database
    @FXML
    private void removeImage() {
        if(!isEditing || choosenImg == null) {
            //  Show the error message toast
            ToastController.getInstance(stage).showToast(ERROR, "Something went wrong.");
            return;
        }
        try {
            //  Execute the delete operation
            DatabaseHelper.getInstance().deleteImage(choosenImg.getId());
            //  Show success message toast if if the operation success
            ToastController.getInstance(stage).showToast(SUCCESS, "Deleted successfully.");
            //  Clear the image content and update the image list after the operation
            clearImage();
            updateImageList();
        } catch (SQLException ex) {
            //  Show the error message toast
            ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    //  Update the image list
    private void updateImageList() {
        //  Create new thread to read all images from database and start the thread
        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("--> Read: " + Thread.currentThread().getName());
                    //  Set the retrieved images to the image list
                    imageList.setAll(DatabaseHelper.getInstance().readImages());
                } catch (SQLException | IOException ex) {
                    //  Show the error message toast in JavaFX application thread
                    Platform.runLater(() -> {
                        ToastController.getInstance(stage).showToast(ERROR, ex.getClass().getName() + ": " + ex.getMessage());
                    });
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }
}
