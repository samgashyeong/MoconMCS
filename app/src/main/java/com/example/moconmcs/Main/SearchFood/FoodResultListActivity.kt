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
        var failLength = 0
        if(foodList != null){
            foodArrayList = foodList as ArrayList<Material>
            Log.d(TAG, "onCreate: 1.$foodAdapterList")
            val badFoods = arrayListOf("수산물", "축산물")
            val userKind = intent.getStringExtra("userKind");

            if(userKind != "락토" && userKind != "락토오보")
                badFoods.addAll(arrayOf("혼합분유", "탈지분유", "우유", "유제품", "체다치즈 분말", "까망베르 치즈분말", "치즈혼합분말", "연성치즈"))
            if(userKind != "오보" && userKind != "락토오보") badFoods.add("난류")

            for (i in foodArrayList){
                if(badFoods.contains(i.MLSFC_NM)){
                    foodAdapterList.add(i)
                }
            }
            failLength = foodAdapterList.count()
            Log.d(TAG, "onCreate: 2$foodAdapterList")
            for (i in foodArrayList){
                if(!badFoods.contains(i.MLSFC_NM)){
                    foodAdapterList.add(i)
                }
            }
            Log.d(TAG, "onCreate: 3$foodAdapterList")
            Log.d(TAG, "onCreate: $failLength")
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