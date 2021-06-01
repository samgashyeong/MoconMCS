package com.example.moconmcs.Main.FoodDiary;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moconmcs.R;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FoodDiaryFragment extends Fragment implements CalenderAdapter.OnItemListener {

    Button prevMonth, nextMonth, prevDay, nextDay;
    TextView dateText, mainDateText;
    RecyclerView calenderRecyclerView;
    LocalDate currentDate; //다이얼로그 안에서 쓰일 날짜 인스턴스
    LocalDate selectedDate; //다이어리를 보여줄 날짜
    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_diary, container, false);

        currentDate = LocalDate.now();
        selectedDate = LocalDate.now();

        mainDateText = view.findViewById(R.id.mainDateText);
        prevDay = view.findViewById(R.id.diaryPrevDay);
        nextDay = view.findViewById(R.id.diaryNextDay);

        updateMainDateText();

        mainDateText.setOnClickListener(v -> openCalenderDialog());
        prevDay.setOnClickListener(v -> {
            selectedDate = selectedDate.minusDays(1);
            updateMainDateText();
        });
        nextDay.setOnClickListener(v -> {
            selectedDate = selectedDate.plusDays(1);
            updateMainDateText();
        });

        return view;
    }

    private void openCalenderDialog() {
        currentDate = LocalDate.from(selectedDate);
        dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.layout_custom_calender);

        prevMonth = dialog.findViewById(R.id.prevMonth);
        nextMonth = dialog.findViewById(R.id.nextMonth);
        dateText = dialog.findViewById(R.id.curDateText);
        calenderRecyclerView = dialog.findViewById(R.id.calRecycler);

        prevMonth.setOnClickListener(v -> {
            currentDate = currentDate.minusMonths(1);
            setCalenderView();
        });

        nextMonth.setOnClickListener(v -> {
            currentDate = currentDate.plusMonths(1);
            setCalenderView();
        });

        setCalenderView();

        dialog.show();
    }

    private void setCalenderView() {
        dateText.setText(DateTimeFormatter.ofPattern("yyyy년 MM월").format(currentDate));
        updateMainDateText();

        ArrayList<LocalDate> daysInMonth = getDaysInMonth(currentDate);
        CalenderAdapter calenderAdapter = new CalenderAdapter(selectedDate, daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(
                requireActivity().getApplicationContext(), 7);

        calenderRecyclerView.setLayoutManager(layoutManager);
        calenderRecyclerView.setAdapter(calenderAdapter);
    }

    private ArrayList<LocalDate> getDaysInMonth(LocalDate localDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(localDate);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfWeek = currentDate.withDayOfMonth(1);
        int dayOfWeek = firstOfWeek.getDayOfWeek().getValue() % 7;

        for(int i = 1; i <= 42; i++) {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                days.add(null);
            }
            else {
                days.add(localDate.withDayOfMonth(i - dayOfWeek));
            }
        }
        return days;
    }

    @Override
    public void onItemClick(int pos, String day) {
        if(!day.equals("")) {
            try {
                int dayOfMonth = Integer.parseInt(day);
                selectedDate = currentDate.withDayOfMonth(dayOfMonth);
                updateMainDateText();
                dialog.dismiss();
            }
            catch (NumberFormatException ignored) {}
        }
    }

    private void updateMainDateText() {
        mainDateText.setText(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(selectedDate));
    }
}