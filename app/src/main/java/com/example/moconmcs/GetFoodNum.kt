package com.example.moconmcs

import com.example.moconmcs.data.C005
import com.example.moconmcs.data.Row
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GetFoodNum {
    @GET("/1/1/BAR_CD={BarcodeNum}")
    fun getFoodCreateNum(@Path("BarCodeNum") BarCodeNum : String) : Call<Row>

    @GET("/1/1/BAR_CD={BarcodeNum}")
    fun isExecutionCode(@Path("BarCodeNum") BarCodeNum : String) : Call<C005>
}