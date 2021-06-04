package com.example.moconmcs.Main.SearchFood

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moconmcs.data.KyungrokApi.FoodData

class FoodViewModel : ViewModel(){
    var foodResult : MutableLiveData<FoodData> = MutableLiveData()
}