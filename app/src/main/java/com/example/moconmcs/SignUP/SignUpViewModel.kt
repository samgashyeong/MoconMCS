package com.example.moconmcs.SignUP

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moconmcs.data.FirebaseDb.SignUpUser

class SignUpViewModel:ViewModel() {
    var email : MutableLiveData<String> = MutableLiveData()
    var pw : MutableLiveData<String> = MutableLiveData()
    var name : MutableLiveData<String> = MutableLiveData()

//    fun setSignUser(email:String ){
//        email.value =
//    }
}