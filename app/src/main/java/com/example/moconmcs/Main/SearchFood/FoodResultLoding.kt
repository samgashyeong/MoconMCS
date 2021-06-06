package com.example.moconmcs.Main.SearchFood

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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
    private lateinit var okHttpClient: OkHttpClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result_loding)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_food_result_loding
        )
        viewModel = ViewModelProvider(this).get(FoodViewModel::class.java)

        //checkInternet()
        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .build()
        val barcode: String?
        val foodNum: String?
        if(intent.hasExtra("barcodenum")){
            barcode = intent.getStringExtra("barcodenum").toString()
            Toast.makeText(this, "${barcode}", Toast.LENGTH_SHORT).show()
            getFoodNum(barcode)
        }
        else{
            foodNum = intent.getStringExtra("foodNum").toString()
            getFoodResult(foodNum)
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

                    Log.d("asdf", "getData :  ${isExecution}\n${isExecution?.C005?.total_count}")
                    if(isExecution?.C005?.total_count.equals("0")){
                        Log.d(TAG, "getFoodNum: 데이터를 불러오지못함.")
                        startActivity(Intent(this@FoodResultLoding, FoodNumInput::class.java)
                            .putExtra("barCodeFail", "바코드 인식에 실패하셨습니다. 품목보고번호를 입력해주세요."))
                        finish()
                    }
                    else{ //나온다?
                        val foodName = isExecution!!.C005.row[0].PRDLST_NM
                        val foodNumber = isExecution.C005.row[0].PRDLST_REPORT_NO
                        Log.d(TAG, "getFoodNum: ${isExecution}")
                        getFoodResult(foodNumber)
                    }
                }
            }
        }
    }

    fun getFoodResult(FoodNum : String){
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL_KYUNGROK_API)
            .client(okHttpClient)
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
                        startActivity(Intent(this@FoodResultLoding, FoodResultActivity::class.java)
                            .putExtra("FoodResult", isExecution))
                        finish()
                    }
                }
            }
        }

    }

//    fun checkInternet(){
//        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//    } 개발 예정
    companion object {
        const val serViceKey = "6a957af97bed49989b74"
        const val BASE_URL_BARCODE = "https://openapi.foodsafetykorea.go.kr/api/$serViceKey/C005/json/"
        const val BASE_URL_KYUNGROK_API = "https://vcheck-api.herokuapp.com/api/"
    }
}