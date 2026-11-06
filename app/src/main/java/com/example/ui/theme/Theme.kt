package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NeonEmerald,
    onPrimary = BgDark,
    primaryContainer = SurfaceCard,
    onPrimaryContainer = EmeraldLight,
    secondary = CyberCyan,
    onSecondary = BgDark,
    secondaryContainer = SurfaceCard,
    onSecondaryContainer = CyanLight,
    tertiary = WarningAmber,
    onTertiary = BgDark,
    background = BgDark,
    onBackground = TextWhite,
    surface = SurfaceDark,
    onSurface = TextWhite,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextMuted,
    error = FaultRed,
    onError = TextWhite,
    outline = BorderDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
