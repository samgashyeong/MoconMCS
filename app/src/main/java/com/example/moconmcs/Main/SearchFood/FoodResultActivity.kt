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
            , "?????? ??????"
            , "????????? ???????????? ????????????????????? ???????????????.\n????????? ????????? ?????? ????????? ??????????????? ????????????.")
        whyDialog = WhyDialog(this
        ,this
        , "??? ?????????????"
        , "????????? ?????? ???????????? ?????? ????????? ??????????????? ?????? ??????????????? ??? ?????? ????????????.\n????????? ????????? ????????? ??? ??? ?????? ?????????????????????.")

        if(intent.hasExtra("ResultFail")){
            failResult()
        }
        else if(intent.hasExtra("barCodeFail")){
            barCodeFailResult()
        }
        //?????? ????????? ??????
        else if(intent.hasExtra("FoodResult")){
            foodResultData = intent.getSerializableExtra("FoodResult") as FoodData
            binding.button.setOnClickListener {
                startActivity(Intent(this, FoodResultListActivity::class.java)
                    .putExtra("foodList", foodResultData.data_res.materials)
                    .putExtra("prodName", foodResultData.data_res.prodName)
                    .putExtra("userKind", intent.getStringExtra("userKind")))
            }
//            Log.d(TAG, "onCreate: ?????${intent.getStringExtra("IsEat")}")
//            Log.d(TAG, "onCreate: FoodResult?????? ??????")
            when(intent.getBooleanExtra("canEat", true)){

                false -> {
//                    Log.d(TAG, "onCreate: ?????? ??? ??????")
                    when(intent.getStringExtra("cause")){
                        "egg"->{
                            whyIsNotEat("????????? ?????????????????????!")
                            binding.resultIV.setImageResource(R.drawable.ic_eggs_icon)
                        }
                        "meat|fish"->{
                            whyIsNotEat("??????????????? ????????? ?????????????????????!")
                            binding.resultIV.setImageResource(R.drawable.ic_fish_meat)
                        }
                        "fish"->{
                            whyIsNotEat("???????????? ?????????????????????!")
                            binding.resultIV.setImageResource(R.drawable.ic_fish)
                        }
                        "meat"->{
                            whyIsNotEat("????????? ????????? ???????????? ?????????!")
                            binding.resultIV.setImageResource(R.drawable.ic_meat_icon)
                        }
                        "milk"->{
                            whyIsNotEat("???????????? ???????????? ?????????!")
                            binding.resultIV.setImageResource(R.drawable.ic_dailyproduct_icon)
                        }
                        "egg|milk"->{
                            whyIsNotEat("???????????? ????????? ???????????? ?????????!")
                            binding.resultIV.setImageResource(R.drawable.ic_all_icon)
                        }
                    }
                    binding.resultTV.text = "?????? ??? ????????????.."
                }
                true -> {
//                    Log.d(TAG, "onCreate: ?????? ??? ??????.")
                    binding.resultTV.text = "?????? ??? ????????????."
                    binding.foodProductTv.text = foodResultData.data_res.prodName
                    when(intent.getStringExtra("userKind")){
                        "??????"->{
                            binding.resultIV.setImageResource(R.drawable.ic_vegan_icon)
                        }
                        "??????"->{
                            binding.resultIV.setImageResource(R.drawable.ic_locto_icon)
                        }
                        "??????"->{
                            binding.resultIV.setImageResource(R.drawable.ic_ovo_icon)
                        }
                        "????????????"->{
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
        binding.resultTV.text = "?????? ??????"
        binding.foodProductTv.text = "????????? ?????? ???????????? ????????????."
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
                    Toast.makeText(this@FoodResultActivity, "????????? ?????????????????????", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun barCodeFailResult(){
        binding.resultTV.text = "?????? ??????"
        binding.resultIV.setImageResource(R.drawable.ic_hehe)
        binding.foodProductTv.text = "???????????? ?????????????????? ?????????????????????."
        binding.IsStrangeTV.visibility = View.INVISIBLE
        binding.button.text = "?????????????????? ???????????? ??????"
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