package com.example.moconmcs.Main.SearchFood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.R
import com.example.moconmcs.databinding.ActivityFoodNumInputBinding

class FoodNumInput : AppCompatActivity() {
    private lateinit var binding: ActivityFoodNumInputBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_num_input)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_food_num_input
        )

        binding.btn1.setOnClickListener {
            startActivity(Intent(this, FoodResultLoding::class.java)
                .putExtra("Etname", binding.EtFoodNum.text.toString()))
            finish()
        }
    }
}