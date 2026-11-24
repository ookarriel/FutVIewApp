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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.futview.data.Match
import com.example.futview.ui.components.FirestoreErrorBanner
import com.example.futview.ui.components.LoadingState
import com.example.futview.ui.viewmodel.MatchesViewModel
import com.example.futview.ui.viewmodel.UiState
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
    onAvatarClick: () -> Unit,
    onMatchClick: (String) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("Todos") }

    val viewModel: MatchesViewModel = viewModel(factory = MatchesViewModel.Factory)
    val matchesState by viewModel.matches.collectAsStateWithLifecycle()

    val allMatches = (matchesState as? UiState.Success)?.data.orEmpty()
    val filtered = allMatches.filter { match ->
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
            when (matchesState) {
                is UiState.Loading -> item(key = "state") { LoadingState() }
                is UiState.Error -> item(key = "state") { FirestoreErrorBanner(onRetry = viewModel::retry) }
                else -> {}
            }


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
                        onClick = { onMatchClick(match.id) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            if (filtered.isEmpty() && matchesState is UiState.Success) {
                item {
                    Text(
                        text = if (allMatches.isEmpty()) {
                            "Todavía no hay partidos programados."
                        } else {
                            "No encontramos partidos con esos filtros."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                    )
                }
            }

            item {
                InfoNote(
                    text = "Horarios en hora de Chile continental. Los partidos se sincronizan desde Firestore."
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
