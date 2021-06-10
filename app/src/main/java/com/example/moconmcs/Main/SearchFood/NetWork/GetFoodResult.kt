package com.example.moconmcs.Main.SearchFood.NetWork

import com.example.moconmcs.data.KyungrokApi.FoodData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GetFoodResult {
    @GET("{FoodNumber}")
    fun isExecutionCode(@Path("FoodNumber") FoodNumber : String) : Call<FoodData>

    @GET("report/{FoodNumber}/error")
    fun errorResult(@Path("FoodNumber") FoodNumber: String) : Call<FoodData>
}