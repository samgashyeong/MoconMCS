package com.example.moconmcs

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.data.C005
import com.example.moconmcs.data.Row
import com.example.moconmcs.databinding.ActivityFoodResultLodingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

class FoodResultLoding : AppCompatActivity() {
    private lateinit var binding: ActivityFoodResultLodingBinding
//    val BASE_URL = "https://openapi.foodsafetykorea.go.kr/api/${serViceKey}/C005/json/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result_loding)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_result_loding)

        val Intent = intent
        val barCode = intent.getStringExtra("barcodenum")

        Toast.makeText(this, "로딩화면에서 뜸${barCode}", Toast.LENGTH_SHORT).show()

        Log.d("asdf", "onCreate: ${barCode}")
        if (barCode != null) {
            getData(barCode)
        }
    }

    @SuppressLint("SetTextI18n")
    fun getData(barCode:String){
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GetFoodNum::class.java)

        Log.d("asdf", "api: ${api}")

        GlobalScope.launch(Dispatchers.IO){
            val response = api.getFoodCreateNum(barCode).awaitResponse()
            val execution = api.isExecutionCode(barCode).awaitResponse()
            Log.d("response", "response : ${response}${execution}")
            if(response.isSuccessful and execution.isSuccessful){
                val foodData = response.body()
                val isExecution = execution.body()
                


                withContext(Dispatchers.Main){
//                    val foodName = isExecution?.C005?.row?.get(0)!!.PRDLST_NM
//                    val foodNum = isExecution.C005.row[0].PRDLST_REPORT_NO

                    Log.d("asdf", "getData : ${foodData}\n ${isExecution}\n${isExecution?.C005?.total_count}")
                    if(isExecution?.C005?.total_count.equals("0")){
                        binding.tvResultDataSuccess.visibility = View.VISIBLE
                        binding.tvResultDataSuccess.text = "데이터를 불러오는데에 실패하였습니다."
                        binding.tvResutFoodName.visibility = View.VISIBLE
                        binding.tvResutFoodName.text = "NULL"
                        binding.tvResutFoodNum.visibility = View.VISIBLE
                        binding.tvResutFoodNum.text = "NULL"
                    }
                    else{
                        binding.tvResultDataSuccess.visibility = View.VISIBLE
                        binding.tvResultDataSuccess.text = "데이터를 불러오는데에 성공하였습니다."
                        binding.tvResutFoodName.visibility = View.VISIBLE
                        binding.tvResutFoodName.text = "식품명 : ${isExecution!!.C005.row.get(0).PRDLST_NM}"
                        binding.tvResutFoodNum.visibility = View.VISIBLE
                        binding.tvResutFoodNum.text = isExecution.C005.row[0].PRDLST_REPORT_NO
                        Log.d("asdf", "getData: ${isExecution.C005.row[0].PRDLST_REPORT_NO}\n" +
                                isExecution.C005.row[0].PRDLST_NM
                        )
                    }
//                    Log.d("asdf", "getData: ${isExecution.total_count.toInt()}")
//                    if(isExecution?.total_count?.toInt()?.equals(0)!!){
//                        Toast.makeText(this@FoodResultLoding, "해당되는 데이터가 없음", Toast.LENGTH_SHORT).show()
//                    }else{
//
//                    }
//                    Toast.makeText(this@FoodResultLoding, foodData!!.PRDLST_REPORT_NO.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        const val serViceKey = "6a957af97bed49989b74"
        const val BASE_URL = "https://openapi.foodsafetykorea.go.kr/api/${serViceKey}/C005/json/"
    }
}
