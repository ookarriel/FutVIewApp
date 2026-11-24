package com.example.futview.data.model

import com.example.futview.data.Match
import com.example.futview.data.MatchStatus
import com.example.futview.data.Stadium
import com.example.futview.data.StandingRow
import com.example.futview.data.SystemUser
import com.example.futview.data.Team
import com.google.firebase.firestore.GeoPoint

/**
 * Documentos de Firestore (DTOs) y su mapeo a los modelos de dominio.
 *
 * Convenciones:
 * - Los nombres de propiedad coinciden con los campos del documento en Firestore.
 * - Todos los campos tienen valor por defecto para que Firestore pueda instanciarlos.
 * - Los mapeadores resuelven las referencias entre colecciones (ids) una vez que el
 *   repositorio cargó los documentos relacionados.
 *
 * Se usan desde la Fase 3, cuando las implementaciones de repositorio lean de Firestore.
 */

/** Colección `teams`. */
data class TeamDoc(
    val name: String = "",
    val short: String = "",
    val city: String = "",
    val stadiumId: String = "",
    val stadiumName: String = ""
) {
    fun toModel(id: String): Team = Team(
        id = id,
        name = name,
        short = short,
        city = city,
        stadium = stadiumName,
        lastResult = "",
        nextMatch = ""
    )
}

/** Colección `stadiums`. */
data class StadiumDoc(
    val name: String = "",
    val city: String = "",
    val capacity: Long = 0,
    val address: String = "",
    val homeTeamId: String = "",
    val homeTeamName: String = "",
    val location: GeoPoint? = null
) {
    fun toModel(id: String): Stadium = Stadium(
        id = id,
        name = name,
        city = city,
        capacity = capacity.toInt(),
        address = address,
        homeTeam = homeTeamName,
        nextMatch = "",
        latitude = location?.latitude?.toString() ?: "",
        longitude = location?.longitude?.toString() ?: ""
    )
}

/** Colección `matches`. */
data class MatchDoc(
    val dayLabel: String = "",
    val shortDate: String = "",
    val matchday: Long = 0,
    val time: String = "",
    val status: String = MatchStatus.PROGRAMADO.name,
    val homeTeamId: String = "",
    val awayTeamId: String = "",
    val stadiumName: String = "",
    val homeGoals: Long? = null,
    val awayGoals: Long? = null
) {
    fun toModel(id: String, teams: Map<String, Team>): Match? {
        val home = teams[homeTeamId] ?: return null
        val away = teams[awayTeamId] ?: return null
        return Match(
            id = id,
            dayLabel = dayLabel,
            shortDate = shortDate,
            matchday = matchday.toInt(),
            time = time,
            status = runCatching { MatchStatus.valueOf(status) }.getOrDefault(MatchStatus.PROGRAMADO),
            home = home,
            away = away,
            stadium = stadiumName,
            homeGoals = homeGoals?.toInt(),
            awayGoals = awayGoals?.toInt()
        )
    }
}

/** Colección `standings` (una fila por equipo). */
data class StandingDoc(
    val teamId: String = "",
    val played: Long = 0,
    val points: Long = 0,
    val goalDifference: Long = 0
) {
    fun toModel(position: Int, team: Team?): StandingRow? = team?.let {
        StandingRow(
            position = position,
            team = it,
            played = played.toInt(),
            points = points.toInt(),
            goalDifference = goalDifference.toInt()
        )
    }
}

/** Colección `standings` (tabla oficial descargada de Wikipedia, temporada 2026). */
data class StandingsDoc(
    val season: Int = 0,
    val updatedAt: String = "",
    val rows: List<RowDoc> = emptyList()
)

data class RowDoc(
    val teamName: String = "",
    val teamShort: String = "",
    val played: Long = 0,
    val points: Long = 0,
    val goalDifference: Long = 0
)

/** Colección `users`. */
data class UserDoc(
    val name: String = "",
    val email: String = "",
    val role: String = "Usuario"
) {
    fun toModel(uid: String): SystemUser = SystemUser(
        uid = uid,
        name = name,
        email = email,
        role = role,
        initials = name
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
    )
}
