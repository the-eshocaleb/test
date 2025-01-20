package com.example.fitnesstracker;


import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.content.Intent;

import com.example.fitnesstracker.api.FoodApiService;
import com.example.fitnesstracker.api.FoodResponse;
import com.example.fitnesstracker.api.SearchResponse;
import com.example.fitnesstracker.data.FoodDatabase;
import com.example.fitnesstracker.models.Food;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class FoodDetailsActivity extends AppCompatActivity {
    private static final String API_KEY = "VurbT0regQC96uO16roDEEkDGXDrDNwwDF9TgMEQ";
    private TextView foodTitle;
    private TextView servingSize;
    private TextView carbsValue;
    private TextView proteinValue;
    private TextView fatValue;
    private Button addFoodButton;
    private FoodApiService apiService;
    private FoodDatabase foodDb;
    private Food currentFood;
    private String mealType;
    private EditText quantityInput;
    private double baseQuantity = 100.0; // Base quantity in grams

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        setupRetrofit();
        setupViews();
        foodDb = new FoodDatabase(this);
        mealType = getIntent().getStringExtra("MEAL_TYPE");
        loadFoodDetails();
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nal.usda.gov/fdc/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(FoodApiService.class);
    }

    private void setupViews() {
        foodTitle = findViewById(R.id.foodTitle);
        servingSize = findViewById(R.id.servingSize);
        carbsValue = findViewById(R.id.carbsValue);
        proteinValue = findViewById(R.id.proteinValue);
        fatValue = findViewById(R.id.fatValue);
        addFoodButton = findViewById(R.id.addFoodButton);
        quantityInput = findViewById(R.id.quantityInput);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        addFoodButton.setOnClickListener(v -> addFoodToDiary());
        
        // Add text change listener for quantity
        quantityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double grams = s.length() > 0 ? Double.parseDouble(s.toString()) : 0;
                    updateNutrientsForServing(grams);
                } catch (NumberFormatException e) {
                    quantityInput.setText("100");
                }
            }
        });
    }

    private void loadFoodDetails() {
        String barcode = getIntent().getStringExtra("BARCODE");
        if (barcode != null) {
            // Search by barcode using UPC code
            apiService.searchFoodByBarcode(
                barcode,           // Remove "upc:" prefix
                "Branded",        // Only search branded foods
                1,               // Limit to 1 result
                API_KEY
            ).enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    if (response.isSuccessful() && response.body() != null 
                        && !response.body().getFoods().isEmpty()) {
                        
                        FoodResponse foodResponse = response.body().getFoods().get(0);
                        currentFood = foodResponse.toFood();
                        updateUI(currentFood);
                    } else {
                        // Try alternative search if first attempt fails
                        searchWithAlternativeFormat(barcode);
                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    showError("Network error: " + t.getMessage());
                    finish();
                }
            });
            return;
        }
        
        // Continue with existing fdcId search if no barcode
        String fdcId = getIntent().getStringExtra("FDC_ID");
        if (fdcId != null) {
            // Existing code for fdcId search
            apiService.getFoodDetails(fdcId, API_KEY).enqueue(new Callback<FoodResponse>() {
                @Override
                public void onResponse(Call<FoodResponse> call, Response<FoodResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        currentFood = response.body().toFood();
                        updateUI(currentFood);
                    } else {
                        showError("Failed to load food details");
                    }
                }

                @Override
                public void onFailure(Call<FoodResponse> call, Throwable t) {
                    showError("Network error: " + t.getMessage());
                }
            });
        }
    }

    private void searchWithAlternativeFormat(String barcode) {
        // Try with "upc:" prefix
        apiService.searchFoodByBarcode(
            "upc:" + barcode,
            "Branded",
            1,
            API_KEY
        ).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null 
                    && !response.body().getFoods().isEmpty()) {
                    
                    FoodResponse foodResponse = response.body().getFoods().get(0);
                    currentFood = foodResponse.toFood();
                    updateUI(currentFood);
                } else {
                    showError("No food found with this barcode");
                    finish();
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                showError("Network error: " + t.getMessage());
                finish();
            }
        });
    }

    private void updateUI(Food food) {
        runOnUiThread(() -> {
            foodTitle.setText(food.getDescription());
            servingSize.setText(food.getServingSize());

            // Calculate macro percentages
            double totalMacros = food.getTotalCarbs() + food.getTotalProtein() + food.getTotalFat();
            int carbsPercent = (int)((food.getTotalCarbs() / totalMacros) * 100);
            int proteinPercent = (int)((food.getTotalProtein() / totalMacros) * 100);
            int fatPercent = (int)((food.getTotalFat() / totalMacros) * 100);

            // Update macro circles
            updateMacroCircle(findViewById(R.id.carbsCircle), carbsPercent, R.color.carbs_color);
            updateMacroCircle(findViewById(R.id.proteinCircle), proteinPercent, R.color.protein_color);
            updateMacroCircle(findViewById(R.id.fatCircle), fatPercent, R.color.fat_color);

            // Update all nutrition rows
            LinearLayout nutritionContainer = findViewById(R.id.nutritionContainer);
            nutritionContainer.removeAllViews();

            // Add main nutrition info
            addNutritionRow(nutritionContainer, "Calories", String.format("%.0f kcal", food.getTotalCalories()));
            addNutritionRow(nutritionContainer, "Carbs", String.format("%.1f g", food.getTotalCarbs()));
            addNutritionRow(nutritionContainer, "Fiber", String.format("%.1f g", food.getFiber()));
            addNutritionRow(nutritionContainer, "Sugars", String.format("%.1f g", food.getSugars()));
            
            addNutritionRow(nutritionContainer, "Protein", String.format("%.1f g", food.getTotalProtein()));
            
            addNutritionRow(nutritionContainer, "Fat", String.format("%.1f g", food.getTotalFat()));
            addNutritionRow(nutritionContainer, "Saturated fat", String.format("%.1f g", food.getSaturatedFat()));
            addNutritionRow(nutritionContainer, "Unsaturated fat", String.format("%.1f g", food.getUnsaturatedFat()));
            
            addNutritionRow(nutritionContainer, "Cholesterol", String.format("%.1f mg", food.getCholesterol()));
            addNutritionRow(nutritionContainer, "Sodium", String.format("%.1f mg", food.getSodium()));
            addNutritionRow(nutritionContainer, "Potassium", String.format("%.1f mg", food.getPotassium()));
        });
    }

    private void addNutritionRow(LinearLayout container, String label, String value) {
        View row = getLayoutInflater().inflate(R.layout.nutrition_row, container, false);
        ((TextView) row.findViewById(R.id.nutritionLabel)).setText(label);
        ((TextView) row.findViewById(R.id.nutritionValue)).setText(value);
        container.addView(row);
    }

    private void updateMacroCircle(CircularProgressIndicator indicator, int percentage, int colorResId) {
        indicator.setProgress(percentage);
        indicator.setIndicatorColor(getResources().getColor(colorResId, getTheme()));
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void addFoodToDiary() {
        if (currentFood == null) return;
        
        try {
            double grams = Double.parseDouble(quantityInput.getText().toString());
            double ratio = grams / 100.0;
            
            // Create a new food object with scaled nutrients
            Food trackedFood = new Food(
                currentFood.getFdcId(),
                currentFood.getDescription(),
                currentFood.getBrandOwner(),
                grams + "g",
                currentFood.getCalories() * ratio,
                currentFood.getProtein() * ratio,
                currentFood.getCarbs() * ratio,
                currentFood.getFat() * ratio,
                currentFood.getFiber() * ratio,
                currentFood.getSugars() * ratio,
                currentFood.getSaturatedFat() * ratio,
                currentFood.getUnsaturatedFat() * ratio,
                currentFood.getCholesterol() * ratio,
                currentFood.getSodium() * ratio,
                currentFood.getPotassium() * ratio
            );
            
            long result = foodDb.addTrackedFood(trackedFood, mealType);
            if (result != -1) {
                Intent intent = new Intent("com.example.fitnesstracker.FOOD_ADDED");
                sendBroadcast(intent);
                Toast.makeText(this, "Food added to " + mealType.toLowerCase(), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error adding food", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNutrientsForServing(double grams) {
        if (currentFood == null) return;
        
        // Calculate ratio based on input grams
        double ratio = grams / 100.0; // since base quantity is 100g
        
        // Create a temporary food object for display
        Food displayFood = new Food(
            currentFood.getFdcId(),
            currentFood.getDescription(),
            currentFood.getBrandOwner(),
            currentFood.getServingSize(),
            currentFood.getCalories(),
            currentFood.getProtein(),
            currentFood.getCarbs(),
            currentFood.getFat(),
            currentFood.getFiber(),
            currentFood.getSugars(),
            currentFood.getSaturatedFat(),
            currentFood.getUnsaturatedFat(),
            currentFood.getCholesterol(),
            currentFood.getSodium(),
            currentFood.getPotassium()
        );
        
        // Update nutrients for display
        displayFood.updateNutrientsForServing(ratio);
        
        // Update UI with scaled values
        updateUI(displayFood);
    }

    private void setupServingSizes(List<String> servingSizes, Map<String, Double> servingSizeGrams) {
        // This method is no longer used in the updated implementation
    }
}
