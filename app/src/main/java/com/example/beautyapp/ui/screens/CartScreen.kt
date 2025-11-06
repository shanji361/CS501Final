package com.example.beautyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.beautyapp.data.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: Map<Int, Int>,
    products: List<Product>,
    onAddToCart: (Int) -> Unit,
    onRemoveFromCart: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cart",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Your cart is empty",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems.toList()) { (productId, quantity) ->
                        val product = products.find { it.id == productId }
                        product?.let {
                            CartItemCard(
                                product = it,
                                quantity = quantity,
                                onAddToCart = { onAddToCart(productId) },
                                onRemoveFromCart = { onRemoveFromCart(productId) }
                            )
                        }
                    }
                }
                
                // Total and Checkout
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Divider(modifier = Modifier.padding(bottom = 16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$${calculateTotal(cartItems, products)}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { /* Handle checkout */ },
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

@Composable
fun CartItemCard(
    product: Product,
    quantity: Int,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                product.brand?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                
                product.price?.let {
                    Text(
                        text = "$$it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                // Quantity Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onRemoveFromCart,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    ) {
                        Text(text = "−", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                    }
                    
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    ) {
                        Text(
                            text = "+",
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

fun calculateTotal(cartItems: Map<Int, Int>, products: List<Product>): String {
    val total = cartItems.entries.sumOf { (productId, quantity) ->
        val product = products.find { it.id == productId }
        val price = product?.price?.toDoubleOrNull() ?: 0.0
        price * quantity
    }
    return String.format("%.2f", total)
}
