package com.example.futview.data.repository

import android.content.Context
import com.example.futview.data.MatchStatus
import com.example.futview.data.SeedData
import com.example.futview.data.model.MatchDoc
import com.example.futview.data.model.RowDoc
import com.example.futview.data.model.StandingsDoc
import com.example.futview.data.model.TeamDoc
import com.example.futview.data.model.initialsOf
import com.example.futview.data.remote.EspnEvent
import com.example.futview.data.remote.EspnService
import com.example.futview.data.remote.EspnStandingEntry
import com.example.futview.data.remote.EspnStatusType
import com.example.futview.data.remote.EspnTeam
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Sincronización gratuita con ESPN para la Primera División de Chile (`chi.1`).
 *
 * - Descarga los 240 partidos de la temporada con fecha/hora real, estado y marcador.
 * - Escribe `matches` con id `espn-<eventId>` (idempotente: cada sync actualiza los mismos).
 * - Reutiliza los equipos y estadios ya sembrados emparejando nombres normalizados; solo
 *   crea documentos nuevos para clubes que no existan.
 * - Escribe la tabla oficial en `standings/2026` (PJ, puntos y diferencia de goles).
 * - Se ejecuta como máximo cada 6 horas (4 veces al día) desde la app, o al forzarla en admin.
 *
 * No requiere API key, cuenta ni cuota.
 */
class EspnSyncRepository {

    private val db get() = Firebase.firestore

    suspend fun sync(context: Context, force: Boolean = false): Result<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastSync = prefs.getLong(KEY_LAST_SYNC, 0L)
        if (!force && System.currentTimeMillis() - lastSync < SIX_HOURS_MILLIS) {
            return Result.success("Sincronización al día (se actualiza 4 veces al día).")
        }

