package com.example.moconmcs.Menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.R
import com.example.moconmcs.databinding.ActivityDeleteUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeleteUserActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityDeleteUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_user)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_delete_user
        )

        val curEmail = intent.getStringExtra("emailString")
        val curUid = auth.currentUser!!.uid

        binding.cancelBtn.setOnClickListener {
            finish()
        }

        binding.deleteUserBtn.setOnClickListener {
            startActivity(Intent(this, DeletUserCheckActivity::class.java)
                .putExtra("emailString", curEmail))
            finish()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

//        binding.deleteUserBtn.setOnClickListener {
//            if(binding.editText.text.toString().isEmpty()){
//                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
//            }
//            else{
//                auth.signOut()
//                Log.d(TAG, "onCreate: ${curEmail}")
//                auth.signInWithEmailAndPassword(curEmail.toString(), binding.editText.text.toString())
//                    .addOnCompleteListener{
//                        if(it.isSuccessful){
//                            val user = auth.currentUser
//                            user!!.delete()
//                                .addOnCompleteListener {
//                                    if(it.isSuccessful){
//                                        db.collection("User").document(curUid).delete()
//                                            .addOnCompleteListener {
//                                                startActivity(Intent(this, ThankYouActivity::class.java))
//                                                finish()
//                                            }
//                                    }
//                                    else{
//                                        Toast.makeText(this, "오류가 발생하였습니다. 다시시도해주세요", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                        }
//                        else{
//                            Toast.makeText(this, "오류가 발생하였습니다 다시시도해주세요.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//            }
//        }
    }
}