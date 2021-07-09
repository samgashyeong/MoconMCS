package com.example.moconmcs.Main.SearchFood.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moconmcs.data.KyungrokApi.Material
import java.io.Serializable

@Entity
data class FoodListEntity(
    @PrimaryKey val foodNum : String,
    val foodName : String,
    val foodResult : String
) : Serializable