        return try {
            val service = buildService()
            val events = service.scoreboard(dates = SEASON.toString(), limit = 500)
                .events
                .mapNotNull { it.toSnapshot() }
                .sortedBy { it.date }

            if (events.isEmpty()) {
                return Result.failure(IllegalStateException("ESPN no devolvió partidos para la temporada $SEASON."))
            }

            val existingTeams = db.collection("teams").get().await().documents.mapNotNull { doc ->
                doc.toObject(TeamDoc::class.java)?.let { doc.id to it }
            }
            val existingStadiums = db.collection("stadiums").get().await().documents.mapNotNull { doc ->
                doc.toObject(com.example.futview.data.model.StadiumDoc::class.java)?.let { doc.id to it }
            }

            val batch = db.batch()

            // --- Equipos: reutilizar los existentes, crear solo los que falten. ---
            val espnTeams = events.flatMap { listOf(it.home, it.away) }.distinctBy { it.id }
            val venueByTeamId = events.mapNotNull { event -> event.home.id to event.venue }
                .toMap()
            val teamIdByEspnId = mutableMapOf<String, String>()
            var createdTeams = 0

            espnTeams.forEach { espnTeam ->
                val normalized = normalizeEntityName(espnTeam.displayName)
                val existing = existingTeams.firstOrNull { (_, team) ->
                    normalizeEntityName(team.name) == normalized
                }
                if (existing != null) {
                    teamIdByEspnId[espnTeam.id] = existing.first
                } else {
                    val docId = "espn-${espnTeam.id}"
                    val venue = venueByTeamId[espnTeam.id].orEmpty()
                    val stadium = existingStadiums.firstOrNull { (_, item) ->
                        normalizeEntityName(item.name) == normalizeEntityName(venue)
                    }
                    teamIdByEspnId[espnTeam.id] = docId
                    batch.set(
                        db.collection("teams").document(docId),
                        TeamDoc(
                            name = espnTeam.displayName,
                            short = espnTeam.abbreviation.ifBlank { initialsOf(espnTeam.displayName) },
                            stadiumId = stadium?.first.orEmpty(),
                            stadiumName = stadium?.second?.name ?: venue
                        )
                    )
                    createdTeams++
                }
            }

            // --- Limpieza: documentos heredados de API-Football y resultados del seed. ---
            var removedLegacy = 0
            val seedSignatures = SeedData.results.map { result ->
                SeedSignature(
                    homeTeamId = slugify(result.home),
                    awayTeamId = slugify(result.away),
                    homeGoals = result.homeGoals.toLong(),
                    awayGoals = result.awayGoals.toLong()
                )
            }
            db.collection("matches").get().await().documents.forEach { doc ->
                if (doc.id.startsWith("espn-")) return@forEach
                val match = doc.toObject(MatchDoc::class.java) ?: return@forEach
                val isApiFootball = doc.id.startsWith("af-")
                val isSeed = seedSignatures.any { signature ->
                    signature.homeTeamId == match.homeTeamId &&
                        signature.awayTeamId == match.awayTeamId &&
                        signature.homeGoals == match.homeGoals &&
                        signature.awayGoals == match.awayGoals
                }
                if (isApiFootball || isSeed) {
                    batch.delete(doc.reference)
                    removedLegacy++
                }
            }

            // --- Partidos: 8 por jornada, 30 jornadas; id estable por evento de ESPN. ---
            var syncedMatches = 0
            events.chunked(MATCHES_PER_ROUND).forEachIndexed { index, round ->
                val matchday = (index + 1).toLong()
                round.forEach { event ->
                    val homeId = teamIdByEspnId[event.home.id]
                    val awayId = teamIdByEspnId[event.away.id]
                    if (homeId != null && awayId != null) {
                        val stadium = existingStadiums.firstOrNull { (_, item) ->
                            normalizeEntityName(item.name) == normalizeEntityName(event.venue)
                        }
                        batch.set(
                            db.collection("matches").document("espn-${event.id}"),
                            MatchDoc(
                                dayLabel = formatDayLabel(event.date),
                                shortDate = formatShortDate(event.date),
                                matchday = matchday,
                                time = formatTime(event.date),
                                status = event.status.name,
                                homeTeamId = homeId,
                                awayTeamId = awayId,
                                stadiumName = stadium?.second?.name ?: event.venue,
                                homeGoals = if (event.status == MatchStatus.FINALIZADO) event.homeGoals else null,
                                awayGoals = if (event.status == MatchStatus.FINALIZADO) event.awayGoals else null
                            )
                        )
                        syncedMatches++
                    }
                }
            }

            // --- Tabla oficial de posiciones (si ESPN la entrega). ---
            val standingsEntries = loadStandings(service)
            var standingsRows = 0
            if (standingsEntries.size >= 16) {
                val rows = standingsEntries.mapNotNull { entry ->
                    val team = entry.team ?: return@mapNotNull null
                    val stats = entry.stats.associate { it.name to it.value.toLong() }
                    RowDoc(
                        teamName = team.displayName,
                        teamShort = team.abbreviation,
                        played = stats["gamesPlayed"] ?: 0L,
                        points = stats["points"] ?: 0L,
                        goalDifference = stats["pointDifferential"] ?: 0L
                    )
                }
                if (rows.size >= 16) {
                    batch.set(
                        db.collection("standings").document(SEASON.toString()),
                        StandingsDoc(
                            season = SEASON,
                            updatedAt = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale("es", "CL"))),
                            rows = rows
                        )
                    )
                    standingsRows = rows.size
                }
            }

            batch.commit().await()
            prefs.edit().putLong(KEY_LAST_SYNC, System.currentTimeMillis()).apply()

            val extras = buildList {
                if (createdTeams > 0) add("$createdTeams equipos nuevos")
                if (standingsRows > 0) add("tabla con $standingsRows equipos")
                if (removedLegacy > 0) add("$removedLegacy registros antiguos eliminados")
            }
            Result.success(
                "ESPN: $syncedMatches partidos sincronizados" +
                    if (extras.isEmpty()) "." else " (${extras.joinToString(", ")})."
            )
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            Result.failure(throwable)
        }
    }

    /** Descarga la tabla; si falla, el sync de partidos igual se guarda. */
    private suspend fun loadStandings(service: EspnService): List<EspnStandingEntry> = try {
        service.standings(SEASON)
            .children
            .flatMap { it.standings?.entries.orEmpty() }
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        emptyList()
    }

    /** Convierte un evento de ESPN a un modelo plano, ignorando los incompletos. */
    private fun EspnEvent.toSnapshot(): EventSnapshot? {
        val competition = competitions.firstOrNull() ?: return null
        val home = competition.competitors.firstOrNull { it.homeAway == "home" }?.team ?: return null
        val away = competition.competitors.firstOrNull { it.homeAway == "away" }?.team ?: return null
        if (id.isBlank() || home.id.isBlank() || away.id.isBlank()) return null
        val homeScore = competition.competitors.firstOrNull { it.homeAway == "home" }?.score?.toIntOrNull()
        val awayScore = competition.competitors.firstOrNull { it.homeAway == "away" }?.score?.toIntOrNull()
        val status = statusOf(competition.status?.type)
        return EventSnapshot(
            id = id,
            date = competition.date.ifBlank { date },
            home = home,
            away = away,
            homeGoals = homeScore?.toLong(),
            awayGoals = awayScore?.toLong(),
            status = status,
            venue = competition.venue?.fullName.orEmpty()
        )
    }

    private fun statusOf(type: EspnStatusType?): MatchStatus = when {
        type == null -> MatchStatus.PROGRAMADO
        type.name.contains("CANCEL") ||
            type.name.contains("POSTPON") ||
            type.name.contains("ABANDON") ||
            type.name.contains("SUSPEND") -> MatchStatus.PENDIENTE
        type.state == "in" -> MatchStatus.EN_JUEGO
        type.state == "post" -> MatchStatus.FINALIZADO
        type.state == "pre" -> MatchStatus.PROGRAMADO
        else -> MatchStatus.PENDIENTE
    }

    private fun localDateTime(isoDate: String) = try {
        OffsetDateTime.parse(isoDate).atZoneSameInstant(ZoneId.of("America/Santiago"))
    } catch (exception: Exception) {
        null
    }

    private fun formatDayLabel(isoDate: String): String {
        val zoned = localDateTime(isoDate) ?: return ""
        return zoned
            .format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "CL")))
            .replaceFirstChar { it.uppercase() }
    }

    private fun formatShortDate(isoDate: String): String {
        val zoned = localDateTime(isoDate) ?: return ""
        return zoned.format(DateTimeFormatter.ofPattern("d MMM", Locale("es", "CL")))
    }

    private fun formatTime(isoDate: String): String {
        val zoned = localDateTime(isoDate) ?: return ""
        return zoned.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    private fun slugify(name: String): String = name
        .lowercase(Locale.ROOT)
        .replace("ñ", "n")
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')

    private fun buildService(): EspnService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(EspnService::class.java)

    private data class EventSnapshot(
        val id: String,
        val date: String,
        val home: EspnTeam,
        val away: EspnTeam,
        val homeGoals: Long?,
        val awayGoals: Long?,
        val status: MatchStatus,
        val venue: String
    )

    private data class SeedSignature(
        val homeTeamId: String,
        val awayTeamId: String,
        val homeGoals: Long,
        val awayGoals: Long
    )

    companion object {
        private const val BASE_URL = "https://site.api.espn.com/"
        private const val SEASON = 2026
        private const val MATCHES_PER_ROUND = 8
        private const val PREFS_NAME = "futview_sync"
        private const val KEY_LAST_SYNC = "last_sync_espn"
        private const val SIX_HOURS_MILLIS = 6L * 60L * 60L * 1000L  // 4 veces al día
    }
}
