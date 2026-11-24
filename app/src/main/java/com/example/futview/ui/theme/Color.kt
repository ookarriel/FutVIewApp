package com.example.futview.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Paleta principal de FutView (blanco, negro y grises). */
val Ink = Color(0xFF101010)
val InkSoft = Color(0xFF1C1C1C)
val InkElevated = Color(0xFF3A3A3A)

val TextOnDark = Color(0xFFF7F7F7)
val TextOnDarkMuted = Color(0xFFB8B8B8)

@Immutable
data class FutViewPalette(
    val page: Color,
    val card: Color,
    val field: Color,
    val fieldStrong: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val crestBackground: Color,
    val success: Color,
    val successSoft: Color,
    val isDark: Boolean
)

internal val LightPalette = FutViewPalette(
    page = Color(0xFFF3F3F3),
    card = Color(0xFFFFFFFF),
    field = Color(0xFFF1F1F1),
    fieldStrong = Color(0xFFE9E9E9),
    border = Color(0xFFE5E5E5),
    textPrimary = Color(0xFF111111),
    textSecondary = Color(0xFF6E6E6E),
    textTertiary = Color(0xFF9B9B9B),
    crestBackground = Color(0xFFE8E8E8),
    success = Color(0xFF1E7A4D),
    successSoft = Color(0xFFE8F3EC),
    isDark = false
)

internal val DarkPalette = FutViewPalette(
    page = Color(0xFF0D0D0D),
    card = Color(0xFF191919),
    field = Color(0xFF232323),
    fieldStrong = Color(0xFF2E2E2E),
    border = Color(0xFF333333),
    textPrimary = Color(0xFFF4F4F4),
    textSecondary = Color(0xFFA3A3A3),
    textTertiary = Color(0xFF6F6F6F),
    crestBackground = Color(0xFF2A2A2A),
    success = Color(0xFF57D98A),
    successSoft = Color(0xFF17341F),
    isDark = true
)

internal val LocalFutViewPalette = staticCompositionLocalOf { LightPalette }

/** Indica si la app está usando la paleta oscura (para barras del sistema). */
val IsDarkTheme: Boolean
    @Composable get() = LocalFutViewPalette.current.isDark

val Page: Color
    @Composable get() = LocalFutViewPalette.current.page

val Card: Color
    @Composable get() = LocalFutViewPalette.current.card

val Field: Color
    @Composable get() = LocalFutViewPalette.current.field

val FieldStrong: Color
    @Composable get() = LocalFutViewPalette.current.fieldStrong

val Border: Color
    @Composable get() = LocalFutViewPalette.current.border

val TextPrimary: Color
    @Composable get() = LocalFutViewPalette.current.textPrimary

val TextSecondary: Color
    @Composable get() = LocalFutViewPalette.current.textSecondary

val TextTertiary: Color
    @Composable get() = LocalFutViewPalette.current.textTertiary

val CrestBackground: Color
    @Composable get() = LocalFutViewPalette.current.crestBackground

val Success: Color
    @Composable get() = LocalFutViewPalette.current.success

val SuccessSoft: Color
    @Composable get() = LocalFutViewPalette.current.successSoft
