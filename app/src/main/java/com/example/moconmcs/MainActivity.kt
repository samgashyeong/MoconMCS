package com.example.moconmcs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.moconmcs.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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
                    R.id.food_map ->{
                        setFragment(1, "푸드맵")
                    }
                    R.id.setting->{
                        setFragment(2, "식단다이어리")
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
}