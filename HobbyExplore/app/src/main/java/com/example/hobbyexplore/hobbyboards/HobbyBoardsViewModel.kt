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

    fun postMessageData(){
        val articles = FirebaseFirestore.getInstance()
            .collection("sports")
            .document("baseball")
            .collection("Appliance")
        val document = articles.document()
        val data = hashMapOf(
            "title" to "KIPSTA 棒球 BA180 (2 入)",
            "content" to "此為真皮棒球。適合練習與比賽用這款帶有浮凸縫線的硬式皮革棒球，讓你的練習和比賽表現更上一層樓。球觸佳又耐用。",
            "id" to document.id,
            "image" to "https://decathlon.tw/media/catalog/product/0/7/07_D341164_p2486007.jpg?optimize=high&bg-color=255,255,255&fit=bounds&height=600&width=600&canvas=600:600",
            "price" to "NT$299"
        )
        Log.i("addData", "${data}")
        document.set(data)
    }

    fun refresh() {
        getMessageData()
    }
}