package com.example.beautyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beautyapp.data.Product
import com.example.beautyapp.network.MakeupApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val likedProducts: Set<Int> = emptySet(),
    val cartItems: Map<Int, Int> = emptyMap(),
    val loading: Boolean = false,
    val activeTab: String = "home",
    val selectedBrands: Set<String> = emptySet(),
    val selectedProductTypes: Set<String> = emptySet(),
    val availableBrands: List<String> = emptyList(),
    val availableProductTypes: List<String> = emptyList()
)

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            try {
                val products = MakeupApi.service.getProducts()

                // Extract unique brands and product types
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
                e.printStackTrace()
            }
        }
    }

    fun toggleBrandFilter(brand: String) {
        val currentBrands = _state.value.selectedBrands.toMutableSet()
        if (currentBrands.contains(brand)) {
            currentBrands.remove(brand)
        } else {
            currentBrands.add(brand)
        }
        _state.value = _state.value.copy(selectedBrands = currentBrands)
        applyFilters()
    }

    fun toggleProductTypeFilter(productType: String) {
        val currentTypes = _state.value.selectedProductTypes.toMutableSet()
        if (currentTypes.contains(productType)) {
            currentTypes.remove(productType)
        } else {
            currentTypes.add(productType)
        }
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
            val brandMatch = _state.value.selectedBrands.isEmpty() ||
                    _state.value.selectedBrands.contains(product.brand)
            val typeMatch = _state.value.selectedProductTypes.isEmpty() ||
                    _state.value.selectedProductTypes.contains(product.productType)
            brandMatch && typeMatch
        }
        _state.value = _state.value.copy(filteredProducts = filtered)
    }

    fun toggleLike(productId: Int) {
        val currentLiked = _state.value.likedProducts.toMutableSet()
        if (currentLiked.contains(productId)) {
            currentLiked.remove(productId)
        } else {
            currentLiked.add(productId)
        }
        _state.value = _state.value.copy(likedProducts = currentLiked)
    }

    fun addToCart(productId: Int) {
        val currentCart = _state.value.cartItems.toMutableMap()
        val currentQty = currentCart[productId] ?: 0
        currentCart[productId] = currentQty + 1
        _state.value = _state.value.copy(cartItems = currentCart)
    }

    fun removeFromCart(productId: Int) {
        val currentCart = _state.value.cartItems.toMutableMap()
        val currentQty = currentCart[productId] ?: 0
        if (currentQty <= 1) {
            currentCart.remove(productId)
        } else {
            currentCart[productId] = currentQty - 1
        }
        _state.value = _state.value.copy(cartItems = currentCart)
    }

    fun setActiveTab(tab: String) {
        _state.value = _state.value.copy(activeTab = tab)
    }

    fun getCartCount(): Int {
        return _state.value.cartItems.values.sum()
    }

    fun getFeaturedProducts(): List<Product> {
        val products = _state.value.products
        val foundation = products.firstOrNull { it.productType == "foundation" }
        val blush = products.firstOrNull { it.productType == "blush" }
        val lipstick = products.firstOrNull { it.productType == "lipstick" }
        return listOfNotNull(foundation, blush, lipstick)
    }

    fun getDisplayProducts(): List<Product> {
        return _state.value.filteredProducts
    }

    fun hasActiveFilters(): Boolean {
        return _state.value.selectedBrands.isNotEmpty() ||
                _state.value.selectedProductTypes.isNotEmpty()
    }
}