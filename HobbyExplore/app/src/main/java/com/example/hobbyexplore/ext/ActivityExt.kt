package com.example.hobbyexplore.ext

import android.app.Activity
import com.example.hobbyexplore.Application
import com.example.hobbyexplore.factory.ViewModelFactory


fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as Application).repository
    return ViewModelFactory(repository)
}