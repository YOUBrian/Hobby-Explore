package brian.project.hobbyexplore.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _isLoggedIn = MutableLiveData<Boolean>(false)
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    private val _defaultName = MutableLiveData<String>("Default Name")
    val defaultName: LiveData<String> get() = _defaultName

    fun setLoggedIn(value: Boolean) {
        _isLoggedIn.value = value
    }

    fun resetValues() {
        _userPhotoUrl.value = "https://firebasestorage.googleapis.com/v0/b/hobby-explore.appspot.com/o/images%2Fuser%20(2).png?alt=media&token=d7986b56-0d57-44cb-bd8e-1dfce4e45d19&_gl=1*10s2fv6*_ga*MjA2MTUwOTE5LjE2OTI1OTUxNzY.*_ga_CW55HF8NVT*MTY5NzUwNzkzMy4xNDguMS4xNjk3NTA9MDM8LjUyLjAuMA.."
        _defaultName.value = "Default Name"
    }


    private val _userPhotoUrl = MutableLiveData<String>()
    val userPhotoUrl: LiveData<String>
        get() = _userPhotoUrl


    fun getUserPhoto(photo :String){
        _userPhotoUrl.value = photo
    }
}