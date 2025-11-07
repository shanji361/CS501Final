// ApiService.kt
package com.example.instaglowhome

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

interface ApiService {
    @GET("weather")
    suspend fun getWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}

object ApiClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Explicitly add KotlinJsonAdapterFactory
        .build() // Build the Moshi instance


    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}