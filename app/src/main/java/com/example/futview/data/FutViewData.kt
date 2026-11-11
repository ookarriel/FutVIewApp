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

/** Datos de demostración basados en los mockups de FutView. */
object FutViewData {

    val coloColo = Team(
        id = "cc",
        name = "Colo-Colo",
        short = "CC",
        city = "Macul",
        stadium = "Monumental",
        lastResult = "2–0 vs. Palestino",
        nextMatch = "vs. U. de Chile · 19 feb · 18:00"
    )

    val palestino = Team(
        id = "pal",
        name = "Palestino",
        short = "PAL",
        city = "La Cisterna",
        stadium = "Municipal de La Cisterna",
        lastResult = "0–2 vs. Colo-Colo",
        nextMatch = "vs. U. Católica · 19 feb · 20:30"
    )

    val universidadCatolica = Team(
        id = "uc",
        name = "Universidad Católica",
        short = "UC",
        city = "Santiago",
        stadium = "Santa Laura",
        lastResult = "1–1 vs. U. de Chile",
        nextMatch = "Visita a Palestino · 19 feb · 20:30"
    )

    val universidadDeChile = Team(
        id = "uch",
        name = "Universidad de Chile",
        short = "UCH",
        city = "Santiago",
        stadium = "Nacional Julio Martínez",
        lastResult = "1–1 vs. U. Católica",
        nextMatch = "Visita a Colo-Colo · 19 feb · 18:00"
    )

    val teams = listOf(coloColo, palestino, universidadCatolica, universidadDeChile)

    val monumental = Stadium(
        id = "monumental",
        name = "Monumental",
        city = "Macul",
        capacity = 47347,
        address = "Av. Marathon 5300",
        homeTeam = "Colo-Colo",
        nextMatch = "Colo-Colo vs. U. de Chile · 19 feb · 18:00",
        latitude = "-33.5066",
        longitude = "-70.6054"
    )

    val nacional = Stadium(
        id = "nacional",
        name = "Nacional Julio Martínez",
        city = "Ñuñoa",
        capacity = 48665,
        address = "Av. Grecia 2001",
        homeTeam = "U. de Chile",
        nextMatch = "U. de Chile vs. Palestino · 26 feb · 18:00",
        latitude = "-33.4646",
        longitude = "-70.6106"
    )

    val laCisterna = Stadium(
        id = "la-cisterna",
        name = "Municipal de La Cisterna",
        city = "La Cisterna",
        capacity = 12000,
        address = "Av. El Parrón 0999",
        homeTeam = "Palestino",
        nextMatch = "Palestino vs. U. Católica · 19 feb · 20:30",
        latitude = "-33.5233",
        longitude = "-70.6644"
    )

    val santaLaura = Stadium(
        id = "santa-laura",
        name = "Santa Laura",
        city = "Independencia",
        capacity = 19000,
        address = "Julio Martínez Prádanos 1365",
        homeTeam = "U. Católica",
        nextMatch = "U. Católica vs. Colo-Colo · 26 feb · 20:30",
        latitude = "-33.4068",
        longitude = "-70.6699",
        note = "Sede de ejemplo"
    )

    val stadiums = listOf(monumental, nacional, laCisterna, santaLaura)

    val upcomingMatches = listOf(
        Match(
            id = "m2",
            dayLabel = "Viernes, 19 de febrero",
            shortDate = "19 feb",
            matchday = 2,
            time = "18:00",
            status = MatchStatus.PROGRAMADO,
            home = coloColo,
            away = universidadDeChile,
            stadium = "Monumental · Macul"
        ),
        Match(
            id = "m3",
            dayLabel = "Viernes, 19 de febrero",
            shortDate = "19 feb",
            matchday = 2,
            time = "20:30",
            status = MatchStatus.PROGRAMADO,
            home = palestino,
            away = universidadCatolica,
            stadium = "Municipal de La Cisterna"
        ),
        Match(
            id = "m5",
            dayLabel = "Viernes, 26 de febrero",
            shortDate = "26 feb",
            matchday = 3,
            time = "18:00",
            status = MatchStatus.PROGRAMADO,
            home = universidadDeChile,
            away = palestino,
            stadium = "Nacional Julio Martínez · Ñuñoa"
        ),
        Match(
            id = "m6",
            dayLabel = "Viernes, 26 de febrero",
            shortDate = "26 feb",
            matchday = 3,
            time = "20:30",
            status = MatchStatus.PROGRAMADO,
            home = universidadCatolica,
            away = coloColo,
            stadium = "Santa Laura · Independencia"
        )
    )

    val results = listOf(
        Match(
            id = "m1",
            dayLabel = "Viernes, 12 de febrero de 2027",
            shortDate = "12 feb",
            matchday = 1,
            time = "18:00",
            status = MatchStatus.FINALIZADO,
            home = coloColo,
            away = palestino,
            stadium = "Monumental · Macul · 18:00",
            homeGoals = 2,
            awayGoals = 0
        ),
        Match(
            id = "m4",
            dayLabel = "Viernes, 12 de febrero de 2027",
            shortDate = "12 feb",
            matchday = 1,
            time = "20:30",
            status = MatchStatus.FINALIZADO,
            home = universidadDeChile,
            away = universidadCatolica,
            stadium = "Nacional Julio Martínez · 20:30",
            homeGoals = 1,
            awayGoals = 1
        )
    )

    val standings = listOf(
        StandingRow(1, coloColo, 1, 3, 2),
        StandingRow(2, universidadCatolica, 1, 1, 0),
        StandingRow(3, universidadDeChile, 1, 1, 0),
        StandingRow(4, palestino, 1, 0, -2)
    )

    val fixtureDays = listOf(
        FixtureDay(
            dateLabel = "12 FEB 2027",
            matchday = 1,
            matches = listOf(
                Match(
                    id = "f1",
                    dayLabel = "12 feb",
                    shortDate = "12 feb",
                    matchday = 1,
                    time = "18:00",
                    status = MatchStatus.FINALIZADO,
                    home = coloColo,
                    away = palestino,
                    stadium = "Monumental",
                    homeGoals = 2,
                    awayGoals = 0
                ),
                Match(
                    id = "f2",
                    dayLabel = "12 feb",
                    shortDate = "12 feb",
                    matchday = 1,
                    time = "20:30",
                    status = MatchStatus.FINALIZADO,
                    home = universidadDeChile,
                    away = universidadCatolica,
                    stadium = "Nacional Julio Martínez",
                    homeGoals = 1,
                    awayGoals = 1
                )
            )
        ),
        FixtureDay(
            dateLabel = "19 FEB 2027",
            matchday = 2,
            matches = listOf(
                Match(
                    id = "f3",
                    dayLabel = "19 feb",
                    shortDate = "19 feb",
                    matchday = 2,
                    time = "18:00",
                    status = MatchStatus.PENDIENTE,
                    home = coloColo,
                    away = universidadDeChile,
                    stadium = "Monumental"
                ),
                Match(
                    id = "f4",
                    dayLabel = "19 feb",
                    shortDate = "19 feb",
                    matchday = 2,
                    time = "20:30",
                    status = MatchStatus.PENDIENTE,
                    home = palestino,
                    away = universidadCatolica,
                    stadium = "Municipal de La Cisterna"
                )
            )
        )
    )

    val users = listOf(
        SystemUser("Camila Morales", "camila@ejemplo.cl", "Admin", "CM"),
        SystemUser("Diego Soto", "diego@ejemplo.cl", "Editor", "DS"),
        SystemUser("Valentina Rojas", "valentina@ejemplo.cl", "Editor", "VR")
    )
}
