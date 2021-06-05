package com.example.moconmcs.Main.FoodDiary;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moconmcs.Main.AppDatabase;
import com.example.moconmcs.R;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class FoodDiaryFragment extends Fragment implements CalenderAdapter.OnItemListener {

    Button prevMonth, nextMonth, prevDay, nextDay;
    TextView dateText, mainDateText;
    RecyclerView calenderRecyclerView;
    LocalDate currentDate; //다이얼로그 안에서 쓰일 날짜 인스턴스
    Dialog dialog;
    DiaryDao diaryDao;
    FoodDiaryViewModel viewModel; //다른 프래그먼트에 다녀와도 유지되게 할 날짜 인스턴스

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_diary, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(FoodDiaryViewModel.class);

        currentDate = LocalDate.now();

        if(viewModel.getSelectedDate() == null) {
            viewModel.setSelectedDate(currentDate);
        }
        else {
            currentDate = viewModel.getSelectedDate();
        }

        AppDatabase db = AppDatabase.getInstance(requireContext().getApplicationContext());
        diaryDao = db.diaryDao();

        mainDateText = view.findViewById(R.id.mainDateText);
        prevDay = view.findViewById(R.id.diaryPrevDay);
        nextDay = view.findViewById(R.id.diaryNextDay);

        updateDateChangeBtn();
        updateMainDateText();

        mainDateText.setOnClickListener(v -> openCalenderDialog());
        prevDay.setOnClickListener(v -> {
            viewModel.setSelectedDate(viewModel.getSelectedDate().minusDays(1));
            updateMainDateText();
            updateDateChangeBtn();
        });
        nextDay.setOnClickListener(v -> {
            viewModel.setSelectedDate(viewModel.getSelectedDate().plusDays(1));
            updateMainDateText();
            updateDateChangeBtn();
        });

        return view;
    }

    private void updateDateChangeBtn() {
        if(nextDay != null) {
            if (viewModel.getSelectedDate().atTime(0, 0)
                    .isEqual(LocalDate.now().atTime(0, 0)))
                nextDay.setVisibility(View.INVISIBLE);
            else
                nextDay.setVisibility(View.VISIBLE);
        }
        if(nextMonth != null) {
            if (currentDate.withDayOfMonth(1).atTime(0, 0)
                    .isEqual(LocalDate.now().withDayOfMonth(1).atTime(0, 0)))
                nextMonth.setVisibility(View.INVISIBLE);
            else
                nextMonth.setVisibility(View.VISIBLE);
        }
    }

    private void openCalenderDialog() {
        currentDate = LocalDate.from(viewModel.getSelectedDate());
        dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.layout_custom_calender);

        prevMonth = dialog.findViewById(R.id.prevMonth);
        nextMonth = dialog.findViewById(R.id.nextMonth);
        dateText = dialog.findViewById(R.id.curDateText);
        calenderRecyclerView = dialog.findViewById(R.id.calRecycler);

        updateDateChangeBtn();

        prevMonth.setOnClickListener(v -> {
            currentDate = currentDate.minusMonths(1);
            setCalenderView();
            updateDateChangeBtn();
        });

        nextMonth.setOnClickListener(v -> {
            currentDate = currentDate.plusMonths(1);
            setCalenderView();
            updateDateChangeBtn();
        });

        setCalenderView();

        dialog.setOnDismissListener(d -> {
            updateDateChangeBtn();
        });
        dialog.show();
    }

    private void setCalenderView() {
        dateText.setText(DateTimeFormatter.ofPattern("yyyy년 MM월").format(currentDate));
        updateMainDateText();

        ArrayList<LocalDate> daysInMonth = getDaysInMonth(currentDate);
        CalenderAdapter calenderAdapter = new CalenderAdapter(viewModel.getSelectedDate(), daysInMonth, this);
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
            int dayOfMonth = i - dayOfWeek;
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                days.add(null);
            }
            else {
                days.add(localDate.withDayOfMonth(dayOfMonth));
            }
        }
        return days;
    }

    @Override
    public void onItemClick(int pos, String day) {
        if(!day.equals("")) {
            try {
                int dayOfMonth = Integer.parseInt(day);
                viewModel.setSelectedDate(currentDate.withDayOfMonth(dayOfMonth));
                updateMainDateText();
                dialog.dismiss();
            }
            catch (NumberFormatException ignored) {}
        }
        updateDateChangeBtn();
    }

    private void updateMainDateText() {
        mainDateText.setText(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(viewModel.getSelectedDate()));
    }
}