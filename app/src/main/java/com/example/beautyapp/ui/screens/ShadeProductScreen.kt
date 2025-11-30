package com.example.beautyapp.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.beautyapp.ui.components.ShadeSelectionSection
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import com.example.beautyapp.ui.components.ProductRecommendationSection
import com.example.beautyapp.viewmodel.ShadeProductViewModel

@Composable
fun ShadeProductScreen(
    viewModel: ShadeProductViewModel
) {
    val shades by viewModel.shades.observeAsState(emptyList())
    val selectedShade by viewModel.selectedShade.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            //this allows us to see lipliner
            .verticalScroll(rememberScrollState())    ) {

        ShadeSelectionSection(
            shades = shades,
            selectedShade = selectedShade,
            onShadeSelected = { viewModel.onShadeSelected(it) }
        )

        Spacer(Modifier.height(24.dp))

        selectedShade?.let {
            Text("Your Shade", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(it.hexCode)))
            )
        }

        Spacer(Modifier.height(24.dp))

        val products by viewModel.products.observeAsState(emptyList())

        ProductRecommendationSection(products = products)

    }
}
