package com.example.beautyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.beautyapp.data.Product
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onViewProducts: () -> Unit,
    featuredProducts: List<Product>,
    onAddToCart: (Int) -> Unit,
    loading: Boolean
) {
    var addedProductId by remember { mutableStateOf<Int?>(null) }

    // Use LaunchedEffect to handle the animation reset
    LaunchedEffect(addedProductId) {
        addedProductId?.let {
            delay(800)
            addedProductId = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Hero Image
        AsyncImage(
            model = "https://images.unsplash.com/photo-1515688594390-b649af70d282?w=800&h=1200&fit=crop",
            contentDescription = "Beauty",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // AR Overlay (simplified - you can enhance with Canvas)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        )

        // Bottom Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Special for you",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Product Carousel
            if (loading) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .width(96.dp)
                                .height(128.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                        )
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    items(featuredProducts) { product ->
                        FeaturedProductCard(
                            product = product,
                            isAdded = addedProductId == product.id,
                            onAddToCart = {
                                onAddToCart(product.id)
                                addedProductId = product.id
                            }
                        )
                    }
                }
            }

            // CTA Button
            Button(
                onClick = onViewProducts,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF472B6) // Pink-400
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(48.dp)
            ) {
                Text(
                    text = "View all products",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun FeaturedProductCard(
    product: Product,
    isAdded: Boolean,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(96.dp)
            .height(128.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Product Image
                AsyncImage(
                    model = product.imageLink,
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                // Product Name
                Text(
                    text = product.brand ?: product.name ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Add Button
            FloatingActionButton(
                onClick = onAddToCart,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp),
                containerColor = if (isAdded) Color(0xFF10B981) else Color(0xFFF472B6),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (isAdded) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = "Add to cart",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}