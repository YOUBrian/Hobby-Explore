package com.example.hobbyexplore.hobbyboards

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class PostViewModel : ViewModel() {
    val userContent: MutableLiveData<String> = MutableLiveData()
    val userRating: MutableLiveData<Float> = MutableLiveData()
    private val _uploadPhoto = MutableLiveData<String>()
    val uploadPhoto: MutableLiveData<String>
        get() = _uploadPhoto


    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName



    fun updateUserName(name: String) {
        _userName.value = name
    }
    fun postMessageData(content: String, rating: Float, imageUrl: String){
        val articles = FirebaseFirestore.getInstance()
            .collection("baseball_board")
        val document = articles.document()
        val data = hashMapOf(
            "name" to "Brian",
            "content" to content,
            "id" to document.id,
            "image" to imageUrl,
            "createdTime" to Calendar.getInstance()
                .timeInMillis,
            "rating" to rating
        )
        Log.i("addData", "${data}")
        document.set(data)
    }
}