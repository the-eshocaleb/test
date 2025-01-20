package com.example.fitnesstracker.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Food {
    private String fdcId;
    private String description;
    private String brandOwner;
    private String servingSize;
    private double calories;
    private double carbs;
    private double protein;
    private double fat;
    private int quantity = 1;
    private double fiber;
    private double sugars;
    private double saturatedFat;
    private double unsaturatedFat;
    private double cholesterol;
    private double sodium;
    private double potassium;

    public Food(String fdcId, String description, String brandOwner) {
        this.fdcId = fdcId;
        this.description = description;
        this.brandOwner = brandOwner;
    }

    public Food(String fdcId, String description, String brandOwner, String servingSize,
               double calories, double protein, double carbs, double fat,
               double fiber, double sugars, double saturatedFat, double unsaturatedFat,
               double cholesterol, double sodium, double potassium) {
        this.fdcId = fdcId;
        this.description = description;
        this.brandOwner = brandOwner;
        this.servingSize = servingSize;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
        this.sugars = sugars;
        this.saturatedFat = saturatedFat;
        this.unsaturatedFat = unsaturatedFat;
        this.cholesterol = cholesterol;
        this.sodium = sodium;
        this.potassium = potassium;
    }

    // Getters and setters
    public String getFdcId() { return fdcId; }
    public String getDescription() { return description; }
    public String getBrandOwner() { return brandOwner; }
    public double getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getCarbs() { return carbs; }
    public double getFat() { return fat; }
    public String getServingSize() { return servingSize; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getFiber() { return fiber; }
    public double getSugars() { return sugars; }
    public double getSaturatedFat() { return saturatedFat; }
    public double getUnsaturatedFat() { return unsaturatedFat; }
    public double getCholesterol() { return cholesterol; }
    public double getSodium() { return sodium; }
    public double getPotassium() { return potassium; }

    public void setNutrients(double calories, double protein, double carbs, double fat,
                           double fiber, double sugars, double saturatedFat, 
                           double unsaturatedFat, double cholesterol,
                           double sodium, double potassium) {
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
        this.sugars = sugars;
        this.saturatedFat = saturatedFat;
        this.unsaturatedFat = unsaturatedFat;
        this.cholesterol = cholesterol;
        this.sodium = sodium;
        this.potassium = potassium;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public double getTotalCalories() { return calories * quantity; }
    public double getTotalProtein() { return protein * quantity; }
    public double getTotalCarbs() { return carbs * quantity; }
    public double getTotalFat() { return fat * quantity; }

    public void updateNutrientsForServing(double ratio) {
        this.calories *= ratio;
        this.protein *= ratio;
        this.carbs *= ratio;
        this.fat *= ratio;
        this.fiber *= ratio;
        this.sugars *= ratio;
        this.saturatedFat *= ratio;
        this.unsaturatedFat *= ratio;
        this.cholesterol *= ratio;
        this.sodium *= ratio;
        this.potassium *= ratio;
    }
} 