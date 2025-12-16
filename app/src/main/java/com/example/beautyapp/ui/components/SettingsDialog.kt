package com.example.beautyapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beautyapp.data.ColorBlindMode
import com.example.beautyapp.data.FontSize
import com.example.beautyapp.data.Settings

// Main settings dialog that shows all accessibility options
@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    settings: Settings,
    onUpdateDarkMode: (Boolean) -> Unit,
    onUpdateFontSize: (FontSize) -> Unit,
    onUpdateColorBlindMode: (ColorBlindMode) -> Unit,
    onUpdateReduceAnimations: (Boolean) -> Unit
) {
    // State for showing/hiding the font size dropdown menu
    var fontSizeExpanded by remember { mutableStateOf(false) }

    // State for showing/hiding the color blind mode dropdown menu
    var colorBlindExpanded by remember { mutableStateOf(false) }

    // AlertDialog provides the popup window with dismiss behavior
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            // Dialog header with settings icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color(0xFFF472B6)  // Pink color matching app theme
                )
                Text(
                    text = "Accessibility Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface  // FIXED: Auto-switches with theme
                )
            }
        },
        text = {
            // Main content area with all settings options
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // SETTING 1: Dark Mode Toggle
                SettingItem(
                    icon = if (settings.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    title = "Theme",
                    description = if (settings.isDarkMode) "Dark mode" else "Light mode"
                ) {
                    Switch(
                        checked = settings.isDarkMode,
                        onCheckedChange = onUpdateDarkMode,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFF472B6),
                            checkedTrackColor = Color(0xFFF472B6).copy(alpha = 0.5f)
                        )
                    )
                }

                HorizontalDivider()  // FIXED: Changed from Divider() to HorizontalDivider()

                // SETTING 2: Font Size Dropdown
                SettingItem(
                    icon = Icons.Default.FormatSize,
                    title = "Font Size",
                    description = settings.fontSize.name.lowercase().replaceFirstChar { it.uppercase() }
                ) {
                    Box {
                        TextButton(onClick = { fontSizeExpanded = true }) {
                            Text(
                                text = settings.fontSize.name,
                                color = Color(0xFFF472B6)  // Pink is fine (brand color)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expand",
                                tint = Color(0xFFF472B6)
                            )
                        }

                        DropdownMenu(
                            expanded = fontSizeExpanded,
                            onDismissRequest = { fontSizeExpanded = false }
                        ) {
                            FontSize.values().forEach { fontSize ->
                                DropdownMenuItem(
                                    text = { Text(fontSize.name) },
                                    onClick = {
                                        onUpdateFontSize(fontSize)
                                        fontSizeExpanded = false
                                    },
                                    trailingIcon = if (settings.fontSize == fontSize) {
                                        { Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFF472B6)) }
                                    } else null
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()  // FIXED: Changed from Divider()

                // SETTING 3: Color Blind Mode Dropdown
                SettingItem(
                    icon = Icons.Default.Palette,
                    title = "Color Blind Mode",
                    description = when (settings.colorBlindMode) {
                        ColorBlindMode.NONE -> "None"
                        ColorBlindMode.PROTANOPIA -> "Protanopia (Red-weak)"
                        ColorBlindMode.DEUTERANOPIA -> "Deuteranopia (Green-weak)"
                    }
                ) {
                    Box {
                        TextButton(onClick = { colorBlindExpanded = true }) {
                            Text(
                                text = settings.colorBlindMode.name,
                                color = Color(0xFFF472B6)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expand",
                                tint = Color(0xFFF472B6)
                            )
                        }

                        DropdownMenu(
                            expanded = colorBlindExpanded,
                            onDismissRequest = { colorBlindExpanded = false }
                        ) {
                            ColorBlindMode.values().forEach { mode ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(mode.name)
                                            if (mode != ColorBlindMode.NONE) {
                                                Text(
                                                    text = when (mode) {
                                                        ColorBlindMode.PROTANOPIA -> "Red-weak"
                                                        ColorBlindMode.DEUTERANOPIA -> "Green-weak"
                                                        else -> ""
                                                    },
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant  // FIXED: Auto-switches
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        onUpdateColorBlindMode(mode)
                                        colorBlindExpanded = false
                                    },
                                    trailingIcon = if (settings.colorBlindMode == mode) {
                                        { Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFF472B6)) }
                                    } else null
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()  // FIXED: Changed from Divider()

                // SETTING 4: Reduce Animations Toggle
                SettingItem(
                    icon = Icons.Default.Animation,
                    title = "Reduce Animations",
                    description = if (settings.reduceAnimations) "Animations disabled" else "Animations enabled"
                ) {
                    Switch(
                        checked = settings.reduceAnimations,
                        onCheckedChange = onUpdateReduceAnimations,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFF472B6),
                            checkedTrackColor = Color(0xFFF472B6).copy(alpha = 0.5f)
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFF472B6)
                )
            ) {
                Text("Close")
            }
        }
    )
}

// Reusable component for each setting row
@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    action: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Icon and text
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Setting icon
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,  // FIXED: Auto-switches with theme
                modifier = Modifier.size(24.dp)
            )

            // Setting title and description
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface  // FIXED: Auto-switches (dark in light mode, light in dark mode)
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant  // FIXED: Auto-switches (gray in light, lighter gray in dark)
                )
            }
        }

        // Right side: The action component
        action()
    }
}