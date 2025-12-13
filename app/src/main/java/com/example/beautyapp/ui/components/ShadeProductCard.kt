package com.example.beautyapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.beautyapp.data.MakeupProduct

/* This file ShadeProductCard.kt contains reusable UI component that displays
* a single recommended product from the local makeup.db in a compact horizontal
* card. Showing the product's image, name, price, and action buttons.
 */

@Composable
fun ShadeProductCard(
    product: MakeupProduct,
    isLiked: Boolean,
    onToggleLike: () -> Unit,
    onAddToCart: (MakeupProduct) -> Unit,
    isLikingEnabled: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.type.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "${product.brand} — ${product.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Action Icons
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Like button is controlled by isLikingEnabled
                IconButton(onClick = onToggleLike, enabled = isLikingEnabled) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        //this is what gives heart the red color when liked
                        tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = if (isLikingEnabled) 0.7f else 0.3f)
                    )
                }
                // Add to cart button
                IconButton(onClick = { onAddToCart(product) }) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Add to Cart"
                    )
                }
            }
        }
    }
}
