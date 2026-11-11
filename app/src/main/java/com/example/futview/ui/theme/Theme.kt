package com.example.futview.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FutViewColorScheme = lightColorScheme(
    primary = Ink,
    onPrimary = Color.White,
    primaryContainer = FieldStrong,
    onPrimaryContainer = TextPrimary,
    secondary = TextSecondary,
    onSecondary = Color.White,
    secondaryContainer = Field,
    onSecondaryContainer = TextPrimary,
    tertiary = TextSecondary,
    onTertiary = Color.White,
    background = Page,
    onBackground = TextPrimary,
    surface = Card,
    onSurface = TextPrimary,
    surfaceVariant = Field,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = Card,
    surfaceContainerHigh = Field,
    surfaceContainerLow = Card,
    outline = Border,
    outlineVariant = Border,
    error = Color(0xFFB3261E),
    onError = Color.White
)

@Composable
fun FutViewTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FutViewColorScheme,
        typography = FutViewTypography,
        content = content
    )
}
