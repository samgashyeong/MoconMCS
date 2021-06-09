package com.example.moconmcs.Main.FoodDiary;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface DiaryDao {
    @Query("SELECT * FROM DiaryEntity")
    List<DiaryEntity> getAll();

    @Insert
    void insertAll(DiaryEntity... diaryEntities);

    @Delete
    void delete(DiaryEntity diaryEntity);
}
