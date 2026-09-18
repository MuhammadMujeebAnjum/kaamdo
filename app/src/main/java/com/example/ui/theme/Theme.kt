package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = KaamNavy,
    onPrimary = Color.White,
    primaryContainer = KaamGreenLight,
    onPrimaryContainer = KaamNavy,
    secondary = KaamGreen,
    onSecondary = Color.White,
    secondaryContainer = KaamGreenLight,
    onSecondaryContainer = KaamGreenDark,
    tertiary = KaamAmber,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight,
    outlineVariant = BorderSubtle,
    error = KaamRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = KaamGreen,
    onPrimary = KaamNavy,
    primaryContainer = KaamNavyLight,
    onPrimaryContainer = KaamGreenLight,
    secondary = KaamGreenLight,
    onSecondary = KaamNavy,
    secondaryContainer = KaamNavy,
    onSecondaryContainer = Color.White,
    tertiary = KaamAmber,
    onTertiary = Color.Black,
    background = Color(0xFF08101D),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF0F1A2A),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF18263D),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF263954),
    error = KaamRed,
    onError = Color.White
)

@Composable
fun KaamGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our handcrafted KaamGo brand colors
    content: @Composable () -> Unit,
) {
    KaamGoTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
