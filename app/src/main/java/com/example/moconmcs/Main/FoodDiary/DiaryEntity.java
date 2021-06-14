package com.example.moconmcs.Main.FoodDiary;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Entity
public class DiaryEntity {

    @PrimaryKey
    private final long date;

    private String breakfast;
    private String lunch;
    private String dinner;
    private final String uid;

    public DiaryEntity(String uid, long date, String breakfast, String lunch, String dinner) {
        this.uid = uid;
        this.date = date;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public static long toDate(int y, int m, int d) {
        return new GregorianCalendar(y, m, d).getTimeInMillis() / (1000 * 3600 * 24L);
    }

    public static long toDate(Calendar calendar) {
        return calendar.getTimeInMillis() / (1000 * 3600 * 24L);
    }

    public long getDate() {
        return date;
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

    @NotNull
    @Override
    public String toString() {
        return "DiaryEntity{" +
                "date=" + date +
                ", breakfast='" + breakfast + '\'' +
                ", lunch='" + lunch + '\'' +
                ", dinner='" + dinner + '\'' +
                '}';
    }

    public String getUid() {
        return uid;
    }
}
