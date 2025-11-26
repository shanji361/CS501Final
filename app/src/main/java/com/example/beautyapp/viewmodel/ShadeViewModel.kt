package com.example.beautyapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beautyapp.data.database.MakeupDatabase
import com.example.beautyapp.data.entities.Shade
//this file stores data pertaining to user shades, getting data from shades db and loading data as well
class ShadeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MakeupDatabase.getDatabase(application)
    private val shadeDao = db.shadeDao()

    private val _shades = MutableLiveData<List<Shade>>()
    val shades: LiveData<List<Shade>> = _shades

    fun loadShades() {
        _shades.value = shadeDao.getAllShades()
    }
}
