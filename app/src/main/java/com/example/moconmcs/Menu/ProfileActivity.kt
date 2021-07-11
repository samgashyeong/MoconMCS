package com.example.moconmcs.Menu

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.moconmcs.Menu.ChangePw.UserInfoChangeActivity
import com.example.moconmcs.Menu.DeletUser.DeleteUserActivity
import com.example.moconmcs.Menu.ProfileInfo.ProfileInfoActivity
import com.example.moconmcs.R
import com.example.moconmcs.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var curUser: FirebaseUser
    private lateinit var curUserUid : String
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_profile
        )

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        curUser = firebaseAuth.currentUser!!
        curUserUid = firebaseAuth.currentUser!!.uid
        firebaseFirestore.collection("User").document(curUserUid).get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    viewModel.setUserProfile(it.result.data!!.getValue("name").toString()
                        , it.result.data!!.getValue("userKind").toString()
                        , firebaseAuth.currentUser!!.email.toString()
                        ,it.result.data!!.getValue("pw").toString())
                    Log.d(TAG, "onCreate: ${viewModel.userHash!!.value.toString()}")
                }
            }



//        binding.logoutBtn.setOnClickListener {
//            firebaseAuth.signOut()
//            startActivity(Intent(this, LoginActivity::class.java))
//            ActivityCompat.finishAffinity(this)
//        }

        binding.profileInfoBtn.setOnClickListener {
            startActivityForResult(Intent(this, ProfileInfoActivity::class.java)
                .putExtra("myName", viewModel.userName!!.value)
                .putExtra("myKind", viewModel.userKind!!.value)
                .putExtra("myEmail", firebaseAuth.currentUser?.email),
            200)

        }

        binding.deleteUserBtn.setOnClickListener {
            Log.d(TAG, "onCreate: $curUser")
            startActivity(Intent(this, DeleteUserActivity::class.java)
                .putExtra("emailString", curUser.email.toString())
                .putExtra("userHash", viewModel.userHash!!.value)
                .putExtra("userName", viewModel.userName!!.value)
                .putExtra("userKind", viewModel.userKind!!.value))
        }

        binding.changePwBtn.setOnClickListener {
            startActivityForResult(Intent(this, UserInfoChangeActivity::class.java)
                .putExtra("userHash", viewModel.userHash!!.value)
                .putExtra("emailString", curUser.email.toString()), 100)
        }


        setSupportActionBar(binding.toolbar)

        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_icon_toolbar)
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




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                data!!.getStringExtra("writeData")
                Log.d("asdf", "onActivityResult: " + data.getStringExtra("curHash"))
                viewModel.userHash!!.value = data.getStringExtra("curHash")
                Log.d("데이터 뷰모델", "onActivityResult: ${viewModel.userHash!!.value}")
            }
        }
        if(requestCode == 200){
            curUserUid = firebaseAuth.currentUser!!.uid
            firebaseFirestore.collection("User").document(curUserUid).get()
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        viewModel.setUserProfile(it.result.data!!.getValue("name").toString()
                            , it.result.data!!.getValue("userKind").toString()
                            , firebaseAuth.currentUser!!.email.toString()
                            ,it.result.data!!.getValue("pw").toString())
                        Log.d(TAG, "onCreate: ${viewModel.userHash!!.value.toString()}")
                    }
                }
        }
    }
}


