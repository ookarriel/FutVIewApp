package com.example.futview.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

/*
 * Resúmenes de actividad para las tarjetas de equipos y estadios.
 *
 * Los documentos de Firestore no guardan estos textos (llegan vacíos al mapear
 * `teams` y `stadiums`): se calculan a partir de la colección `matches`.
 */

/** Formato de `dayLabel` escrito por la sincronización ESPN y los formularios admin. */
private val kickoffFormatter: DateTimeFormatter = DateTimeFormatterBuilder()
    // `dayLabel` arranca con mayúscula ("Sábado, 28 de…"); sin esto el parseo falla.
    .parseCaseInsensitive()
    .appendPattern("EEEE, d 'de' MMMM 'de' yyyy")
    .toFormatter(Locale("es", "CL"))

/** Fecha de kickoff a partir de `dayLabel`; `LocalDate.MIN` si no se puede interpretar. */
fun Match.kickoffDate(): LocalDate = try {
    LocalDate.parse(dayLabel, kickoffFormatter)
} catch (_: Exception) {
    LocalDate.MIN
}

private val matchOrder: Comparator<Match> =
    compareBy({ it.kickoffDate() }, { it.time }, { it.matchday })

fun recentResultFor(teamId: String, results: List<Match>): Match? =
    results.filter { it.home.id == teamId || it.away.id == teamId }
        .maxWithOrNull(matchOrder)

fun nextMatchForTeam(teamId: String, upcoming: List<Match>): Match? =
    upcoming.filter { it.home.id == teamId || it.away.id == teamId }
        .minWithOrNull(matchOrder)

fun nextMatchForStadium(stadiumName: String, upcoming: List<Match>): Match? =
    upcoming
        .filter { it.stadium == stadiumName || it.stadium.startsWith(stadiumName) }
        .minWithOrNull(matchOrder)

/** "Ganó 2-1 a Colo Colo · 21 nov" para el último resultado finalizado del equipo. */
fun resultSummary(match: Match, teamId: String): String {
    val homeGoals = match.homeGoals ?: return ""
    val awayGoals = match.awayGoals ?: return ""
    val isHome = match.home.id == teamId
    val scored = if (isHome) homeGoals else awayGoals
    val conceded = if (isHome) awayGoals else homeGoals
    val rival = if (isHome) match.away.name else match.home.name
    val verb = when {
        scored > conceded -> "Ganó"
        scored < conceded -> "Perdió"
        else -> "Empató"
    }
    val connector = if (scored > conceded) "a" else "con"
    return "$verb $scored-$conceded $connector $rival · ${match.shortDate}"
}

/** "vs Colo Colo · 28 nov · 18:00" para el equipo; "X vs Y · …" sin `teamId`. */
fun fixtureSummary(match: Match, teamId: String? = null): String {
    val headline = if (teamId == null) {
        "${match.home.name} vs ${match.away.name}"
    } else {
        val isHome = match.home.id == teamId
        val rival = if (isHome) match.away.name else match.home.name
        if (isHome) "vs $rival" else "visita a $rival"
    }
    return "$headline · ${match.shortDate} · ${match.time}"
}

/** Copia cada equipo con su último resultado y próximo partido reales. */
fun List<Team>.withActivityResult(
    results: List<Match>,
    upcomingMatches: List<Match>
): List<Team> = map { team ->
    team.copy(
        lastResult = recentResultFor(team.id, results)
            ?.let { resultSummary(it, team.id) }
            .orEmpty(),
        nextMatch = nextMatchForTeam(team.id, upcomingMatches)
            ?.let { fixtureSummary(it, team.id) }
            .orEmpty()
    )
}

/** Copia cada estadio con su próximo partido programado. */
fun List<Stadium>.withNextMatch(upcomingMatches: List<Match>): List<Stadium> = map { stadium ->
    stadium.copy(
        nextMatch = nextMatchForStadium(stadium.name, upcomingMatches)
            ?.let { fixtureSummary(it) }
            .orEmpty()
    )
}
