package com.example.beautyapp.data

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val brand: String?,
    val name: String?,
    val price: String?,
    @SerializedName("price_sign")
    val priceSign: String?,
    val currency: String?,
    @SerializedName("image_link")
    val imageLink: String?,
    @SerializedName("product_link")
    val productLink: String?,
    @SerializedName("website_link")
    val websiteLink: String?,
    val description: String?,
    val rating: Double?,
    val category: String?,
    @SerializedName("product_type")
    val productType: String?,
    @SerializedName("tag_list")
    val tagList: List<String>?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("product_api_url")
    val productApiUrl: String?,
    @SerializedName("api_featured_image")
    val apiFeaturedImage: String?,
    @SerializedName("product_colors")
    val productColors: List<ProductColor>?
)

data class ProductColor(
    @SerializedName("hex_value")
    val hexValue: String?,
    @SerializedName("colour_name")
    val colourName: String?
)