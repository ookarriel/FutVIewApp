package com.example.futview.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ActivityResultTest {

    private fun team(id: String, name: String = "Club $id") = Team(
        id = id,
        name = name,
        short = id.take(2).uppercase(),
        city = "Santiago",
        stadium = "Estadio $id",
        lastResult = "",
        nextMatch = ""
    )

    private fun match(
        id: String,
        dayLabel: String,
        matchday: Long,
        time: String,
        home: Team,
        away: Team,
        homeGoals: Int? = null,
        awayGoals: Int? = null,
        status: MatchStatus = MatchStatus.FINALIZADO
    ) = Match(
        id = id,
        dayLabel = dayLabel,
        shortDate = "28 nov",
        matchday = matchday.toInt(),
        time = time,
        status = status,
        home = home,
        away = away,
        stadium = "Estadio ${home.id}",
        homeGoals = homeGoals,
        awayGoals = awayGoals
    )

    @Test
    fun `kickoffDate parsea dayLabel con mayuscula inicial`() {
        val m = match("m1", "Sábado, 28 de noviembre de 2026", 29, "18:00", team("a"), team("b"))
        assertEquals(LocalDate.of(2026, 11, 28), m.kickoffDate())
    }

    @Test
    fun `kickoffDate parsea en minusculas`() {
        val m = match("m1", "sábado, 28 de noviembre de 2026", 29, "18:00", team("a"), team("b"))
        assertEquals(LocalDate.of(2026, 11, 28), m.kickoffDate())
    }

    @Test
    fun `kickoffDate con texto invalido cae a MIN en vez de lanzar`() {
        val m = match("m1", "", 29, "18:00", team("a"), team("b"))
        assertEquals(LocalDate.MIN, m.kickoffDate())
    }

    @Test
    fun `withActivityResult elige el resultado mas reciente y el proximo partido`() {
        val home = team("colocolo", "Colo Colo")
        val rival = team("uc", "U. Católica")
        val results = listOf(
            match("r1", "Sábado, 1 de agosto de 2026", 20, "18:00", home, rival, 1, 0),
            match("r2", "Sábado, 28 de noviembre de 2026", 29, "20:00", rival, home, 2, 1)
        )
        val upcoming = listOf(
            match("p1", "Domingo, 6 de diciembre de 2026", 30, "18:00", home, rival, status = MatchStatus.PROGRAMADO)
        )

        val decorated = listOf(home).withActivityResult(results, upcoming).single()

        // Debe quedar el más reciente (noviembre), no el de agosto.
        assertTrue(decorated.lastResult.startsWith("Perdió 1-2 con U. Católica"))
        assertEquals("vs U. Católica · 28 nov · 18:00", decorated.nextMatch)
    }

    @Test
    fun `withActivityResult con partidos de visita usa visita a`() {
        val local = team("uc", "U. Católica")
        val visitante = team("colocolo", "Colo Colo")
        val upcoming = listOf(
            match("p1", "Sábado, 28 de noviembre de 2026", 29, "20:00", local, visitante, status = MatchStatus.PROGRAMADO)
        )

        val decorated = listOf(visitante).withActivityResult(emptyList(), upcoming).single()

        assertEquals("visita a U. Católica · 28 nov · 20:00", decorated.nextMatch)
        assertEquals("", decorated.lastResult)
    }

    @Test
    fun `resultSummary describe victoria empate y derrota`() {
        val home = team("a", "Alfa")
        val away = team("b", "Beta")
        val victoria = match("m1", "Sábado, 28 de noviembre de 2026", 29, "18:00", home, away, 2, 1)
        val derrota = match("m2", "Sábado, 28 de noviembre de 2026", 29, "18:00", away, home, 3, 0)
        val empate = match("m3", "Sábado, 28 de noviembre de 2026", 29, "18:00", home, team("c", "Gamma"), 1, 1)

        assertEquals("Ganó 2-1 a Beta · 28 nov", resultSummary(victoria, home.id))
        assertEquals("Perdió 0-3 con Beta · 28 nov", resultSummary(derrota, home.id))
        assertEquals("Empató 1-1 con Gamma · 28 nov", resultSummary(empate, home.id))
    }

    @Test
    fun `fixtureSummary sin teamId lista local y visita`() {
        val m = match("m1", "Sábado, 28 de noviembre de 2026", 29, "18:00", team("a"), team("b"))
        assertEquals("Club a vs Club b · 28 nov · 18:00", fixtureSummary(m))
    }
}
