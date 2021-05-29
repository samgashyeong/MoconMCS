package com.example.moconmcs.SignUP

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.moconmcs.R
import com.example.moconmcs.data.FirebaseDb.SignUpUser
import com.example.moconmcs.databinding.FragmentSignUp1Binding

class SignUpFragment1 : Fragment() {

    private lateinit var binding : FragmentSignUp1Binding
    private lateinit var userViewModel : SignUpViewModel

    private lateinit var email : String
    private lateinit var pw: String
    private lateinit var name: String
//
//
//    fun saveData(){
//
//        userViewModel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)
//        userViewModel.setSignUser(binding.emailLogin.text.toString()
//            , binding.pwLogin.text.toString()
//            , binding.nameEt.text.toStringe())
//    }
    fun saveData(email : String, pw : String, name : String){
        this.email = email
        this.pw = pw
        this.name = name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_sign_up1, container, false)
        userViewModel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userViewModel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)
    }

    fun loadData(fm : SignUpFragment2){
        fm.saveData(binding.emailLogin.text.toString(), binding.pwLogin.text.toString(), binding.nameEt.text.toString())
    }
}