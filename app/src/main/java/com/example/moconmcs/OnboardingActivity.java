package com.example.moconmcs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class OnboardingActivity extends AppCompatActivity {

    int curPage = 0;

    private void switchFragment() {

        Fragment fr = null;
        switch (curPage) {
            case 0:
                fr = new OnboardingFragment1();
                break;
            case 1:
                fr = new OnboardingFragment2();
                break;
            case 2:
                fr = new OnboardingFragment3();
                break;
            case 3:
                fr = new OnboardingFragment4();
                break;
        }
        if(fr == null) fr = new OnboardingFragment1();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.obframe, fr);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.add(R.id.obframe, new OnboardingFragment1());

        Button next = findViewById(R.id.obnext);
        Button prev = findViewById(R.id.obprev);

        next.setOnClickListener(v -> {
            curPage++;
            if(curPage > 3) {
                Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else switchFragment();
        });
        prev.setOnClickListener(v -> {
            curPage--;
            if(curPage < 0) curPage = 0;
            else switchFragment();
        });

        fragmentTransaction.commit();
    }
}