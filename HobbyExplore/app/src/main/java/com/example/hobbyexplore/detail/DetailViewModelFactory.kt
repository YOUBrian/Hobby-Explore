package com.example.hobbyexplore.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hobbyexplore.data.Introduce

class DetailViewModelFactory(
    private val Introduce: Introduce
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(Introduce) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}