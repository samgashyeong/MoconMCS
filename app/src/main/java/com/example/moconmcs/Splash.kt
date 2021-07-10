package com.example.moconmcs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.moconmcs.Onboarding.OnboardingActivity

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({ //스플래쉬
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }, 1500)
    }

}