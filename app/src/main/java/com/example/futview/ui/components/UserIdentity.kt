package com.example.futview.ui.components

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class UserIdentity(
    val displayName: String,
    val initials: String
)

val LocalUserIdentity = staticCompositionLocalOf {
    UserIdentity(displayName = "Usuario", initials = "U")
}
