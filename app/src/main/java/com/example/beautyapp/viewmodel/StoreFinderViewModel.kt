package com.example.beautyapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beautyapp.data.Product
import com.example.beautyapp.data.StoreFinderUiState
import com.example.beautyapp.network.StoreAPIService
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StoreFinderViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StoreFinderUiState())
    val uiState: StateFlow<StoreFinderUiState> = _uiState.asStateFlow()

    fun onLocationPermissionGranted(context: Context, product: Product) {
        Log.d("StoreFinder", "Permission granted, getting location")
        _uiState.update { it.copy(isSearching = true, errorMessage = null) }

        getUserLocationAndFindStores(context, product)
    }

    fun onLocationPermissionDenied() {
        Log.d("StoreFinder", "Permission denied")
        _uiState.update {
            it.copy(errorMessage = "Location permission denied. You can still search without your location.")
        }
    }

    fun searchForStores(context: Context, product: Product) {
        _uiState.update { it.copy(isSearching = true, errorMessage = null) }
        getUserLocationAndFindStores(context, product)
    }

    fun onMapReady(map: GoogleMap) {
        _uiState.update { it.copy(googleMap = map) }
    }

    private fun getUserLocationAndFindStores(context: Context, product: Product) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    Log.d("StoreFinder", "Got location: ${location.latitude}, ${location.longitude}")

                    searchNearbyStores(context, userLatLng, product)
                } else {
                    Log.d("StoreFinder", "Location is null")
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            errorMessage = "Unable to get your location. Please try again."
                        )
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("StoreFinder", "Failed to get location: ${exception.message}")
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        errorMessage = "Failed to get location: ${exception.message}"
                    )
                }
            }
        } catch (e: SecurityException) {
            Log.e("StoreFinder", "Security exception: ${e.message}")
            _uiState.update {
                it.copy(
                    isSearching = false,
                    errorMessage = "Location permission denied"
                )
            }
        }
    }

    private fun searchNearbyStores(context: Context, userLocation: LatLng, product: Product) {
        viewModelScope.launch {
            val stores = StoreAPIService.searchNearbyStores(context, userLocation, product)

            _uiState.update {
                it.copy(
                    isSearching = false,
                    userLocation = userLocation,
                    nearbyStores = stores,
                    showMap = true
                )
            }
        }
    }
}