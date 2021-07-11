package com.example.moconmcs.Main.FoodMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moconmcs.R;

import java.util.ArrayList;
import java.util.List;

public class PlaceSearchActivity extends AppCompatActivity {

    private EditText searchContent;
    private ImageView backButton;
    private RecyclerView recyclerView;
    private PlaceSearchAdapter adapter;
    private List<Placemark> searchedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);

        backButton = findViewById(R.id.backBtn2);
        recyclerView = findViewById(R.id.placeSearchList);
        searchContent = findViewById(R.id.searchContent);
        searchContent.requestFocus();
        new Handler().postDelayed(() -> searchContent.dispatchTouchEvent(MotionEvent.obtain(
                SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP , 0, 0, 0)), 1000);

        searchContent.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchedList.clear();
                searchedList.addAll(FoodMapFragment.searchPlace(this, searchContent.getText().toString()));
                adapter.notifyDataSetChanged();
                if(searchedList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        backButton.setOnClickListener(v -> finish());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new PlaceSearchAdapter(searchedList, position -> {
            FoodMapFragment.searchedPlacemark = searchedList.get(position);
            setResult(1235);
            finish();
        });
        recyclerView.setAdapter(adapter);

    }
}