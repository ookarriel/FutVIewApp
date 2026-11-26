package com.example.futview.data.repository

import com.example.futview.data.Match
import com.example.futview.data.MatchStatus
import com.example.futview.data.Stadium
import com.example.futview.data.StandingRow
import com.example.futview.data.Team
import com.example.futview.data.model.MatchDoc
import com.example.futview.data.model.StadiumDoc
import com.example.futview.data.model.StandingsDoc
import com.example.futview.data.model.TeamDoc
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

/**
 * Implementaciones de repositorio sobre Firestore.
 * Colecciones: `teams`, `stadiums`, `matches` y `users/{uid}/favorites`.
 */

class FirestoreTeamRepository : TeamRepository {

    private val collection get() = Firebase.firestore.collection("teams")

    override fun observeTeams(): Flow<List<Team>> = callbackFlow {
        val registration = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val teams = snapshot?.documents
                ?.mapNotNull { it.toObject(TeamDoc::class.java)?.toModel(it.id) }
                .orEmpty()
                .sortedBy { it.name }
            trySend(teams)
        }
        awaitClose { registration.remove() }
    }

    override fun observeTeam(id: String): Flow<Team?> =
        if (id.isBlank()) flowOf(null) else callbackFlow {
            val registration = collection.document(id).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(TeamDoc::class.java)?.toModel(snapshot.id))
            }
            awaitClose { registration.remove() }
        }

    override suspend fun createTeam(name: String, short: String, city: String, stadiumId: String): Result<Unit> =
        runFirestore {
            collection.document().set(
                TeamDoc(name = name, short = short, city = city, stadiumId = stadiumId)
            )
        }

    override suspend fun updateTeam(id: String, name: String, short: String, city: String, stadiumId: String): Result<Unit> =
        runFirestore {
            collection.document(id).set(
                TeamDoc(name = name, short = short, city = city, stadiumId = stadiumId)
            )
        }

    override suspend fun deleteTeam(id: String): Result<Unit> = runFirestore {
        collection.document(id).delete()
    }
}

class FirestoreStadiumRepository : StadiumRepository {

    private val collection get() = Firebase.firestore.collection("stadiums")

    override fun observeStadiums(): Flow<List<Stadium>> = callbackFlow {
        val registration = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val stadiums = snapshot?.documents
                ?.mapNotNull { it.toObject(StadiumDoc::class.java)?.toModel(it.id) }
                .orEmpty()
                .sortedBy { it.name }
            trySend(stadiums)
        }
        awaitClose { registration.remove() }
    }

    override fun observeStadium(id: String): Flow<Stadium?> =
        if (id.isBlank()) flowOf(null) else callbackFlow {
            val registration = collection.document(id).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(StadiumDoc::class.java)?.toModel(snapshot.id))
            }
            awaitClose { registration.remove() }
        }

    override suspend fun createStadium(
        name: String,
        city: String,
        capacity: Int,
        address: String,
        latitude: Double?,
        longitude: Double?
    ): Result<Unit> = runFirestore {
        collection.document().set(stadiumDoc(name, city, capacity, address, latitude, longitude))
    }

    override suspend fun updateStadium(
        id: String,
        name: String,
        city: String,
        capacity: Int,
        address: String,
        latitude: Double?,
        longitude: Double?
    ): Result<Unit> = runFirestore {
        collection.document(id).set(stadiumDoc(name, city, capacity, address, latitude, longitude))
    }

    override suspend fun deleteStadium(id: String): Result<Unit> = runFirestore {
        collection.document(id).delete()
    }

    private fun stadiumDoc(
        name: String,
        city: String,
        capacity: Int,
        address: String,
        latitude: Double?,
        longitude: Double?
    ) = StadiumDoc(
        name = name,
        city = city,
        capacity = capacity.toLong(),
        address = address,
        location = if (latitude != null && longitude != null) GeoPoint(latitude, longitude) else null
    )
}

class FirestoreMatchRepository : MatchRepository {

    private val collection get() = Firebase.firestore.collection("matches")

    override fun observeUpcomingMatches(): Flow<List<Match>> = observeMatches(finalized = false)

    override fun observeResults(): Flow<List<Match>> = observeMatches(finalized = true)

