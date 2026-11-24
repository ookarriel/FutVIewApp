package com.example.futview.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Cliente del endpoint público de ESPN para la Primera División de Chile (`chi.1`).
 *
 * - Sin API key, sin registro y sin cuota.
 * - `scoreboard?dates=<año>&limit=500` devuelve los 240 partidos de la temporada con
 *   fecha/hora real, estado y marcador.
 * - `standings?season=<año>` devuelve la tabla oficial (PJ, puntos y diferencia de goles).
 */
interface EspnService {

    @GET("apis/site/v2/sports/soccer/chi.1/scoreboard")
    suspend fun scoreboard(
        @Query("dates") dates: String,
        @Query("limit") limit: Int = 500
    ): EspnScoreboardResponse

    @GET("apis/v2/sports/soccer/chi.1/standings")
    suspend fun standings(@Query("season") season: Int): EspnStandingsResponse
}

data class EspnScoreboardResponse(
    @SerializedName("events") val events: List<EspnEvent> = emptyList()
)

data class EspnEvent(
    val id: String = "",
    val date: String = "",
    val name: String = "",
    val competitions: List<EspnCompetition> = emptyList()
)

data class EspnCompetition(
    val date: String = "",
    val status: EspnStatus? = null,
    val venue: EspnVenue? = null,
    val competitors: List<EspnCompetitor> = emptyList()
)

data class EspnStatus(val type: EspnStatusType? = null)

data class EspnStatusType(
    val state: String = "",
    val completed: Boolean = false,
    val name: String = "",
    val detail: String = ""
)

data class EspnVenue(
    @SerializedName("fullName") val fullName: String = ""
)

data class EspnCompetitor(
    @SerializedName("homeAway") val homeAway: String = "",
    val score: String? = null,
    val team: EspnTeam? = null
)

data class EspnTeam(
    val id: String = "",
    val displayName: String = "",
    val abbreviation: String = ""
)

data class EspnStandingsResponse(
    val children: List<EspnStandingsGroup> = emptyList()
)

data class EspnStandingsGroup(
    val standings: EspnStandings? = null
)

data class EspnStandings(
    val entries: List<EspnStandingEntry> = emptyList()
)

data class EspnStandingEntry(
    val team: EspnTeam? = null,
    val stats: List<EspnStat> = emptyList()
)

data class EspnStat(
    val name: String = "",
    val value: Double = 0.0
)
