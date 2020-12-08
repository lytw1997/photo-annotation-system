/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author lytw1
 */
public class ImageModel {
    private int id;
    private String fileName;
    private File imageFile;
    private BufferedImage image;
    private String annotation;
    private int isAnnotated;

    public ImageModel(String fileName, File imageFile, String annotation, int isAnnotated) {
        this.fileName = fileName;
        this.imageFile = imageFile;
        this.annotation = annotation;
        this.isAnnotated = isAnnotated;
    }
    
    public ImageModel(int id, String fileName, BufferedImage image, String annotation, int isAnnotated) {
        this.id = id;
        this.fileName = fileName;
        this.image = image;
        this.annotation = annotation;
        this.isAnnotated = isAnnotated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage imageBytes) {
        this.image = image;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public int getIsAnnotated() {
        return isAnnotated;
    }

    public void setIsAnnotated(int isAnnotated) {
        this.isAnnotated = isAnnotated;
    }

}
