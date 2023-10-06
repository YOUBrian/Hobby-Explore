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

    val dataList: MutableLiveData<List<Pair<String, Int>>> = MutableLiveData()



    private val _uploadPhoto = MutableLiveData<String>()
    val uploadPhoto: MutableLiveData<String>
        get() = _uploadPhoto


    init {
//        getCalendarData()
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
    fun getCalendarData(userIdFromPref: String) {
        val docRef = db.collection("calendarData")
            .whereEqualTo("eventUserId", userIdFromPref)
            .orderBy("eventDate", Query.Direction.ASCENDING)

        docRef.get()
            .addOnSuccessListener { querySnapshot ->
                val ratings = mutableListOf<CalendarEvent>()
                val dataListValues = dataList.value?.toMutableList() ?: mutableListOf()
                for (document in querySnapshot) {
                    val rating = document.toObject(CalendarEvent::class.java)
                    val eventDate = document.getString("eventDate") ?: ""
                    val eventRating = document.getLong("eventRating")?.toInt() ?: 0
                    Log.i("FirebaseData", "Event Date: $eventDate, Event Rating: $eventRating")
                    if (rating != null) {
                        val pair = Pair(eventDate, eventRating)
                        if (!dataListValues.contains(pair)) {
                            ratings.add(rating)
                            Log.i("getCalendarData", "rating: $rating")
                            dataListValues.add(pair)
                        }
                    }
                }
                _ratingDate.postValue(ratings)
                dataListValues.sortBy { it.first }
                dataList.postValue(dataListValues.toList())
                Log.i("dataListttttt","dataList:$dataListValues")
            }
            .addOnFailureListener { e ->
                Log.w("READ_DATA", "Error reading data.", e)
            }
    }

    fun getDataForSpecificDate(date: String, userIdFromPref: String): LiveData<CalendarEvent?> {
        val eventLiveData = MutableLiveData<CalendarEvent?>()
        if (userIdFromPref != null) {
            db.collection("calendarData")
                .whereEqualTo("eventDate", date)
                .whereEqualTo("eventUserId", userIdFromPref) // Add this line to compare userId
                .get()
                .addOnSuccessListener { documents ->
                    val event = documents.documents.firstOrNull()?.toObject(CalendarEvent::class.java)
                    eventLiveData.postValue(event)
                }
                .addOnFailureListener {
                    eventLiveData.postValue(null)
                }
        } else {
            Log.e("ERROR", "UserId not found in SharedPreferences.")
            eventLiveData.postValue(null) // Optionally, you can post a null value here to indicate an error.
        }

        return eventLiveData
    }




}