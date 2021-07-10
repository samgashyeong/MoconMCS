package com.example.moconmcs.Main.SearchFood

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.Main.SearchFood.db.FoodListEntity
import com.example.moconmcs.R
import com.example.moconmcs.databinding.ActivityPrevResultBinding

class PrevResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrevResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prev_result)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_prev_result)

        setSupportActionBar(binding.toolbar)

        var a = intent.getSerializableExtra("foodlist")
        Log.d(TAG, "onCreate: 넘겨받음$a")


        binding.recycler.adapter = PrevResultFoodListAdapter(a as ArrayList<FoodListEntity>)

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
}