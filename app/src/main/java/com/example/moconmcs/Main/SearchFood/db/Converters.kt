package com.example.moconmcs.Main.SearchFood.db

import androidx.room.TypeConverter
import com.example.moconmcs.data.KyungrokApi.Material
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun listToJson(value: List<Material>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Material>::class.java).toList()
}