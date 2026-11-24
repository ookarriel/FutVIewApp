package com.example.futview.data.repository

import com.example.futview.data.Match
import com.example.futview.data.Stadium
import com.example.futview.data.StandingRow
import com.example.futview.data.SystemUser
import com.example.futview.data.Team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * TEMPORAL (Fase 1): implementaciones vacías para que la app compile y las pantallas
 * muestren sus estados vacíos con la arquitectura final ya conectada.
 *
 * Fase 3: reemplazarlas por las implementaciones que leen de Firestore;
 * el cambio ocurre solo en [com.example.futview.data.AppContainer].
 */

class EmptyTeamRepository : TeamRepository {
    override fun observeTeams(): Flow<List<Team>> = flowOf(emptyList())
    override fun observeTeam(id: String): Flow<Team?> = flowOf(null)
}

class EmptyStadiumRepository : StadiumRepository {
    override fun observeStadiums(): Flow<List<Stadium>> = flowOf(emptyList())
    override fun observeStadium(id: String): Flow<Stadium?> = flowOf(null)
}

class EmptyMatchRepository : MatchRepository {
    override fun observeUpcomingMatches(): Flow<List<Match>> = flowOf(emptyList())
    override fun observeResults(): Flow<List<Match>> = flowOf(emptyList())
    override fun observeMatch(id: String): Flow<Match?> = flowOf(null)
    override fun observeStandings(): Flow<List<StandingRow>> = flowOf(emptyList())
}

class EmptyUserRepository : UserRepository {
    override fun observeUsers(): Flow<List<SystemUser>> = flowOf(emptyList())
    override fun observeUserRole(uid: String): Flow<String?> = flowOf(null)
    override suspend fun ensureUserDoc(uid: String, name: String, email: String): Result<Unit> =
        Result.success(Unit)
}
