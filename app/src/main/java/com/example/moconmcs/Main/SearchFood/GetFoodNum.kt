package com.example.moconmcs.Main.SearchFood

import com.example.moconmcs.data.FoodNumberApi.BarCode
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GetFoodNum {

    @GET("1/1/BAR_CD={BarcodeNum}")
    fun isExecutionCode(@Path("BarcodeNum") BarCodeNum : String) : Call<BarCode>
}