package com.example.hobbyexplore.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.example.hobbyexplore.data.DataSource
import com.example.hobbyexplore.data.DefaultRepository
import com.example.hobbyexplore.data.Repository
import com.example.hobbyexplore.data.local.Database
import com.example.hobbyexplore.data.local.LocalDataSource
import com.example.hobbyexplore.data.remote.RemoteDataSource

object ServiceLocator {

    @Volatile
    var repository: Repository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): Repository {
        synchronized(this) {
            return repository
                ?: repository
                ?: createRepository(context)
        }
    }

    private fun createRepository(context: Context): Repository {
        return DefaultRepository(
            RemoteDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): DataSource {
        return LocalDataSource(Database.getInstance(context).databaseDao)
    }
}