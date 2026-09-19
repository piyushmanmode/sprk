package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SparkWarmAmber,
    onPrimary = Color.White,
    primaryContainer = SparkDeepOrange,
    onPrimaryContainer = Color.White,
    secondary = SparkOrange,
    onSecondary = Color.White,
    background = Color(0xFF140D09),
    surface = Color(0xFF221610),
    onBackground = Color(0xFFFFF6F0),
    onSurface = Color(0xFFFFF6F0),
    surfaceVariant = Color(0xFF2E1F18),
    onSurfaceVariant = Color(0xFFDECBC3),
    outline = Color(0xFF5A4439)
)

private val LightColorScheme = lightColorScheme(
    primary = SparkOrange,
    onPrimary = Color.White,
    primaryContainer = SparkPeachLight,
    onPrimaryContainer = SparkDeepOrange,
    secondary = SparkFlame,
    onSecondary = Color.White,
    background = SparkCanvas,
    surface = SparkSurface,
    onBackground = SparkTextPrimary,
    onSurface = SparkTextPrimary,
    surfaceVariant = Color(0xFFFBF4EE),
    onSurfaceVariant = SparkTextSecondary,
    outline = SparkCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep signature Spark brand palette by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
