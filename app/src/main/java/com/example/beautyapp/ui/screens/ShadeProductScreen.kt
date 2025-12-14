package com.example.beautyapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.beautyapp.data.MakeupProduct
import com.example.beautyapp.ui.components.ProductRecommendationSection
import com.example.beautyapp.ui.components.ShadeSelectionSection
import com.example.beautyapp.viewmodel.ShadeProductViewModel
import com.example.beautyapp.data.Product

/*
--This file ShadeProductScreen, responsible for the Shade Section of our navbar, primarily fetching products from the
local makeup.db, so a user select their skin shade and get three respective product recommendations
foundation, blush, and lip liner
*/

@Composable
fun ShadeProductScreen(
    viewModel: ShadeProductViewModel,
    allApiProducts: List<Product>,
    likedProductIds: Set<Int>,
    likedLocalProductIds: Set<Int>,
    onToggleLike: (Int) -> Unit,
    onToggleLocalLike: (Int) -> Unit,
    onAddToCart: (MakeupProduct) -> Unit
) {
    val shades by viewModel.shades.observeAsState(emptyList())
    val selectedShade by viewModel.selectedShade.observeAsState()
    val products by viewModel.products.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // shade selection grid
        ShadeSelectionSection(
            shades = shades,
            selectedShade = selectedShade,
            onShadeSelected = { viewModel.onShadeSelected(it) }
        )

        Spacer(Modifier.height(32.dp))

        // displays selected shade with color circle
        selectedShade?.let {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Your Shade", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(it.hexCode)))
                            .border(1.dp, Color.LightGray, CircleShape)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = it.description ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // products recommendations : need to pass these parameters allowing for user action
        ProductRecommendationSection(
            products = products,
            onAddToCart = onAddToCart,
            allApiProducts = allApiProducts,
            likedProductIds = likedProductIds,
            likedLocalProductIds = likedLocalProductIds,
            onToggleLocalLike = onToggleLocalLike
        )
    }
}
