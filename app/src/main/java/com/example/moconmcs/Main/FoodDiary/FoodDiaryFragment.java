package com.example.moconmcs.Main.FoodDiary;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.example.moconmcs.Main.AppDatabase;
import com.example.moconmcs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class FoodDiaryFragment extends Fragment {


    private FirebaseAuth auth;
    private String uid;

    private Button nextMonth;
    private Button prevDay;
    private Button nextDay;
    private TextView dateText, mainDateText;
    private final String[] foodTimeTexts = {"아침", "점심", "저녁"};
    private final TextView[] timeTextViews = new TextView[3];
    private EditText ateFoodLog;
    private GridView gridView;
    private LocalDate currentDate; //다이얼로그 안에서 쓰일 날짜 인스턴스
    private Dialog dialog;
    private DiaryDao diaryDao;
    private FoodDiaryViewModel viewModel; //다른 프래그먼트에 다녀와도 유지되게 할 날짜 인스턴스

    int selectedFoodTime = 0; //0:아침 1:점심 2:저녁

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_diary, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(FoodDiaryViewModel.class);

        auth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

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

        ateFoodLog = view.findViewById(R.id.logAteFood);
        timeTextViews[0] = view.findViewById(R.id.diary_t1);
        timeTextViews[1] = view.findViewById(R.id.diary_t2);
        timeTextViews[2] = view.findViewById(R.id.diary_t3);

        for(int i = 0; i < timeTextViews.length; i++) {
            final int finalI = i;
            timeTextViews[i].setOnClickListener(v -> {
                uploadDiaryDatabase();
                selectedFoodTime += finalI - 1;
                if(selectedFoodTime < 0) selectedFoodTime = 0;
                if(selectedFoodTime >= timeTextViews.length) selectedFoodTime = timeTextViews.length - 1;
                updateFoodTime();
                loadDiaryDatabase();
            });
        }

        loadDiaryDatabase();
        updateFoodTime();
        updateDateChangeBtn();
        updateMainDateText();

        mainDateText.setOnClickListener(v -> openCalenderDialog());
        prevDay.setOnClickListener(v -> {
            uploadDiaryDatabase();
            viewModel.setSelectedDate(viewModel.getSelectedDate().minusDays(1));
            loadDiaryDatabase();
            updateMainDateText();
            updateDateChangeBtn();
        });
        nextDay.setOnClickListener(v -> {
            uploadDiaryDatabase();
            viewModel.setSelectedDate(viewModel.getSelectedDate().plusDays(1));
            loadDiaryDatabase();
            updateMainDateText();
            updateDateChangeBtn();
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        uploadDiaryDatabase();
    }

    private void uploadDiaryDatabase() {

        String ateLog = ateFoodLog.getText().toString();
        DiaryEntity foundEntity = null;
        for(DiaryEntity diaryEntity : diaryDao.getAll()) {
            if(diaryEntity.getDate() == DiaryEntity.toDate(
                    viewModel.getSelectedDate().getYear(),
                    viewModel.getSelectedDate().getMonthValue(),
                    viewModel.getSelectedDate().getDayOfMonth())) {
                foundEntity = diaryEntity;
                diaryDao.delete(diaryEntity);
            }
        }
        if(foundEntity != null) {
            switch (selectedFoodTime) {
                case 0:
                    foundEntity.setBreakfast(ateLog);
                    break;
                case 1:
                    foundEntity.setLunch(ateLog);
                    break;
                case 2:
                    foundEntity.setDinner(ateLog);
                    break;
            }
            diaryDao.insertAll(foundEntity);
        }
        else {
            String[] diary = {"", "", ""};
            diary[selectedFoodTime] = ateFoodLog.getText().toString();
            DiaryEntity insertEntity = new DiaryEntity(uid, DiaryEntity.toDate(
                    viewModel.getSelectedDate().getYear(),
                    viewModel.getSelectedDate().getMonthValue(),
                    viewModel.getSelectedDate().getDayOfMonth()), diary[0], diary[1], diary[2]);
            diaryDao.insertAll(insertEntity);
        }
    }

    private void loadDiaryDatabase() {
        for(DiaryEntity diaryEntity : diaryDao.getAll()) {
            if(diaryEntity.getDate() == DiaryEntity.toDate(
                    viewModel.getSelectedDate().getYear(),
                    viewModel.getSelectedDate().getMonthValue(),
                    viewModel.getSelectedDate().getDayOfMonth())
                    && diaryEntity.getUid().equals(uid)) {

                switch (selectedFoodTime) {
                    case 0:
                        ateFoodLog.setText(diaryEntity.getBreakfast());
                        break;
                    case 1:
                        ateFoodLog.setText(diaryEntity.getLunch());
                        break;
                    case 2:
                        ateFoodLog.setText(diaryEntity.getDinner());
                        break;
                    default:
                        ateFoodLog.setText("");
                }
                return;
            }
        }
        ateFoodLog.setText("");
    }

    private void updateFoodTime() {
        for(int i = 0; i < timeTextViews.length; i++) {
            int idx = selectedFoodTime + i - 1;
            if(idx >= 0 && idx < timeTextViews.length) {
                timeTextViews[i].setText(foodTimeTexts[idx]);
                timeTextViews[i].setVisibility(View.VISIBLE);
            }
            else {
                timeTextViews[i].setVisibility(View.INVISIBLE);
            }
        }
        timeTextViews[1].setTextColor(0xff2b7214);
        ateFoodLog.setHint(foodTimeTexts[selectedFoodTime] + "에 먹은 것을 기록해보세요.");
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

        Button prevMonth = dialog.findViewById(R.id.prevMonth);
        nextMonth = dialog.findViewById(R.id.nextMonth);
        dateText = dialog.findViewById(R.id.curDateText);
        gridView = dialog.findViewById(R.id.calGrid);

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
            uploadDiaryDatabase();
            updateDateChangeBtn();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void setCalenderView() {
        dateText.setText(DateTimeFormatter.ofPattern("yyyy.MM").format(currentDate));
        updateMainDateText();

        ArrayList<LocalDate> daysInMonth = getDaysInMonth(currentDate);
        CalenderAdapter calenderAdapter = new CalenderAdapter(viewModel.getSelectedDate(), daysInMonth);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(calenderAdapter.isCanClick(position)) {
                    try {
                        int dayOfMonth = Integer.parseInt(((TextView)view.findViewById(R.id.cell_text)).getText().toString());
                        uploadDiaryDatabase();
                        viewModel.setSelectedDate(currentDate.withDayOfMonth(dayOfMonth));
                        loadDiaryDatabase();
                        updateMainDateText();
                        dialog.dismiss();
                    }
                    catch (NumberFormatException ignored) {}
                }
                updateDateChangeBtn();
            }
        });
        gridView.setAdapter(calenderAdapter);
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

    private void updateMainDateText() {
        mainDateText.setText(DateTimeFormatter.ofPattern("yyyy.MM.dd").format(viewModel.getSelectedDate()));
    }
}