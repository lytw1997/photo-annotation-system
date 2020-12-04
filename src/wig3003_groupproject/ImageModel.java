/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

/**
 *
 * @author lytw1
 */
public class ImageModel {
    private String fileName;
    private byte[] imageByteArray;
    private String annotation;
    private int isAnnotated;

    public ImageModel(String fileName, byte[] imageByteArray, String annotation, int isAnnotated) {
        this.fileName = fileName;
        this.imageByteArray = imageByteArray;
        this.annotation = annotation;
        this.isAnnotated = isAnnotated;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getImageByteArray() {
        return imageByteArray;
    }

    public void setImageByteArray(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public int isIsAnnotated() {
        return isAnnotated;
    }

    public void setIsAnnotated(int isAnnotated) {
        this.isAnnotated = isAnnotated;
    }
    
    
}
