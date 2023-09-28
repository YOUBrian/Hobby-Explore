package com.example.hobbyexplore.calendar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hobbyexplore.data.CalendarEvent
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CalendarViewModel : ViewModel() {
    val db = Firebase.firestore
    val _ratingDate = MutableLiveData<List<CalendarEvent>>()

    val ratingDate: LiveData<List<CalendarEvent>>
        get() = _ratingDate

    private val _progress = MutableLiveData<Int>()
    val progress: MutableLiveData<Int>
    get() = _progress

    val dataList = mutableListOf<Pair<String, Int>>()



    init {
        getCalendarData()
//        getDateData()
        progress.value = 50
    }

//    fun setProgress(progressValue: Int) {
//        _progress.value = progressValue
//        Log.i("ratingValue", "progress.value:${_progress.value}")
//    }

    //snapshot
    fun getDateData() {
        val docRef = db.collection("calendarData")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("READ_DATA", "Listen failed.", e)
                return@addSnapshotListener
            }
            try {
                val ratings = mutableListOf<CalendarEvent>()
                for (document in snapshot?.documents.orEmpty()) {
                    Log.i("getDateData", "$snapshot")
                    Log.i("getDateData", "$document")
                    val rating = document.toObject(CalendarEvent::class.java)
                    if (rating != null) {
                        ratings.add(rating)
                        Log.i("getDateData", "$rating")
                    }
                }

                _ratingDate.postValue(ratings)
                Log.i("GetRating", "$ratings")
            } catch (e: Exception) {
                println("error: ${e.message}")
            }
        }

    }


    //get
    fun getCalendarData() {
        val docRef = db.collection("calendarData")
        docRef.orderBy("eventDate", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val ratings = mutableListOf<CalendarEvent>()
                for (document in querySnapshot) {
                    val rating = document.toObject(CalendarEvent::class.java)
                    val eventDate = document.getString("eventDate") ?: ""
                    val eventRating = document.getLong("eventRating")?.toInt() ?: 0
                    if (rating != null) {
                        // 在添加数据之前检查是否已存在相同的数据
                        val pair = Pair(eventDate, eventRating)
                        if (!dataList.contains(pair)) {
                            ratings.add(rating)
                            Log.i("getCalendarData", "rating: $rating")
                            dataList.add(pair)
                        }
                    }
                }
                _ratingDate.postValue(ratings)
                dataList.sortBy { it.first }
                Log.i("dataListttttt","dataList:$dataList")
            }
            .addOnFailureListener { e ->
                Log.w("READ_DATA", "Error reading data.", e)
            }
    }

}