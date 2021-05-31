package com.example.moconmcs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.Main.MainActivity
import com.example.moconmcs.SignUP.SignupActivity
import com.example.moconmcs.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        binding.signupBut.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener {
            Log.d("asdf", "onCreate: ${binding.emailLogin.text.toString()} ${binding.pwLogin.text}")
            auth.signInWithEmailAndPassword(binding.emailLogin.text.toString(), binding.pwLogin.text.toString())
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(this, "로그인이 되셨습니다.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    else{
                        Toast.makeText(this, "비밀번호랑 이메일을 확인하여주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}