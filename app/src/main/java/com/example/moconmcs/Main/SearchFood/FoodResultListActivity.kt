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
        val foodAdapterList : ArrayList<Material> = ArrayList()
        var failLength : Int = 0
        if(foodList != null){
            foodArrayList = foodList as ArrayList<Material>
            when(intent.getStringExtra("userKind")){
                "비건"->{
                    for(i in 0 until foodArrayList.count()){
                        if (foodArrayList[i].MLSFC_NM == "혼합분유"
                            || foodArrayList[i].MLSFC_NM == "탈지분유"
                            || foodArrayList[i].MLSFC_NM == "난류"
                            || foodArrayList[i].MLSFC_NM == "우유"
                            || foodArrayList[i].MLSFC_NM == "체다치즈 분말"
                            || foodArrayList[i].MLSFC_NM == "까망베르 치즈분말"
                            || foodArrayList[i].MLSFC_NM == "치즈혼합분말"){
                            foodAdapterList.add(foodArrayList[i])
                        }
                    }
                }
                "오보"->{
                    for(i in 0 until foodArrayList.count()){
                        if (foodArrayList[i].MLSFC_NM == "혼합분유"
                            || foodArrayList[i].MLSFC_NM == "탈지분유"
                            || foodArrayList[i].MLSFC_NM == "우유"
                            || foodArrayList[i].MLSFC_NM == "체다치즈 분말"
                            || foodArrayList[i].MLSFC_NM == "까망베르 치즈분말"
                            || foodArrayList[i].MLSFC_NM == "치즈혼합분말"){
                            foodAdapterList.add(foodArrayList[i])
                        }
                    }
                }
                "락토"->{
                    for(i in 0 until foodArrayList.count()){
                        if (foodArrayList[i].MLSFC_NM == "난류"){
                            foodAdapterList.add(foodArrayList[i])
                        }
                    }
                }
            }
            for (i in 0 until foodArrayList.count()){
                if(foodArrayList[i].MLSFC_NM == "축산물" || foodArrayList[i].MLSFC_NM == "수산물"){
                    foodAdapterList.add(foodArrayList[i])
                    failLength = foodAdapterList.count()
                }
            }
            for (i in 0 until foodArrayList.count()){
                if(foodArrayList[i].MLSFC_NM != "축산물" && foodArrayList[i].MLSFC_NM != "수산물"){
                    Log.d(TAG, "onCreate: 맞는지 확인${foodArrayList[i].MLSFC_NM != "수산물"}")
                    foodAdapterList.add(foodArrayList[i])
                }
            }
            binding.recycler.adapter = FoodAdapter(foodAdapterList, failLength)
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