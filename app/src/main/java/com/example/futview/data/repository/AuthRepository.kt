package com.example.futview.data.repository

import com.example.futview.data.model.AuthUser
import kotlinx.coroutines.flow.Flow

/** Contrato de autenticación. La sesión se emite al iniciar la app y con cada cambio. */
interface AuthRepository {
    fun observeAuth(): Flow<AuthUser?>
    fun signOut()
    suspend fun signIn(email: String, password: String): Result<AuthUser>
    suspend fun signUp(name: String, email: String, password: String): Result<AuthUser>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun changePassword(newPassword: String): Result<Unit>
    suspend fun resendLink(email: String): Result<Unit>
}

/** Traduce errores de Firebase Auth a mensajes en español para la UI. */
fun authErrorMessage(throwable: Throwable): String {
    val code = (throwable as? com.google.firebase.auth.FirebaseAuthException)?.errorCode
    return when (code) {
        "ERROR_INVALID_EMAIL" -> "El correo electrónico no es válido."
        "ERROR_WRONG_PASSWORD",
        "ERROR_INVALID_CREDENTIAL",
        "ERROR_INVALID_LOGIN_CREDENTIALS" -> "Correo o contraseña incorrectos."
        "ERROR_USER_NOT_FOUND" -> "No existe una cuenta con ese correo."
        "ERROR_EMAIL_ALREADY_IN_USE" -> "Ya existe una cuenta con ese correo."
        "ERROR_WEAK_PASSWORD" -> "La contraseña es muy débil. Usa al menos 6 caracteres."
        "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Vuelve a intentarlo más tarde."
        "ERROR_NETWORK_REQUEST_FAILED" -> "Sin conexión. Revisa tu red e inténtalo de nuevo."
        "ERROR_REQUIRES_RECENT_LOGIN" -> "Tu sesión es antigua. Cierra sesión y vuelve a entrar para cambiar la contraseña."
        else -> throwable.message?.take(140) ?: "No se pudo completar la operación."
    }
}
