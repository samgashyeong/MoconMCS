package com.example.moconmcs.Menu

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.Dialog.LodingDialog
import com.example.moconmcs.R
import com.example.moconmcs.ThankYouActivity
import com.example.moconmcs.databinding.ActivityDeletUserCheckBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeletUserCheckActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeletUserCheckBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var lodingDialog: LodingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delet_user_check)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_delet_user_check
        )
        lodingDialog = LodingDialog(this, "회원탈퇴 진행중...")

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        val curEmail = intent.getStringExtra("emailString")
        val curUid = auth.currentUser!!.uid

        binding.button3.setOnClickListener {
            lodingDialog.show()
            if(binding.passwordEt.text.toString().isEmpty()){
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                lodingDialog.dismiss()
            }
            else{
                auth.signOut()
                Log.d(ContentValues.TAG, "onCreate: ${curEmail}")
                auth.signInWithEmailAndPassword(curEmail.toString(), binding.passwordEt.text.toString())
                    .addOnCompleteListener{
                        if(it.isSuccessful){
                            val user = auth.currentUser
                            user!!.delete()
                                .addOnCompleteListener {
                                    if(it.isSuccessful){
                                        db.collection("User").document(curUid).delete()
                                            .addOnCompleteListener {
                                                startActivity(Intent(this, ThankYouActivity::class.java))
                                                lodingDialog.dismiss()
                                                ActivityCompat.finishAffinity(this)
                                                finish()
                                            }
                                    }
                                    else{
                                        lodingDialog.dismiss()
                                        Toast.makeText(this, "오류가 발생하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        else{
                            lodingDialog.dismiss()
                            Toast.makeText(this, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}