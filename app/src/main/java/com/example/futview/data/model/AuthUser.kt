package com.example.futview.data.model

/** Usuario autenticado en Firebase. */
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val emailVerified: Boolean
)

/** Iniciales a partir del nombre (p. ej. "Ana García" → "AG"). */
fun initialsOf(name: String?): String =
    name.orEmpty()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")
