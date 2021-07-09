package com.example.moconmcs.Main.SearchFood.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.moconmcs.Main.FoodDiary.DiaryEntity

@Dao
interface FoodListDao {
    @Query("SELECT * FROM FoodListEntity")
    fun getAll(): List<FoodListEntity>?

    @Insert
    fun insert(vararg foodListEntity: FoodListEntity?)

    @Delete
    fun delete(foodListEntity: FoodListEntity)
}