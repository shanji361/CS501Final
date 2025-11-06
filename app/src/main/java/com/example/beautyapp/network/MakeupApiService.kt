package com.example.beautyapp.network

import com.example.beautyapp.data.Product
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface MakeupApiService {
    @GET("api/v1/products.json")
    suspend fun getProducts(): List<Product>
}

object MakeupApi {
    private const val BASE_URL = "https://makeup-api.herokuapp.com/"
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val service: MakeupApiService by lazy {
        retrofit.create(MakeupApiService::class.java)
    }
}
