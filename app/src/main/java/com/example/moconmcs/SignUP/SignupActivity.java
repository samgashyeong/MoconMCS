package com.example.moconmcs.SignUP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.moconmcs.R;
import com.example.moconmcs.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.frame, new SignUpFragment1())
                .commit();
//        binding.backBtn.setVisibility(View.INVISIBLE);
//        binding.prevBtn.setVisibility(View.VISIBLE);
//
//        binding.prevBtn.setOnClickListener(v -> {
//            prev();
//        });
//        binding.backBtn.setOnClickListener(v ->{
//            back();
//        });

//        signup.setOnClickListener(v -> {
//            if(!pw1.getText().toString().equals(pw2.getText().toString())) {
//                Toast.makeText(getApplicationContext(), "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
//            }
//            else auth.createUserWithEmailAndPassword(
//                    email.getText().toString(), pw1.getText().toString())
//                    .addOnCompleteListener(task -> {
//                if(task.isSuccessful()) {
//                    db.collection("User").document(""+uid).set(new User(name.getText().toString()))
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(SignupActivity.this, "오류가 났습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                    Toast.makeText(getApplicationContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
//                    intent.putExtra("isLoginBack", true);
//                    startActivity(intent);
//                }
//            });
//        });
    }

    private void prev(){
//        SignUpFragment1 tf = (SignUpFragment1) getSupportFragmentManager().findFragmentById(R.id.frame);
//        assert tf != null;
        SignUpFragment2 fm = new SignUpFragment2();
        FragmentManager fmm = getSupportFragmentManager();
        fmm.beginTransaction()
                .replace(R.id.frame, fm)
                .commit();
//        SignUpFragment2 tf1 = (SignUpFragment2) getSupportFragmentManager().findFragmentById();
//        tf.loadData(fm);
//        binding.backBtn.setVisibility(View.VISIBLE);
//        binding.prevBtn.setVisibility(View.INVISIBLE);
    }

    private void back(){
//        SignUpFragment2 tf = (SignUpFragment2) getSupportFragmentManager().findFragmentById(R.id.frame);
//        assert tf != null;
        SignUpFragment1 fm = new SignUpFragment1();
        FragmentManager fmm = getSupportFragmentManager();
        fmm.beginTransaction()
                .replace(R.id.frame, fm)
                .commit();
//        tf.loadData(fm);
//        binding.backBtn.setVisibility(View.INVISIBLE);
//        binding.prevBtn.setVisibility(View.VISIBLE);
    }


    public void changeFragment(int i){
        if(i == 0){
            FragmentManager fmm = getSupportFragmentManager();
            fmm.beginTransaction()
                    .replace(R.id.frame, new SignUpFragment1())
                    .commit();
        }
        else if(i == 1){
            FragmentManager fmm = getSupportFragmentManager();
            fmm.beginTransaction()
                    .replace(R.id.frame, new SignUpFragment2())
                    .commit();
        }
    }
}