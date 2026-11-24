package com.example.futview.data.repository

import com.example.futview.data.Match
import com.example.futview.data.MatchStatus
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
    suspend fun createTeam(name: String, short: String, city: String, stadiumId: String): Result<Unit>
    suspend fun updateTeam(id: String, name: String, short: String, city: String, stadiumId: String): Result<Unit>
    suspend fun deleteTeam(id: String): Result<Unit>
}

interface StadiumRepository {
    fun observeStadiums(): Flow<List<Stadium>>
    fun observeStadium(id: String): Flow<Stadium?>
    suspend fun createStadium(
        name: String,
        city: String,
        capacity: Int,
        address: String,
        latitude: Double?,
        longitude: Double?
    ): Result<Unit>

    suspend fun updateStadium(
        id: String,
        name: String,
        city: String,
        capacity: Int,
        address: String,
        latitude: Double?,
        longitude: Double?
    ): Result<Unit>

    suspend fun deleteStadium(id: String): Result<Unit>
}

interface MatchRepository {
    fun observeUpcomingMatches(): Flow<List<Match>>
    fun observeResults(): Flow<List<Match>>
    fun observeMatch(id: String): Flow<Match?>
    fun observeStandings(): Flow<List<StandingRow>>
    suspend fun createMatch(
        dayLabel: String,
        shortDate: String,
        matchday: Int,
        time: String,
        homeTeamId: String,
        awayTeamId: String,
        stadiumName: String,
        status: MatchStatus
    ): Result<Unit>

    suspend fun deleteMatch(id: String): Result<Unit>
    suspend fun updateMatchResult(id: String, homeGoals: Int, awayGoals: Int, status: MatchStatus): Result<Unit>
}

interface UserRepository {
    fun observeUsers(): Flow<List<SystemUser>>
    /** Rol del usuario (colección `users`): "Admin", "Editor" o "Usuario". */
    fun observeUserRole(uid: String): Flow<String?>
    /** Crea el documento del usuario en Firestore si no existe (al registrarse). */
    suspend fun ensureUserDoc(uid: String, name: String, email: String): Result<Unit>
    suspend fun updateRole(uid: String, role: String): Result<Unit>
    suspend fun removeUserDoc(uid: String): Result<Unit>
    suspend fun createInvite(email: String, role: String): Result<Unit>
}

/**
 * Favoritos por usuario en `users/{uid}/favorites`. Los ids se guardan con prefijo de tipo:
 * "team:<id>", "match:<id>" o "stadium:<id>".
 */
interface FavoritesRepository {
    fun observeFavorites(uid: String): Flow<Set<String>>
    suspend fun toggle(uid: String, favoriteId: String): Result<Unit>
}
