package com.example.moconmcs.Main.SearchFood

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.moconmcs.Main.SearchFood.NetWork.GetFoodNum
import com.example.moconmcs.Main.SearchFood.NetWork.GetFoodResult
import com.example.moconmcs.R
import com.example.moconmcs.databinding.ActivityFoodResultLodingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class FoodResultLoding : AppCompatActivity() {
    private lateinit var binding: ActivityFoodResultLodingBinding
    private lateinit var viewModel: FoodViewModel
//    val BASE_URL = "https://openapi.foodsafetykorea.go.kr/api/${serViceKey}/C005/json/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result_loding)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_food_result_loding
        )
        viewModel = ViewModelProvider(this).get(FoodViewModel::class.java)

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(100, TimeUnit.SECONDS)
        .build()
        val Intent = intent
        val barCode = intent.getStringExtra("barcodenum")

        Toast.makeText(this, "로딩화면에서 뜸${barCode}", Toast.LENGTH_SHORT).show()

        Log.d("asdf", "onCreate: ${barCode}") //바코드 번호가져오기
        if (barCode != null) {
            getFoodNum(barCode)
        }
        if(Intent.getStringExtra("Etname")!=null){
            getFoodResult(Intent.getStringExtra("Etname")!!)
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
                    if(isExecution?.C005?.total_count.equals("0")){
                        Log.d(TAG, "getFoodNum: 데이터를 불러오지못함.")
                    }
                    else{ //나온다?
                        val foodName = isExecution!!.C005.row[0].PRDLST_NM
                        val foodNumber = isExecution.C005.row[0].PRDLST_REPORT_NO
//                        binding.tvResultDataSuccess.visibility = View.VISIBLE
//                        binding.tvResultDataSuccess.text = "데이터를 불러오는데에 성공하였습니다."
//                        binding.tvResutFoodName.visibility = View.VISIBLE
//                        binding.tvResutFoodName.text = "식품명 : ${isExecution!!.C005.row[0].PRDLST_NM}"
//                        binding.tvResutFoodNum.visibility = View.VISIBLE
//                        binding.tvResutFoodNum.text = isExecution.C005.row[0].PRDLST_REPORT_NO
//                        Log.d("asdf", "getData: ${isExecution.C005.row[0].PRDLST_REPORT_NO}\n" +
//                                isExecution.C005.row[0].PRDLST_NM
//                        )
                        getFoodResult(foodNumber)
                    }
                }
            }
        }
    }

    fun getFoodResult(FoodNum : String){
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL_KYUNGROK_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GetFoodResult::class.java)

        GlobalScope.launch(Dispatchers.IO){
            val execution = api.isExecutionCode(FoodNum).awaitResponse()
            Log.d("response", "response :${execution}")
            if(execution.isSuccessful){
                val isExecution = execution.body()

                withContext(Dispatchers.Main){

//                    Log.d("asdf", "getData :  ${isExecution}\n${isExecution?.C005?.total_count}")
                    if(isExecution==null){
                        Log.d(TAG, "getFoodResult: 내용이 없습니다.")
                    }
                    else{
                        Log.d(TAG, "getFoodResult: ${isExecution?._id}" +
                                ", ${isExecution?.prodName}" +
                                ", ${isExecution?.materials}")
                        viewModel.foodResult.value = isExecution
                        startActivity(Intent(this@FoodResultLoding, FoodResultActivity::class.java))

                        //나온다?
//                        val foodName = isExecution!!.C005.row[0].PRDLST_NM
//                        val foodNumber = isExecution.C005.row[0].PRDLST_REPORT_NO
//                        binding.tvResultDataSuccess.visibility = View.VISIBLE
//                        binding.tvResultDataSuccess.text = "데이터를 불러오는데에 성공하였습니다."
//                        binding.tvResutFoodName.visibility = View.VISIBLE
//                        binding.tvResutFoodName.text = "식품명 : ${isExecution!!.C005.row[0].PRDLST_NM}"
//                        binding.tvResutFoodNum.visibility = View.VISIBLE
//                        binding.tvResutFoodNum.text = isExecution.C005.row[0].PRDLST_REPORT_NO
//                        Log.d("asdf", "getData: ${isExecution.C005.row[0].PRDLST_REPORT_NO}\n" +
//                                isExecution.C005.row[0].PRDLST_NM
//                        )
//                        getFoodResult(isExecution.C005.row[0].PRDLST_REPORT_NO)
                    }
                }
            }
        }

    }
    companion object {
        const val serViceKey = "6a957af97bed49989b74"
        const val BASE_URL_BARCODE = "https://openapi.foodsafetykorea.go.kr/api/$serViceKey/C005/json/"
        const val BASE_URL_KYUNGROK_API = "https://vcheck-api.herokuapp.com/api/"
    }
}
