package brian.project.hobbyexplore

import android.app.Application
import brian.project.hobbyexplore.data.Repository
import brian.project.hobbyexplore.util.ServiceLocator
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