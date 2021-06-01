package com.example.moconmcs.Main.FoodDiary;

import androidx.lifecycle.ViewModel;

import java.time.LocalDate;

public class FoodDiaryViewModel extends ViewModel {
    private LocalDate selectedDate;

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }
}
