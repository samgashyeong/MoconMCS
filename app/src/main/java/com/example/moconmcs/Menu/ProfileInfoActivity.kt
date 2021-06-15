package com.example.moconmcs.Menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.R
import com.example.moconmcs.databinding.ActivityProfileInfoBinding

class ProfileInfoActivity : AppCompatActivity() {
    private lateinit var binding:ActivityProfileInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_info)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_info)
        binding.myNameTv.text = intent.getStringExtra("myName")
        binding.myEmailTv.text = intent.getStringExtra("myEmail")
        binding.myKindTv.text = intent.getStringExtra("myKind")

        setSupportActionBar(binding.toolbar)


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