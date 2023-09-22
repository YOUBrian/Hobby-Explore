package com.example.hobbyexplore.hobbyboards

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hobbyexplore.data.Appliance
import com.example.hobbyexplore.data.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar


class HobbyBoardsViewModel : ViewModel() {
    val db = Firebase.firestore

    private val _messageList = MutableLiveData<List<Message>>()

    val messageList: LiveData<List<Message>>
        get() = _messageList

    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    init {
        getMessageData()
    }

    private fun getMessageData() {
        val docRef = db.collection("baseball_board")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("READ_DATA", "Listen failed.", e)
                return@addSnapshotListener
            }
            try {
                val messages = mutableListOf<Message>()
                for (document in snapshot?.documents.orEmpty()) {
                    val message = document.toObject(Message::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }


                _messageList.postValue(messages)
            } catch (e: Exception) {
                println("error: ${e.message}")
            }
        }
        _refreshStatus.value = false
    }

    fun postApplianceData(){
        val articles = FirebaseFirestore.getInstance()
            .collection("sports")
            .document("baseball")
            .collection("Place")
        val document = articles.document()
        val data = hashMapOf(
            "title" to "浮洲橋棒球場",
            "content" to "河濱簡易型棒球場",
            "id" to document.id,
            "image" to "https://www.hrcm.ntpc.gov.tw/Uploads/%E6%96%BD%E6%94%BF%E5%9C%B0%E5%9C%96/%E8%A8%AD%E6%96%BD%E4%BB%8B%E7%B4%B9/%E7%90%83%E5%A0%B4/%E6%A3%92%E5%A3%98%E7%90%83%E5%A0%B4/%E6%B5%AE%E6%B4%B2%E6%A3%92%E7%90%83%E5%A0%B41.JPG",
            "price" to "免費對外場地租借"
        )
        Log.i("addData", "${data}")
        document.set(data)
    }

    fun postMessageData(){
        val articles = FirebaseFirestore.getInstance()
            .collection("baseball_board")
        val document = articles.document()
        val data = hashMapOf(
            "name" to "Brian",
            "content" to "棒球讚",
            "id" to document.id,
            "image" to "https://decathlon.tw/media/catalog/product/0/7/07_D341164_p2486007.jpg?optimize=high&bg-color=255,255,255&fit=bounds&height=600&width=600&canvas=600:600",
            "createdTime" to Calendar.getInstance()
                .timeInMillis
        )
        Log.i("addData", "${data}")
        document.set(data)
    }

    fun refresh() {
        getMessageData()
    }
}