package com.example.moconmcs.Main.SearchFood

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.Dialog.CommDialog
import com.example.moconmcs.Dialog.CommDialogInterface
import com.example.moconmcs.R
import com.example.moconmcs.databinding.ActivityFoodNumInputBinding


class FoodNumInput : AppCompatActivity(), CommDialogInterface{
    private lateinit var binding: ActivityFoodNumInputBinding
    private lateinit var dialog: CommDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_num_input)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_food_num_input
        )
        dialog = CommDialog(this, this, "검색 종료", "검색을 종료하시겠습니까?")


        binding.completeBtn.setOnClickListener {
            if(binding.EtFoodNum.text.isEmpty()){
                Toast.makeText(this, "숫자를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else{
                startActivity(Intent(this, FoodResultLoading::class.java)
                    .putExtra("foodNum", binding.EtFoodNum.text.toString()))
                finish()
            }
        }
        binding.backBtn.setOnClickListener {
            dialog.show()
        }
    }


    override fun onBackPressed() {
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT

        dialog.show()
        dialog.window!!.attributes = lp
    }

    override fun onCheckBtnClick() {
        finish()
    }

    override fun onCancleBtnClick() {
        dialog.dismiss()
    }


}