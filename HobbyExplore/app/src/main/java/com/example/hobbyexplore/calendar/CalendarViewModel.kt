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
    val specificDateData: MutableLiveData<CalendarEvent?> = MutableLiveData()

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
        getCalendarData(userIdFromPref = String())
        dataList.value = mutableListOf()
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
        docRef.orderBy("eventDate", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val ratings = mutableListOf<CalendarEvent>()
                val dataListValues = dataList.value?.toMutableList() ?: mutableListOf()

                for (document in querySnapshot) {
                    val rating = document.toObject(CalendarEvent::class.java)
                    val eventDate = document.getString("eventDate") ?: ""
                    val eventRating = document.getLong("eventRating")?.toInt() ?: 0

                    val existingPair = dataListValues.find { it.first == eventDate }
                    if (existingPair != null) {
                        dataListValues[dataListValues.indexOf(existingPair)] = Pair(eventDate, eventRating)
                    } else {
                        dataListValues.add(Pair(eventDate, eventRating))
                    }

                    if (rating != null) {
                        ratings.add(rating)
                    }
                }
                _ratingDate.postValue(ratings)
                dataListValues.sortBy { it.first }
                dataList.postValue(dataListValues.toList())
            }
            .addOnFailureListener { e ->
                Log.e("DEBUGGGGG", "Error reading data.", e)
            }
    }


    fun getDataForSpecificDate(date: String, userIdFromPref: String) {
        if (userIdFromPref != null) {
            db.collection("calendarData")
                .whereEqualTo("eventDate", date)
                .whereEqualTo("eventUserId", userIdFromPref) // Add this line to compare userId
                .get()
                .addOnSuccessListener { documents ->
                    val event = documents.documents.firstOrNull()?.toObject(CalendarEvent::class.java)
                    specificDateData.postValue(event)
                }
                .addOnFailureListener {
                    specificDateData.postValue(null)
                }
        } else {
            Log.e("ERROR", "UserId not found in SharedPreferences.")
            specificDateData.postValue(null)
        }
    }


    fun updateOrAddEventToFirestore(event: CalendarEvent) {
        val docRef = db.collection("calendarData")
            .document(event.eventId)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // 如果文檔存在，我們就更新它
                    docRef.set(event)
                } else {
                    // 如果文檔不存在，我們就新增它
                    db.collection("calendarData").document(event.eventId).set(event)
                }
            }
    }




}