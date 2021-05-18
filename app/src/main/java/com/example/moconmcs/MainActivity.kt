package com.example.moconmcs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.databinding.ActivityMainBinding
import com.google.android.gms.common.util.DataUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MainActivity : AppCompatActivity(), BottomSheetButtonClickListener {
    private lateinit var binding: ActivityMainBinding
    val TAG : String = "MainActivity"
    val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.bottmnavview.background=null
        binding.bottmnavview.run{
            setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.food_map ->{
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame, FoodMapFragment())
                            .commit()
                        binding.toolbar.title = "푸드맵"
                    }
                    R.id.setting->{
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame, FoodDiaryFragment())
                            .commit()
                        binding.toolbar.title = "식단다이어리"
                    }
                }
                true
            }
            selectedItemId = R.id.food_map
        }


        binding.floatingBtn.setOnClickListener {
            bottomSheetDialog.show(supportFragmentManager, "foodBottomSheet")
        }
    }

    override fun layoutClick(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}