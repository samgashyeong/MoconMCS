package com.example.moconmcs;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
        });

        view.findViewById(R.id.loginButton).setOnClickListener(v -> {
            auth.signInWithEmailAndPassword(emailEdit.getText().toString(), pwEdit.getText().toString()).addOnCompleteListener(task -> {
               if(task.isSuccessful()) {
                   Toast.makeText(getContext(), "로그인 성공!", Toast.LENGTH_SHORT).show();
                   startActivity(new Intent(getContext(), MainActivity.class));
                   if(getActivity() != null)
                       getActivity().finish();
               }
               else {
                   Toast.makeText(getContext(), "로그인 실패!", Toast.LENGTH_SHORT).show();
               }
            });
        });

        return view;
    }
}