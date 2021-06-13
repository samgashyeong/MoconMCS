package com.example.moconmcs.Menu

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.moconmcs.LoginActivity
import com.example.moconmcs.Main.MainActivity
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

        val intent = intent
        val userName = intent.getStringExtra("userName")
        val userKind = intent.getStringExtra("userKind")
        val userEmail = intent.getStringExtra("userEmail")

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_profile
        )

        if(firebaseAuth.currentUser != null){
            curUser = firebaseAuth.currentUser!!
            curUserUid = curUser.uid
        }



//        binding.logoutBtn.setOnClickListener {
//            firebaseAuth.signOut()
//            startActivity(Intent(this, LoginActivity::class.java))
//            ActivityCompat.finishAffinity(this)
//        }

        binding.deleteUserBtn.setOnClickListener {
            Log.d(TAG, "onCreate: $curUser")
            startActivity(Intent(this, DeleteUserActivity::class.java)
                .putExtra("emailString", curUser.email.toString()))
        }

        binding.button2.setOnClickListener {
            startActivity(Intent(this, UserInfoChangeActivity::class.java)
                .putExtra("myName", userName)
                .putExtra("myKind", userKind))
        }



        binding.myNameTv.text = userName
        binding.myKindTv.text = userKind
        binding.myEmailTv.text = userEmail

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
}


