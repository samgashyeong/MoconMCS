package com.example.moconmcs.Main;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.moconmcs.Main.FoodDiary.DiaryDao;
import com.example.moconmcs.Main.FoodDiary.DiaryEntity;

@Database(entities = {DiaryEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DiaryDao diaryDao();

    private static AppDatabase instance = null;

    public static AppDatabase getInstance(Context applicationContext) {
        if(instance == null) instance = Room.databaseBuilder(applicationContext,
                AppDatabase.class, "v_check")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        return instance;
    }

}
