package com.example.moconmcs.Main.SearchFood

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.Dialog.ErrorDialog
import com.example.moconmcs.Dialog.ErrorDialogInterface
import com.example.moconmcs.Main.SearchFood.NetWork.GetFoodResult
import com.example.moconmcs.R
import com.example.moconmcs.data.KyungrokApi.FoodData
import com.example.moconmcs.data.KyungrokApi.Material
import com.example.moconmcs.databinding.ActivityFoodResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable


class FoodResultActivity : AppCompatActivity(), ErrorDialogInterface {
    private lateinit var binding: ActivityFoodResultBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userKind: String
    private lateinit var foodList : ArrayList<Material>
    private lateinit var foodResultData: FoodData
    private lateinit var errorDialog : ErrorDialog
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_result)
        errorDialog = ErrorDialog(this@FoodResultActivity
            , this
            , "오류 신고"
            , "식품의 오류점을 서버개발자한테 전달합니다.\\n확인을 누르면 오류 정보를 개발자한테 보냅니다.")

        if(intent.hasExtra("barCodeFail")){
            failResult()
        }
        else{
            foodResultData = intent.getSerializableExtra("FoodResult") as FoodData
            if(auth.currentUser != null){
                db.collection("User").document(auth.currentUser!!.uid).get()
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            userKind = it.result.data!!.getValue("userKind").toString()
                            checkIsBadResult(userKind)
                        }
                    }
            }
            Log.d(TAG, "onCreate: ${foodResultData}")

            if(foodResultData?.data_res.livestock > 0){
                badResult()
            }
//        checkIsBadResult(userKind)
            binding.notFoundProductTv.text = "검색되지않은 상품 수 : ${ foodResultData?.data_res.notFound}"
            binding.foodProductTv.text = foodResultData.data_res.prodName.toString()

            binding.button.setOnClickListener {
                startActivity(Intent(this, FoodResultListActivity::class.java)
                    .putExtra("foodList", foodResultData?.data_res?.materials as Serializable)
                    .putExtra("prodName", foodResultData.data_res.prodName.toString()))
            }
        }
        binding.IsStrangeTV.setOnClickListener {
            errorDialog.show()

        }
    }


    fun checkIsBadResult(userKind : String){
        when(userKind){
            "비건"->{
                //채소, 과일 아닌것만 판별
            }
            "락토"->{
                //유제품, 꿀, 채소 과일 아닌것만 판별
            }
            "오보"->{
                //채소, 과일, 꿀, 달걀 아닌것만 판별
            }
            "락토오보"->{
                //채소, 과일, 꿀, 달걀, 유제품이 아닌것만 판별
            }
        }
    }

    fun badResult(){
        binding.resultTV.text = "안좋음"
        binding.resultIV.setImageResource(R.drawable.undraw_memory_storage_reh01)
    }
    fun failResult(){
        binding.resultTV.text = "검색 실패"
        binding.resultIV.setImageResource(R.drawable.undraw_memory_storage_reh01)
        binding.notFoundProductTv.visibility = View.INVISIBLE
        binding.IsStrangeTV.text = "오류를 신고하시겠어요?"
        binding.button.visibility = View.INVISIBLE
        binding.foodProductTv.visibility = View.INVISIBLE
        binding.IsStrangeTV.visibility = View.INVISIBLE
    }

    override fun onCheckBtnClick() {
        errorResultToServer()
    }

    override fun onCancleBtnClick() {
        errorDialog.dismiss()
    }

    fun errorResultToServer(){
        val api = Retrofit.Builder()
            .baseUrl(FoodResultLoding.BASE_URL_KYUNGROK_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GetFoodResult::class.java)

        GlobalScope.launch(Dispatchers.IO){
            val execution = api.errorResult(foodResultData.data_res.prodNum).awaitResponse()
            Log.d("response", "response :${execution}")
            if(execution.isSuccessful){
                val isExecution = execution.body()
                withContext(Dispatchers.Main){
                    errorDialog.dismiss()
                    Toast.makeText(this@FoodResultActivity, "오류가 신고되었습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}