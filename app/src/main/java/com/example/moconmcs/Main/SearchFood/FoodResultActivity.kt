package com.example.moconmcs.Main.SearchFood

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.moconmcs.FoodResultListActivity
import com.example.moconmcs.R
import com.example.moconmcs.data.KyungrokApi.FoodData
import com.example.moconmcs.databinding.ActivityFoodResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FoodResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodResultBinding
    private lateinit var viewModel: FoodViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userKind: String
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_result)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        viewModel = ViewModelProvider(this).get(FoodViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_result)

        if(auth.currentUser != null){
            db.collection("User").document(auth.currentUser!!.uid).get()
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        userKind = it.result.data!!.getValue("UserKind").toString()
                    }
                }
        }

        viewModel.foodResult.observe(this, Observer {
            Log.d(TAG, "onCreate: ${it}")
//            when(it.status){
//                "normal" -> {
//                    if(it.livestock != 0){
//
//                    }
//                }
//                "error" ->{
//                }
//                "not-found" ->{
//
//                }
//
//
//            }
            if(it.livestock > 0 || it.otherThanLivestock > 0 ){
                badResult()
            }
            binding.notFoundProductTv.text = "검색되지않은 상품 : ${it.notFound}"
            binding.button.setOnClickListener {
                startActivity(Intent(this, FoodResultListActivity::class.java))
            }

        })

    }

    fun badResult(){
        binding.resultTV.text = "안좋음"
        binding.resultIV.setImageResource(R.drawable.undraw_memory_storage_reh01)
    }
}