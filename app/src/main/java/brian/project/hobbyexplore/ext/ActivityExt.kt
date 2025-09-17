package brian.project.hobbyexplore.ext

import android.app.Activity
import brian.project.hobbyexplore.Application
import brian.project.hobbyexplore.factory.ViewModelFactory

fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as Application).repository
    return ViewModelFactory(repository)
}