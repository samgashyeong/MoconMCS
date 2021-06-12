package com.example.moconmcs.Onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.moconmcs.LoginActivity;
import com.example.moconmcs.Main.MainActivity;
import com.example.moconmcs.R;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private float x1, x2;
    private static final float MIN_SWIPE_DISTANCE = 150;

    private ImageView[] obProgressCircles;
    private Button success;
    private int curPage = 0;
    private final List<Class<? extends Fragment>> pageFragments = Arrays.asList(
            OnboardingFragment1.class,
            OnboardingFragment2.class,
            OnboardingFragment3.class,
            OnboardingFragment4.class
    );
    private FirebaseAuth auth;

    private void switchFragment() {

        if(curPage == pageFragments.size() - 1) {
            success.setVisibility(View.VISIBLE);
            success.setActivated(true);
            success.setText("완료!");
        }
        else {
            success.setVisibility(View.INVISIBLE);
            success.setActivated(false);
        }

        for(int i = 0; i < obProgressCircles.length; i++) {
            if(i == curPage)
                obProgressCircles[i].setImageResource(R.drawable.circle_green);
            else
                obProgressCircles[i].setImageResource(R.drawable.circle_green_stroke);
        }

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
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (deltaX > MIN_SWIPE_DISTANCE)
                {
                    prevPage();
                }
                else if (deltaX < -MIN_SWIPE_DISTANCE)
                {
                    nextPage();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        auth = FirebaseAuth.getInstance();

        //로그인 되어 있으면 메인 액티비티로
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        obProgressCircles = new ImageView[] {
                findViewById(R.id.ob_prog_1),
                findViewById(R.id.ob_prog_2),
                findViewById(R.id.ob_prog_3),
                findViewById(R.id.ob_prog_4)
        };

        success = findViewById(R.id.obsuccess);
        success.setOnClickListener(v -> {
            nextPage();
            SharedPreferences preferences = getSharedPreferences("FirstCheck", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("checkFirst", true);
            editor.apply();
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.add(R.id.obframe, new OnboardingFragment1());

        //온보딩 이미 봤으면 바로 로그인 화면으로
        SharedPreferences preferences = getSharedPreferences("FirstCheck", Activity.MODE_PRIVATE);
        boolean checkFirst = preferences.getBoolean("checkFirst", false);
        if(checkFirst){
            startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
            finish();
        }

        fragmentTransaction.commit();
    }

    void nextPage() {
        curPage++;
        if(curPage >= pageFragments.size()) {
            startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
            finish();
        }
        else switchFragment();
    }

    void prevPage() {
        curPage--;
        if(curPage < 0) curPage = 0;
        else switchFragment();
    }

}