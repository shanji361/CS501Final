// storefinderviewmodel.kt
package com.example.beautyapp.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beautyapp.data.StoreFinderUiState
import com.example.beautyapp.network.StoreAPIService
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StoreFinderViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StoreFinderUiState())
    val uiState: StateFlow<StoreFinderUiState> = _uiState.asStateFlow()



     // Called when location permission is granted by the user.
     // It immediately tries to search for stores.

    fun onLocationPermissionGranted(context: Context, productName: String, productBrand: String?) {
        Log.d("StoreFinder", "Permission granted. Starting store search.")
        searchForStores(context, productName, productBrand)
    }


     //Called when location permission is denied by the user.
     //Updates the UI to show an error message.

    fun onLocationPermissionDenied() {
        Log.d("StoreFinder", "Permission denied.")
        _uiState.update {
            it.copy(errorMessage = "Location permission is required to find nearby stores.")
        }
    }


     //The main function to find stores. It gets the user's current location first,
     // then uses that location to search for nearby stores via the API.

    @SuppressLint("MissingPermission") // Suppressed because this is only called after a permission check.
    fun searchForStores(context: Context, productName: String, productBrand: String?) {
        if (_uiState.value.isSearching) return
        _uiState.update { it.copy(isSearching = true, errorMessage = null) }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // Get current location
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    Log.d("StoreFinder", "Got user location: $userLatLng")

                    // Now with the location,  search for stores
                    viewModelScope.launch {
                        val stores = StoreAPIService.searchNearbyStores(
                            context = context.applicationContext,
                            userLocation = userLatLng,
                            productName = productName,
                            productBrand = productBrand
                        )

                        _uiState.update {
                            it.copy(
                                isSearching = false,
                                showMap = stores.isNotEmpty(),
                                userLocation = userLatLng,
                                nearbyStores = stores,
                                errorMessage = if (stores.isEmpty()) "No stores found nearby." else null
                            )
                        }



                    }
                } else {
                    Log.e("StoreFinder", "Failed to get location, it was null.")
                    _uiState.update {
                        it.copy(isSearching = false, errorMessage = "Unable to get your location. Please ensure location services are enabled.")
                    }
                }
            }
            //listens for failure and updates the UI accordingly
            .addOnFailureListener { exception ->
                Log.e("StoreFinder", "Location fetch failed", exception)
                _uiState.update {
                    it.copy(isSearching = false, errorMessage = "Failed to get location: ${exception.message}")
                }
            }
    }


}
