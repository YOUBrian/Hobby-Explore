package brian.project.hobbyexplore.calendar

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brian.project.hobbyexplore.data.CalendarEvent
import com.github.mikephil.charting.data.Entry
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CalendarViewModel : ViewModel() {

    // Variables and references
    private val db = Firebase.firestore
    private val storageReference = FirebaseStorage.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()

    // LiveData and MutableLiveData
    private val _ratingDate = MutableLiveData<List<CalendarEvent>>()
    val ratingDate: LiveData<List<CalendarEvent>> get() = _ratingDate

    val specificDateData: MutableLiveData<CalendarEvent?> = MutableLiveData()

    val uploadStatus: MutableLiveData<String> = MutableLiveData()

    private val _progress = MutableLiveData<Int>()
    val progress: MutableLiveData<Int>
        get() = _progress

    val dataList: MutableLiveData<List<Pair<String, Int>>> = MutableLiveData()

    private val _uploadPhoto = MutableLiveData<String>()
    val uploadPhoto: LiveData<String> get() = _uploadPhoto

    val eventSaveStatus: MutableLiveData<Status> = MutableLiveData()


    val selectedPhotoUri = MutableLiveData<Uri?>()

    val eventForDate = MutableLiveData<CalendarEvent?>()

    var stringDateSelected: String? = null

    var isNewPhotoSelected = false

    // Initialization
    init {
        getCalendarData(userIdFromPref = String())
        dataList.value = mutableListOf()
        progress.value = 50
    }

    fun fetchEventForDate(userId: String, date: String) {
        firestore.collection("calendarData")
            .whereEqualTo("eventUserId", userId)
            .whereEqualTo("eventDate", date)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val event = querySnapshot.documents.firstOrNull()
                    ?.toObject(CalendarEvent::class.java)
                eventForDate.value = event
            }
            .addOnFailureListener {

            }
    }

    fun handleSelectedPhoto(data: Intent?) {
        selectedPhotoUri.value = data?.data
        isNewPhotoSelected = true
    }

    fun getFormattedDataList(): List<Entry> {
        val entries = mutableListOf<Entry>()
        dataList.value?.let { list ->
            for ((index, pair) in list.withIndex()) {
                entries.add(Entry(index.toFloat() + 1, pair.second.toFloat()))
            }
        }
        return entries
    }

    fun handleDateChange(year: Int, month: Int, dayOfMonth: Int, userId: String) {
        val formattedMonth = String.format("%02d", month + 1)
        val formattedDay = String.format("%02d", dayOfMonth)
        stringDateSelected = "$year/$formattedMonth/$formattedDay"

        getDataForSpecificDate(stringDateSelected!!, userId)
    }

    fun handleRecordButtonPress(
        stringDateSelected: String,
        progress: Int,
        content: String,
        userId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val imageUrl = if (isNewPhotoSelected && selectedPhotoUri.value != null) {
                isNewPhotoSelected = false
                uploadImageToFirebase(selectedPhotoUri.value!!)
            } else {
                specificDateData.value?.eventImage ?: ""
            }

            val eventIdToUse = specificDateData.value?.eventId ?: UUID.randomUUID().toString()

            val event = CalendarEvent(
                eventId = eventIdToUse,
                eventDate = stringDateSelected,
                eventRating = progress,
                eventImage = imageUrl,
                eventContent = content,
                eventUserId = userId
            )

            saveEventToFirestore(event)
                .addOnSuccessListener {
                    specificDateData.postValue(event)
                    eventSaveStatus.postValue(Status.SUCCESS)
                }
                .addOnFailureListener {
                    eventSaveStatus.postValue(Status.FAILURE)
                }
        }
    }








    fun generateMockData(): List<Entry> {
        val yValues = listOf(
            40f, 45f, 60f, 54f, 66f, 70f, 77f, 83f, 88f, 65f,
            53f, 55f, 57f, 58f, 63f, 54f, 56f, 60f, 92f, 94f, 96f
        )

        return yValues.mapIndexed { index, yValue ->
            Entry(index.toFloat() + 1, yValue)
        }
    }

    fun getCalendarData(userIdFromPref: String) {
        val docRef = db.collection("calendarData")
            .whereEqualTo("eventUserId", userIdFromPref)
        docRef.orderBy("eventDate", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val ratings = mutableListOf<CalendarEvent>()
                val dataListValues = dataList.value?.toMutableList() ?: mutableListOf()

                for (document in querySnapshot) {
                    val rating = document.toObject(CalendarEvent::class.java)
                    val eventDate = document.getString("eventDate") ?: ""
                    val eventRating = document.getLong("eventRating")?.toInt() ?: 0

                    val existingPair = dataListValues.find { it.first == eventDate }
                    if (existingPair != null) {
                        dataListValues[dataListValues.indexOf(existingPair)] =
                            Pair(eventDate, eventRating)
                    } else {
                        dataListValues.add(Pair(eventDate, eventRating))
                    }

                    if (rating != null) {
                        ratings.add(rating)
                    }
                }
                _ratingDate.postValue(ratings)
                dataListValues.sortBy { it.first }
                dataList.postValue(dataListValues.toList())
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG", "Error reading data.", e)
            }
    }

    fun updateOrAddEventToFirestore(event: CalendarEvent) {
        val docRef = db.collection("calendarData")
            .document(event.eventId)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    docRef.set(event)
                } else {
                    db.collection("calendarData").document(event.eventId).set(event)
                }
            }
    }

    private suspend fun uploadImageToFirebase(selectedPhotoUri: Uri): String {
        val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")
        return try {
            imageRef.putFile(selectedPhotoUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("uploadImageToFirebase", "Upload failed", e)
            uploadStatus.postValue("Upload failed: ${e.message}")
            ""
        }
    }

    private fun saveEventToFirestore(event: CalendarEvent): Task<Void> {
        return firestore.collection("calendarData")
            .document(event.eventId)
            .set(event)
    }

    private fun getDataForSpecificDate(date: String, userIdFromPref: String) {
        if (userIdFromPref != null) {
            db.collection("calendarData")
                .whereEqualTo("eventDate", date)
                .whereEqualTo("eventUserId", userIdFromPref) // Add this line to compare userId
                .get()
                .addOnSuccessListener { documents ->
                    val event =
                        documents.documents.firstOrNull()?.toObject(CalendarEvent::class.java)
                    specificDateData.postValue(event)
                }
                .addOnFailureListener {
                    specificDateData.postValue(null)
                }
        } else {
            Log.e("ERROR", "UserId not found in SharedPreferences.")
            specificDateData.postValue(null)
        }
    }
}
enum class Status {
    SUCCESS, FAILURE
}