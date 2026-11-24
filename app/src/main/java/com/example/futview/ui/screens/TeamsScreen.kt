package com.example.futview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.futview.data.Team
import com.example.futview.ui.components.FirestoreErrorBanner
import com.example.futview.ui.components.LoadingState
import com.example.futview.ui.viewmodel.TeamsViewModel
import com.example.futview.ui.viewmodel.UiState
import com.example.futview.ui.components.Crest
import com.example.futview.ui.components.EmptyState
import com.example.futview.ui.components.FilterDropdownPill
import com.example.futview.ui.components.FilterPill
import com.example.futview.ui.components.FutViewSearchField
import com.example.futview.ui.components.InfoNote
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SectionHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.components.StarToggle
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Field
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary
import com.example.futview.ui.theme.TextTertiary

@Composable
fun TeamsScreen(
    favoriteTeamIds: List<String>,
    onToggleTeamFavorite: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onTeamClick: (String) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("Todos") }
    var city by rememberSaveable { mutableStateOf("Todas las ciudades") }

    val viewModel: TeamsViewModel = viewModel(factory = TeamsViewModel.Factory)
    val teamsState by viewModel.teams.collectAsStateWithLifecycle()

    val filtered = (teamsState as? UiState.Success)?.data.orEmpty().filter { team ->
        val matchesFilter = filter != "Mis equipos" || team.id in favoriteTeamIds
        val matchesCity = city == "Todas las ciudades" || team.city == city
        val matchesQuery = query.isBlank() ||
            team.name.contains(query, ignoreCase = true) ||
            team.city.contains(query, ignoreCase = true)
        matchesFilter && matchesCity && matchesQuery
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Equipos",
            subtitle = "Conoce los clubes y sigue a tus favoritos.",
            onAvatarClick = onAvatarClick
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (teamsState) {
                is UiState.Loading -> item(key = "state") { LoadingState() }
                is UiState.Error -> item(key = "state") { FirestoreErrorBanner(onRetry = viewModel::retry) }
                else -> {}
            }


            item {
                FutViewSearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Buscar club o ciudad",
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
                    FilterDropdownPill(
                        label = if (city == "Todas las ciudades") "Ciudad" else city,
                        options = listOf("Todas las ciudades"),
                        onSelect = { city = it }
                    )
                }
            }

            item {
                SectionHeader(
                    title = "Clubes de la competición",
                    trailing = "${filtered.size} equipos"
                )
            }

            item {
                Text(
                    text = "Primera División · Orden alfabético",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            items(filtered, key = { it.id }) { team ->
                TeamCard(
                    team = team,
                    favorite = team.id in favoriteTeamIds,
                    onToggleFavorite = { onToggleTeamFavorite(team.id) },
                    onClick = { onTeamClick(team.id) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            if (filtered.isEmpty() && teamsState is UiState.Success) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.SearchOff,
                        title = "No encontramos ese equipo",
                        message = "No hay coincidencias para \"$query\" en los clubes de la competición. Revisa la escritura o busca por ciudad.",
                        primaryText = "Limpiar búsqueda y filtros",
                        onPrimary = {
                            query = ""
                            filter = "Todos"
                            city = "Todas las ciudades"
                        },
                        secondaryText = "Ver todos los equipos",
                        onSecondary = {
                            query = ""
                            filter = "Todos"
                            city = "Todas las ciudades"
                        },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            item {
                InfoNote(
                    text = "Toca la estrella para seguir un club y encontrarlo en tus favoritos."
                )
            }
        }
    }
}

@Composable
private fun TeamCard(
    team: Team,
    favorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Card)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Crest(initials = team.short, width = 38, height = 44)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = team.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${team.city} · Primera División",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            StarToggle(selected = favorite, onToggle = onToggleFavorite)
        }

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.AccountBalance,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = team.stadium,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "ÚLTIMO",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = team.lastResult,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Field)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = team.nextMatch.ifBlank { "Por confirmar" },
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
