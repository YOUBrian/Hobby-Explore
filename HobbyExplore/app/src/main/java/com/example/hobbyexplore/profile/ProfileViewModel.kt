package com.example.hobbyexplore.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    private val _userPhotoUrl = MutableLiveData<String>()
    val userPhotoUrl: LiveData<String>
        get() = _userPhotoUrl


    fun getUserPhoto(photo :String){
        _userPhotoUrl.value = photo
    }
}