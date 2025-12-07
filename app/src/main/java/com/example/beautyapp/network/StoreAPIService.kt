// Data classes for Moshi parsing
package com.example.beautyapp.network

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.example.beautyapp.data.Product
import com.example.beautyapp.data.StoreLocation
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@JsonClass(generateAdapter = true)
data class PlacesResponse(
    val results: List<PlaceResult>,
    val status: String
)

@JsonClass(generateAdapter = true)
data class PlaceResult(
    val name: String?,
    val vicinity: String?,
    val geometry: Geometry
)

@JsonClass(generateAdapter = true)
data class Geometry(
    val location: Location
)

@JsonClass(generateAdapter = true)
data class Location(
    val lat: Double,
    val lng: Double
)

// Retrofit API Interface

interface PlacesApiService {
    @GET("place/nearbysearch/json")
    suspend fun searchNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("keyword") keyword: String,
        @Query("key") apiKey: String
    ): PlacesResponse
}

// Retrofit Client Setup



object RetrofitClient {
    private const val BASE_URL = "https://maps.googleapis.com/maps/api/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val placesApiService: PlacesApiService by lazy {
        retrofit.create(PlacesApiService::class.java)
    }
}

// Updated StoreAPIService


object StoreAPIService {

    suspend fun searchNearbyStores(
        context: Context,
        userLocation: LatLng,
        product: Product
    ): List<StoreLocation> = withContext(Dispatchers.IO) {
        try {
            val apiKey = context.packageManager
                .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                .metaData?.getString("com.google.android.geo.API_KEY") ?: ""

            val keyword = buildString {
                append("beauty store")
                product.brand?.let {
                    append(" ")
                    append(it)
                }
            }

            val radiusMeters = 5000
            val locationString = "${userLocation.latitude},${userLocation.longitude}"

            val response = RetrofitClient.placesApiService.searchNearbyPlaces(
                location = locationString,
                radius = radiusMeters,
                keyword = keyword,
                apiKey = apiKey
            )

            val storeList = response.results.map { place ->
                StoreLocation(
                    name = place.name ?: "Store",
                    location = LatLng(
                        place.geometry.location.lat,
                        place.geometry.location.lng
                    ),
                    address = place.vicinity ?: ""
                )
            }

            Log.d("StoreFinder", "Found ${storeList.size} stores via Retrofit API")
            storeList

        } catch (e: Exception) {
            Log.e("StoreFinder", "Places API error: ${e.message}", e)
            emptyList()
        }
    }
}
