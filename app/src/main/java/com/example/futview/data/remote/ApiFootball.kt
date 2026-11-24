package com.example.futview.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Cliente de API-Football (api-sports.io) — capa gratuita: 100 requests/día.
 * Liga de Primera de Chile: league = 265.
 */
interface ApiFootballService {

    /** Todos los partidos de una temporada, con marcadores y estado. */
    @GET("fixtures")
    suspend fun fixtures(
        @Query("league") league: Int,
        @Query("season") season: Int,
        @Header("x-apisports-key") key: String
    ): FixturesResponse
}

data class FixturesResponse(val response: List<FixtureItem> = emptyList())

data class FixtureItem(
    val fixture: FixtureInfo? = null,
    val league: LeagueInfo? = null,
    val teams: TeamsInfo? = null,
    val goals: GoalsInfo? = null
)

data class FixtureInfo(
    val id: Long = 0,
    val date: String = "",
    val status: FixtureStatus? = null,
    val venue: FixtureVenue? = null
)

data class FixtureStatus(val short: String = "")

data class FixtureVenue(val name: String? = null)

data class LeagueInfo(val round: String = "")

data class TeamsInfo(
    val home: FixtureTeam? = null,
    val away: FixtureTeam? = null
)

data class FixtureTeam(val id: Long = 0, val name: String = "")

data class GoalsInfo(val home: Int? = null, val away: Int? = null)
