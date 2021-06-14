package com.example.moconmcs.Menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel:ViewModel() {
    var userName : MutableLiveData<String>? = MutableLiveData()
    var userKind : MutableLiveData<String>? = MutableLiveData()
    var userEmail : MutableLiveData<String>? = MutableLiveData()
    var userHash : MutableLiveData<String>? = MutableLiveData()

    fun setUserProfile(userName : String, userKind : String, userEmail : String, userHash : String){
        this.userName!!.value = userName
        this.userKind!!.value = userKind
        this.userEmail!!.value = userEmail
        this.userHash!!.value = userHash
    }
}