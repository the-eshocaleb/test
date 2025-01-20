package com.example.fitnesstracker.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface FoodApiService {
    @GET("foods/search")
    Call<SearchResponse> searchFoods(
        @Query("api_key") String apiKey,
        @Query("query") String query,
        @Query("pageSize") int pageSize
    );

    @GET("food/{fdcId}")
    Call<FoodResponse> getFoodDetails(
        @Path("fdcId") String fdcId,
        @Query("api_key") String apiKey
    );

    @GET("foods/search")
    Call<SearchResponse> searchFoodByBarcode(
        @Query("query") String barcode,
        @Query("dataType") String dataType,
        @Query("pageSize") int pageSize,
        @Query("api_key") String apiKey
    );
} 