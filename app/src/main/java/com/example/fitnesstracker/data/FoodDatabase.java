package com.example.fitnesstracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fitnesstracker.models.Food;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FoodDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FoodTracker.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_TRACKED_FOODS = "tracked_foods";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FDC_ID = "fdc_id";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_BRAND = "brand";
    private static final String COLUMN_SERVING_SIZE = "serving_size";
    private static final String COLUMN_CALORIES = "calories";
    private static final String COLUMN_CARBS = "carbs";
    private static final String COLUMN_PROTEIN = "protein";
    private static final String COLUMN_FAT = "fat";
    private static final String COLUMN_FIBER = "fiber";
    private static final String COLUMN_SUGARS = "sugars";
    private static final String COLUMN_SATURATED_FAT = "saturated_fat";
    private static final String COLUMN_UNSATURATED_FAT = "unsaturated_fat";
    private static final String COLUMN_CHOLESTEROL = "cholesterol";
    private static final String COLUMN_SODIUM = "sodium";
    private static final String COLUMN_POTASSIUM = "potassium";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_MEAL_TYPE = "meal_type";
    private static final String COLUMN_DATE = "date";

    public FoodDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRACKED_FOODS_TABLE = "CREATE TABLE " + TABLE_TRACKED_FOODS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_FDC_ID + " TEXT, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_BRAND + " TEXT, "
            + COLUMN_SERVING_SIZE + " REAL, "
            + COLUMN_QUANTITY + " REAL, "
            + COLUMN_CALORIES + " REAL, "
            + COLUMN_PROTEIN + " REAL, "
            + COLUMN_CARBS + " REAL, "
            + COLUMN_FAT + " REAL, "
            + COLUMN_FIBER + " REAL, "
            + COLUMN_SUGARS + " REAL, "
            + COLUMN_SATURATED_FAT + " REAL, "
            + COLUMN_UNSATURATED_FAT + " REAL, "
            + COLUMN_CHOLESTEROL + " REAL, "
            + COLUMN_SODIUM + " REAL, "
            + COLUMN_POTASSIUM + " REAL, "
            + COLUMN_DATE + " TEXT, "
            + COLUMN_MEAL_TYPE + " TEXT)";
        
        db.execSQL(CREATE_TRACKED_FOODS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKED_FOODS);
        onCreate(db);
    }

    public long addTrackedFood(Food food, String mealType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Add current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        values.put(COLUMN_DATE, currentDate);

        values.put(COLUMN_FDC_ID, food.getFdcId());
        values.put(COLUMN_DESCRIPTION, food.getDescription());
        values.put(COLUMN_BRAND, food.getBrandOwner());
        values.put(COLUMN_SERVING_SIZE, food.getServingSize());
        values.put(COLUMN_CALORIES, food.getTotalCalories());
        values.put(COLUMN_CARBS, food.getTotalCarbs());
        values.put(COLUMN_PROTEIN, food.getTotalProtein());
        values.put(COLUMN_FAT, food.getTotalFat());
        values.put(COLUMN_FIBER, food.getFiber());
        values.put(COLUMN_SUGARS, food.getSugars());
        values.put(COLUMN_SATURATED_FAT, food.getSaturatedFat());
        values.put(COLUMN_UNSATURATED_FAT, food.getUnsaturatedFat());
        values.put(COLUMN_CHOLESTEROL, food.getCholesterol());
        values.put(COLUMN_SODIUM, food.getSodium());
        values.put(COLUMN_POTASSIUM, food.getPotassium());
        values.put(COLUMN_MEAL_TYPE, mealType);

        long result = db.insert(TABLE_TRACKED_FOODS, null, values);
        db.close();
        return result;
    }

    public int getTotalMacroForDate(String macroType, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String columnName = "";
        
        switch (macroType.toLowerCase()) {
            case "carbs":
                columnName = COLUMN_CARBS;
                break;
            case "protein":
                columnName = COLUMN_PROTEIN;
                break;
            case "fat":
                columnName = COLUMN_FAT;
                break;
            default:
                return 0;
        }
        
        String query = "SELECT SUM(" + columnName + ") FROM " + TABLE_TRACKED_FOODS +
                      " WHERE " + COLUMN_DATE + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{date});
        int total = 0;
        
        if (cursor.moveToFirst()) {
            total = (int) cursor.getDouble(0);
        }
        
        cursor.close();
        return total;
    }

    public int getTotalCaloriesForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_CALORIES + ") FROM " + TABLE_TRACKED_FOODS +
                      " WHERE " + COLUMN_DATE + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{date});
        int total = 0;
        
        if (cursor.moveToFirst()) {
            total = (int) cursor.getDouble(0);
        }
        
        cursor.close();
        return total;
    }
}