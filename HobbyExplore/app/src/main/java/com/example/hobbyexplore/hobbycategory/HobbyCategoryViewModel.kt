package com.example.hobbyexplore.hobbycategory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hobbyexplore.data.Introduce
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HobbyCategoryViewModel() : ViewModel() {
    val db = Firebase.firestore

    private val _introduceList = MutableLiveData<List<Introduce>>()

    val introduceList: LiveData<List<Introduce>>
        get() = _introduceList

    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    // Handle navigation to detail
    private val _navigateToDetail = MutableLiveData<Introduce>()
    val navigateToDetail: LiveData<Introduce>
        get() = _navigateToDetail

    init {
        getCategoryData("")
    }

    private fun getCategoryData(sportName:String) {
        val docRef = db.collection("sports")//.document(sport)
//            .document("baseball")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("READ_DATA", "Listen failed.", e)
                return@addSnapshotListener
            } else if (snapshot != null && !snapshot.metadata.hasPendingWrites()) {
//            Log.i("getdata", "document:${snapshot}")
            try {
                val introduceData = mutableListOf<Introduce>()

                for (document in snapshot) {
                    Log.i("getdata", "document:${document.data}")
                    Log.i("getdata", "snapshot:$snapshot")
                    val introduce = document.toObject(Introduce::class.java)
                    Log.i("getdata", "introduce:$introduce")
                    if (introduce != null) {
                        introduceData.add(introduce)
                    }
                }


                _introduceList.postValue(introduceData)
                Log.i("getdata", "introduceData:$introduceData")
            } catch (e: Exception) {
                println("error: ${e.message}")
            }
            }
            _refreshStatus.value = false
        }
    }
//fun getData() {
//    val docRef = db.collection("sports")
//    docRef.addSnapshotListener { querySnapshot, e ->
//        if (e != null) {
//            Log.w("READ_DATA", "Listen failed.", e)
//            return@addSnapshotListener
//        }
//
//        val sports = mutableListOf<Introduce>()
//        for (document in querySnapshot?.documents.orEmpty()) {
//            if (document.exists()) {
//                Log.i("get_hobby", "document:$document")
//                val introduceData = document.toObject(Introduce::class.java)
//                if (introduceData != null) {
//                    sports.add(introduceData)
//                    Log.i("get_hobby", "introduceData:$introduceData")
//                }
//            } else {
//                Log.i("get_hobby", "Document does not exist")
//            }
//        }
//        _introduceList.postValue(sports)
//    }
//    _refreshStatus.value = false
//}

    fun refresh() {
        getCategoryData("")
    }

    fun navigateToDetail(introduce: Introduce) {
        _navigateToDetail.value = introduce
    }
    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }
}