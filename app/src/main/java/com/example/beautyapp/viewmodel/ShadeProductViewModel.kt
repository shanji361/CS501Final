package com.example.beautyapp.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beautyapp.data.database.MakeupDatabase
import com.example.beautyapp.data.entities.MakeupProduct
import com.example.beautyapp.data.entities.Shade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// this class manages both shades and their respective products
class ShadeProductViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MakeupDatabase.getDatabase(application)
    private val shadeDao = db.shadeDao()
    private val productDao = db.productDao()

    // live data for all shades
    private val _shades = MutableLiveData<List<Shade>>()
    val shades: LiveData<List<Shade>> = _shades

    // live data for the currently selected shade
    private val _selectedShade = MutableLiveData<Shade?>()
    val selectedShade: LiveData<Shade?> = _selectedShade

    private val _products = MutableLiveData<List<MakeupProduct>>()
    val products: LiveData<List<MakeupProduct>> = _products

    init {
        loadShades()
    }

    // load all 8 shades from the database.
    private fun loadShades() {
        viewModelScope.launch(Dispatchers.IO) {
            val shadeList = shadeDao.getAllShades()
            _shades.postValue(shadeList)
        }
    }

    // when a user taps a shade, update the selected shade and load its products.
    fun onShadeSelected(shade: Shade) {
        _selectedShade.value = shade
        //coroutine, thread runs in the background
        viewModelScope.launch(Dispatchers.IO) {
            val result = productDao.getProductsForShade(shade.shadeId)
            _products.postValue(result)
        }
    }
}
