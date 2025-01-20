package com.example.fitnesstracker.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchResponse {
    @SerializedName("foods")
    private List<FoodResponse> foods;

    public List<FoodResponse> getFoods() {
        return foods;
    }
} 