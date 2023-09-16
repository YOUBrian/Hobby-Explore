package com.example.hobbyexplore.hobbycategory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hobbyexplore.data.Hobby
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HobbyCategoryViewModel : ViewModel() {
    val db = Firebase.firestore

    private val _articleList = MutableLiveData<List<Hobby>>()

    val articleList: LiveData<List<Hobby>>
        get() = _articleList

    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    init {
        getData()
    }

    fun getData() {
        val docRef = db.collection("sports")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("READ_DATA", "Listen failed.", e)
                return@addSnapshotListener
            }
            try {
                val articles = mutableListOf<Hobby>()
                for (document in snapshot?.documents.orEmpty()) {
                    Log.i("get_hobby", "SportData:$document")
                    val hobby = document.toObject(Hobby::class.java)
                    if (hobby != null) {
                        articles.add(hobby)
                        Log.i("get_hobby", "SportData:$hobby")

                    }
                }


                _articleList.postValue(articles)
            } catch (e: Exception) {
                println("error: ${e.message}")
            }
        }
        _refreshStatus.value = false
    }

    fun refresh() {
        getData()
    }
}