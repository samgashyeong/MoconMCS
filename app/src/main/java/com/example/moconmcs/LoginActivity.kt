package com.example.moconmcs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.Dialog.LodingDialog
import com.example.moconmcs.Main.MainActivity
import com.example.moconmcs.SignUP.SignupActivity
import com.example.moconmcs.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: LodingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        dialog = LodingDialog(this, "로그인을 하는 중..")

        binding.signupBut.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        if(auth.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener {
            if(binding.emailLogin.text.length <= 0 || binding.pwLogin.text.length<=0){
                binding.errorTv.visibility = View.VISIBLE
                binding.errorTv.text = "아이디랑 비밀번호를 입력해주세요."
            }
            else{
                Log.d("asdf", "onCreate: ${binding.emailLogin.text.toString()} ${binding.pwLogin.text}")
                dialog.show()
                auth.signInWithEmailAndPassword(binding.emailLogin.text.toString(), binding.pwLogin.text.toString())
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                            dialog.dismiss()
                        }
                        else{
                            dialog.dismiss()
                            binding.errorTv.visibility = View.VISIBLE
                            binding.errorTv.text = "비밀번호랑 이메일을 확인하여주세요."
                        }
                    }
            }
        }
    }
}