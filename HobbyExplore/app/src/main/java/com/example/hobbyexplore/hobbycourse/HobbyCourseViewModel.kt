package com.example.hobbyexplore.hobbycourse

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hobbyexplore.data.Course
import com.example.hobbyexplore.data.Place
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HobbyCourseViewModel : ViewModel() {
    val db = Firebase.firestore

    private val _courseList = MutableLiveData<List<Course>>()

    val courseList: LiveData<List<Course>>
        get() = _courseList

    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    private val _navigateToYoutube = MutableLiveData<Course>()
    val navigateToYoutube: LiveData<Course>
        get() = _navigateToYoutube
    init {
        getCourseData()
    }
    private fun getCourseData() {
        val docRef = db.collection("sports")
            .document("baseball")
            .collection("Course")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("READ_DATA", "Listen failed.", e)
                return@addSnapshotListener
            } else if (snapshot != null && !snapshot.metadata.hasPendingWrites()) {
//            Log.i("getdata", "document:${snapshot?.data}")
                try {
                    val courseData = mutableListOf<Course>()

                    for (document in snapshot) {
                        Log.i("getApplianceData", "document:${document.data}")
                        Log.i("getApplianceData", "snapshot:$snapshot")
                        val course = document.toObject(Course::class.java)
                        Log.i("getApplianceData", "introduce:$course")
                        if (course != null) {
                            courseData.add(course)
                        }
                    }


                    _courseList.postValue(courseData)
                    Log.i("getApplianceData", "introduceData:$courseData")
                } catch (e: Exception) {
                    println("error: ${e.message}")
                }
            }
            _refreshStatus.value = false
        }
    }

    fun navigateToYoutube(course: Course) {
        _navigateToYoutube.value = course
    }
    fun onYoutubeNavigated() {
        _navigateToYoutube.value = null
    }
}