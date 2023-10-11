package com.example.hobbyexplore.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hobbyexplore.MainViewModel
import com.example.hobbyexplore.calendar.CalendarViewModel
import com.example.hobbyexplore.data.Repository
import com.example.hobbyexplore.hobbyboards.HobbyBoardsViewModel
import com.example.hobbyexplore.hobbycategory.HobbyCategoryViewModel

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