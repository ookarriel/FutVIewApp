package com.example.futview.data.repository

import com.example.futview.data.model.AuthUser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implementación de [AuthRepository] sobre Firebase Authentication.
 * La sesión persiste automáticamente entre ejecuciones de la app.
 */
class FirebaseAuthRepository(
    private val userRepository: UserRepository
) : AuthRepository {

    private val auth get() = Firebase.auth

    override fun observeAuth(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.toAuthUser())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun signOut() {
        auth.signOut()
    }

    override suspend fun signIn(email: String, password: String): Result<AuthUser> = authCall {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        result.user?.toAuthUser() ?: error("No se pudo iniciar sesión.")
    }

    override suspend fun signUp(name: String, email: String, password: String): Result<AuthUser> = authCall {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: error("No se pudo crear la cuenta.")
        user.updateProfile(userProfileChangeRequest { displayName = name }).await()
        user.sendEmailVerification().await()
        // Documento `users` con rol por defecto.
        userRepository.ensureUserDoc(user.uid, name, email)
        user.toAuthUser()
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = authCall {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun changePassword(newPassword: String): Result<Unit> = authCall {
        val user = auth.currentUser ?: error("No hay sesión activa.")
        user.updatePassword(newPassword).await()
    }

    override suspend fun resendLink(email: String): Result<Unit> = authCall {
        val user = auth.currentUser
        if (user != null) {
            // Sesión activa: reenvía el correo de verificación de la cuenta.
            user.sendEmailVerification().await()
        } else {
            // Sin sesión: reenvía el enlace de recuperación de contraseña.
            auth.sendPasswordResetEmail(email).await()
        }
    }

    private fun FirebaseUser.toAuthUser() = AuthUser(
        uid = uid,
        email = email,
        displayName = displayName,
        emailVerified = isEmailVerified
    )
}

/** Envuelve llamadas suspendidas de Firebase sin tragar la cancelación de corrutinas. */
private suspend fun <T> authCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (throwable: Throwable) {
    Result.failure(throwable)
}
