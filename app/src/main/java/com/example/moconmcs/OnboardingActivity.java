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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private Button next, prev;
    
    private int curPage = 0;
    private final List<Class<? extends Fragment>> pageFragments = Arrays.asList(
            OnboardingFragment1.class,
            OnboardingFragment2.class,
            OnboardingFragment3.class,
            OnboardingFragment4.class,
            LoginFragment.class
    );

    private void switchFragment() {

        if(prev == null || next == null) return;
        
        if(curPage == 0) prev.setVisibility(View.GONE);
        else prev.setVisibility(View.VISIBLE);
        if(curPage == pageFragments.size() - 1) next.setVisibility(View.GONE);
        else next.setVisibility(View.VISIBLE);

        Fragment fr = null;
        Class<? extends Fragment> frclass = pageFragments.get(curPage);
        try {
            Constructor<? extends Fragment> ctr = frclass.getConstructor();
            fr = ctr.newInstance();
        }
        catch (Exception e) {
            Log.e("fragment_switch", e.getMessage());
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

        Intent intent = getIntent();

        next = findViewById(R.id.obnext);
        prev = findViewById(R.id.obprev);
        
        if(curPage == 0) prev.setVisibility(View.GONE);
        if(curPage == 3) next.setText("완료!");

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if(intent.getBooleanExtra("isLoginBack", false)) {
            curPage = pageFragments.size() - 1;
            fragmentTransaction.add(R.id.obframe, new LoginFragment());
            switchFragment();
        }
        else fragmentTransaction.add(R.id.obframe, new OnboardingFragment1());

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
            if(curPage > 4) {
                startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
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