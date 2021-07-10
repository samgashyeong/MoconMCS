package com.example.moconmcs.Main.SearchFood

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.R
import com.example.moconmcs.data.KyungrokApi.Material
import com.example.moconmcs.databinding.ActivityFoodResultListBinding
import java.io.Serializable

class FoodResultListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodResultListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result_list)
        val foodList : Serializable? = intent.getSerializableExtra("foodList")
        val prodName = intent.getSerializableExtra("prodName")

        Log.d(TAG, "onCreate: ${foodList}, ${prodName}")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_result_list)

        binding.title.text = prodName.toString()
        val foodArrayList: ArrayList<Material>
        if(foodList != null){
            foodArrayList = foodList as ArrayList<Material>
            binding.recycler.adapter = FoodAdapter(foodArrayList)
        }

        setSupportActionBar(binding.toolbar)

        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_icon_toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}