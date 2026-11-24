package com.example.futview.data.repository

import com.example.futview.data.SeedData
import com.example.futview.data.model.StadiumDoc
import com.example.futview.data.model.TeamDoc
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import java.util.Locale

/**
 * Siembra (o completa) el catálogo base de la Liga de Primera 2026: 16 clubes y sus
 * estadios con coordenadas reales.
 *
 * Es idempotente: reutiliza equipos/estadios ya existentes emparejando nombres
 * normalizados (por ejemplo, los creados por la sincronización de ESPN) y solo crea
 * lo que falte. Los partidos y resultados los carga `EspnSyncRepository`.
 */
class FirestoreSeedRepository {

    private val db get() = Firebase.firestore

    suspend fun seedLiga2026(): Result<String> = try {
        val existingTeams = db.collection("teams").get().await().documents.mapNotNull { doc ->
            doc.toObject(TeamDoc::class.java)?.let { doc.id to it }
        }
        val existingStadiums = db.collection("stadiums").get().await().documents.mapNotNull { doc ->
            doc.toObject(StadiumDoc::class.java)?.let { doc.id to it }
        }
        val batch = db.batch()
        var createdTeams = 0
        var createdStadiums = 0

        SeedData.clubs.forEach { club ->
            val stadiumSlug = slugify(club.stadiumName)
            val existingStadium = existingStadiums.firstOrNull { (_, stadium) ->
                normalizeEntityName(stadium.name) == normalizeEntityName(club.stadiumName)
            }
            val stadiumId = existingStadium?.first ?: stadiumSlug
            batch.set(
                db.collection("stadiums").document(stadiumId),
                StadiumDoc(
                    name = club.stadiumName,
                    city = club.city,
                    capacity = club.capacity.toLong(),
                    address = club.stadiumAddress,
                    location = GeoPoint(club.latitude, club.longitude)
                ),
                SetOptions.merge()
            )
            if (existingStadium == null) createdStadiums++

            val teamSlug = slugify(club.name)
            val existingTeam = existingTeams.firstOrNull { (_, team) ->
                normalizeEntityName(team.name) == normalizeEntityName(club.name)
            }
            val teamId = existingTeam?.first ?: teamSlug
            batch.set(
                db.collection("teams").document(teamId),
                TeamDoc(
                    name = club.name,
                    short = club.short,
                    city = club.city,
                    stadiumId = stadiumId,
                    stadiumName = club.stadiumName
                )
            )
            if (existingTeam == null) createdTeams++
        }

        batch.commit().await()
        Result.success(
            "Catálogo 2026 listo: $createdTeams clubes nuevos, $createdStadiums estadios nuevos. " +
                "Los partidos y resultados se cargan con ESPN."
        )
    } catch (cancellation: kotlinx.coroutines.CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        Result.failure(throwable)
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
