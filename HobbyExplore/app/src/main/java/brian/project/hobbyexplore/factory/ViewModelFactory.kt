package brian.project.hobbyexplore.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import brian.project.hobbyexplore.MainViewModel
import brian.project.hobbyexplore.calendar.CalendarViewModel
import brian.project.hobbyexplore.data.Repository
import brian.project.hobbyexplore.hobbyboards.HobbyBoardsViewModel
import brian.project.hobbyexplore.hobbycategory.HobbyCategoryViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val repository: Repository
) : ViewModelProvider.NewInstanceFactory() {

//    override fun <T : ViewModel> create(modelClass: Class<T>) =
//        with(modelClass) {
//            when {
//                isAssignableFrom(MainViewModel::class.java) ->
//                    MainViewModel(repository)
//
//                isAssignableFrom(CalendarViewModel::class.java) ->
//                    CalendarViewModel(repository)
//
//                isAssignableFrom(HobbyBoardsViewModel::class.java) ->
//                    HobbyBoardsViewModel(repository)
//
//                isAssignableFrom(HobbyCategoryViewModel::class.java) ->
//                    HobbyCategoryViewModel(repository)
//                else ->
//                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
//            }
//        } as T
}