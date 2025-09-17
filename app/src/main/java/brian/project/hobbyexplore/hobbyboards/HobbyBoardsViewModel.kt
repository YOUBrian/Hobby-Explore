package brian.project.hobbyexplore.hobbyboards

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import brian.project.hobbyexplore.data.Appliance
import brian.project.hobbyexplore.data.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
        docRef.orderBy("createdTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
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

//    fun postApplianceData() {
//        val articles = FirebaseFirestore.getInstance()
//            .collection("sports")
//            .document("tennis")
//            .collection("Place")
//        val document = articles.document()
//        val data = hashMapOf(
//            "title" to "中正河濱網球場",
//            "latitude" to 25.023411673361995,
//            "longitude" to 121.51419609998652,
//            "id" to document.id,
//            "image" to "https://pic.pimg.tw/gn0930150655/1618299056-1977311349-g.jpg",
//        )
//        Log.i("addData", "${data}")
//        document.set(data)
//    }

//    fun postMessageData() {
//        val articles = FirebaseFirestore.getInstance()
//            .collection("baseball_board")
//        val document = articles.document()
//        val data = hashMapOf(
//            "name" to "Brian",
//            "content" to "棒球讚",
//            "id" to document.id,
//            "image" to "https://decathlon.tw/media/catalog/product/0/7/07_D341164_p2486007.jpg?optimize=high&bg-color=255,255,255&fit=bounds&height=600&width=600&canvas=600:600",
//            "createdTime" to Calendar.getInstance()
//                .timeInMillis
//        )
//        Log.i("addData", "${data}")
//        document.set(data)
//    }

    fun refresh() {
        getMessageData()
    }
}