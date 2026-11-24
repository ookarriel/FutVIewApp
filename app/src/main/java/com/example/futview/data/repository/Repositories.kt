package com.example.futview.data.repository

import com.example.futview.data.Match
import com.example.futview.data.Stadium
import com.example.futview.data.StandingRow
import com.example.futview.data.SystemUser
import com.example.futview.data.Team
import kotlinx.coroutines.flow.Flow

/**
 * Contratos de la capa de datos. Las pantallas/ViewModels no conocen Firestore:
 * solo consumen estos flujos.
 */

interface TeamRepository {
    fun observeTeams(): Flow<List<Team>>
    fun observeTeam(id: String): Flow<Team?>
}

interface StadiumRepository {
    fun observeStadiums(): Flow<List<Stadium>>
    fun observeStadium(id: String): Flow<Stadium?>
}

interface MatchRepository {
    fun observeUpcomingMatches(): Flow<List<Match>>
    fun observeResults(): Flow<List<Match>>
    fun observeMatch(id: String): Flow<Match?>
    fun observeStandings(): Flow<List<StandingRow>>
}

interface UserRepository {
    fun observeUsers(): Flow<List<SystemUser>>
    /** Rol del usuario (colección `users`): "Admin", "Editor" o "Usuario". */
    fun observeUserRole(uid: String): Flow<String?>
    /** Crea el documento del usuario en Firestore si no existe (al registrarse). */
    suspend fun ensureUserDoc(uid: String, name: String, email: String): Result<Unit>
}
