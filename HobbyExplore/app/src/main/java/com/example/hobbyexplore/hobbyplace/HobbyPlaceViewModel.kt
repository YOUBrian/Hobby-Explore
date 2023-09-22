package com.example.hobbyexplore.hobbyplace

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hobbyexplore.data.Introduce
import com.example.hobbyexplore.data.Place

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HobbyPlaceViewModel : ViewModel() {
    val db = Firebase.firestore

    private val _placeList = MutableLiveData<List<Place>>()

    val placeList: LiveData<List<Place>>
        get() = _placeList

    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    private val _navigateToMap = MutableLiveData<Place>()
    val navigateToMap: LiveData<Place>
        get() = _navigateToMap
    init {
        getPlaceData()
    }
    private fun getPlaceData() {
        val docRef = db.collection("sports")
            .document("baseball")
            .collection("Place")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("READ_DATA", "Listen failed.", e)
                return@addSnapshotListener
            } else if (snapshot != null && !snapshot.metadata.hasPendingWrites()) {
//            Log.i("getdata", "document:${snapshot?.data}")
                try {
                    val placeData = mutableListOf<Place>()

                    for (document in snapshot) {
                        Log.i("getApplianceData", "document:${document.data}")
                        Log.i("getApplianceData", "snapshot:$snapshot")
                        val place = document.toObject(Place::class.java)
                        Log.i("getApplianceData", "introduce:$place")
                        if (place != null) {
                            placeData.add(place)
                        }
                    }


                    _placeList.postValue(placeData)
                    Log.i("getApplianceData", "introduceData:$placeData")
                } catch (e: Exception) {
                    println("error: ${e.message}")
                }
            }
            _refreshStatus.value = false
        }
    }

    fun navigateToMap(place: Place) {
        _navigateToMap.value = place
    }
    fun onMapNavigated() {
        _navigateToMap.value = null
    }
}