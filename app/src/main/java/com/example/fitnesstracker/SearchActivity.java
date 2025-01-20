package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstracker.adapters.FoodSearchAdapter;
import com.example.fitnesstracker.api.FoodApiService;
import com.example.fitnesstracker.api.SearchResponse;
import com.example.fitnesstracker.models.Food;
import com.example.fitnesstracker.api.FoodResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity implements FoodSearchAdapter.OnFoodClickListener {
    static final String API_KEY = "VurbT0regQC96uO16roDEEkDGXDrDNwwDF9TgMEQ";
    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1/";
    
    private RecyclerView foodList;
    private FoodSearchAdapter adapter;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private FoodApiService apiService;
    private Call<SearchResponse> currentCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupRetrofit();
        setupViews();
        setupRecyclerView();
        setupSearch();
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        
        apiService = retrofit.create(FoodApiService.class);
    }

    private void setupViews() {
        String mealType = getIntent().getStringExtra("MEAL_TYPE");
        TextView mealTypeTitle = findViewById(R.id.mealTypeTitle);
        mealTypeTitle.setText(mealType);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        View barcodeButton = findViewById(R.id.barcodeButton);
        if (barcodeButton != null) {
            barcodeButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(this, BarcodeScannerActivity.class);
                    intent.putExtra("MEAL_TYPE", mealType);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("SearchActivity", "Error launching scanner: " + e.getMessage());
                    Toast.makeText(this, "Error launching scanner", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("SearchActivity", "Barcode button not found");
        }
    }

    private void setupRecyclerView() {
        foodList = findViewById(R.id.foodList);
        foodList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodSearchAdapter(this);
        foodList.setAdapter(adapter);
    }

    private void setupSearch() {
        EditText searchInput = findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> performSearch(s.toString());
                searchHandler.postDelayed(searchRunnable, 100);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch(String query) {
        if (query.length() < 3) {
            adapter.setFoods(new ArrayList<>());
            return;
        }

        if (currentCall != null) {
            currentCall.cancel();
        }

        currentCall = apiService.searchFoods(API_KEY, query, 50);
        currentCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Food> foods = response.body().getFoods().stream()
                                .map(FoodResponse::toFood)
                                .collect(Collectors.toList());
                        adapter.setFoods(foods);
                        
                        // Add logging to debug
                        Log.d("SearchActivity", "Found " + foods.size() + " foods");
                        
                        // Make sure the RecyclerView is visible
                        foodList.setVisibility(View.VISIBLE);
                    } else {
                        showError("Search failed: " + response.message());
                    }
                });
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    if (!call.isCanceled()) {
                        showError("Network error: " + t.getMessage());
                    }
                });
            }
        });
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentCall != null) {
            currentCall.cancel();
        }
    }

    @Override
    public void onFoodClick(Food food) {
        Intent intent = new Intent(this, FoodDetailsActivity.class);
        intent.putExtra("FDC_ID", food.getFdcId());
        intent.putExtra("MEAL_TYPE", getIntent().getStringExtra("MEAL_TYPE"));
        startActivity(intent);
    }
}
