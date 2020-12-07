/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author lytw1
 */
public class ImageItemController extends StackPane{

    @FXML
    private StackPane ImageItemContainer;
    
    @FXML
    private VBox ImageBox; 
    
    @FXML
    private ImageView ImageItemIV;
    
    @FXML
    private Label ImageItemFilenameLB; 
    
    public ImageItemController(ImageModel image, double containerWidth, double containerHeight) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ImageItem.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        ImageItemContainer.setPrefSize(containerWidth, containerHeight);
//        ImageItemIV.fitWidthProperty().bind(ImageItemContainer.widthProperty());
//        ImageItemIV.fitHeightProperty().bind(ImageItemContainer.heightProperty());
        double imageWidth = image.getImage().getWidth();
        double imageHeight = image.getImage().getHeight();
        if (imageWidth > containerWidth && imageHeight > containerHeight) {
            ImageItemIV.fitWidthProperty().bind(ImageItemContainer.widthProperty());
            ImageItemIV.fitHeightProperty().bind(ImageItemContainer.heightProperty());
        } else if (imageWidth > containerWidth && imageHeight < containerHeight) {
            ImageItemIV.fitWidthProperty().bind(ImageItemContainer.widthProperty());
        } else if (imageWidth < containerWidth && imageHeight > containerHeight) {
            ImageItemIV.fitHeightProperty().bind(ImageItemContainer.heightProperty());
        }
        ImageItemIV.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
        ImageItemFilenameLB.setText(image.getFileName());
    }
    
}
