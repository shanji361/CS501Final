package com.example.beautyapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFF472B6),
    secondary = Color(0xFFEC4899),
    tertiary = Color(0xFFFCE7F3),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF1F2937),
    onBackground = Color(0xFF1F2937),
    onSurface = Color(0xFF1F2937),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFF472B6),
    secondary = Color(0xFFEC4899),
    tertiary = Color(0xFFFCE7F3),
)

@Composable
fun BeautyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
