package com.example.moconmcs.Main.FoodDiary;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DiaryEntity {

    @PrimaryKey
    private long date;

    private String breakfast, lunch, dinner;

    public DiaryEntity(long date, String breakfast, String lunch, String dinner) {
        this.date = date / (1000 * 3600 * 24L);
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }
}
