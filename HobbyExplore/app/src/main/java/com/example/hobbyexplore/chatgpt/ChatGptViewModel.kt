package com.example.hobbyexplore.chatgpt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hobbyexplore.data.Introduce
import com.example.hobbyexplore.data.gpt.ApiClient
import com.example.hobbyexplore.data.gpt.ChatGPTMessage
import com.example.hobbyexplore.data.gpt.CompletionRequest
import com.example.hobbyexplore.data.gpt.CompletionResponse
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatGptViewModel : ViewModel() {

    val db = Firebase.firestore

    private val _introduceList = MutableLiveData<List<Introduce>>()

    val introduceList: LiveData<List<Introduce>>
        get() = _introduceList

    private val _messageList = MutableLiveData<MutableList<ChatGPTMessage>>()
    val messageList : LiveData<MutableList<ChatGPTMessage>> get() = _messageList

    init {
        _messageList.value = mutableListOf()
    }

    fun addToChat(message : String , sentBy : String , timestamp : String){
        val currentList = _messageList.value ?: mutableListOf()
        currentList.add(ChatGPTMessage(message,sentBy,timestamp))
        _messageList.postValue(currentList)
    }


    private fun addResponse(response : String){
        _messageList.value?.removeAt(_messageList.value?.size?.minus(1) ?: 0)
        addToChat(response,ChatGPTMessage.SENT_BY_BOT,getCurrentTimestamp())
    }

    fun callApi(typeString : String){
        addToChat("Typing....",ChatGPTMessage.SENT_BY_BOT,getCurrentTimestamp())

        val completionRequest = CompletionRequest(
            model = "text-davinci-003",
            prompt = typeString,
            max_tokens = 100
        )

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getCompletions(completionRequest)
                handleApiResponse(response)
            }catch (e: SocketTimeoutException){
                addResponse("Timeout :  $e")
            }
        }
    }

//    private suspend fun handleApiResponse(response: Response<CompletionResponse>) {
//        withContext(Dispatchers.Main){
//            if (response.isSuccessful){
//                response.body()?.let { completionResponse ->
//                    val result = completionResponse.choices.firstOrNull()?.text
//                    if (result != null){
//                        addResponse(result.trim())
//                    }else{
//                        addResponse("No choices found")
//                    }
//                }
//            }else{
//                addResponse("Failed to get response ${response.errorBody()}")
//            }
//        }
//
//    }
    private suspend fun handleApiResponse(response: Response<CompletionResponse>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                val sportRecommendation = getRandomSport()
                addResponse(sportRecommendation)
                Log.i("sportResponse", "sportResponse:$sportRecommendation")
                Log.i("sportResponse", "addResponse:${addResponse(sportRecommendation)}")
            } else {
                val statusCode = response.code()
                val errorBodyStr = response.errorBody()?.string()
                Log.e("API_ERROR", "StatusCode: $statusCode, ErrorBody: $errorBodyStr")
                addResponse("Failed to get response ${response.errorBody()}")
            }
        }
    }

    fun getCurrentTimestamp(): String {
        return SimpleDateFormat("hh mm a", Locale.getDefault()).format(Date())
    }

    fun getRandomSport(): String {
        val sports = listOf("baseball", "basketball", "tennis", "badminton", "table_tennis", "volleyball")
        return sports.random()
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
//            _refreshStatus.value = false
        }
    }

}