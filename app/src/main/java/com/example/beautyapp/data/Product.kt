package com.example.beautyapp.data

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val brand: String?,
    val name: String?,
    val price: String?,
    @SerializedName("image_link")
    val imageLink: String?,
    @SerializedName("product_type")
    val productType: String?,
    val description: String?
)
