package com.example.futview.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val FutViewLightColors = lightColorScheme(
    primary = Ink,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = LightPalette.fieldStrong,
    onPrimaryContainer = LightPalette.textPrimary,
    secondary = LightPalette.textSecondary,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = LightPalette.field,
    onSecondaryContainer = LightPalette.textPrimary,
    tertiary = LightPalette.textSecondary,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    background = LightPalette.page,
    onBackground = LightPalette.textPrimary,
    surface = LightPalette.card,
    onSurface = LightPalette.textPrimary,
    surfaceVariant = LightPalette.field,
    onSurfaceVariant = LightPalette.textSecondary,
    surfaceContainer = LightPalette.card,
    surfaceContainerHigh = LightPalette.field,
    surfaceContainerLow = LightPalette.card,
    outline = LightPalette.border,
    outlineVariant = LightPalette.border,
    error = androidx.compose.ui.graphics.Color(0xFFB3261E),
    onError = androidx.compose.ui.graphics.Color.White
)

private val FutViewDarkColors = darkColorScheme(
    primary = TextOnDark,
    onPrimary = Ink,
    primaryContainer = DarkPalette.fieldStrong,
    onPrimaryContainer = DarkPalette.textPrimary,
    secondary = DarkPalette.textSecondary,
    onSecondary = Ink,
    secondaryContainer = DarkPalette.field,
    onSecondaryContainer = DarkPalette.textPrimary,
    tertiary = DarkPalette.textSecondary,
    onTertiary = Ink,
    background = DarkPalette.page,
    onBackground = DarkPalette.textPrimary,
    surface = DarkPalette.card,
    onSurface = DarkPalette.textPrimary,
    surfaceVariant = DarkPalette.field,
    onSurfaceVariant = DarkPalette.textSecondary,
    surfaceContainer = DarkPalette.card,
    surfaceContainerHigh = DarkPalette.field,
    surfaceContainerLow = DarkPalette.card,
    outline = DarkPalette.border,
    outlineVariant = DarkPalette.border,
    error = androidx.compose.ui.graphics.Color(0xFFFF6B6B),
    onError = Ink
)

@Composable
fun FutViewTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val palette = if (darkTheme) DarkPalette else LightPalette
    CompositionLocalProvider(LocalFutViewPalette provides palette) {
        MaterialTheme(
            colorScheme = if (darkTheme) FutViewDarkColors else FutViewLightColors,
            typography = FutViewTypography,
            content = content
        )
    }
}
