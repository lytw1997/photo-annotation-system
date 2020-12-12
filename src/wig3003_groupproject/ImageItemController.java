/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author lytw1
 */
//  ImageItemController class to set the image list item in stack pane
public class ImageItemController extends StackPane{

    @FXML
    private StackPane ImageItemContainer;
    @FXML
    private ImageView ImageItemIV;
    @FXML
    private ImageView IsAnnotatedIV;
    @FXML
    private Label ImageItemLB; 
    
    //  Create the main controller variable
    private final MainController mainController;
    
    //  Create the image model variable
    private final ImageModel image;
    
    //  Constructor for ImageItemController class
    public ImageItemController(ImageModel image, double containerWidth, MainController mainController, String displayMode) throws IOException {
        //  Load the image item fxml file and set the controller to the loader
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImageItem.fxml"));
        fxmlLoader.setRoot(ImageItemController.this);
        fxmlLoader.setController(ImageItemController.this);
        fxmlLoader.load();
        
        //  Store the image and controller in local variables
        this.image = image;
        this.mainController = mainController; 
        ImageItemContainer.setPrefWidth(containerWidth);
        ImageItemIV.setFitWidth(ImageItemContainer.getPrefWidth());
        ImageItemIV.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
        Tooltip tooltip = null;
        //  Set the label using filename or annotaion based on display mode
        switch (displayMode) {
            case MainController.DISPLAY_FN:
                ImageItemLB.setText(image.getFileName());
                tooltip = new Tooltip(image.getFileName());
                break;
            case MainController.DISPLAY_AT:
                if(image.getAnnotation().isEmpty() || image.getAnnotation() == null) {
                    ImageItemLB.setText("No Annotation");
                    tooltip = new Tooltip("Null");
                }   if(!image.getAnnotation().isEmpty()){
                    ImageItemLB.setText(image.getAnnotation());
                    tooltip = new Tooltip(image.getAnnotation());
            }   break;
        }
        //  Add tooltip to the image item container
        if(tooltip != null) {
            tooltip.setWrapText(true);
            tooltip.setPrefWidth(containerWidth);
            Tooltip.install(ImageItemContainer, tooltip);
        }
        //  Show the annotated image view if the annotation is not empty
        if(this.image.getIsAnnotated() > 0) {
            IsAnnotatedIV.setFitWidth(containerWidth * 0.3);
            IsAnnotatedIV.setFitHeight(containerWidth * 0.3);
            IsAnnotatedIV.setVisible(true);
        }
    }
    
    //  Call the main controller select image once the image item container is clicked
    @FXML
    private void SelectImage() {
        mainController.SelectImage(this.image);
    }
}
