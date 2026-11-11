package com.example.futview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.futview.data.FutViewData
import com.example.futview.data.Match
import com.example.futview.ui.components.DemoBanner
import com.example.futview.ui.components.FilterDropdownPill
import com.example.futview.ui.components.FilterPill
import com.example.futview.ui.components.FutViewSearchField
import com.example.futview.ui.components.InfoNote
import com.example.futview.ui.components.MatchCard
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SectionHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary

@Composable
fun MatchesScreen(
    favoriteTeamIds: List<String>,
    favoriteMatchIds: List<String>,
    onToggleMatchFavorite: (String) -> Unit,
    onAvatarClick: () -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    var query by rememberSaveable { mutableStateOf("") }
    var month by rememberSaveable { mutableStateOf("Febrero 2027") }
    var filter by rememberSaveable { mutableStateOf("Todos") }

    val filtered = FutViewData.upcomingMatches.filter { match ->
        val matchesFilter = filter != "Mis equipos" ||
            match.home.id in favoriteTeamIds ||
            match.away.id in favoriteTeamIds
        val matchesQuery = query.isBlank() ||
            match.home.name.contains(query, ignoreCase = true) ||
            match.away.name.contains(query, ignoreCase = true) ||
            match.stadium.contains(query, ignoreCase = true)
        matchesFilter && matchesQuery
    }
    val grouped = filtered.groupBy { it.dayLabel }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Partidos",
            subtitle = "Encuentra tu próximo encuentro.",
            onAvatarClick = onAvatarClick
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { DemoBanner() }

            item {
                FutViewSearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Buscar equipo o estadio",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterDropdownPill(
                        label = month,
                        options = listOf("Febrero 2027", "Marzo 2027", "Abril 2027"),
                        onSelect = { month = it }
                    )
                    FilterPill(
                        label = "Todos",
                        selected = filter == "Todos",
                        onClick = { filter = "Todos" }
                    )
                    FilterPill(
                        label = "Mis equipos",
                        selected = filter == "Mis equipos",
                        onClick = { filter = "Mis equipos" }
                    )
                }
            }

            item {
                SectionHeader(
                    title = "Próximos encuentros",
                    trailing = "${filtered.size} partidos"
                )
            }

            grouped.forEach { (day, matches) ->
                item(key = "day-$day") {
                    DayHeader(day = day, matchday = matches.first().matchday)
                }
                items(matches, key = { it.id }) { match ->
                    MatchCard(
                        match = match,
                        favorite = match.id in favoriteMatchIds,
                        onToggleFavorite = { onToggleMatchFavorite(match.id) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            if (filtered.isEmpty()) {
                item {
                    Text(
                        text = "No encontramos partidos con esos filtros.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                    )
                }
            }

            item {
                InfoNote(
                    text = "Horarios de Chile continental. Calendario ficticio de la temporada 2027, sujeto a cambios."
                )
            }
        }
    }
}

@Composable
private fun DayHeader(day: String, matchday: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Jornada $matchday",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}
