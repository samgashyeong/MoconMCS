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
import com.example.moconmcs.Dialog.CommDialog
import com.example.moconmcs.Dialog.CommDialogInterface
import com.example.moconmcs.Dialog.ErrorDialog
import com.example.moconmcs.Dialog.ErrorDialogInterface
import com.example.moconmcs.Main.SearchFood.NetWork.GetFoodNum
import com.example.moconmcs.Main.SearchFood.NetWork.GetFoodResult
import com.example.moconmcs.R
import com.example.moconmcs.data.KyungrokApi.FoodData
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

class FoodResultLoding : AppCompatActivity(), ErrorDialogInterface, CommDialogInterface{
    private lateinit var binding: ActivityFoodResultLodingBinding
    private lateinit var viewModel: FoodViewModel
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var errorDialog: ErrorDialog
    private lateinit var commDialog: CommDialog
    private lateinit var data : FoodData
    private var ActivityOn = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result_loding)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_food_result_loding
        ) //커밋할끄니ㅏ깐
        viewModel = ViewModelProvider(this).get(FoodViewModel::class.java)
        errorDialog = ErrorDialog(this, this, "신고된 식품", "사용자들로 부터 접수된 데이터가 불명확한 데이터입니다.\n그래도 결과를 보시겠습니까?")
        commDialog = CommDialog(this, this, "검색 취소", "검색을 종료하시겠습니까?")

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
        binding.backBtn.setOnClickListener {
            commDialog.show()
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
                        startActivity(Intent(this@FoodResultLoding, FoodResultActivity::class.java)
                            .putExtra("barCodeFail", "바코드 인식에 실패하셨습니다. 품목보고번호를 입력해주세요."))
                        finish()
                    }
                    else{ //나온다?
                        binding.tvResult.text = "바코드가 인식되었습니다. 잠시 기달려주세요!"
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
                if(ActivityOn){
                    val isExecution = execution.body()

                    withContext(Dispatchers.Main){

//                    Log.d("asdf", "getData :  ${isExecution}\n${isExecution?.C005?.total_count}")
                        if(isExecution?.err_msg != null){
                            Log.d(TAG, "getFoodResult: 내용이 없습니다.")
                            if(commDialog.isShowing){
                                commDialog.dismiss()
                            }
                            startActivity(Intent(this@FoodResultLoding, FoodResultActivity::class.java)
                                .putExtra("ResultFail", true))
                            finish()
                        }
                        else{
                            Log.d(TAG, "getFoodResult: ${isExecution?.data_res?._id}" +
                                    ", ${isExecution?.data_res?.prodName}" +
                                    ", ${isExecution?.data_res?.materials}")
                            if(commDialog.isShowing){
                                commDialog.dismiss()
                            }
                            if(isExecution?.data_res?.status == "Error"){
                                data = isExecution
                                errorDialog.show()
                            }
                            else{
                                startActivity(Intent(this@FoodResultLoding, FoodResultActivity::class.java)
                                    .putExtra("FoodResult", isExecution))
                                finish()
                            }
                        }
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

    override fun onCheckBtnClick() {
        commDialog.dismiss()
        ActivityOn = false
        finish()
    }

    override fun onCancleBtnClick() {
        commDialog.dismiss()
    }

    override fun onCheckBtnClick1() {
        startActivity(Intent(this@FoodResultLoding, FoodResultActivity::class.java)
            .putExtra("FoodResult", data))
        errorDialog.dismiss()
        finish()
    }

    override fun onCancleBtnClick1() {
        finish()
    }

    override fun onBackPressed() {
        commDialog.show()
    }
}