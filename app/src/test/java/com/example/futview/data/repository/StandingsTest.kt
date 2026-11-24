package com.example.futview.data.repository

import com.example.futview.data.Match
import com.example.futview.data.MatchStatus
import com.example.futview.data.Team
import org.junit.Assert.assertEquals
import org.junit.Test

class StandingsTest {

    private fun team(id: String) = Team(
        id = id,
        name = "Club $id",
        short = id.take(2).uppercase(),
        city = "Santiago",
        stadium = "Estadio $id",
        lastResult = "",
        nextMatch = ""
    )

    private fun finished(
        id: String,
        home: Team,
        away: Team,
        homeGoals: Int,
        awayGoals: Int
    ) = Match(
        id = id,
        dayLabel = "Sábado, 28 de noviembre de 2026",
        shortDate = "28 nov",
        matchday = 29,
        time = "18:00",
        status = MatchStatus.FINALIZADO,
        home = home,
        away = away,
        stadium = "Estadio ${home.id}",
        homeGoals = homeGoals,
        awayGoals = awayGoals
    )

    @Test
    fun `suma puntos goles y ordena por puntos y diferencia`() {
        val a = team("a")
        val b = team("b")
        val c = team("c")
        val teams = listOf(a, b, c)
        val results = listOf(
            finished("m1", a, b, 2, 0),   // a: 3 pts, +2
            finished("m2", a, c, 1, 1),   // a: 4 pts, +2 · c: 1 pt
            finished("m3", b, c, 3, 0)    // b: 3 pts, +1 (pierde por DG) · c: 1 pt, -3
        )

        val table = computeStandings(teams, results)

        assertEquals(listOf("a", "b", "c"), table.map { it.team.id })
        assertEquals(2, table[0].played)
        assertEquals(4, table[0].points)
        assertEquals(2, table[0].goalDifference)
        assertEquals(3, table[1].points)
        assertEquals(1, table[1].goalDifference)
        assertEquals(1, table[2].points)
        assertEquals(-3, table[2].goalDifference)
    }

    @Test
    fun `los equipos sin partidos aparecen con ceros`() {
        val a = team("a")
        val b = team("b")
        val results = listOf(finished("m1", a, b, 2, 0))

        val table = computeStandings(listOf(a, team("c")), results)

        val c = table.first { it.team.id == "c" }
        assertEquals(0, c.played)
        assertEquals(0, c.points)
        assertEquals(0, c.goalDifference)
    }

    @Test
    fun `sin resultados devuelve tabla en ceros ordenada por nombre`() {
        val table = computeStandings(listOf(team("b"), team("a")), emptyList())

        assertEquals(listOf("a", "b"), table.map { it.team.id })
        table.forEach { row ->
            assertEquals(0, row.played)
            assertEquals(0, row.points)
            assertEquals(0, row.goalDifference)
        }
    }
}
