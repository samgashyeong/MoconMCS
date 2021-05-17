package com.example.moconmcs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        int a = 1;
        String jun = "sang";
        Button btn1 = findViewById(R.id.button2);
        btn1.setOnClickListener(v -> {
            v.setBackgroundColor(0xff0000);
        });
    }
}