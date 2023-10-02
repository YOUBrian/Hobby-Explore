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
    // All the sports
    private val allSports = listOf("baseball", "basketball", "tennis", "badminton", "table_tennis", "volleyball")
    // Copy of all sports to be modified during random selection
    private var availableSports = mutableListOf<String>().apply { addAll(allSports) }


    val db = Firebase.firestore

    private val _introduceList = MutableLiveData<Introduce?>()

    val introduceList: LiveData<Introduce?>
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

    fun callApi(typeString: String) {
        addToChat("Typing....", ChatGPTMessage.SENT_BY_BOT, getCurrentTimestamp())
        val completionRequest = CompletionRequest(
            model = "text-davinci-003",
            prompt = typeString,
            max_tokens = 100
        )

        val sportRecommendation = getRandomSport() // Get the sport recommendation here.

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getCompletions(completionRequest)
                handleApiResponse(response, sportRecommendation)
            } catch (e: SocketTimeoutException) {
                addResponse("Timeout :  $e")
            }
        }
    }



    //
//    private suspend fun handleApiResponse(response: Response<CompletionResponse>, sportRecommendation: String) {
//        withContext(Dispatchers.Main) {
//            if (response.isSuccessful) {
//                addResponse(sportRecommendation)
//                // 現在，我們用sportRecommendation調用getHobbyData
//                getHobbyData(sportRecommendation)
//                Log.i("sportResponse", "sportResponse:$sportRecommendation")
//                Log.i("sportResponse", "addResponse:${addResponse(sportRecommendation)}")
//            } else {
//                val statusCode = response.code()
//                val errorBodyStr = response.errorBody()?.string()
//                Log.e("API_ERROR", "StatusCode: $statusCode, ErrorBody: $errorBodyStr")
//                addResponse("Failed to get response ${response.errorBody()}")
//            }
//        }
//    }

    private suspend fun handleApiResponse(response: Response<CompletionResponse>, sportRecommendation: String) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                getHobbyData(sportRecommendation)
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


    fun getHobbyData(sportName:String) {
        val docRef = db.collection("sports").document(sportName) // 使用sportName而不是硬編碼的"baseball"
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("READ_DATA", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists() && !snapshot.metadata.hasPendingWrites()) {
                try {
                    val introduce = snapshot.toObject(Introduce::class.java)
                    if (introduce != null) {
                        _introduceList.postValue(introduce)
                        Log.i("gethobbydataaaaaa", "$introduce")
                    } else {
                        Log.e("getdataaaaaa", "Failed to convert snapshot to Introduce")
                    }
                } catch (e: Exception) {
                    println("error: ${e.message}")
                }
            }

        }
    }

    fun getRandomSportWithoutRepetition(): String {
        // If all sports have been chosen, refill the list
        if (availableSports.isEmpty()) {
            availableSports.addAll(allSports)
        }
        // Randomly select and remove a sport from the list
        val sport = availableSports.random()
        availableSports.remove(sport)
        return sport
    }


}