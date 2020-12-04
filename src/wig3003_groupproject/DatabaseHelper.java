/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wig3003_groupproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
    
    public void insertData(ImageModel image) {
        try {
            String query = "INSERT INTO " + TABLE_NAME + " (" + FILENAME + "," + IMAGE + "," + ANNOTATION + "," + ISANNOTATED + ") " +
                        "VALUES (?,?,?,?);"; 
            PreparedStatement stmt=conn.prepareStatement(query);  
            stmt.setString(1, image.getFileName());
            stmt.setBytes(2, image.getImageByteArray());
            stmt.setString(3, image.getAnnotation());
            stmt.setInt(4, image.isIsAnnotated());
            stmt.executeUpdate();
            System.out.println("Save successfully");
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
