package com.mobdeve.xx22.pascualreynadojaime.mco.myapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Name and Version
    private static final String DATABASE_NAME = "explorePage.db";
    private static final int DATABASE_VERSION = 3; // Incremented version for new table

    // Table Names
    private static final String TABLE_POSTS = "posts";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_RESERVATIONS = "reservations"; // New table for reservations

    // Columns for TABLE_POSTS
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_BAR_NAME = "bar_name";
    private static final String COLUMN_POST_CAPTION = "post_caption";
    private static final String COLUMN_POST_PHOTO = "post_photo";
    private static final String COLUMN_PROFILE_PICTURE = "profile_picture";

    // Columns for TABLE_USERS
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";

    // Columns for TABLE_RESERVATIONS
    private static final String COLUMN_RESERVATION_ID = "reservation_id";
    private static final String COLUMN_RESERVATION_USER_ID = "user_id";
    private static final String COLUMN_RESERVATION_BAR_NAME = "bar_name";
    private static final String COLUMN_RESERVATION_DATE = "date";
    private static final String COLUMN_RESERVATION_PAX = "pax";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table for posts
        String CREATE_TABLE_POSTS = "CREATE TABLE " + TABLE_POSTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BAR_NAME + " TEXT, "
                + COLUMN_POST_CAPTION + " TEXT, "
                + COLUMN_POST_PHOTO + " TEXT, "
                + COLUMN_PROFILE_PICTURE + " TEXT)";
        db.execSQL(CREATE_TABLE_POSTS);

        // Create table for users
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_EMAIL + " TEXT UNIQUE, "
                + COLUMN_USER_PASSWORD + " TEXT)";
        db.execSQL(CREATE_TABLE_USERS);

        // Create table for reservations
        String CREATE_TABLE_RESERVATIONS = "CREATE TABLE " + TABLE_RESERVATIONS + " ("
                + COLUMN_RESERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_RESERVATION_USER_ID + " INTEGER, "
                + COLUMN_RESERVATION_BAR_NAME + " TEXT, "
                + COLUMN_RESERVATION_DATE + " TEXT, "
                + COLUMN_RESERVATION_PAX + " INTEGER, "
                + "FOREIGN KEY(" + COLUMN_RESERVATION_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(CREATE_TABLE_RESERVATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS); // Drop reservations table
        // Recreate tables
        onCreate(db);
    }

    // Methods for TABLE_POSTS remain unchanged (addPost, getAllPosts, updatePost, deletePost)

    // Methods for TABLE_USERS
    public void addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);

        // Check if user already exists to avoid duplicate entries
        Cursor cursor = getUserByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return; // User already exists, don't add again
        }

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_USER_EMAIL + "=?",
                new String[]{email}, null, null, null);
    }

    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
    }


    public void updateUserPassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PASSWORD, newPassword);

        db.update(TABLE_USERS, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public void deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    // Methods for TABLE_RESERVATIONS
    public void addReservation(int userId, String barName, String date, int pax) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RESERVATION_USER_ID, userId);
        values.put(COLUMN_RESERVATION_BAR_NAME, barName);
        values.put(COLUMN_RESERVATION_DATE, date);
        values.put(COLUMN_RESERVATION_PAX, pax);

        db.insert(TABLE_RESERVATIONS, null, values);
        db.close();
    }

    public Cursor getReservationsByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RESERVATIONS, null, COLUMN_RESERVATION_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
    }

    public void deleteReservation(int reservationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RESERVATIONS, COLUMN_RESERVATION_ID + "=?", new String[]{String.valueOf(reservationId)});
        db.close();
    }

    public Cursor getPostByBarName(String barName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_POSTS, null, COLUMN_BAR_NAME + "=?",
                new String[]{barName}, null, null, null);
    }

    // Add a new post to the database
    public void addPost(String barName, String caption, int profilePicture, int postPhoto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BAR_NAME, barName);
        values.put(COLUMN_POST_CAPTION, caption);
        values.put(COLUMN_PROFILE_PICTURE, profilePicture);
        values.put(COLUMN_POST_PHOTO, postPhoto);

        db.insert(TABLE_POSTS, null, values);
        db.close();
    }

    // Retrieve all posts from the database
    public List<Post> getAllPosts() {
        List<Post> postList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSTS, null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String barName = cursor.getString(cursor.getColumnIndex(COLUMN_BAR_NAME));
                @SuppressLint("Range") String caption = cursor.getString(cursor.getColumnIndex(COLUMN_POST_CAPTION));
                @SuppressLint("Range") int profilePicture = cursor.getInt(cursor.getColumnIndex(COLUMN_PROFILE_PICTURE));
                @SuppressLint("Range") int postPhoto = cursor.getInt(cursor.getColumnIndex(COLUMN_POST_PHOTO));

                postList.add(new Post(barName, caption, profilePicture, postPhoto));
            }
            cursor.close();
        }

        db.close();
        return postList;
    }


}
