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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.futview.data.Match
import com.example.futview.data.StandingRow
import com.example.futview.ui.components.FirestoreErrorBanner
import com.example.futview.ui.components.FilterPill
import com.example.futview.ui.components.LoadingState
import com.example.futview.ui.viewmodel.ResultsViewModel
import com.example.futview.ui.viewmodel.UiState
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

    // TODO Fase 4: filtros por jornada/temporada derivados de los datos de Firestore.

    val viewModel: ResultsViewModel = viewModel(factory = ResultsViewModel.Factory)
    val resultsState by viewModel.results.collectAsStateWithLifecycle()
    val standingsState by viewModel.standings.collectAsStateWithLifecycle()

    val allResults = (resultsState as? UiState.Success)?.data.orEmpty()
    val standings = (standingsState as? UiState.Success)?.data.orEmpty()
    val filtered = allResults.filter { match ->
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
            when (resultsState) {
                is UiState.Loading -> item(key = "state") { LoadingState() }
                is UiState.Error -> item(key = "state") { FirestoreErrorBanner(onRetry = viewModel::retry) }
                else -> {}
            }

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
                    FilterPill(
                        label = "Finalizados",
                        selected = true,
                        onClick = {}
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

            if (filtered.isEmpty() && resultsState is UiState.Success) {
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
                SectionHeader(title = "Tabla de posiciones", trailing = "Temporada 2026")
            }

            item {
                InfoPill(
                    text = "${allResults.count { it.homeGoals != null }} partidos finalizados · " +
                        "${allResults.sumOf { (it.homeGoals ?: 0) + (it.awayGoals ?: 0) }} goles",
                    icon = Icons.Outlined.CheckCircle,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                StandingsTable(
                    rows = standings,
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
                    text = "La tabla se calcula con los resultados registrados. Los empates se ordenan por nombre."
                )
            }
        }
    }
}
