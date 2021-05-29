package com.example.moconmcs.Main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.moconmcs.*
import com.example.moconmcs.Main.BottomSheet.BottomSheetButtonClickListener
import com.example.moconmcs.Main.BottomSheet.BottomSheetDialog
import com.example.moconmcs.Main.FoodDiary.FoodDiaryFragment
import com.example.moconmcs.Main.FoodMap.FoodMapFragment
import com.example.moconmcs.Main.SearchFood.BarCodeActivity
import com.example.moconmcs.Main.SearchFood.FoodNumInput
import com.example.moconmcs.Menu.HelpMenuActivity
import com.example.moconmcs.Menu.ProfileActivity
import com.example.moconmcs.Menu.SettingActivity
import com.example.moconmcs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),
    BottomSheetButtonClickListener {
    private lateinit var binding : ActivityMainBinding
    val bottomSheetDialog : BottomSheetDialog =
        BottomSheetDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )


        setSupportActionBar(binding.toolbar)

        binding.bottmnavview.run{
            setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.food_map -> setFragment(1, "푸드맵")
                    R.id.setting -> setFragment(2, "식단다이어리")
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profile ->{
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.setting ->{
                startActivity(Intent(this, SettingActivity::class.java))
            }
            R.id.helpMenu ->{
                startActivity(Intent(this, HelpMenuActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)

    }
}