    override fun observeMatch(id: String): Flow<Match?> =
        if (id.isBlank()) {
            flowOf(null)
        } else {
            combine(
                observeTeamsInternal(),
                callbackFlow {
                    val registration = collection.document(id).addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }
                        trySend(snapshot?.toObject(MatchDoc::class.java))
                    }
                    awaitClose { registration.remove() }
                }
            ) { teams, doc ->
                doc?.toModel(id, teams.associateBy { it.id })
            }
        }

    override fun observeStandings(): Flow<List<StandingRow>> = combine(
        callbackFlow {
            val registration = Firebase.firestore.collection("standings").document("2026")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(null)
                        return@addSnapshotListener
                    }
                    trySend(snapshot?.toObject(StandingsDoc::class.java))
                }
            awaitClose { registration.remove() }
        },
        observeTeamsInternal(),
        observeResults()
    ) { standingsDoc, teams, results ->
        // Si existe la tabla oficial (ESPN), se usa; si no, se calcula desde los resultados.
        standingsDoc?.rows?.mapIndexed { index, row ->
            val normalized = normalizeEntityName(row.teamName)
            val team = teams.firstOrNull { normalizeEntityName(it.name) == normalized }
                ?: Team(id = "foreign-${index + 1}", name = row.teamName, short = row.teamShort, city = "", stadium = "", lastResult = "", nextMatch = "")
            StandingRow(index + 1, team, row.played.toInt(), row.points.toInt(), row.goalDifference.toInt())
        }?.takeIf { it.isNotEmpty() }
            ?: computeStandings(teams, results)
    }

    override suspend fun createMatch(
        dayLabel: String,
        shortDate: String,
        matchday: Int,
        time: String,
        homeTeamId: String,
        awayTeamId: String,
        stadiumName: String,
        status: MatchStatus
    ): Result<Unit> = runFirestore {
        collection.document().set(
            MatchDoc(
                dayLabel = dayLabel,
                shortDate = shortDate,
                matchday = matchday.toLong(),
                time = time,
                status = status.name,
                homeTeamId = homeTeamId,
                awayTeamId = awayTeamId,
                stadiumName = stadiumName
            )
        )
    }

    override suspend fun deleteMatch(id: String): Result<Unit> = runFirestore {
        collection.document(id).delete()
    }

    override suspend fun updateMatchResult(id: String, homeGoals: Int, awayGoals: Int, status: MatchStatus): Result<Unit> =
        runFirestore {
            collection.document(id).update(
                mapOf(
                    "homeGoals" to homeGoals.toLong(),
                    "awayGoals" to awayGoals.toLong(),
                    "status" to status.name
                )
            )
        }

    /** Escucha `teams` para poder mapear los ids de los partidos a modelos completos. */
    private fun observeMatches(finalized: Boolean): Flow<List<Match>> = combine(
        observeTeamsInternal(),
        callbackFlow {
            val registration = collection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val docs = snapshot?.documents?.map { it.id to it.toObject(MatchDoc::class.java) }.orEmpty()
                trySend(docs)
            }
            awaitClose { registration.remove() }
        }
    ) { teams, matchEntries ->
        val teamsById = teams.associateBy { it.id }
        matchEntries.mapNotNull { (id, doc) -> doc?.toModel(id, teamsById) }
            .filter { match ->
                if (finalized) match.status == MatchStatus.FINALIZADO else match.status != MatchStatus.FINALIZADO
            }
            .sortedWith(compareBy({ it.matchday }, { it.time }, { it.home.name }))
    }

    private fun observeTeamsInternal(): Flow<List<Team>> = callbackFlow {
        val registration = Firebase.firestore.collection("teams").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val teams = snapshot?.documents
                ?.mapNotNull { it.toObject(TeamDoc::class.java)?.toModel(it.id) }
                .orEmpty()
            trySend(teams)
        }
        awaitClose { registration.remove() }
    }
}

/**
 * Tabla de posiciones calculada a partir de los resultados: PJ, puntos (3/1/0) y
 * diferencia de goles, ordenada por puntos, DG y nombre.
 */
internal fun computeStandings(teams: List<Team>, results: List<Match>): List<StandingRow> {
    data class Accumulator(val team: Team, var played: Int = 0, var points: Int = 0, var goalsFor: Int = 0, var goalsAgainst: Int = 0)

    val table = teams.associate { it.id to Accumulator(it) }
    results.forEach { match ->
        val homeGoals = match.homeGoals ?: return@forEach
        val awayGoals = match.awayGoals ?: return@forEach
        val home = table[match.home.id] ?: return@forEach
        val away = table[match.away.id] ?: return@forEach
        home.played++
        away.played++
        home.goalsFor += homeGoals
        home.goalsAgainst += awayGoals
        away.goalsFor += awayGoals
        away.goalsAgainst += homeGoals
        when {
            homeGoals > awayGoals -> home.points += 3
            homeGoals < awayGoals -> away.points += 3
            else -> {
                home.points += 1
                away.points += 1
            }
        }
    }
    return table.values
        .sortedWith(
            compareByDescending<Accumulator> { it.points }
                .thenByDescending { it.goalsFor - it.goalsAgainst }
                .thenBy { it.team.name }
        )
        .mapIndexed { index, row ->
            StandingRow(
                position = index + 1,
                team = row.team,
                played = row.played,
                points = row.points,
                goalDifference = row.goalsFor - row.goalsAgainst
            )
        }
}

class FirestoreFavoritesRepository : FavoritesRepository {

    private fun favoritesOf(uid: String) = Firebase.firestore.collection("users").document(uid).collection("favorites")

    override fun observeFavorites(uid: String): Flow<Set<String>> =
        if (uid.isBlank()) flowOf(emptySet()) else callbackFlow {
            val registration = favoritesOf(uid).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.map { it.id }?.toSet().orEmpty())
            }
            awaitClose { registration.remove() }
        }

    override suspend fun toggle(uid: String, favoriteId: String): Result<Unit> = runFirestore {
        val doc = favoritesOf(uid).document(favoriteId)
        val exists = doc.get().await().exists()
        if (exists) doc.delete() else doc.set(mapOf("createdAt" to System.currentTimeMillis()))
    }
}

/** Envuelve una operación de escritura de Firestore en [Result], respetando la cancelación. */
internal suspend fun runFirestore(block: suspend () -> com.google.android.gms.tasks.Task<*>): Result<Unit> =
    try {
        block().await()
        Result.success(Unit)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }
