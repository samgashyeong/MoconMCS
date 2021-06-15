package com.example.moconmcs.Menu.ProfileInfo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moconmcs.R
import com.example.moconmcs.databinding.ActivityProfileInfoBinding

class ProfileInfoActivity : AppCompatActivity() {
    private lateinit var binding:ActivityProfileInfoBinding
    private lateinit var vM : ProfileChangeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_info)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_info)
        vM = ViewModelProvider(this).get(ProfileChangeViewModel::class.java)
        vM.userName.value = intent.getStringExtra("myName")
        vM.userEmail.value = intent.getStringExtra("myEmail")
        vM.userKind.value = intent.getStringExtra("myKind")

        setSupportActionBar(binding.toolbar)


        supportFragmentManager.beginTransaction().replace(R.id.frame, ProfileInfoFragment()).commit()


        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_icon_toolbar)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                setResult(Activity.RESULT_OK, Intent())
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    public fun change(i : Int){
        when(i){
            1->supportFragmentManager.beginTransaction().replace(R.id.frame, ProfileInfoFragment()).commit()
            2->supportFragmentManager.beginTransaction().replace(R.id.frame, ChangeUser_Info()).commit()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }
}