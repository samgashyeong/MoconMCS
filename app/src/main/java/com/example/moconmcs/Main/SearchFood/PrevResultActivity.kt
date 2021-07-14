package com.example.moconmcs.Main.SearchFood

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.Main.AppDatabase
import com.example.moconmcs.Main.SearchFood.db.FoodListEntity
import com.example.moconmcs.R
import com.example.moconmcs.data.KyungrokApi.Material
import com.example.moconmcs.databinding.ActivityPrevResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class PrevResultActivity : AppCompatActivity(), PrevResultFoodListAdapter.OnClickList {
    private lateinit var binding: ActivityPrevResultBinding
    private lateinit var foodList: ArrayList<FoodListEntity>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prev_result)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_prev_result)

        setSupportActionBar(binding.toolbar)

        foodList = intent.getSerializableExtra("foodlist") as ArrayList<FoodListEntity>

        if(foodList.isEmpty()){
            binding.notDataTv.visibility = View.VISIBLE
        }

        binding.recycler.adapter = PrevResultFoodListAdapter(foodList, this)

        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_icon_toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(position: Int) {
        val auth = FirebaseAuth.getInstance()
        val Fdb = FirebaseFirestore.getInstance()
        Fdb.collection("User").document(auth.currentUser!!.uid).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val userKind = it.result.data!!.getValue("userKind").toString()
                    startActivity(Intent(this, FoodResultListActivity::class.java)
                        .putExtra("foodList", foodList.get(position).foodList as ArrayList<Material>)
                        .putExtra("prodName", foodList.get(position).foodName)
                        .putExtra("userKind", userKind))
                }
            }
    }

}