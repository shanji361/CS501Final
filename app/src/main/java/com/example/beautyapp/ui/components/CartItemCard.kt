package com.example.beautyapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.beautyapp.data.ProductColor
import com.example.beautyapp.utils.parseHexColor
//This screen CartItemCard.kt was created for products that are recommended my shade selection screen, the three products you get back, now
//you have the ability to add those makeup products to your cart.

@Composable
fun CartItemCard(
    productName: String,
    productBrand: String,
    productPrice: String,
    imageUrl: String,
    quantity: Int,
    selectedShade: ProductColor?,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onFindStores: () -> Unit,
    isLocalProduct: Boolean, // Kept for potential styling differences
    isStoreFinderEnabled: Boolean // Explicitly control this button
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = productName,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = productBrand, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = productName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(4.dp))
                    if (selectedShade != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(16.dp).background(color = parseHexColor(selectedShade.hexValue), shape = CircleShape).border(1.dp, Color.Gray, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = selectedShade.colourName ?: "Selected Shade", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "$${productPrice}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Plus/minus +/- icons with quantity
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease) { Icon(Icons.Default.Remove, contentDescription = "Decrease quantity") }
                    Text(text = quantity.toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onIncrease) { Icon(Icons.Default.Add, contentDescription = "Increase quantity") }
                }

                // Find in Stores Button
                Button(
                    onClick = onFindStores,
                    enabled = isStoreFinderEnabled,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513).copy(alpha = if (isStoreFinderEnabled) 1f else 0.5f)),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Find Stores")
                }
            }
        }
    }
}
