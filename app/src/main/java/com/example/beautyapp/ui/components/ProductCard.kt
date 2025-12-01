package com.example.beautyapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource  //new - add this import
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.ripple.rememberRipple  //new - add this import
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.beautyapp.data.Product
import kotlinx.coroutines.delay

@Composable
fun ProductCard(
    product: Product,
    isLiked: Boolean,
    onToggleLike: (Int) -> Unit,
    colorIndex: Int,
    onAddToCart: (Int) -> Unit,
    onClick: () -> Unit = {}
) {
    var isAdded by remember { mutableStateOf(false) }

    LaunchedEffect(isAdded) {
        if (isAdded) {
            delay(800)
            isAdded = false
        }
    }

    val bgColors = listOf(
        Color(0xFFFEF3C7),
        Color(0xFFD1FAE5),
        Color(0xFFF1F5F9),
        Color(0xFFFFF1F2),
        Color(0xFFDFEFFF),
        Color(0xFFFAF5FF),
    )

    val bgColor = bgColors[colorIndex % bgColors.size]
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(  //new - updated clickable with explicit parameters
                onClick = onClick,
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Product Image with better configuration
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(product.imageLink)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp)
                        .align(Alignment.Center),
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFFF472B6),
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "No image",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "No image",
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                )

                // Heart Icon - Top Left
                IconButton(
                    onClick = { onToggleLike(product.id) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .zIndex(10f)
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
                        .padding(8.dp)
                        .size(32.dp)
                        .background(
                            if (isAdded) Color(0xFF10B981) else Color.White,
                            CircleShape
                        )
                        .zIndex(10f)
                ) {
                    Icon(
                        imageVector = if (isAdded) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = "Add to cart",
                        tint = if (isAdded) Color.White else Color(0xFF374151),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Product Title
        Text(
            text = product.name ?: "Product",
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
    }
}