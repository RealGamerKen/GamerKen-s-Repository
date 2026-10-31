package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkGamingColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = CyberCyanDark,
    primaryContainer = Color(0xFF0D3742),
    onPrimaryContainer = CyberCyan,
    secondary = ElectricPurple,
    onSecondary = ElectricPurpleDark,
    secondaryContainer = Color(0xFF2E1065),
    onSecondaryContainer = ElectricPurple,
    tertiary = NeonGreen,
    onTertiary = Color(0xFF022C19),
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = NeonGreen,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceContainerHighest = DarkSurfaceElevated,
    outline = DarkCardBorder,
    outlineVariant = Color(0xFF1B2435),
    error = NeonRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Gaming dashboard is tailored dark-mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkGamingColorScheme,
        typography = Typography,
        content = content
    )
}
