package com.example.moconmcs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.databinding.ActivityFoodResultLodingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

class FoodResultLoding : AppCompatActivity() {
    private lateinit var binding: ActivityFoodResultLodingBinding

    val BASE_URL = "http://openapi.foodsafetykorea.go.kr/api/6a957af97bed49989b74/C005/json"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result_loding)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_result_loding)

        val Intent = intent

        val barCode = intent.getStringExtra("barcodenum")

        Toast.makeText(this, "로딩화면에서 뜸${barCode}", Toast.LENGTH_SHORT).show()

    }

    fun getData(barCode:String){
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GetFoodNum::class.java)

        GlobalScope.launch(Dispatchers.IO){
            val response = api.getFoodCreateNum(barCode).awaitResponse()
            val execution = api.isExecutionCode(barCode).awaitResponse()
            if(response.isSuccessful){
                val foodData = response.body()
                val isExecution = execution.body()

                withContext(Dispatchers.Main){
                    if(isExecution?.total_count?.equals(0)!!){
                        Toast.makeText(this@FoodResultLoding, "", Toast.LENGTH_SHORT).show()
                    }else{

                    }
                }


            }
        }
    }
}