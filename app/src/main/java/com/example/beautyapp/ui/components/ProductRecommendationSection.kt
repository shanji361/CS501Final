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

/*
--- This file ProductRecommendationSection.kt is responsible for the Product Section of our navbar, primarily fetching products from the
makeup service api, local products refers to makeup products recommended by shade select screen in navbar
*/

@Composable
fun ProductRecommendationSection(
    products: List<MakeupProduct>,
    allApiProducts: List<com.example.beautyapp.data.Product>,
    likedProductIds: Set<Int>,
    likedLocalProductIds: Set<Int>,
    onToggleLocalLike: (Int) -> Unit,
    onAddToCart: (MakeupProduct) -> Unit
) {
    if (products.isNotEmpty()) {
        Text(
            text = "Recommended Products",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            products.forEach { recommendedProduct ->
                ShadeProductCard(
                    product = recommendedProduct,
                    //  check the local liked set ---
                    isLiked = recommendedProduct.productId in likedLocalProductIds,
                    // Liking is always enabled for local products now
                    isLikingEnabled = true,
                    // --- FIX 3: Call the NEW local like function ---
                    onToggleLike = {
                        onToggleLocalLike(recommendedProduct.productId)
                    },
                    onAddToCart = onAddToCart

                )
            }
        }
    }
}
