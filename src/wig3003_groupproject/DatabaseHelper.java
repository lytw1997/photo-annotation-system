/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import sun.misc.IOUtils;

/**
 *
 * @author lytw1
 */
public class DatabaseHelper {
    public static DatabaseHelper databaseHelper;
    public Connection conn;
    
    static final String DB_NAME = "ImageAnnotationDB";
    static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "IMAGES";
    
    public static final String ID = "id";
    public static final String FILENAME = "filename";
    public static final String IMAGE = "image";
    public static final String ANNOTATION = "annotation";
    public static final String ISANNOTATED = "isAnnotated";
    
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + 
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FILENAME + " TEXT NOT NULL, " +
            IMAGE + " BLOB NOT NULL, " +
            ANNOTATION + " TEXT, " +
            ISANNOTATED + " INTEGER DEFAULT 0)";
    
    public static DatabaseHelper getInstance() {
        if(databaseHelper == null) {
            databaseHelper = new DatabaseHelper();
        }
        return databaseHelper;
    }
    
    public DatabaseHelper() {
        conn = SqliteConnection.connect(DB_NAME);
    }

    public Connection getConn() {
        return conn;
    }
    
    public void createTable(){
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(CREATE_TABLE);
            stmt.close();
        } catch(SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    
    public void createImage(ImageModel image) {
        try {
            String query = "INSERT INTO " + TABLE_NAME + " (" + FILENAME + "," + IMAGE + "," + ANNOTATION + "," + ISANNOTATED + ") " +
                        "VALUES (?,?,?,?);"; 
            PreparedStatement stmt=conn.prepareStatement(query);  
            stmt.setString(1, image.getFileName());
            stmt.setBinaryStream(2, (InputStream) new FileInputStream(image.getImageFile()), (int)image.getImageFile().length());
            stmt.setString(3, image.getAnnotation());
            stmt.setInt(4, image.getIsAnnotated());
            stmt.executeUpdate();
            System.out.println("Save successfully");
            stmt.close();
        } catch(SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        } catch(FileNotFoundException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
    
    public List<ImageModel> readImages() {
        List<ImageModel> imageList = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + TABLE_NAME; 
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while ( rs.next() ) {
                int id = rs.getInt(ID);
                String  filename = rs.getString(FILENAME);
                InputStream imageStream  = rs.getBinaryStream(IMAGE);
                BufferedImage imageBuff = ImageIO.read(imageStream);
                String  annotation = rs.getString(ANNOTATION);
                int isAnnotated = rs.getInt(ISANNOTATED);
                imageList.add(new ImageModel(id, filename, imageBuff, annotation, isAnnotated));
             }
            System.out.println("Read successfully");
            rs.close();
            stmt.close();
            return imageList;
        } catch(SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
            return null;
        } catch(IOException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
            return null;
        }
    }
    
    public void updateImage(ImageModel image) {
        try {
            String query = "UPDATE " + TABLE_NAME + " SET " + FILENAME + " = ?, " + ANNOTATION + " = ?, " + ISANNOTATED + " = ? WHERE " + ID + " = ?"; 
            PreparedStatement stmt = conn.prepareStatement(query);  
            stmt.setString(1, image.getFileName());
            stmt.setString(2, image.getAnnotation());
            stmt.setInt(3, image.getIsAnnotated());
            stmt.setInt(4, image.getId());
            stmt.executeUpdate();
            System.out.println("Update successfully");
            stmt.close();
        } catch(SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        } 
    }
    
    public void deleteImage(int id) {
        try {
            String query = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = ?";
            PreparedStatement stmt = conn.prepareStatement(query); 
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Delete successfully");
            stmt.close();
        } catch(SQLException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        } 
    }
    
    public boolean isDbConnected(){
        try {
            return !conn.isClosed();
        }catch(SQLException ex) {
            return false;
        }
    }
}
