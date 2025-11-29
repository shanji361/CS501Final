package com.example.beautyapp

import androidx.compose.ui.platform.LocalContext
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.beautyapp.ui.screens.*
import com.example.beautyapp.ui.components.BottomNavBar
import com.example.beautyapp.ui.theme.BeautyAppTheme
import com.example.beautyapp.viewmodel.MainViewModel
import com.example.beautyapp.viewmodel.ShadeProductViewModel
import com.example.beautyapp.viewmodel.WeatherViewModel
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BeautyAppTheme {
                // The faulty CompositionLocalProvider has been removed.
                // We are now using your original, correct navigation flow.
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val auth = FirebaseAuth.getInstance()
                    val currentUser = auth.currentUser
                    val isLoggedIn = currentUser != null

                    val mainViewModel: MainViewModel = viewModel()
                    val weatherViewModel: WeatherViewModel = viewModel()
                    val shadeProductViewModel: ShadeProductViewModel = viewModel()

                    if (isLoggedIn) {
                        BeautyApp(
                            context = this@MainActivity,
                            userName = currentUser?.displayName ?: "User",
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                recreate()
                            },  productViewModel = mainViewModel,
                            weatherViewModel = weatherViewModel,
                            shadeProductViewModel = shadeProductViewModel
                        )
                    } else {
                        AppNavigation(
                            mainViewModel = mainViewModel,
                            weatherViewModel = weatherViewModel,
                            shadeProductViewModel = shadeProductViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation( mainViewModel: MainViewModel,
                   weatherViewModel: WeatherViewModel,
                   shadeProductViewModel: ShadeProductViewModel) {
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
            BeautyApp(
                context = context,
                userName = userName,
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                productViewModel = mainViewModel,
                weatherViewModel = weatherViewModel,
                shadeProductViewModel = shadeProductViewModel
            )
        }
    }
}

@Composable
fun BeautyApp(
    context: ComponentActivity,
    userName: String,
    onLogout: () -> Unit,
    productViewModel: MainViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel(),
    shadeProductViewModel: ShadeProductViewModel = viewModel()
) {
    val productState by productViewModel.state.collectAsState()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedTab by remember { mutableStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }

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

    if (selectedProduct != null) {
        ProductDetailScreen(
            product = selectedProduct!!,
            isLiked = productState.likedProducts.contains(selectedProduct!!.id),
            onToggleLike = { productViewModel.toggleLike(it) },
            onAddToCart = { productViewModel.addToCart(it) },
            onBack = { selectedProduct = null }
        )
    } else {
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    activeTab = when (selectedTab) {
                        0 -> "home"
                        1 -> "search"
                        2 -> "scan"
                        3 -> "cart"
                        4 -> "profile"
                        else -> "home"
                    },
                    onTabChange = { tab ->
                        selectedTab = when (tab) {
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
                    0 -> WeatherScreen(
                        modifier = Modifier.fillMaxSize(),
                        context = context,
                        viewModel = weatherViewModel,
                        userName = userName
                    )

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

                    2 -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ShadeProductScreen(viewModel = shadeProductViewModel)
                    }

                    3 -> CartScreen(
                        cartItems = productState.cartItems,
                        products = productState.products,
                        onAddToCart = { productViewModel.addToCart(it) },
                        onRemoveFromCart = { productViewModel.removeFromCart(it) }
                    )

                    4 -> ProfileScreen(
                        userName = userName,
                        likedProducts = productState.products.filter {
                            productState.likedProducts.contains(it.id)
                        },
                        likedProductIds = productState.likedProducts,
                        onToggleLike = { productViewModel.toggleLike(it) },
                        onAddToCart = { productViewModel.addToCart(it) },
                        onProductClick = { product -> selectedProduct = product },
                        onLogout = { showLogoutDialog = true }
                    )
                }
            }
        }
    }
}
