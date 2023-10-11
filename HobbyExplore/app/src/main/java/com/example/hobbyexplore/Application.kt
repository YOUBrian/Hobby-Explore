package com.example.hobbyexplore

import android.app.Application
import com.example.hobbyexplore.data.Repository
import com.example.hobbyexplore.util.ServiceLocator
import kotlin.properties.Delegates

class Application : Application() {

    // Depends on the flavor,
    val repository: Repository
        get() = ServiceLocator.provideTasksRepository(this)

    companion object {
        var instance: Application by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}