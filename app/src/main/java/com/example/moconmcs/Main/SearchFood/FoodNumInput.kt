package com.example.moconmcs.Main.SearchFood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
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

        if(intent.hasExtra("barCodeFail")){
            binding.bcFailTv.visibility = View.VISIBLE
        }
        binding.btn1.setOnClickListener {
            if(binding.EtFoodNum.text.isEmpty()){
                Toast.makeText(this, "숫자를 입력하고 눌러주세요.", Toast.LENGTH_SHORT).show()
            }
            else{
                startActivity(Intent(this, FoodResultLoding::class.java)
                    .putExtra("foodNum", binding.EtFoodNum.text.toString()))
                finish()
            }
        }
    }
}