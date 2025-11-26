package com.example.beautyapp.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beautyapp.data.database.MakeupDatabase
import com.example.beautyapp.data.entities.MakeupProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val productDao = MakeupDatabase.getDatabase(application).productDao()

    //use 'MakeupProduct' type for the live data.
    private val _products = MutableLiveData<List<MakeupProduct>>()
    val products: LiveData<List<MakeupProduct>> = _products

    /**
     * viewModelScope to launch a coroutine on a background thread (Dispatchers.IO).
     * this prevents the app from crashing by running the database query off the main UI thread.
     */
    fun loadProductsForShade(shadeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // uses postValue to safely update LiveData from a background thread.
            val productList = productDao.getProductsForShade(shadeId)
            _products.postValue(productList)
        }
    }

    /**
     *this function is synchronous and will block the thread it's called on.
     */
    fun getProduct(productId: Int): MakeupProduct? {
        return productDao.getProductById(productId)
    }
}
