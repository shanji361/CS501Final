package com.example.beautyapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NavItem(
    val id: String,
    val icon: ImageVector,
    val label: String,
    val isCenter: Boolean = false
)

@Composable
fun BottomNavBar(
    activeTab: String,
    onTabChange: (String) -> Unit,
    cartCount: Int
) {
    val navItems = listOf(
        NavItem("home", Icons.Default.Home, "Home"),
        NavItem("clean", Icons.Default.Eco, "Clean"),
        NavItem("scan", Icons.Default.CameraAlt, "AI Scan", isCenter = true),
        NavItem("cart", Icons.Default.ShoppingCart, "Cart"),
        NavItem("profile", Icons.Default.Person, "Profile")
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            navItems.forEach { item ->
                if (item.isCenter) {
                    // Center AI Scan Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTabChange(item.id) }
                    ) {
                        FloatingActionButton(
                            onClick = { onTabChange(item.id) },
                            containerColor = Color(0xFFF472B6),
                            modifier = Modifier
                                .size(56.dp)
                                .offset(y = (-24).dp),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Regular Nav Items - ADD CLICKABLE HERE!
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTabChange(item.id) }  // THIS IS THE KEY FIX!
                            .padding(vertical = 8.dp)
                    ) {
                        Box {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (activeTab == item.id) Color(0xFFEC4899) else Color(0xFF9CA3AF),
                                modifier = Modifier.size(20.dp)
                            )

                            // Cart Badge
                            if (item.id == "cart" && cartCount > 0) {
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 4.dp, y = (-4).dp)
                                ) {
                                    Text(
                                        text = if (cartCount > 9) "9+" else cartCount.toString(),
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            color = if (activeTab == item.id) Color(0xFFEC4899) else Color(0xFF6B7280),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}