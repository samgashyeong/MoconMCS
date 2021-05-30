package com.example.moconmcs.Onboarding;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moconmcs.Main.MainActivity;
import com.example.moconmcs.R;
import com.example.moconmcs.SignUP.SignupActivity;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {

    EditText emailEdit, pwEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        emailEdit = view.findViewById(R.id.emailLogin);
        pwEdit = view.findViewById(R.id.pwLogin);

        view.findViewById(R.id.signupBut).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SignupActivity.class));
            requireActivity().finish();
        });

        view.findViewById(R.id.loginButton).setOnClickListener(v -> {
            if(emailEdit.getText().length() <=0 || pwEdit.getText().length() <= 0){
                Toast.makeText(getActivity(), "빈칸을 채워주세요", Toast.LENGTH_SHORT).show();
            }
            else{
                auth.signInWithEmailAndPassword(emailEdit.getText().toString(), pwEdit.getText().toString()).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(getContext(), "로그인 성공!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        requireActivity().finish();
                    }
                    else {
                        Toast.makeText(getContext(), "비밀번호랑 이메일을 확인하고 다시시도해주세요!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }
}