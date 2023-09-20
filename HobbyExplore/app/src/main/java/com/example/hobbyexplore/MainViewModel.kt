package com.example.hobbyexplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hobbyexplore.data.Repository
import com.example.hobbyexplore.data.User
import com.example.hobbyexplore.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MainViewModel : ViewModel() {

    // user: MainViewModel has User info to provide Drawer UI
    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user


    // Handle navigation to profile by bottom nav directly which includes icon change
    private val _navigateToProfileByBottomNav = MutableLiveData<User>()

    val navigateToProfileByBottomNav: LiveData<User>
        get() = _navigateToProfileByBottomNav

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun navigateToProfileByBottomNav(user: User) {
        _navigateToProfileByBottomNav.value = user
    }

    fun onProfileNavigated() {
        _navigateToProfileByBottomNav.value = null
    }

}
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.ai.generativelanguage.v1beta2.DiscussServiceClient
//import com.google.ai.generativelanguage.v1beta2.DiscussServiceSettings
//import com.google.ai.generativelanguage.v1beta2.Example
//import com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest
//import com.google.ai.generativelanguage.v1beta2.Message
//import com.google.ai.generativelanguage.v1beta2.MessagePrompt
//import com.google.api.gax.core.FixedCredentialsProvider
//import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider
//import com.google.api.gax.rpc.FixedHeaderProvider
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//
//class MainViewModel : ViewModel() {
//    private val _messages = MutableStateFlow<List<Message>>(value = listOf())
//    val messages: StateFlow<List<Message>>
//        get() = _messages
//
//    private var client: DiscussServiceClient
//
//    init {
//        // Initialize the Discuss Service Client
//        client = initializeDiscussServiceClient(
//            apiKey = "AIzaSyCWpDtkiG3ssl4-ISuMbGYCc1UqCqPe620"
//        )
//
//        // Create the message prompt
//        val prompt = createPrompt("How tall is the Eiffel Tower?")
//
//        // Send the first request to kickstart the conversation
//        val request = createMessageRequest(prompt)
//        generateMessage(request)
//    }
//
//    fun sendMessage(userInput: String) {
//        val prompt = createPrompt(userInput)
//
//        val request = createMessageRequest(prompt)
//        generateMessage(request)
//    }
//
//    private fun initializeDiscussServiceClient(
//        apiKey: String
//    ): DiscussServiceClient {
//        // (This is a workaround because GAPIC java libraries don't yet support API key auth)
//        val transportChannelProvider = InstantiatingGrpcChannelProvider.newBuilder()
//            .setHeaderProvider(FixedHeaderProvider.create(hashMapOf("x-goog-api-key" to apiKey)))
//            .build()
//
//        // Create DiscussServiceSettings
//        val settings = DiscussServiceSettings.newBuilder()
//            .setTransportChannelProvider(transportChannelProvider)
//            .setCredentialsProvider(FixedCredentialsProvider.create(null))
//            .build()
//
//        // Initialize a DiscussServiceClient
//        val discussServiceClient = DiscussServiceClient.create(settings)
//
//        return discussServiceClient
//    }
//
//    private fun createCaliforniaExample(): Example {
//        val input = Message.newBuilder()
//            .setContent("What is the capital of California?")
//            .build()
//
//        val response = Message.newBuilder()
//            .setContent("If the capital of California is what you seek, Sacramento is where you ought to peek.")
//            .build()
//
//        val example = Example.newBuilder()
//            .setInput(input)
//            .setOutput(response)
//            .build()
//
//        return example
//    }
//
//    private fun createPrompt(
//        messageContent: String
//    ): MessagePrompt {
//        val palmMessage = Message.newBuilder()
//            .setAuthor("0")
//            .setContent(messageContent)
//            .build()
//
//        // Add the new Message to the UI
//        _messages.update {
//            it.toMutableList().apply {
//                add(palmMessage)
//            }
//        }
//
//        val messagePrompt = MessagePrompt.newBuilder()
//            .addMessages(palmMessage) // required
//            .setContext("Respond to all questions with a rhyming poem.") // optional
//            .addExamples(createCaliforniaExample()) // use addAllExamples() to add a list of examples
//            .build()
//
//        return messagePrompt
//    }
//
//    private fun createMessageRequest(prompt: MessagePrompt): GenerateMessageRequest {
//        return GenerateMessageRequest.newBuilder()
//            .setModel("models/chat-bison-001") // Required, which model to use to generate the result
//            .setPrompt(prompt) // Required
//            .setTemperature(0.5f) // Optional, controls the randomness of the output
//            .setCandidateCount(1) // Optional, the number of generated messages to return
//            .build()
//    }
//
//    private fun generateMessage(
//        request: GenerateMessageRequest
//    ) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val response = client.generateMessage(request)
//
//                val returnedMessage = response.candidatesList.last()
//                // display the returned message in the UI
//                _messages.update {
//                    // Add the response to the list
//                    it.toMutableList().apply {
//                        add(returnedMessage)
//                    }
//                }
//            } catch (e: Exception) {
//                // There was an error, let's add a new message with the details
//                _messages.update { messages ->
//                    val mutableList = messages.toMutableList()
//                    mutableList.apply {
//                        add(
//                            Message.newBuilder()
//                                .setAuthor("API Error")
//                                .setContent(e.message)
//                                .build()
//                        )
//                    }
//                }
//            }
//        }
//    }
//}