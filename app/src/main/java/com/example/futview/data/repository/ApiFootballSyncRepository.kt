package com.example.futview.data.repository

import android.content.Context
import com.example.futview.BuildConfig
import com.example.futview.data.MatchStatus
import com.example.futview.data.model.MatchDoc
import com.example.futview.data.model.TeamDoc
import com.example.futview.data.model.initialsOf
import com.example.futview.data.remote.ApiFootballService
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Sincronización diaria con API-Football (layer gratuita).
 *
 * - Descarga el fixture de la Liga de Primera de Chile (league 265) de la temporada actual.
 * - Escribe los partidos en `matches` con id "af-<fixtureId>" y crea los TeamDocs
 *   "af-<teamId>" necesarios para el join (nombres oficiales del proveedor).
 * - Guarda la marca de tiempo en SharedPreferences para no repetir antes de 24 horas.
 *
 * Requiere `API_FOOTBALL_KEY` en local.properties (gratuita en api-sports.io).
 */
class ApiFootballSyncRepository {

    private val db get() = Firebase.firestore

    suspend fun sync(context: Context, force: Boolean = false): Result<String> {
        val key = BuildConfig.API_FOOTBALL_KEY
        if (key.isBlank()) {
            return Result.failure(IllegalStateException("Configura API_FOOTBALL_KEY en local.properties (gratuita en api-sports.io)."))
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastSync = prefs.getLong(KEY_LAST_SYNC, 0L)
        if (!force && System.currentTimeMillis() - lastSync < DAY_MILLIS) {
            return Result.success("Sincronización al día (se actualiza cada 24 horas).")
        }

        return try {
            val service = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiFootballService::class.java)

            val response = service.fixtures(LEAGUE_ID, SEASON, key)
            val batch = db.batch()
            var synced = 0

            response.response.forEach { item ->
                val fixture = item.fixture ?: return@forEach
                val teams = item.teams ?: return@forEach
                val home = teams.home ?: return@forEach
                val away = teams.away ?: return@forEach
                val goals = item.goals

                // Equipos del proveedor con id propio ("af-<teamId>") para que el join funcione.
                batch.set(
                    db.collection("teams").document("af-${home.id}"),
                    TeamDoc(
                        name = home.name,
                        short = initialsOf(home.name),
                        stadiumName = fixture.venue?.name.orEmpty()
                    )
                )
                batch.set(
                    db.collection("teams").document("af-${away.id}"),
                    TeamDoc(
                        name = away.name,
                        short = initialsOf(away.name),
                        stadiumName = fixture.venue?.name.orEmpty()
                    )
                )

                val status = when (fixture.status?.short.orEmpty()) {
                    "FT", "AET", "PEN" -> MatchStatus.FINALIZADO
                    "1H", "HT", "2H", "ET", "BT", "LIVE" -> MatchStatus.EN_JUEGO
                    "PST", "CANC", "ABD", "SUSP" -> MatchStatus.PENDIENTE
                    else -> MatchStatus.PROGRAMADO
                }

                val matchday = Regex("Regular Season - (\\d+)")
                    .find(item.league?.round.orEmpty())
                    ?.groupValues?.getOrNull(1)?.toLong() ?: 0L

                batch.set(
                    db.collection("matches").document("af-${fixture.id}"),
                    MatchDoc(
                        dayLabel = formatDayLabel(fixture.date),
                        shortDate = formatShortDate(fixture.date),
                        matchday = matchday,
                        time = formatTime(fixture.date),
                        status = status.name,
                        homeTeamId = "af-${home.id}",
                        awayTeamId = "af-${away.id}",
                        stadiumName = fixture.venue?.name.orEmpty(),
                        homeGoals = if (status == MatchStatus.FINALIZADO) goals?.home?.toLong() else null,
                        awayGoals = if (status == MatchStatus.FINALIZADO) goals?.away?.toLong() else null
                    )
                )
                synced++
            }

            batch.commit().await()
            prefs.edit().putLong(KEY_LAST_SYNC, System.currentTimeMillis()).apply()
            Result.success("Sincronizados $synced partidos desde API-Football.")
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            Result.failure(throwable)
        }
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

    companion object {
        private const val BASE_URL = "https://v3.football.api-sports.io/"
        private const val LEAGUE_ID = 265        // Liga de Primera de Chile
        private const val SEASON = 2026
        private const val PREFS_NAME = "futview_sync"
        private const val KEY_LAST_SYNC = "last_sync_api_football"
        private const val DAY_MILLIS = 24L * 60L * 60L * 1000L
    }
}
