/*
 * CartScreen.kt
 *
 * Shopping cart screen that displays cart items
 * Features:
 * - Shows all products in cart with quantity controls
 * - Displays selected shade for each product (color circle + name)
 * - "Find at Nearby Stores" button for each item (NEW! - Store Finder integration)
 * - Calculate and display cart total
 * - Checkout button
 */

package com.example.beautyapp.ui.screens

import android.util.Log
import com.example.beautyapp.utils.parseHexColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.beautyapp.data.CartItem
import com.example.beautyapp.data.Product
import com.example.beautyapp.data.ProductColor

// TAG for logging - identifies logs from CartScreen
private const val TAG = "CartScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartItem>,  // List of items in cart (productId, quantity, selectedShade)
    products: List<Product>,  // All available products for lookups
    onAddToCart: (Int, ProductColor?) -> Unit,  // Add 1 more of this product
    onRemoveFromCart: (Int, ProductColor?) -> Unit,  // Remove 1 of this product
    onFindStores: (Product) -> Unit = {} // NEW: Callback to open Store Finder map
) {
    // Log cart state when screen is displayed
    Log.d(TAG, "CartScreen displayed with ${cartItems.size} items")
    cartItems.forEachIndexed { index, item ->
        Log.d(TAG, "Cart item $index: productId=${item.productId}, quantity=${item.quantity}, shade=${item.selectedShade?.colourName}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cart",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        Log.d(TAG, "User clicked back button")
                        /* Handle back */
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop",
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Log.d(TAG, "Cart is empty")

            // Empty cart state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Your cart is empty",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            Log.d(TAG, "Cart has ${cartItems.size} items, total value: $${calculateTotal(cartItems, products)}")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Scrollable list of cart items
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { cartItem ->
                        // Find product details from product ID
                        val product = products.find { it.id == cartItem.productId }

                        if (product != null) {
                            Log.d(TAG, "Rendering cart item: ${product.name}, quantity=${cartItem.quantity}")
                            CartItemCard(
                                product = product,
                                cartItem = cartItem,
                                onAddToCart = {
                                    Log.d(TAG, "User adding to cart: productId=${cartItem.productId}, current quantity=${cartItem.quantity}, new quantity=${cartItem.quantity + 1}")
                                    onAddToCart(cartItem.productId, cartItem.selectedShade)
                                },
                                onRemoveFromCart = {
                                    Log.d(TAG, "User removing from cart: productId=${cartItem.productId}, current quantity=${cartItem.quantity}, new quantity=${cartItem.quantity - 1}")
                                    onRemoveFromCart(cartItem.productId, cartItem.selectedShade)
                                },
                                onFindStores = {
                                    Log.d(TAG, "User clicked 'Find at Nearby Stores' for product: ${product.name}")
                                    Log.d(TAG, "Triggering Store Finder with product: ${product.name}")
                                    onFindStores(product)
                                } // NEW: Pass store finder callback
                            )
                        } else {
                            Log.e(TAG, "Product not found for cartItem: productId=${cartItem.productId}")
                        }
                    }
                }

                // Total price and Checkout button (bottom section)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Divider(
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$${calculateTotal(cartItems, products)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                Log.d(TAG, "User clicked Checkout button, cart total: $${calculateTotal(cartItems, products)}")
                                /* Handle checkout */
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF472B6)
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(text = "Checkout", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// Individual cart item card
// Shows: product image, name, brand, shade, price, quantity controls, store finder button
@Composable
fun CartItemCard(
    product: Product,  // Product details
    cartItem: CartItem,  // Cart item (includes quantity and selected shade)
    onAddToCart: () -> Unit,  // Add one more
    onRemoveFromCart: () -> Unit,  // Remove one
    onFindStores: () -> Unit = {} // NEW: Open store finder for this product
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Product Image
            AsyncImage(
                model = product.imageLink,
                contentDescription = product.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            // Product Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                product.brand?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                // Show selected shade with color circle (if product has shade variants)
                cartItem.selectedShade?.let { shade ->
                    Log.d("CartItemCard", "Displaying shade for ${product.name}: ${shade.colourName} (${shade.hexValue})")

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        // Color circle showing the shade
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    color = parseHexColor(shade.hexValue),
                                    shape = CircleShape
                                )
                        )
                        // Shade name
                        Text(
                            text = "Shade: ${shade.colourName ?: "Custom"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF472B6),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                product.price?.let {
                    Text(
                        text = "$$it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Quantity Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    IconButton(
                        onClick = {
                            Log.d("CartItemCard", "Remove button clicked for ${product.name}")
                            onRemoveFromCart()
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    ) {
                        Text(
                            text = "−",
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = cartItem.quantity.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = {
                            Log.d("CartItemCard", "Add button clicked for ${product.name}")
                            onAddToCart()
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    ) {
                        Text(
                            text = "+",
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // NEW: Find Nearby Stores Button!
                // Opens Google Maps with nearby beauty stores carrying this product
                OutlinedButton(
                    onClick = {
                        Log.d("CartItemCard", "'Find at Nearby Stores' button clicked for product: ${product.name}")
                        onFindStores()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF472B6)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Find at Nearby Stores")
                }
            }
        }
    }
}

// Calculate total price of all items in cart
// Formula: sum(product price × quantity) for each cart item
fun calculateTotal(cartItems: List<CartItem>, products: List<Product>): String {
    val total = cartItems.sumOf { cartItem ->
        val product = products.find { it.id == cartItem.productId }
        val price = product?.price?.toDoubleOrNull() ?: 0.0
        val itemTotal = price * cartItem.quantity

        Log.d(TAG, "Item calculation: productId=${cartItem.productId}, price=${product?.price}, quantity=${cartItem.quantity}, itemTotal=$itemTotal")

        itemTotal
    }

    Log.d(TAG, "Total cart value: $${"%.2f".format(total)}")
    return String.format("%.2f", total)
}