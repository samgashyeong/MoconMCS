package com.example.moconmcs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText email, pw1, pw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.emailLogin);
        pw1 = findViewById(R.id.pwLogin);
        pw2 = findViewById(R.id.pwLogin2);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        Button signup = findViewById(R.id.loginButton);

        signup.setOnClickListener(v -> {
            if(!pw1.getText().toString().equals(pw2.getText().toString())) {
                Toast.makeText(getApplicationContext(), "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
            else auth.createUserWithEmailAndPassword(
                    email.getText().toString(), pw1.getText().toString())
                    .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
                    intent.putExtra("isLoginBack", true);
                    startActivity(intent);
                }
            });
        });

    }
}