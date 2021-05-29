package com.example.moconmcs.Main.SearchFood

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.R
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
//    val BASE_URL = "https://openapi.foodsafetykorea.go.kr/api/${serViceKey}/C005/json/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result_loding)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_food_result_loding
        )

        val Intent = intent
        val barCode = intent.getStringExtra("barcodenum")

        Toast.makeText(this, "로딩화면에서 뜸${barCode}", Toast.LENGTH_SHORT).show()

        Log.d("asdf", "onCreate: ${barCode}") //바코드 번호가져오기
        if (barCode != null) {
            getFoodNum(barCode)
        }
        if(Intent.getStringExtra("Etname")!=null){
            getResult(Intent.getStringExtra("Etname")!!)
        }
    }

    @SuppressLint("SetTextI18n")
    fun getFoodNum(barCode:String){ //데이터 호출
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL_BARCODE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GetFoodNum::class.java)

        Log.d("asdf", "api: ${api}")

        GlobalScope.launch(Dispatchers.IO){
            val execution = api.isExecutionCode(barCode).awaitResponse()
            Log.d("response", "response :${execution}")
            if(execution.isSuccessful){
                val isExecution = execution.body()

                withContext(Dispatchers.Main){
//                    val foodName = isExecution?.C005?.row?.get(0)!!.PRDLST_NM
//                    val foodNum = isExecution.C005.row[0].PRDLST_REPORT_NO

                    Log.d("asdf", "getData :  ${isExecution}\n${isExecution?.C005?.total_count}")
                    if(isExecution?.C005?.total_count.equals("0")){ //만약 데이터가 안나온다
                        binding.tvResultDataSuccess.visibility = View.VISIBLE
                        binding.tvResultDataSuccess.text = "데이터를 불러오는데에 실패하였습니다."
                        binding.tvResutFoodName.visibility = View.VISIBLE
                        binding.tvResutFoodName.text = "NULL"
                        binding.tvResutFoodNum.visibility = View.VISIBLE
                        binding.tvResutFoodNum.text = "NULL"
                    }
                    else{ //나온다?
                        binding.tvResultDataSuccess.visibility = View.VISIBLE
                        binding.tvResultDataSuccess.text = "데이터를 불러오는데에 성공하였습니다."
                        binding.tvResutFoodName.visibility = View.VISIBLE
                        binding.tvResutFoodName.text = "식품명 : ${isExecution!!.C005.row.get(0).PRDLST_NM}"
                        binding.tvResutFoodNum.visibility = View.VISIBLE
                        binding.tvResutFoodNum.text = isExecution.C005.row[0].PRDLST_REPORT_NO
                        Log.d("asdf", "getData: ${isExecution.C005.row[0].PRDLST_REPORT_NO}\n" +
                                isExecution.C005.row[0].PRDLST_NM
                        )
                        getResult(isExecution.C005.row[0].PRDLST_REPORT_NO)
                    }
                }
            }
        }
    }

    fun getResult(FoodNum : String){

    }
    companion object {
        const val serViceKey = "6a957af97bed49989b74"
        const val BASE_URL_BARCODE = "https://openapi.foodsafetykorea.go.kr/api/$serViceKey/C005/json/"
        const val BASE_URL_KYUNGROK_API = "엄경록 화이팅!"
    }
}