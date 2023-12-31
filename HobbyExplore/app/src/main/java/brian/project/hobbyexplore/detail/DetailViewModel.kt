package brian.project.hobbyexplore.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import brian.project.hobbyexplore.data.Introduce

class DetailViewModel(introduce: Introduce) : ViewModel() {

    private val _selectProduct = MutableLiveData<Introduce>()
    val selectProduct: LiveData<Introduce>
        get() = _selectProduct

    init {
        _selectProduct.value = introduce
    }
}