package com.example.moconmcs.Menu.ProfileInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileChangeViewModel : ViewModel(){
    var userName : MutableLiveData<String> = MutableLiveData()
    var userKind : MutableLiveData<String> = MutableLiveData()
    var userEmail :MutableLiveData<String> = MutableLiveData()

}