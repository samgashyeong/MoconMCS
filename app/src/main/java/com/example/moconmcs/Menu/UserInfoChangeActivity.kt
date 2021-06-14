package com.example.moconmcs.Menu

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.R
import com.example.moconmcs.data.FirebaseDb.User
import com.example.moconmcs.databinding.ActivityUserInfoChangeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates

class UserInfoChangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserInfoChangeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private var curSelect by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info_change)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_user_info_change
        )
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_icon_toolbar)

        val curUser = auth.currentUser
        val curName = intent.getStringExtra("myName")
        var curKind = intent.getStringExtra("myKind")

        val items = resources.getStringArray(R.array.my_array)
        val myAdapter = ArrayAdapter.createFromResource(this, R.array.my_array, R.layout.spinner_layout)
        myAdapter.setDropDownViewResource(R.layout.spinner_layout)


        //사실이름칸임 ㅈㅅ
        binding.myEmailTv.setText(curName)

        when(curKind){
            "비건"-> curSelect = 3
            "락토"-> curSelect = 2
            "오보"-> curSelect = 1
            "락토오보"->curSelect = 0
        }
        //myNameTv는 스피너임
        binding.myNameTv.adapter = myAdapter
        binding.myNameTv.setSelection(curSelect)

        binding.myNameTv.onItemSelectedListener= object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                when (position) {
                    0 -> {
                        curKind = "락토오보"
                    }
                    1 -> {
                        curKind = "오보"
                    }
                    2 ->{
                        curKind = "락토"
                    }
                    3->{
                        curKind = "비건"
                    }
                }
            }
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }
        }




        binding.btn.setOnClickListener {
            db.collection("User").document(curUser!!.uid).update("name", binding.myEmailTv.text.toString()
            , "userKind", curKind!!)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        curUser.updatePassword(binding.myNewPwEt.text.toString())
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    Toast.makeText(this, "정보변경에 성공", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                else{
                                    binding.errorTv.visibility = View.VISIBLE
                                    binding.errorTv.text = "오류가 발생하였습니다.1"
                                }
                            }
                    }
                    else{
                        binding.errorTv.text = "오류가 발생하였습니다."
                    }
                }
        }

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