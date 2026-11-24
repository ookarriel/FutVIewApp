package com.example.futview.data.repository

import com.example.futview.data.SystemUser
import com.example.futview.data.model.UserDoc
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

/**
 * Implementación de [UserRepository] sobre Firestore (colección `users`).
 * Conectada en la Fase 2 para el rol del usuario; la Fase 3 extiende
 * la lectura del directorio de usuarios del panel admin.
 */
class FirestoreUserRepository : UserRepository {

    private val usersCollection get() = Firebase.firestore.collection("users")

    override fun observeUsers(): Flow<List<SystemUser>> = callbackFlow {
        val registration = usersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val users = snapshot?.documents
                ?.mapNotNull { it.toObject(UserDoc::class.java)?.toModel() }
                .orEmpty()
            trySend(users)
        }
        awaitClose { registration.remove() }
    }

    override fun observeUserRole(uid: String): Flow<String?> =
        if (uid.isBlank()) {
            flowOf(null)
        } else {
            callbackFlow {
                val registration = usersCollection.document(uid).addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Sin reglas configuradas o sin conexión: se asume rol base sin bloquear la sesión.
                        trySend(null)
                        return@addSnapshotListener
                    }
                    trySend(snapshot?.toObject(UserDoc::class.java)?.role)
                }
                awaitClose { registration.remove() }
            }
        }

    override suspend fun ensureUserDoc(uid: String, name: String, email: String): Result<Unit> = try {
        usersCollection
            .document(uid)
            .set(UserDoc(name = name, email = email, role = ROLE_USER))
            .await()
        Result.success(Unit)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }

    companion object {
        const val ROLE_ADMIN = "Admin"
        const val ROLE_EDITOR = "Editor"
        const val ROLE_USER = "Usuario"
    }
}
