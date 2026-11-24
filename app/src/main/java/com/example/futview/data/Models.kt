package com.example.futview.data

/** Estado de un partido dentro del fixture. */
enum class MatchStatus(val label: String) {
    PROGRAMADO("PROGRAMADO"),
    EN_JUEGO("EN JUEGO"),
    FINALIZADO("FINALIZADO"),
    PENDIENTE("PENDIENTE")
}

data class Team(
    val id: String,
    val name: String,
    val short: String,
    val city: String,
    val stadium: String,
    val lastResult: String,
    val nextMatch: String
)

data class Stadium(
    val id: String,
    val name: String,
    val city: String,
    val capacity: Int,
    val address: String,
    val homeTeam: String,
    val nextMatch: String,
    val latitude: String,
    val longitude: String,
    val note: String? = null
)

data class Match(
    val id: String,
    val dayLabel: String,
    val shortDate: String,
    val matchday: Int,
    val time: String,
    val status: MatchStatus,
    val home: Team,
    val away: Team,
    val stadium: String,
    val homeGoals: Int? = null,
    val awayGoals: Int? = null
)

data class StandingRow(
    val position: Int,
    val team: Team,
    val played: Int,
    val points: Int,
    val goalDifference: Int
)

data class SystemUser(
    val uid: String = "",
    val name: String,
    val email: String,
    val role: String,
    val initials: String
)

data class FixtureDay(
    val dateLabel: String,
    val matchday: Int,
    val matches: List<Match>
)
