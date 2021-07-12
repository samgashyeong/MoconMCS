package com.example.moconmcs.Main;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.moconmcs.Main.FoodDiary.DiaryDao;
import com.example.moconmcs.Main.FoodDiary.DiaryEntity;
import com.example.moconmcs.Main.SearchFood.db.Converters;
import com.example.moconmcs.Main.SearchFood.db.FoodListDao;
import com.example.moconmcs.Main.SearchFood.db.FoodListEntity;


@TypeConverters(Converters.class)
@Database(entities = {DiaryEntity.class, FoodListEntity.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DiaryDao diaryDao();
    public abstract FoodListDao foodListDao();

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
