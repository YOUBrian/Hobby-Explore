package com.example.hobbyexplore.hobbyappliance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hobbyexplore.data.Appliance
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HobbyApplianceViewModel : ViewModel() {
    val db = Firebase.firestore

    private val _applianceList = MutableLiveData<List<Appliance>>()

    val applianceList: LiveData<List<Appliance>>
        get() = _applianceList

    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    init {
//        getApplianceData("")
    }
    fun getApplianceData(sportName: String) {
        val docRef = db.collection("sports")
            .document(sportName)
            .collection("Appliance")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("READ_DATA", "Listen failed.", e)
                return@addSnapshotListener
            } else if (snapshot != null && !snapshot.metadata.hasPendingWrites()) {
//            Log.i("getdata", "document:${snapshot?.data}")
            try {
                val applianceData = mutableListOf<Appliance>()

                for (document in snapshot) {
                    Log.i("getApplianceData", "document:${document.data}")
                    Log.i("getApplianceData", "snapshot:$snapshot")
                    val appliance = document.toObject(Appliance::class.java)
                    Log.i("getApplianceData", "introduce:$appliance")
                    if (appliance != null) {
                        applianceData.add(appliance)
                    }
                }


                _applianceList.postValue(applianceData)
                Log.i("getApplianceData", "introduceData:$applianceData")
            } catch (e: Exception) {
                println("error: ${e.message}")
            }
            }
            _refreshStatus.value = false
        }
    }
}