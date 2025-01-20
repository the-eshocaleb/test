package com.example.fitnesstracker.api;

import com.google.gson.annotations.SerializedName;
import com.example.fitnesstracker.models.Food;

public class FoodResponse {
    @SerializedName("fdcId")
    private String fdcId;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("brandOwner")
    private String brandOwner;
    
    @SerializedName("servingSize")
    private String servingSize;

    public Food toFood() {
        Food food = new Food(fdcId, description, brandOwner);
        food.setServingSize(servingSize);
        return food;
    }
} 