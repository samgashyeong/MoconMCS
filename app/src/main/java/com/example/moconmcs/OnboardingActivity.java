package com.example.moconmcs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

        Button next = findViewById(R.id.obnext);
        Button prev = findViewById(R.id.obprev);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.add(R.id.obframe, new OnboardingFragment1());

//        SharedPreferences preferences = getSharedPreferences("FirstCheck", Activity.MODE_PRIVATE);
//        boolean checkFirst = preferences.getBoolean("checkFirst", false);
//        if(checkFirst){
//            Log.d("asdf", "onCreate: if가 실행이됨"+checkFirst);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putBoolean("checkFirst", false);
//            editor.apply();
//            startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
//           finish();
//        }else{
//            Log.d("asdf", "onCreate: else가 실행이됨"+checkFirst);
//            startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
//            finish();
//        }

        next.setOnClickListener(v -> {
            curPage++;
            if(curPage > 3) {
                Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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