package com.example.moconmcs

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.moconmcs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), BottomSheetButtonClickListener {
    private lateinit var binding : ActivityMainBinding
    val bottomSheetDialog : BottomSheetDialog = BottomSheetDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        binding.bottmnavview.background=null
        binding.bottmnavview.run{
            setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.food_map -> setFragment(1, "푸드맵")
                    R.id.setting-> setFragment(2, "식단다이어리")
                }
                true
            }
            selectedItemId = R.id.food_map
        }
        binding.floatingBtn.setOnClickListener {
            bottomSheetDialog.show(supportFragmentManager, "foodBottomSheet")
        }
    }

    fun setFragment(num: Int, title: String){
        lateinit var fr :Fragment
        when(num){
            1->fr = FoodMapFragment()
            2->fr = FoodDiaryFragment()
        }

        supportFragmentManager.beginTransaction()
                            .replace(R.id.frame, fr)
                            .commit()
        binding.toolbar.title = title
    }

    override fun layoutClick(num: Int) {
        when(num){
            1->{
                startActivity(Intent(this, BarCodeActivity::class.java))
                bottomSheetDialog.dismiss()
            }
            2->{
                startActivity(Intent(this, FoodNumInput::class.java))
                bottomSheetDialog.dismiss()
            }
        }
    }
}