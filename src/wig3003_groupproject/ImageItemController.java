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
public class ImageItemController extends StackPane{

    @FXML
    private StackPane ImageItemContainer;
    @FXML
    private ImageView ImageItemIV;
    @FXML
    private ImageView IsAnnotatedIV;
    @FXML
    private Label ImageItemLB; 
    
    private final MainController mainController;
    
    private final ImageModel image;
    
    public ImageItemController(ImageModel image, double containerWidth, MainController mainController, String displayMode) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImageItem.fxml"));
        fxmlLoader.setRoot(ImageItemController.this);
        fxmlLoader.setController(ImageItemController.this);
        fxmlLoader.load();
        this.image = image;
        this.mainController = mainController;
        ImageItemContainer.setPrefWidth(containerWidth);
        ImageItemIV.setFitWidth(ImageItemContainer.getPrefWidth());
        ImageItemIV.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
        Tooltip tooltip = null;
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
        if(tooltip != null) {
            tooltip.setWrapText(true);
            tooltip.setPrefWidth(containerWidth);
            Tooltip.install(ImageItemContainer, tooltip);
        }
        if(this.image.getIsAnnotated() > 0) {
            IsAnnotatedIV.setFitWidth(containerWidth * 0.3);
            IsAnnotatedIV.setFitHeight(containerWidth * 0.3);
            IsAnnotatedIV.setVisible(true);
        }
    }
    
    @FXML
    private void SelectImage() {
        mainController.SelectImage(this.image);
    }
}
