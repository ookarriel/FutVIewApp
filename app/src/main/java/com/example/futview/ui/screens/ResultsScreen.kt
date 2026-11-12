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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
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
import com.example.futview.ui.components.DemoBanner
import com.example.futview.ui.components.FilterDropdownPill
import com.example.futview.ui.components.FutViewSearchField
import com.example.futview.ui.components.InfoNote
import com.example.futview.ui.components.InfoPill
import com.example.futview.ui.components.ResultCard
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SectionHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.components.StandingsTable
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary

@Composable
fun ResultsScreen(
    favoriteMatchIds: List<String>,
    onToggleMatchFavorite: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onResultClick: (String) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    var query by rememberSaveable { mutableStateOf("") }
    var season by rememberSaveable { mutableStateOf("Temporada 2027") }
    var matchday by rememberSaveable { mutableStateOf("Jornada 1") }

    val filtered = FutViewData.results.filter { match ->
        query.isBlank() ||
            match.home.name.contains(query, ignoreCase = true) ||
            match.away.name.contains(query, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Resultados",
            subtitle = "Revive cada jornada y consulta los marcadores.",
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
                    placeholder = "Buscar equipo",
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
                        label = season,
                        options = listOf("Temporada 2027", "Temporada 2026"),
                        onSelect = { season = it }
                    )
                    FilterDropdownPill(
                        label = matchday,
                        options = listOf("Jornada 1", "Jornada 2", "Jornada 3", "Jornada 4"),
                        onSelect = { matchday = it },
                        selected = true
                    )
                }
            }

            item {
                SectionHeader(
                    title = "Últimos resultados",
                    trailing = "${filtered.size} partidos"
                )
            }

            if (filtered.isNotEmpty()) {
                item {
                    Text(
                        text = filtered.first().dayLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            items(filtered, key = { it.id }) { match ->
                ResultCard(
                    match = match,
                    favorite = match.id in favoriteMatchIds,
                    onToggleFavorite = { onToggleMatchFavorite(match.id) },
                    onClick = { onResultClick(match.id) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            if (filtered.isEmpty()) {
                item {
                    Text(
                        text = "No encontramos resultados para esa búsqueda.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                    )
                }
            }

            item {
                SectionHeader(title = "Tabla de posiciones", trailing = "Jornada 1")
            }

            item {
                InfoPill(
                    text = "2 partidos finalizados · 4 goles",
                    icon = Icons.Outlined.CheckCircle,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                StandingsTable(
                    rows = FutViewData.standings,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Text(
                    text = "PJ: partidos jugados · PTS: puntos · DG: diferencia de goles",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            item {
                InfoNote(
                    text = "Clasificación de ejemplo, no oficial. Los empates en puntos y diferencia de goles se ordenan por nombre."
                )
            }
        }
    }
}
