package com.example.moconmcs.Onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

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

        if(auth.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        success = findViewById(R.id.obsuccess);
        success.setOnClickListener(v -> nextPage());


        if(curPage == pageFragments.size() - 1) {
            success.setVisibility(View.VISIBLE);
            success.setActivated(true);
            success.setText("완료!");
        }
        else {
            success.setVisibility(View.INVISIBLE);
            success.setActivated(false);
        }

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