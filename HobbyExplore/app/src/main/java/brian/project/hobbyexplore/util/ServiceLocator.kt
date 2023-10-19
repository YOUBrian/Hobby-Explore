package brian.project.hobbyexplore.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import brian.project.hobbyexplore.data.DataSource
import brian.project.hobbyexplore.data.DefaultRepository
import brian.project.hobbyexplore.data.Repository
import brian.project.hobbyexplore.data.local.HobbyDatabase
import brian.project.hobbyexplore.data.local.LocalDataSource
import brian.project.hobbyexplore.data.remote.RemoteDataSource

object ServiceLocator {

    @Volatile
    var repository: Repository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): Repository {
        synchronized(this) {
            return repository
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
        return LocalDataSource(HobbyDatabase.getInstance(context).hobbyDatabaseDao)
    }
}