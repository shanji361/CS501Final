package com.example.beautyapp.network


import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.example.beautyapp.data.Product
import com.example.beautyapp.data.StoreLocation
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

            val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                    "location=${userLocation.latitude},${userLocation.longitude}" +
                    "&radius=$radiusMeters" +
                    "&keyword=${Uri.encode(keyword)}" +
                    "&key=$apiKey"

            val client = okhttp3.OkHttpClient()
            val request = okhttp3.Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (body != null) {
                val json = org.json.JSONObject(body)
                val results = json.getJSONArray("results")
                val storeList = mutableListOf<StoreLocation>()

                for (i in 0 until results.length()) {
                    val place = results.getJSONObject(i)
                    val name = place.optString("name", "Store")
                    val address = place.optString("vicinity", "")
                    val locationObj = place.getJSONObject("geometry").getJSONObject("location")
                    val lat = locationObj.getDouble("lat")
                    val lng = locationObj.getDouble("lng")

                    storeList.add(
                        StoreLocation(
                            name = name,
                            location = LatLng(lat, lng),
                            address = address
                        )
                    )
                }
                Log.d("StoreFinder", "Found ${storeList.size} stores via Web API")
                storeList
            } else {
                Log.e("StoreFinder", "Empty response body from Places Web API")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("StoreFinder", "Places Web API error: ${e.message}", e)
            emptyList()
        }
    }
}