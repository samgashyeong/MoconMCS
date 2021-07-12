package com.example.moconmcs.Main.SearchFood

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.Dialog.ErrorDialog
import com.example.moconmcs.Dialog.ErrorDialogInterface
import com.example.moconmcs.Dialog.WhyDialog
import com.example.moconmcs.Dialog.WhyDialogInterface
import com.example.moconmcs.Main.AppDatabase
import com.example.moconmcs.Main.SearchFood.NetWork.GetFoodResult
import com.example.moconmcs.Main.SearchFood.db.FoodListEntity
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


class FoodResultActivity : AppCompatActivity(), ErrorDialogInterface, WhyDialogInterface {
    private lateinit var binding: ActivityFoodResultBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userKind: String
    private lateinit var foodList : ArrayList<Material>
    private lateinit var foodResultData: FoodData
    private lateinit var errorDialog : ErrorDialog
    private lateinit var whyDialog: WhyDialog
    private lateinit var rDb : AppDatabase
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result)

        rDb = AppDatabase.getInstance(this)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_result)
        errorDialog = ErrorDialog(this@FoodResultActivity
            , this
            , "오류 신고"
            , "식품의 오류점을 서버개발자한테 전달합니다.\n확인을 누르면 오류 정보를 개발자한테 보냅니다.")
        whyDialog = WhyDialog(this
        ,this
        , "왜 이러나요?"
        , "식품에 대한 데이터가 없는 관계로 해당식품에 대한 검색결과를 낼 수가 없습니다.\n원활한 서비스 이용을 할 수 있게 노력하겠습니다.")

        if(intent.hasExtra("ResultFail")){
            failResult()
        }
        else if(intent.hasExtra("barCodeFail")){
            barCodeFailResult()
        }
        //코드 더러움 주의
        else if(intent.hasExtra("FoodResult")){
            foodResultData = intent.getSerializableExtra("FoodResult") as FoodData
            val a:FoodData = intent.getSerializableExtra("FoodResult") as FoodData
            binding.button.setOnClickListener {
                startActivity(Intent(this, FoodResultListActivity::class.java)
                    .putExtra("foodList", a.data_res.materials)
                    .putExtra("prodName", a.data_res.prodName))
            }
            Log.d(TAG, "onCreate: ?????${intent.getStringExtra("IsEat")}")
            Log.d(TAG, "onCreate: FoodResult에서 받음")
            when(intent.getStringExtra("IsEat")){
                "0"->{
                    Log.d(TAG, "onCreate: 먹을 수 없음")
                    when(intent.getStringExtra("cause")){
                        "bad_egg"->{
                            whyIsNotEat("계란이 포함되어있어요!")
                            binding.resultIV.setImageResource(R.drawable.ic_eggs_icon)
                        }
                        "bad_meatAndFish"->{
                            whyIsNotEat("해산물이랑 고기가 포함되어있어요!")
                            binding.resultIV.setImageResource(R.drawable.ic_fish_meat)
                        }
                        "bad_fish"->{
                            whyIsNotEat("해산물이 포함되어있어요!")
                            binding.resultIV.setImageResource(R.drawable.ic_fish)
                        }
                        "bad_meat"->{
                            whyIsNotEat("동물성 성분이 포함되어 있어요!")
                            binding.resultIV.setImageResource(R.drawable.ic_meat_icon)
                        }
                    }
                    binding.resultTV.text = "드실 수 없습니다.."
                }
                "1"->{
                    Log.d(TAG, "onCreate: 먹을 수 있음.")
                    val a : FoodData = intent.getSerializableExtra("FoodResult") as FoodData
                    binding.resultTV.text = "드실 수 있습니다."
                    binding.foodProductTv.text = a.data_res.prodName
                    when(intent.getStringExtra("userKind")){
                        "비건"->{
                            binding.resultIV.setImageResource(R.drawable.ic_vegan_icon)
                        }
                        "락토"->{
                            binding.resultIV.setImageResource(R.drawable.ic_locto_icon)
                        }
                        "오보"->{
                            binding.resultIV.setImageResource(R.drawable.ic_ovo_icon)
                        }
                        "락토오보"->{
                            binding.resultIV.setImageResource(R.drawable.ic_locto_ovo_icon)
                        }
                    }
                }
            }
        }
        binding.IsStrangeTV.setOnClickListener {
            errorDialog.show()

        }
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.WhatTv.setOnClickListener {
            whyDialog.show()
        }
    }



    private fun whyIsNotEat(cause: String) {
        binding.foodProductTv.text = cause
    }

    fun failResult(){
        binding.resultTV.text = "검색 실패"
        binding.foodProductTv.text = "상품에 대한 데이터가 없습니다."
        binding.resultIV.setImageResource(R.drawable.ic_hehe)
        binding.button.visibility = View.GONE
        binding.IsStrangeTV.visibility = View.INVISIBLE
        binding.WhatTv.visibility = View.VISIBLE
    }



    fun errorResultToServer(){
        val api = Retrofit.Builder()
            .baseUrl(FoodResultLoading.BASE_URL_KYUNGROK_API)
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
    fun barCodeFailResult(){
        binding.resultTV.text = "검색 실패"
        binding.resultIV.setImageResource(R.drawable.ic_hehe)
        binding.foodProductTv.text = "바코드 인식에 실패하셨습니다."
        binding.IsStrangeTV.visibility = View.INVISIBLE
        binding.button.text = "품목보고번호 조회하러 가기"
        binding.button.setOnClickListener {
            startActivity(Intent(this@FoodResultActivity, FoodNumInput::class.java))
            finish()
        }
    }

    override fun onCheckBtnClick1() {
        errorResultToServer()
    }

    override fun onCancleBtnClick1() {
        errorDialog.dismiss()
    }

    override fun onCheckIsWhy() {
        whyDialog.dismiss()
    }
}