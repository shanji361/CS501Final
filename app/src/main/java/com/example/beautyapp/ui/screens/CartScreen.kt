package com.example.beautyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beautyapp.data.CartItem
import com.example.beautyapp.data.MakeupProduct
import com.example.beautyapp.data.Product
import com.example.beautyapp.data.ProductColor
import com.example.beautyapp.ui.components.CartItemCard

data class DisplayableCartProduct(
    val id: Int,
    val name: String,
    val brand: String?,
    val price: String?,
    val imageUrl: String?,
    val quantity: Int,
    val selectedShade: ProductColor?,
    val isLocal: Boolean,
    val originalLocalProduct: MakeupProduct? = null //this is for recommended products from shadescreenselection
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    localCartItems: List<MakeupProduct>,
    products: List<Product>,
    total: Double,
    onAddToCart: (Int, ProductColor?) -> Unit,
    onRemoveFromCart: (Int, ProductColor?) -> Unit,
//    onFindStores: (Product) -> Unit,
    onFindStores: (DisplayableCartProduct) -> Unit,
    // ADDED functions for local products
    onAddLocalToCart: (MakeupProduct) -> Unit,
    onRemoveLocalFromCart: (MakeupProduct) -> Unit
) {
    val allDisplayableItems = remember(cartItems, localCartItems, products) {
        // API products in the cart
        val apiCartProducts = cartItems.mapNotNull { cartItem ->
            products.find { it.id == cartItem.productId }?.let { product ->
                DisplayableCartProduct(
                    id = product.id,
                    name = product.name ?: "Unknown Product",
                    brand = product.brand,
                    price = product.price,
                    imageUrl = product.imageLink,
                    quantity = cartItem.quantity,
                    selectedShade = cartItem.selectedShade,
                    isLocal = false
                )
            }
        }

        // local products, now grouped by ID to calculate quantity
        val localCartProducts = localCartItems
            .groupBy { it.productId }
            .map { (productId, items) ->
                val firstItem = items.first()
                DisplayableCartProduct(
                    id = productId,
                    name = firstItem.name,
                    brand = firstItem.brand,
                    price = firstItem.price.toString(),
                    imageUrl = firstItem.imageUrl,
                    quantity = items.size, // the quantity is the count of items with the same ID
                    selectedShade = null,
                    isLocal = true,
                    originalLocalProduct = firstItem // keeps a reference to one of the original objects
                )
            }

        apiCartProducts + localCartProducts
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Cart", fontWeight = FontWeight.Bold) }) }
    ) { paddingValues ->
        if (allDisplayableItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Your cart is empty", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allDisplayableItems, key = { "${it.id}-${it.isLocal}" }) { item ->
                        val originalApiProduct = if (!item.isLocal) products.find { it.id == item.id } else null

                        CartItemCard(
                            productName = item.name,
                            productBrand = item.brand ?: "",
                            productPrice = item.price ?: "0.0",
                            imageUrl = item.imageUrl ?: "",
                            quantity = item.quantity,
                            selectedShade = item.selectedShade,
                            // onIncrease and onDecrease now handle both types for localproducts coming from makeup.db
                            onIncrease = {
                                if (item.isLocal && item.originalLocalProduct != null) {
                                    onAddLocalToCart(item.originalLocalProduct)
                                } else {
                                    onAddToCart(item.id, item.selectedShade)
                                }
                            },
                            onDecrease = {
                                if (item.isLocal && item.originalLocalProduct != null) {
                                    onRemoveLocalFromCart(item.originalLocalProduct)
                                } else {
                                    onRemoveFromCart(item.id, item.selectedShade)
                                }
                            },
                            onFindStores = {
                                onFindStores(item)
                            },
                            isLocalProduct = item.isLocal,
                            // BUG FIX 2: Enable the button for ALL products.
                            isStoreFinderEnabled = true
                        )
                    }
                }
                // Checkout total bar
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "$${String.format("%.2f", total)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = { /* TODO: Checkout */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513)),
                            modifier = Modifier.height(50.dp)
                        ) {
                            Text("Checkout", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
