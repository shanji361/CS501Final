package com.example.beautyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beautyapp.data.Product
import com.example.beautyapp.ui.components.BottomNavBar
import com.example.beautyapp.ui.screens.*
import com.example.beautyapp.ui.theme.BeautyAppTheme
import com.example.beautyapp.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeautyAppTheme {
                BeautyApp()
            }
        }
    }
}

@Composable
fun BeautyApp(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    // Show product detail if a product is selected
    if (selectedProduct != null) {
        ProductDetailScreen(
            product = selectedProduct!!,
            isLiked = state.likedProducts.contains(selectedProduct!!.id),
            onToggleLike = { viewModel.toggleLike(it) },
            onAddToCart = {
                viewModel.addToCart(it)
                // Optionally show a toast or snackbar
            },
            onBack = { selectedProduct = null }
        )
    } else {
        // Main app with navigation
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    activeTab = state.activeTab,
                    onTabChange = { viewModel.setActiveTab(it) },
                    cartCount = viewModel.getCartCount()
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (state.activeTab) {
                    "home" -> {
                        HomeScreen(
                            onViewProducts = { viewModel.setActiveTab("search") },
                            featuredProducts = viewModel.getFeaturedProducts(),
                            onAddToCart = { viewModel.addToCart(it) },
                            loading = state.loading
                        )
                    }
                    "search" -> {
                        ProductsScreen(
                            products = viewModel.getDisplayProducts(),
                            likedProducts = state.likedProducts,
                            onToggleLike = { viewModel.toggleLike(it) },
                            onAddToCart = { viewModel.addToCart(it) },
                            loading = state.loading,
                            brands = state.availableBrands,
                            productTypes = state.availableProductTypes,
                            selectedBrands = state.selectedBrands,
                            selectedProductTypes = state.selectedProductTypes,
                            onBrandToggle = { viewModel.toggleBrandFilter(it) },
                            onProductTypeToggle = { viewModel.toggleProductTypeFilter(it) },
                            onClearFilters = { viewModel.clearFilters() },
                            hasActiveFilters = viewModel.hasActiveFilters(),
                            onProductClick = { product -> selectedProduct = product }
                        )
                    }
                    "cart" -> {
                        CartScreen(
                            cartItems = state.cartItems,
                            products = state.products,
                            onAddToCart = { viewModel.addToCart(it) },
                            onRemoveFromCart = { viewModel.removeFromCart(it) }
                        )
                    }
                    "profile" -> {
                        ProductsScreen(
                            products = state.products.filter { state.likedProducts.contains(it.id) },
                            likedProducts = state.likedProducts,
                            onToggleLike = { viewModel.toggleLike(it) },
                            onAddToCart = { viewModel.addToCart(it) },
                            loading = state.loading,
                            brands = emptyList(),
                            productTypes = emptyList(),
                            selectedBrands = emptySet(),
                            selectedProductTypes = emptySet(),
                            onProductClick = { product -> selectedProduct = product }
                        )
                    }
                    "clean" -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Clean Products - Coming soon...")
                        }
                    }
                    "scan" -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "AI Face Scan - Coming soon...")
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Coming soon...")
                        }
                    }
                }
            }
        }
    }
}