package com.example.moconmcs.SignUP

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel:ViewModel() {
    var email : MutableLiveData<String> = MutableLiveData()
    var pw : MutableLiveData<String> = MutableLiveData()
    var name : MutableLiveData<String> = MutableLiveData()

    fun setSignUser(email:String, pw:String, name:String  ){
        this.email.value = email
        this.pw.value = pw
        this.name.value = name
    }
}