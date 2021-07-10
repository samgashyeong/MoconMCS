package com.example.moconmcs.Menu.DeletUser

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.Dialog.LodingDialog
import com.example.moconmcs.Hash.sha
import com.example.moconmcs.Main.AppDatabase
import com.example.moconmcs.Main.FoodDiary.DiaryEntity
import com.example.moconmcs.R
import com.example.moconmcs.ThankYouActivity
import com.example.moconmcs.databinding.ActivityDeletUserCheckBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import java.math.BigInteger
import java.util.*

class DeleteUserCheckActivity : AppCompatActivity() {
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
        val userHash = intent.getStringExtra("userHash")
        val userName = intent.getStringExtra("userName")
        val userKind = intent.getStringExtra("userKind")

        val room = AppDatabase.getInstance(this.applicationContext)
        val diaryDao = room.diaryDao()

        val deleteEntities = LinkedList<DiaryEntity>()
        for (diaryEntity in diaryDao.all) {
            if (diaryEntity.uid.equals(curUid, ignoreCase = true)) deleteEntities.add(diaryEntity)
        }
        for (diaryEntity in deleteEntities) {
            diaryDao.delete(diaryEntity)
        }

        binding.button3.setOnClickListener {
            Log.d(TAG, "onCreate: fewfew${userHash}")
            val input : ByteArray = binding.passwordEt.text.toString().toByteArray()
            var output : ByteArray = input
            try{
                output = sha.encryptSHA(output, "SHA-256")
            }catch (e: Exception){
                Log.d(ContentValues.TAG, "onCreateView: ${e}")
            }
            val data = BigInteger(1, output)
            lodingDialog.show()
            if(binding.passwordEt.text.toString().isEmpty()){
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                lodingDialog.dismiss()
            }
            else if(userHash!! != data.toString(16)){
                Log.d(TAG, "onCreate: else if문 실행됨")
                Log.d(TAG, "onCreate: $userHash")
                Log.d(TAG, "onCreate: ${data.toString(16)}")
                Toast.makeText(this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                lodingDialog.dismiss()
            }
            else{
                Log.d(TAG, "onCreate: else문 실행됨")

                db.collection("User").document(curUid).delete()
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            auth.currentUser?.delete()?.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    startActivity(Intent(this, ThankYouActivity::class.java))
                                    lodingDialog.dismiss()
                                    ActivityCompat.finishAffinity(this)
                                    finish()
                                }
                                else{
                                    Toast.makeText(this, "오류가 발생하였습니다.104", Toast.LENGTH_SHORT).show()
                                    db.collection("User").document(curUid).set(com.example.moconmcs.data.FirebaseDb.User(userName.toString(), userKind.toString(), userHash))
                                    lodingDialog.dismiss()
                                }
                            }
                        }
                        else{
                            Toast.makeText(this, "오류가 발생하였습니다.105", Toast.LENGTH_SHORT).show()
                            lodingDialog.dismiss()
                        }
                    }
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }


}