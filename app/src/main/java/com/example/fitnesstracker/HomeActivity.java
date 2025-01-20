package com.example.fitnesstracker;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

import com.example.fitnesstracker.utils.DateManager;
import com.example.fitnesstracker.data.FoodDatabase;
import com.example.fitnesstracker.models.UserMetrics;
import com.example.fitnesstracker.models.MacroTargets;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class HomeActivity extends AppCompatActivity {
    private int waterCount = 0;
    private TextView waterCountText;
    private ImageButton[] waterBottles;
    private ProgressBar carbsProgressBar, proteinProgressBar, fatProgressBar;
    private TextView carbsProgressText, proteinProgressText, fatProgressText;
    private DateManager dateManager;
    private TextView currentDateText;
    private MacroTargets macroTargets;
    private FoodDatabase foodDb;
    private CircularProgressIndicator caloriesProgress;
    private TextView caloriesRemainingText;
    private BroadcastReceiver foodUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        caloriesProgress = findViewById(R.id.caloriesProgress);
        caloriesRemainingText = findViewById(R.id.caloriesRemaining);

        // Initialize database and date manager first
        foodDb = new FoodDatabase(this);
        dateManager = new DateManager();
        
        // Get user metrics
        UserMetrics userMetrics = new UserMetrics(30, 170, 70, "male", "moderately active");
        macroTargets = userMetrics.calculateMacroTargets();

        // Setup UI components
        setupDateNavigation();
        setupWaterTracking();
        setupMealCards();
        setupMacroProgress();
        registerFoodUpdateReceiver();
    }

    private void setupWaterTracking() {
        waterCountText = findViewById(R.id.waterCount);
        waterBottles = new ImageButton[]{
            findViewById(R.id.waterBottle1),
            findViewById(R.id.waterBottle2),
            findViewById(R.id.waterBottle3),
            findViewById(R.id.waterBottle4),
            findViewById(R.id.waterBottle5)
        };

        for (int i = 0; i < waterBottles.length; i++) {
            final int bottleIndex = i;
            waterBottles[i].setOnClickListener(v -> updateWaterCount(bottleIndex + 1));
        }
    }

    private void setupMealCards() {
        setupMealCard(R.id.breakfastCard, "Breakfast", R.string.breakfast_calories);
        setupMealCard(R.id.lunchCard, "Lunch", R.string.lunch_calories);
        setupMealCard(R.id.dinnerCard, "Dinner", R.string.dinner_calories);
        setupMealCard(R.id.snacksCard, "Snacks", R.string.snacks_calories);
    }

    private void setupMealCard(int cardId, String title, int caloriesStringId) {
        View cardView = findViewById(cardId);
        TextView titleText = cardView.findViewById(R.id.mealTitle);
        TextView caloriesText = cardView.findViewById(R.id.mealCalories);
        ImageView mealIcon = cardView.findViewById(R.id.mealIcon);
        ImageButton addButton = cardView.findViewById(R.id.addFoodButton);

        titleText.setText(title);
        caloriesText.setText(getString(caloriesStringId));

        // Set appropriate icon based on meal type
        int iconResource;
        switch (title) {
            case "Lunch":
                iconResource = R.drawable.lunch_icon;
                break;
            case "Dinner":
                iconResource = R.drawable.dinner_icon;
                break;
            case "Snacks":
                iconResource = R.drawable.snack_icon;
                break;
            default: // Breakfast
                iconResource = R.drawable.breakfast_icon;
                break;
        }
        mealIcon.setImageResource(iconResource);

        // Setup add button click listener
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("MEAL_TYPE", title.toUpperCase());
            startActivity(intent);
        });
    }

    private void updateWaterCount(int count) {
        waterCount = count;
        for (int i = 0; i < waterBottles.length; i++) {
            waterBottles[i].setAlpha(i < waterCount ? 1.0f : 0.3f);
        }
        waterCountText.setText(String.format("Water (%d l)", waterCount));
    }

    private void setupMacroProgress() {
        // Initialize progress bars
        carbsProgressBar = findViewById(R.id.carbsProgressBar);
        proteinProgressBar = findViewById(R.id.proteinProgressBar);
        fatProgressBar = findViewById(R.id.fatProgressBar);

        // Initialize progress texts
        carbsProgressText = findViewById(R.id.carbsProgress);
        proteinProgressText = findViewById(R.id.proteinProgress);
        fatProgressText = findViewById(R.id.fatProgress);

        // Set initial progress (you can update these values when food is added)
        updateMacroProgress();
    }

    private void updateMacroProgress() {
        String currentDate = dateManager.getCurrentDateFormatted();
        
        // Get consumed macros from database
        int consumedCarbs = foodDb.getTotalMacroForDate("carbs", currentDate);
        int consumedProtein = foodDb.getTotalMacroForDate("protein", currentDate);
        int consumedFat = foodDb.getTotalMacroForDate("fat", currentDate);
        
        // Update progress bars and text
        carbsProgressBar.setMax(macroTargets.getCarbsTarget());
        proteinProgressBar.setMax(macroTargets.getProteinTarget());
        fatProgressBar.setMax(macroTargets.getFatTarget());

        carbsProgressBar.setProgress(consumedCarbs);
        proteinProgressBar.setProgress(consumedProtein);
        fatProgressBar.setProgress(consumedFat);

        carbsProgressText.setText(String.format("%d/%dg", consumedCarbs, macroTargets.getCarbsTarget()));
        proteinProgressText.setText(String.format("%d/%dg", consumedProtein, macroTargets.getProteinTarget()));
        fatProgressText.setText(String.format("%d/%dg", consumedFat, macroTargets.getFatTarget()));

        // Update calories
        int consumedCalories = foodDb.getTotalCaloriesForDate(currentDate);
        int remainingCalories = macroTargets.getCalorieTarget() - consumedCalories;
        int calorieTarget = macroTargets.getCalorieTarget();
        
        int caloriePercentage = (int)(((float)consumedCalories / calorieTarget) * 100);
        
        if (caloriesProgress != null) {
            caloriesProgress.setProgress(caloriePercentage);
        }
        
        if (caloriesRemainingText != null) {
            caloriesRemainingText.setText(String.format("%d", remainingCalories));
        }
    }

    private void setupDateNavigation() {
        currentDateText = findViewById(R.id.currentDate);
        
        // Set initial date
        updateDateDisplay();
        
        // Setup navigation buttons
        ImageButton prevButton = findViewById(R.id.previousDay);
        ImageButton nextButton = findViewById(R.id.nextDay);
        View dateContainer = findViewById(R.id.dateContainer);
        
        if (prevButton != null) {
            prevButton.setOnClickListener(v -> {
                dateManager.previousDay();
                updateDateDisplay();
            });
        }
        
        if (nextButton != null) {
            nextButton.setOnClickListener(v -> {
                dateManager.nextDay();
                updateDateDisplay();
            });
        }
        
        if (dateContainer != null) {
            dateContainer.setOnClickListener(v -> showDatePicker());
        }
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                dateManager.setDate(year, month, dayOfMonth);
                updateDateDisplay();
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        if (currentDateText != null) {
            currentDateText.setText(dateManager.getFormattedDate());
        }
    }

    private void registerFoodUpdateReceiver() {
        foodUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateMacroProgress();
            }
        };
        registerReceiver(foodUpdateReceiver, 
            new IntentFilter("com.example.fitnesstracker.FOOD_ADDED"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (foodUpdateReceiver != null) {
            unregisterReceiver(foodUpdateReceiver);
        }
    }
}
