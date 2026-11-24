package com.example.futview.data.repository

import com.example.futview.data.model.RowDoc
import com.example.futview.data.model.StandingsDoc
import com.example.futview.data.model.initialsOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Descarga la tabla de posiciones REAL de la Liga de Primera 2026 desde Wikipedia
 * (página oficial actualizada durante el torneo) y la escribe en Firestore
 * (documento `standings/2026`).
 */
class WikipediaStandingsRepository {

    private val db get() = Firebase.firestore

    suspend fun syncStandings(): Result<String> {
        val html = try {
            withContext(Dispatchers.IO) { URL(PAGE_URL).readText() }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            return Result.failure(IllegalStateException("Sin conexión a Wikipedia: ${throwable.message}"))
        }

        val table = Regex("<table[^>]*>.*?</table>", RegexOption.DOT_MATCHES_ALL)
            .findAll(html)
            .firstOrNull { it.value.contains("Copa Libertadores group stage") }
            ?: return Result.failure(IllegalStateException("No se encontró la tabla de posiciones en Wikipedia."))

        val rows = mutableListOf<RowDoc>()
        Regex("<tr[^>]*>(.*?)</tr>", RegexOption.DOT_MATCHES_ALL).findAll(table.value).forEach { match ->
            val cells = Regex("<t[dh][^>]*>(.*?)</t[dh]>", RegexOption.DOT_MATCHES_ALL)
                .findAll(match.groupValues[1])
                .map { clean(it.value) }
                .toList()
            if (cells.size >= 10 && cells[0].toIntOrNull() != null) {
                rows += RowDoc(
                    teamName = cells[1],
                    teamShort = initialsOf(cells[1]),
                    played = (cells[2].toIntOrNull() ?: 0).toLong(),
                    points = (cells[9].toIntOrNull() ?: 0).toLong(),
                    goalDifference = (parseSigned(cells[8])).toLong()
                )
            }
        }

        if (rows.size < 16) {
            return Result.failure(IllegalStateException("La tabla de Wikipedia trajo ${rows.size} equipos; se esperaban 16."))
        }

        if (rows.size < 16) {
            return Result.failure(IllegalStateException("La tabla de Wikipedia trajo ${rows.size} equipos; se esperaban 16."))
        }

        val doc = StandingsDoc(
            season = 2026,
            updatedAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("d MMM yyyy · HH:mm", Locale("es", "CL"))),
            rows = rows
        )
        db.collection("standings").document("2026").set(doc).await()
        return Result.success("Tabla 2026 actualizada desde Wikipedia (${rows.size} equipos).")
    }

    private fun clean(htmlCell: String): String = htmlCell
        .replace(Regex("<!--.*?-->", RegexOption.DOT_MATCHES_ALL), "")
        .replace(Regex("<span[^>]*>.*?</span>", RegexOption.DOT_MATCHES_ALL), "")
        .replace(Regex("<style[^>]*>.*?</style>", RegexOption.DOT_MATCHES_ALL), "")
        .replace(Regex("<a[^>]*>|</a>"), "")
        .replace(Regex("<[^>]*>"), "")
        .replace("&amp;", "&")
        .replace("&#160;", " ")
        .replace("&nbsp;", " ")
        .replace('\u00a0', ' ')
        .trim()

    private fun parseSigned(value: String): Int = value
        .replace("\u2212", "-")
        .replace("+", "")
        .trim()
        .toIntOrNull() ?: 0

    private companion object {
        const val PAGE_URL = "https://en.wikipedia.org/wiki/2026_Liga_de_Primera"
    }
}
