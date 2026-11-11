package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TvDarkColorScheme = darkColorScheme(
    primary = TvPrimary,
    onPrimary = TvOnPrimary,
    primaryContainer = TvPrimaryContainer,
    onPrimaryContainer = TvOnPrimaryContainer,
    secondary = TvSecondary,
    onSecondary = TvOnSecondary,
    secondaryContainer = TvSecondaryContainer,
    onSecondaryContainer = TvOnSecondaryContainer,
    tertiary = TvTertiary,
    onTertiary = TvOnTertiary,
    tertiaryContainer = TvTertiaryContainer,
    onTertiaryContainer = TvOnTertiaryContainer,
    background = TvBackground,
    onBackground = TvTextPrimary,
    surface = TvSurface,
    onSurface = TvTextPrimary,
    surfaceVariant = TvSurfaceVariant,
    onSurfaceVariant = TvTextSecondary,
    error = TvError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For Android TV, a rich dark cinema theme is always preferred for 10-foot viewing
    MaterialTheme(
        colorScheme = TvDarkColorScheme,
        typography = Typography,
        content = content
    )
}
