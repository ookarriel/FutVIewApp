package com.example.futview.data.repository

import com.example.futview.data.MatchStatus
import com.example.futview.data.SeedData
import com.example.futview.data.model.MatchDoc
import com.example.futview.data.model.StadiumDoc
import com.example.futview.data.model.TeamDoc
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Siembra el dataset inicial de la Liga de Primera 2026 en Firestore.
 * Solo se ejecuta si la colección `teams` está vacía (no duplica datos).
 */
class FirestoreSeedRepository {

    private val db get() = Firebase.firestore

    suspend fun seedLiga2026(): Result<String> = try {
        val teamsSnapshot = db.collection("teams").limit(1).get().await()
        if (!teamsSnapshot.isEmpty) {
            Result.failure(IllegalStateException("Ya existen equipos en la base de datos."))
        } else {
            val batch = db.batch()

            // Estadios con id fijo y equipos con referencia denormalizada.
            val teamIdsBySlug = mutableMapOf<String, String>()
            SeedData.clubs.forEach { club ->
                val stadiumSlug = slugify(club.stadiumName)
                val teamSlug = slugify(club.name)

                batch.set(
                    db.collection("stadiums").document(stadiumSlug),
                    StadiumDoc(
                        name = club.stadiumName,
                        city = club.city,
                        capacity = club.capacity.toLong(),
                        address = club.stadiumAddress,
                        location = GeoPoint(club.latitude, club.longitude)
                    )
                )
                batch.set(
                    db.collection("teams").document(teamSlug),
                    TeamDoc(
                        name = club.name,
                        short = club.short,
                        city = club.city,
                        stadiumId = stadiumSlug,
                        stadiumName = club.stadiumName
                    )
                )
                teamIdsBySlug[teamSlug] = club.name
            }

            // Resultados reales de la temporada 2026.
            SeedData.results.forEach { result ->
                val homeId = slugify(result.home)
                val awayId = slugify(result.away)
                val localDate = LocalDate.parse(result.date)
                val dayLabel = localDate
                    .format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "CL")))
                    .replaceFirstChar { it.uppercase() }
                val shortDate = localDate.format(DateTimeFormatter.ofPattern("d MMM", Locale("es", "CL")))
                val stadiumName = SeedData.clubs.firstOrNull { slugify(it.name) == homeId }?.stadiumName.orEmpty()

                batch.set(
                    db.collection("matches").document(),
                    MatchDoc(
                        dayLabel = dayLabel,
                        shortDate = shortDate,
                        matchday = matchdayFor(localDate).toLong(),
                        time = "18:00",
                        status = MatchStatus.FINALIZADO.name,
                        homeTeamId = homeId,
                        awayTeamId = awayId,
                        stadiumName = stadiumName,
                        homeGoals = result.homeGoals.toLong(),
                        awayGoals = result.awayGoals.toLong()
                    )
                )
            }

            batch.commit().await()
            Result.success(
                "Sembrados ${SeedData.clubs.size} clubes, ${SeedData.clubs.size} estadios y ${SeedData.results.size} resultados."
            )
        }
    } catch (cancellation: kotlinx.coroutines.CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }

    /** Jornada aproximada según la fecha (estimación editable desde el panel). */
    private fun matchdayFor(date: LocalDate): Int = when {
        date.isBefore(LocalDate.parse("2026-03-01")) -> 3
        date.isBefore(LocalDate.parse("2026-04-15")) -> 10
        date.isBefore(LocalDate.parse("2026-06-01")) -> 14
        else -> 26
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
}
