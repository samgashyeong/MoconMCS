package com.example.moconmcs.Menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel:ViewModel() {
    var userName : MutableLiveData<String>? = MutableLiveData()
    var userKind : MutableLiveData<String>? = MutableLiveData()

    fun setUserProfile(userName : String, userKind : String){
        this.userName!!.value = userName
        this.userKind!!.value = userKind
    }
}