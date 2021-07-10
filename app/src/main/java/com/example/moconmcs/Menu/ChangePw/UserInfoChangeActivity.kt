package com.example.moconmcs.Menu.ChangePw

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.Dialog.LodingDialog
import com.example.moconmcs.Hash.sha
import com.example.moconmcs.R
import com.example.moconmcs.databinding.ActivityUserInfoChangeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception
import java.math.BigInteger
import kotlin.properties.Delegates

class UserInfoChangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserInfoChangeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private var curSelect by Delegates.notNull<Int>()
    private lateinit var lodingDialog:LodingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info_change)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_user_info_change
        )
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        lodingDialog = LodingDialog(this, "비밀번호 변경 중...")

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_icon_toolbar)

        val curUser = auth.currentUser
        val curName = intent.getStringExtra("myName")
        var curKind = intent.getStringExtra("myKind")
        var curHash = intent.getStringExtra("userHash")
        val curEmail = intent.getStringExtra("emailString")
        var data : BigInteger
        var data1 : BigInteger


        binding.btn.setOnClickListener {
            lodingDialog.show()
            val input : ByteArray = binding.myNewPwEt.text.toString().toByteArray()
            var output : ByteArray = input
            try{
                output = sha.encryptSHA(output, "SHA-256")
            }catch (e: Exception){
                Log.d(ContentValues.TAG, "onCreateView: ${e}")
            }
            data = BigInteger(1, output)
            val input1 : ByteArray = binding.curPassEt.text.toString().toByteArray()
            var output1 : ByteArray = input1
            try{
                output1 = sha.encryptSHA(output1, "SHA-256")
            }catch (e: Exception){
                Log.d(ContentValues.TAG, "onCreateView: ${e}")
            }
            data1 = BigInteger(1, output1)
            Log.d(TAG, "onCreate: ${data.toString(16)}\n${data1.toString(16)}")
            if(binding.curPassEt.text.isEmpty()
                || binding.myNewPwEt.text.isEmpty()
                || binding.myAgainPwEt.text.isEmpty()) {
                Toast.makeText(this, "빈칸을 채워주세요", Toast.LENGTH_SHORT).show()
                lodingDialog.dismiss()
            }
            else if(binding.myNewPwEt.text.toString() != binding.myAgainPwEt.text.toString()){
                Toast.makeText(this, "비밀번호가 서로 맞지않습니다.", Toast.LENGTH_SHORT).show()
                lodingDialog.dismiss()
            }
            else if(data1.toString(16) != curHash){
                Toast.makeText(this, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onCreate: ${data1.toString(16)}\n${curHash}")
                lodingDialog.dismiss()
            }
            else if(!binding.myNewPwEt.text.toString()
                    .matches("^(?=.*[A-Za-z])(?=.*[0-9]).{8,}$".toRegex())){
                Log.d(TAG, "onCreateView: 비밀번호 형식이 틀림")
                Toast.makeText(this, "비밀번호 형식을 지켜주세요.", Toast.LENGTH_SHORT).show()
                lodingDialog.dismiss()
            }
            else{
                auth.signOut()
                auth.signInWithEmailAndPassword(curEmail.toString(),binding.curPassEt.text.toString())
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            curUser!!.updatePassword(binding.myNewPwEt.text.toString())
                                .addOnCompleteListener {
                                    if(it.isSuccessful){
                                        db.collection("User").document(curUser.uid).update("pw", data.toString(16))
                                            .addOnCompleteListener {
                                                if(it.isSuccessful){
                                                    Toast.makeText(this, "비밀번호 변경에 성공하셨습니다.", Toast.LENGTH_SHORT).show()
                                                    Log.d(TAG, "onCreate: ${data.toString(16)}")
                                                    lodingDialog.dismiss()
                                                    setResult(Activity.RESULT_OK, Intent().putExtra("curHash", data.toString(16)))
                                                    finish()
                                                }
                                                else{
                                                    Toast.makeText(this, "오류가 발생하였습니다. 다시 시도해주세요.101", Toast.LENGTH_SHORT).show()
                                                    Log.d(TAG, "onCreate: ${it.result}")
                                                    lodingDialog.dismiss()
                                                }
                                            }
                                    }
                                    else{
                                        Toast.makeText(this, "오류가 발생하였습니다. 다시 시도해주세요.102", Toast.LENGTH_SHORT).show()
                                        Log.d(TAG, "onCreate: ${it}")
                                        lodingDialog.dismiss()
                                    }
                                }
                        }
                        else{
                            Toast.makeText(this, "오류가 발생하였습니다. 다시 시도해주세요.103", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "onCreate: ${it}")
                            lodingDialog.dismiss()
                        }
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