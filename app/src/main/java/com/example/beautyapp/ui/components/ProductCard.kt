package com.example.beautyapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.beautyapp.data.Product
import kotlinx.coroutines.delay

@Composable
fun ProductCard(
    product: Product,
    isLiked: Boolean,
    onToggleLike: (Int) -> Unit,
    colorIndex: Int,
    onAddToCart: (Int) -> Unit
) {
    var isAdded by remember { mutableStateOf(false) }

    // Use LaunchedEffect to handle the animation reset
    LaunchedEffect(isAdded) {
        if (isAdded) {
            delay(800)
            isAdded = false
        }
    }

    val bgColors = listOf(
        Color(0xFFFEF3C7), // amber-50
        Color(0xFFD1FAE5), // emerald-50
        Color(0xFFF1F5F9), // slate-100
        Color(0xFFFFF1F2), // rose-50
        Color(0xFFDFEFFF), // blue-50
        Color(0xFFFAF5FF), // purple-50
    )

    val bgColor = bgColors[colorIndex % bgColors.size]

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Product Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Heart Icon - Top Left
                IconButton(
                    onClick = { onToggleLike(product.id) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color(0xFFEF4444) else Color(0xFF9CA3AF),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Plus Icon - Top Right
                IconButton(
                    onClick = {
                        onAddToCart(product.id)
                        isAdded = true
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .background(
                            if (isAdded) Color(0xFF10B981) else Color.White,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isAdded) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = "Add to cart",
                        tint = if (isAdded) Color.White else Color(0xFF374151),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Product Image
                AsyncImage(
                    model = product.imageLink,
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }
        }

        // Product Title
        Text(
            text = product.name ?: "Special facial product",
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
    }
}