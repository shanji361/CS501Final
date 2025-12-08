package com.example.beautyapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.beautyapp.data.MakeupProduct

@Composable
fun ProductRecommendationSection(
    products: List<MakeupProduct>,
    onAddToCart: (MakeupProduct) -> Unit // this is so that users can added recommended products to cart directly
) {
    if (products.isNotEmpty()) {
        Text(
            text = "Recommended Products",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            products.forEach { product ->
                ShadeProductCard(
                    product = product,
                    isLiked = false, // for rn, always false for recommended products
                    isLikingEnabled = false, //for rn, local products like button is always disabled
                    onToggleLike = {}, // does nothing
                    onAddToCart = onAddToCart
                )
            }
        }
    }
}
