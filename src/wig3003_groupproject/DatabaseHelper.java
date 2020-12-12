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

/**
 *
 * @author lytw1
 */
/*
    Get SQLite connection
    Perform CRUD operations
*/
public class DatabaseHelper {
    //  Static variable to store instance of DatabaseHelper
    public static DatabaseHelper databaseHelper;
    
    //  Local variable to store SQLite connection
    public Connection conn;
    
    //  Database name
    static final String DB_NAME = "ImageAnnotationDB";
    
    //  Table name
    private final String TABLE_NAME = "IMAGES";
    
    //  Table columns name
    private final String ID = "id";
    private final String FILENAME = "filename";
    private final String IMAGE = "image";
    private final String ANNOTATION = "annotation";
    private final String ISANNOTATED = "isAnnotated";
     
    /*  
        Get DatabaseHelper instance
        Instantiate new DatabaseHelper objects if it is null
    */
    public static DatabaseHelper getInstance() {
        if(databaseHelper == null) {
            databaseHelper = new DatabaseHelper();
        }
        return databaseHelper;
    }
    
    /*  
        Constructor to create SQLite database connection
        Assign connection to a local variable
    */
    public DatabaseHelper() {
        conn = SqliteConnection.connect(DB_NAME);
    }
    
    //  Get database connection
    public Connection getConn() {
        return conn;
    }
    
    //  Create table operation
    public void createTable() throws SQLException{
        //  Query to create table if it is not exists in the database
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + 
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FILENAME + " TEXT NOT NULL, " +
            IMAGE + " BLOB NOT NULL, " +
            ANNOTATION + " TEXT, " +
            ISANNOTATED + " INTEGER DEFAULT 0)";
        
        //  SQL statement to execute create table query
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        }
    }
    
    //  Store image operation
    public void createImage(ImageModel image) throws SQLException, FileNotFoundException {
        //  Query to insert image into table
        String query = "INSERT INTO " + TABLE_NAME + " (" + FILENAME + "," + IMAGE + "," + ANNOTATION + "," + ISANNOTATED + ") " +
                    "VALUES (?,?,?,?);"; 
        
        // Using prepared statement to store image binary stream
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, image.getFileName());
            stmt.setBinaryStream(2, (InputStream) new FileInputStream(image.getImageFile()), (int)image.getImageFile().length());
            stmt.setString(3, image.getAnnotation());
            stmt.setInt(4, image.getIsAnnotated());
            stmt.executeUpdate();
            System.out.println("Save successfully");
        }
    }
    
    //  Read images stored in the table
    public List<ImageModel> readImages() throws SQLException, IOException{
        //  Create empty imageList variable to store images retrieved from database
        List<ImageModel> imageList = new ArrayList<>();
        
        //  Query to select all images from the table
        String query = "SELECT * FROM " + TABLE_NAME; 
        
        try (
            /*  
                Declare SQL statement to execute read operation
                ResultSet to store rows retrieved from table
            */
            Statement stmt = conn.createStatement(); 
            ResultSet rs = stmt.executeQuery(query)) 
        {
            //  Loop through the result set and store the results in the array list
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
        }
        return imageList;
    }
    
    //  Update image operation
    public void updateImage(ImageModel image) throws SQLException {
        //  Query to update image based on the id
        String query = "UPDATE " + TABLE_NAME + " SET " + FILENAME + " = ?, " + ANNOTATION + " = ?, " + ISANNOTATED + " = ? WHERE " + ID + " = ?"; 
        
        //  Prepared statement to execute update query
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, image.getFileName());
            stmt.setString(2, image.getAnnotation());
            stmt.setInt(3, image.getIsAnnotated());
            stmt.setInt(4, image.getId());
            stmt.executeUpdate();
            System.out.println("Update successfully");
        }
    }
    
    //  Delete image operation
    public void deleteImage(int id) throws SQLException {
        //  Query to delete image from the table
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = ?";
        
        //  Prepared statement to execute delete query
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Delete successfully");
        }
    }
    
    //  Check database connection
    public boolean isDbConnected(){
        try {
            return !conn.isClosed();
        }catch(SQLException ex) {
            return false;
        }
    }
}
