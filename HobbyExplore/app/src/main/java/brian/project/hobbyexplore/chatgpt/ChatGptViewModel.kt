package brian.project.hobbyexplore.chatgpt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brian.project.hobbyexplore.data.Introduce
import brian.project.hobbyexplore.data.gpt.ApiClient
import brian.project.hobbyexplore.data.gpt.ChatGPTMessage
import brian.project.hobbyexplore.data.gpt.CompletionRequest
import brian.project.hobbyexplore.data.gpt.CompletionResponse
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatGptViewModel : ViewModel() {
    // All the sports
    private val allSports =
        listOf("baseball", "basketball", "tennis", "badminton", "table_tennis", "volleyball")

    // Copy of all sports to be modified during random selection
    private var availableSports = mutableListOf<String>().apply { addAll(allSports) }

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String>
        get() = _errorLiveData


    val db = Firebase.firestore

    private val _introduceList = MutableLiveData<Introduce?>()

    val introduceList: LiveData<Introduce?>
        get() = _introduceList

    private val _messageList = MutableLiveData<MutableList<ChatGPTMessage>>()
    val messageList: LiveData<MutableList<ChatGPTMessage>> get() = _messageList

    init {
        _messageList.value = mutableListOf()
    }

    fun addToChat(message: String, sentBy: String, timestamp: String) {
        val currentList = _messageList.value ?: mutableListOf()
        currentList.add(ChatGPTMessage(message, sentBy, timestamp))
        _messageList.postValue(currentList)
    }


    private fun addResponse(response: String) {
        _messageList.value?.removeAt(_messageList.value?.size?.minus(1) ?: 0)
        addToChat(response, ChatGPTMessage.SENT_BY_BOT, getCurrentTimestamp())
    }

    fun callApi(typeString: String) {
        addToChat("Typing....", ChatGPTMessage.SENT_BY_BOT, getCurrentTimestamp())
        val completionRequest = CompletionRequest(
            model = "text-davinci-003",
            prompt = typeString,
            max_tokens = 100
        )

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getCompletions(completionRequest)
                handleApiResponse(response)
            } catch (e: Exception) { // Catch all exceptions, including SocketTimeoutException, IOException, etc.
                Log.e("API_CALL", "Error: $e")

                // When there's an error, randomly select a sport and continue the process
                val randomSport = getRandomSportWithoutRepetition()
                addResponse("We encountered an issue. However, based on our analysis, we recommend trying out $randomSport.")
                getHobbyData(randomSport)
            }
        }
    }


    private suspend fun handleApiResponse(response: Response<CompletionResponse>) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                val sportRecommendation = getRandomSport()
                availableSports.remove(sportRecommendation)
                getHobbyData(sportRecommendation)
            } else {
                val statusCode = response.code()
                val errorBodyStr = response.errorBody()?.string()
                Log.e("API_ERROR", "StatusCode: $statusCode, ErrorBody: $errorBodyStr")

                // If the API call was not successful, get a random sport without repetition.
                val randomSport = getRandomSportWithoutRepetition()
                addResponse("Based on our analysis, we recommend trying out $randomSport.")
                getHobbyData(randomSport)
            }
        }
    }


    fun getCurrentTimestamp(): String {
        return SimpleDateFormat("hh mm a", Locale.getDefault()).format(Date())
    }

    fun getRandomSport(): String {
        val sports =
            listOf("baseball", "basketball", "tennis", "badminton", "table_tennis", "volleyball")
        return sports.random()
    }


    fun getHobbyData(sportName: String) {
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