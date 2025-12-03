/*
 * MainActivity.kt
 *
 * Main entry point for BeautyApp
 * - Handles user authentication (Firebase)
 * - Routes to login or main app based on login status
 * - Manages bottom navigation between 5 tabs (Home, Products, Shade Match, Cart, Profile)
 * - Integrates Store Finder feature (shows map of nearby beauty stores)
 * - Applies dark mode theme based on user settings
 */

package com.example.beautyapp

import androidx.compose.ui.platform.LocalContext
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beautyapp.data.Product
import com.example.beautyapp.data.ProductColor
import com.example.beautyapp.data.Settings
import com.example.beautyapp.ui.screens.*
import com.example.beautyapp.ui.components.BottomNavBar
import com.example.beautyapp.ui.theme.BeautyAppTheme
import com.example.beautyapp.viewmodel.MainViewModel
import com.example.beautyapp.viewmodel.WeatherViewModel
import com.example.beautyapp.viewmodel.SettingsViewModel
import com.example.beautyapp.viewmodel.ShadeProductViewModel
import com.google.firebase.auth.FirebaseAuth


// Main Activity - Entry point for the entire app
class MainActivity : ComponentActivity() {
    // Settings ViewModel - manages dark mode, font size, etc.
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Observe settings to enable live theme switching
            val settings: Settings by settingsViewModel.settings.collectAsState()

            // Apply theme - automatically switches between light/dark mode
            BeautyAppTheme(darkTheme = settings.isDarkMode) {
                // Check if user is logged in
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                val isLoggedIn = currentUser != null

                Log.d("MainActivity", "onCreate - isLoggedIn: $isLoggedIn, user: ${currentUser?.displayName}")

                if (isLoggedIn) {
                    // User is logged in - show main app
                    BeautyApp(
                        context = this@MainActivity,
                        userName = currentUser?.displayName ?: "User",
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            recreate()
                        },
                        settings = settings
                    )
                } else {
                    // User not logged in - show login/signup flow
                    AppNavigation()
                }
            }
        }
    }
}

// Navigation between Login and SignUp screens
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { userName ->
                    Log.d("AppNavigation", "Login success: $userName")
                    navController.navigate("main/$userName") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("signup")
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("main/{userName}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "User"
            Log.d("AppNavigation", "Showing main for: $userName")

            val context = LocalContext.current as ComponentActivity
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsState()

            BeautyApp(
                context = context,
                userName = userName,
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                settings = settings
            )
        }
    }
}

// Main app with bottom navigation and 5 tabs
// Tabs: Home (Weather), Products, Shade Match, Cart, Profile
@Composable
fun BeautyApp(
    context: ComponentActivity,
    userName: String,
    onLogout: () -> Unit,
    settings: Settings,
    productViewModel: MainViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel(),
    shadeProductViewModel: ShadeProductViewModel = viewModel()
) {
    // State management
    val productState by productViewModel.state.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }  // Currently viewed product
    var selectedTab by remember { mutableStateOf(0) }  // Active bottom nav tab
    var showLogoutDialog by remember { mutableStateOf(false) }  // Logout confirmation

    // NEW: Store Finder State - manages map screen visibility
    var showStoreFinder by remember { mutableStateOf(false) }  // Show/hide store finder screen
    var storeFinderProduct by remember { mutableStateOf<Product?>(null) }  // Product to find stores for

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // NEW: Show Store Finder Screen (full screen overlay)
    // When user taps "Find at Nearby Stores" from cart
    if (showStoreFinder && storeFinderProduct != null) {
        StoreFinderScreen(
            product = storeFinderProduct!!,
            onBack = {
                // Close store finder and return to cart
                showStoreFinder = false
                storeFinderProduct = null
            }
        )
        return  // Don't show main app while store finder is open
    }

    // Product detail screen (when user taps a product)
    if (selectedProduct != null) {
        ProductDetailScreen(
            product = selectedProduct!!,
            isLiked = productState.likedProducts.contains(selectedProduct!!.id),
            onToggleLike = { productViewModel.toggleLike(it) },
            onAddToCart = { productId: Int, shade: ProductColor? ->
                productViewModel.addToCart(productId, shade)
            },
            onBack = { selectedProduct = null }
        )
    } else {
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    activeTab = when(selectedTab) {
                        0 -> "home"
                        1 -> "search"
                        2 -> "scan"
                        3 -> "cart"
                        4 -> "profile"
                        else -> "home"
                    },
                    onTabChange = { tab ->
                        selectedTab = when(tab) {
                            "home" -> 0
                            "search" -> 1
                            "scan" -> 2
                            "cart" -> 3
                            "profile" -> 4
                            else -> 0
                        }
                    },
                    cartCount = productViewModel.getCartCount()
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (selectedTab) {
                    // Tab 0: Home - Weather screen with greeting
                    0 -> WeatherScreen(
                        modifier = Modifier.fillMaxSize(),
                        context = context,
                        viewModel = weatherViewModel,
                        userName = userName
                    )

                    // Tab 1: Products - Browse makeup products with filters
                    1 -> ProductsScreen(
                        products = productViewModel.getDisplayProducts(),
                        likedProducts = productState.likedProducts,
                        onToggleLike = { productViewModel.toggleLike(it) },
                        onAddToCart = { productViewModel.addToCart(it) },
                        loading = productState.loading,
                        brands = productState.availableBrands,
                        productTypes = productState.availableProductTypes,
                        selectedBrands = productState.selectedBrands,
                        selectedProductTypes = productState.selectedProductTypes,
                        onBrandToggle = { productViewModel.toggleBrandFilter(it) },
                        onProductTypeToggle = { productViewModel.toggleProductTypeFilter(it) },
                        onClearFilters = { productViewModel.clearFilters() },
                        hasActiveFilters = productViewModel.hasActiveFilters(),
                        onProductClick = { product -> selectedProduct = product }
                    )

                    // Tab 2: Shade Match - Find your perfect shade from 8 skin tones
                    2 -> ShadeProductScreen(viewModel = shadeProductViewModel)

                    // Tab 3: Cart - View cart items with Store Finder feature
                    3 -> CartScreen(
                        cartItems = productState.cartItems,
                        products = productState.products,
                        onAddToCart = { productId, shade -> productViewModel.addToCart(productId, shade) },
                        onRemoveFromCart = { productId, shade -> productViewModel.removeFromCart(productId, shade) },
                        onFindStores = { product ->  // NEW: Store finder callback!
                            // When user taps "Find at Nearby Stores" button
                            storeFinderProduct = product
                            showStoreFinder = true
                        }
                    )

                    // Tab 4: Profile - Favorites, settings, logout
                    4 -> ProfileScreen(
                        userName = userName,
                        likedProducts = productState.products.filter {
                            productState.likedProducts.contains(it.id)
                        },
                        likedProductIds = productState.likedProducts,
                        onToggleLike = { productViewModel.toggleLike(it) },
                        onAddToCart = { productViewModel.addToCart(it) },
                        onProductClick = { product -> selectedProduct = product },
                        onLogout = { showLogoutDialog = true },
                        viewModel = productViewModel
                    )
                }
            }
        }
    }
}