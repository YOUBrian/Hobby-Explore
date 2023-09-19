package com.example.hobbyexplore.calendar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hobbyexplore.data.CalendarEvent
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CalendarViewModel : ViewModel() {
    val db = Firebase.firestore
    val _ratingDate = MutableLiveData<List<CalendarEvent>>()

    val ratingDate: LiveData<List<CalendarEvent>>
        get() = _ratingDate

    init {
        getCalendarData()
    }
    fun getCalendarData() {
        val docRef = db.collection("calendarData")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("READ_DATA", "Listen failed.", e)
                return@addSnapshotListener
            }
            try {
                val ratings = mutableListOf<CalendarEvent>()
                for (document in snapshot?.documents.orEmpty()) {
                    Log.i("GetRating", "$snapshot")
                    Log.i("GetRating", "$document")
                    val rating = document.toObject(CalendarEvent::class.java)
                    if (rating != null) {
                        ratings.add(rating)
                        Log.i("GetRating", "$rating")
                    }
                }


                _ratingDate.postValue(ratings)
                Log.i("GetRating", "$ratings")
            } catch (e: Exception) {
                println("error: ${e.message}")
            }
        }

    }
}