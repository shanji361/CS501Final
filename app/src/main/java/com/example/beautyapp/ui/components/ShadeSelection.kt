package com.example.beautyapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.beautyapp.data.entities.Shade
//this is UI for ShadeSelectionSection shown in MainActivity/ replaces AR screen
@Composable
fun ShadeSelectionSection(
    shades: List<Shade>,
    selectedShade: Shade?,
    onShadeSelected: (Shade) -> Unit
) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            "Select Your Shade",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            //showing shade circle in a row of 4
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.height(300.dp)
        ) {
            items(shades) { shade ->
                ShadeCircle(
                    hex = shade.hexCode,
                    //passing shade name and description
                    name=shade.description,
                    isSelected = (selectedShade?.shadeId == shade.shadeId),
                    onClick = { onShadeSelected(shade) }
                )
            }
        }
    }
}
