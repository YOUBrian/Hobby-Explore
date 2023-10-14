package com.example.hobbyexplore.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _isLoggedIn = MutableLiveData<Boolean>(false)
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    private val _defaultName = MutableLiveData<String>("Default Name")
    val defaultName: LiveData<String> get() = _defaultName

    fun setLoggedIn(value: Boolean) {
        _isLoggedIn.value = value
    }

    fun resetValues() {
        _userPhotoUrl.value = ""
        _defaultName.value = "Default Name"
    }

    private val _userPhotoUrl = MutableLiveData<String>()
    val userPhotoUrl: LiveData<String>
        get() = _userPhotoUrl


    fun getUserPhoto(photo :String){
        _userPhotoUrl.value = photo
    }
}