// storefinder.kt
package com.example.beautyapp.data


import com.google.android.gms.maps.model.LatLng

data class StoreLocation(
    val name: String,
    val location: LatLng,
    val address: String?
)

data class StoreFinderUiState(
    val isSearching: Boolean = false,
    val errorMessage: String? = null,
    val showMap: Boolean = false,
    val userLocation: LatLng? = null,
    val nearbyStores: List<StoreLocation> = emptyList(),

)
