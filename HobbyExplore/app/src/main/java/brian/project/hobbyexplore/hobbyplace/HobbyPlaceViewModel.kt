package brian.project.hobbyexplore.hobbyplace

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import brian.project.hobbyexplore.data.Introduce
import brian.project.hobbyexplore.data.Place

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HobbyPlaceViewModel : ViewModel() {
    val db = Firebase.firestore

    private val _placeList = MutableLiveData<List<Place>>()

    val placeList: LiveData<List<Place>>
        get() = _placeList

    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    private val _navigateToMap = MutableLiveData<Place>()
    val navigateToMap: LiveData<Place>
        get() = _navigateToMap

    private val _placeLatitude = MutableLiveData<Double>()
    val placeLatitude: LiveData<Double>
        get() = _placeLatitude

    private val _placeLongitude = MutableLiveData<Double>()
    val placeLongitude: LiveData<Double>
        get() = _placeLongitude

    private val _placeTitle = MutableLiveData<String>()
    val placeTitle: LiveData<String>
        get() = _placeTitle

//    init {
//        getPlaceData("")
//    }

    fun getPlaceData(sportName: String) {
        val docRef = db.collection("sports")
            .document(sportName)
            .collection("Place")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("READ_DATA", "Listen failed.", e)
                return@addSnapshotListener
            } else if (snapshot != null && !snapshot.metadata.hasPendingWrites()) {
                try {
                    val placeData = mutableListOf<Place>()

                    for (document in snapshot) {
                        Log.i("getApplianceData", "document:${document.data}")
                        Log.i("getApplianceData", "snapshot:$snapshot")
                        val place = document.toObject(Place::class.java)
                        Log.i("getApplianceData", "introduce:$place")
                        if (place != null) {
                            placeData.add(place)
                        }
                    }

                    _placeList.postValue(placeData)
                    Log.i("getApplianceData", "introduceData:$placeData")
                } catch (e: Exception) {
                    println("error: ${e.message}")
                }
            }
            _refreshStatus.value = false
        }
    }

    fun navigateToMap(place: Place) {
        _placeLatitude.value = place.latitude
        _placeLongitude.value = place.longitude
        _placeTitle.value = place.title
        _navigateToMap.value = place
    }

    fun onMapNavigated() {
        _navigateToMap.value = null
    }
}