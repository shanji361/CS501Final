/*
 * MainViewModel.kt
 * PURPOSE: Central state management for BeautyApp - handles products, cart, favorites, notes, and filters
 * ...
 */

package com.example.beautyapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.beautyapp.data.*
import com.example.beautyapp.network.MakeupApiService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

data class AppState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val likedProducts: Set<Int> = emptySet(),
    val cartItems: List<CartItem> = emptyList(),
    val localCartItems: List<MakeupProduct> = emptyList(),
    val loading: Boolean = false,
    val activeTab: String = "home",
    val selectedBrands: Set<String> = emptySet(),
    val selectedProductTypes: Set<String> = emptySet(),
    val availableBrands: List<String> = emptyList(),
    val availableProductTypes: List<String> = emptyList()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val likedProductDao = AppDatabase.getDatabase(application).likedProductDao()
    private val noteDao = AppDatabase.getDatabase(application).noteDao()

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    val notes = noteDao.getAllNotes()

    private val api: MakeupApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://makeup-api.herokuapp.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MakeupApiService::class.java)
    }

    init {
        fetchProducts()
        viewModelScope.launch {
            likedProductDao.getAllLikedProductIds().collect { likedIds ->
                _state.update { it.copy(likedProducts = likedIds.toSet()) }
            }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            try {
                val products = api.getProducts()
                val brands = products.mapNotNull { it.brand }.distinct().sorted()
                val productTypes = products.mapNotNull { it.productType }.distinct().sorted()
                _state.value = _state.value.copy(
                    products = products,
                    filteredProducts = products,
                    loading = false,
                    availableBrands = brands,
                    availableProductTypes = productTypes
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false)
                Log.e("MainViewModel", "Failed to fetch products", e)
            }
        }
    }

    // --- Filter Logic ---
    fun toggleBrandFilter(brand: String) {
        val currentBrands = _state.value.selectedBrands.toMutableSet()
        if (currentBrands.contains(brand)) currentBrands.remove(brand) else currentBrands.add(brand)
        _state.value = _state.value.copy(selectedBrands = currentBrands)
        applyFilters()
    }

    fun toggleProductTypeFilter(productType: String) {
        val currentTypes = _state.value.selectedProductTypes.toMutableSet()
        if (currentTypes.contains(productType)) currentTypes.remove(productType) else currentTypes.add(productType)
        _state.value = _state.value.copy(selectedProductTypes = currentTypes)
        applyFilters()
    }

    fun clearFilters() {
        _state.value = _state.value.copy(
            selectedBrands = emptySet(),
            selectedProductTypes = emptySet(),
            filteredProducts = _state.value.products
        )
    }

    private fun applyFilters() {
        val filtered = _state.value.products.filter { product ->
            val brandMatch = _state.value.selectedBrands.isEmpty() || _state.value.selectedBrands.contains(product.brand)
            val typeMatch = _state.value.selectedProductTypes.isEmpty() || _state.value.selectedProductTypes.contains(product.productType)
            brandMatch && typeMatch
        }
        _state.value = _state.value.copy(filteredProducts = filtered)
    }

    // --- Likes and Notes Logic ---
    fun toggleLike(productId: Int) {
        viewModelScope.launch {
            val currentLikes = _state.value.likedProducts
            if (currentLikes.contains(productId)) {
                likedProductDao.unlikeProduct(LikedProduct(id = productId))
            } else {
                likedProductDao.likeProduct(LikedProduct(id = productId))
            }
        }
    }

    fun addNote(title: String, content: String, imagePath: String?) {
        viewModelScope.launch {
            noteDao.insertNote(Note(id = UUID.randomUUID().toString(), title = title, content = content, imagePath = imagePath))
        }
    }

    fun deleteNote(noteId: String, imagePath: String?) {
        viewModelScope.launch {
            imagePath?.let { path ->
                try { File(path).delete() } catch (e: Exception) { Log.e("MainViewModel", "Failed to delete image", e) }
            }
            noteDao.deleteNoteById(noteId)
        }
    }

    // --- Cart Logic for API Products ---
    fun addToCart(productId: Int, selectedShade: ProductColor? = null) {
        val currentItems = _state.value.cartItems.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.productId == productId && it.selectedShade == selectedShade }
        if (existingIndex != -1) {
            val updatedItem = currentItems[existingIndex].copy(quantity = currentItems[existingIndex].quantity + 1)
            currentItems[existingIndex] = updatedItem
        } else {
            currentItems.add(CartItem(productId = productId, quantity = 1, selectedShade = selectedShade))
        }
        _state.update { it.copy(cartItems = currentItems) }
    }

    fun removeFromCart(productId: Int, selectedShade: ProductColor? = null) {
        val currentItems = _state.value.cartItems.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.productId == productId && it.selectedShade == selectedShade }
        if (existingIndex != -1) {
            val item = currentItems[existingIndex]
            if (item.quantity > 1) {
                val updatedItem = item.copy(quantity = item.quantity - 1)
                currentItems[existingIndex] = updatedItem
            } else {
                currentItems.removeAt(existingIndex)
            }
            _state.update { it.copy(cartItems = currentItems) }
        }
    }

    // --- Cart Logic for Local Products To Add---
    fun addLocalProductToCart(localProduct: MakeupProduct) {
        val currentLocalCart = _state.value.localCartItems.toMutableList()
        currentLocalCart.add(localProduct) // Simply add another instance
        _state.update { it.copy(localCartItems = currentLocalCart) }
        Log.d("MainViewModel", "Added local product to cart: ${localProduct.name}")
    }

    // --- Cart Logic for Local Products To Remove ---
    fun removeLocalProductFromCart(localProduct: MakeupProduct) {
        val currentLocalCart = _state.value.localCartItems.toMutableList()
        // Find the first instance of this product and remove it
        val itemToRemove = currentLocalCart.firstOrNull { it.productId == localProduct.productId }
        if (itemToRemove != null) {
            currentLocalCart.remove(itemToRemove)
            _state.update { it.copy(localCartItems = currentLocalCart) }
            Log.d("MainViewModel", "Removed one instance of local product: ${localProduct.name}")
        }
    }

    // --- Getter Functions for UI ---
    fun getCartTotal(): Double {
        val apiProductsTotal = state.value.cartItems.sumOf { cartItem ->
            val product = state.value.products.find { it.id == cartItem.productId }
            (product?.price?.toDoubleOrNull() ?: 0.0) * cartItem.quantity
        }
        val localProductsTotal = state.value.localCartItems.sumOf { it.price }
        return apiProductsTotal + localProductsTotal
    }

    fun getCartCount(): Int {
        val apiItemCount = _state.value.cartItems.sumOf { it.quantity }
        val localItemCount = _state.value.localCartItems.size
        return apiItemCount + localItemCount
    }

    fun getDisplayProducts(): List<Product> = _state.value.filteredProducts
    fun hasActiveFilters(): Boolean = _state.value.selectedBrands.isNotEmpty() || _state.value.selectedProductTypes.isNotEmpty()
}
