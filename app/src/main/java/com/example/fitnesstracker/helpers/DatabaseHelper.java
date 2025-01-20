package com.example.fitnesstracker.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fitnesstracker.models.User;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FitnessTracker.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "firstName TEXT,"
                + "username TEXT UNIQUE,"
                + "dateOfBirth TEXT,"
                + "sex TEXT,"
                + "height INTEGER,"
                + "weight REAL,"
                + "targetWeight REAL,"
                + "goal TEXT,"
                + "activityLevel TEXT,"
                + "progressRate REAL,"
                + "email TEXT UNIQUE,"
                + "password TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put("firstName", user.getFirstName());
        values.put("username", user.getUsername());
        values.put("dateOfBirth", user.getDateOfBirth());
        values.put("sex", user.getSex());
        values.put("height", user.getHeight());
        values.put("weight", user.getWeight());
        values.put("targetWeight", user.getTargetWeight());
        values.put("goal", user.getGoal());
        values.put("activityLevel", user.getActivityLevel());
        values.put("progressRate", user.getProgressRate());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }
} 