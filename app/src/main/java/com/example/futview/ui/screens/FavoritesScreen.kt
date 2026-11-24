package com.example.futview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.StarBorder
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
import com.example.futview.data.Match
import com.example.futview.data.Stadium
import com.example.futview.data.Team
import com.example.futview.ui.components.FirestoreErrorBanner
import com.example.futview.ui.components.LoadingState
import com.example.futview.ui.viewmodel.FavoritesUiData
import com.example.futview.ui.viewmodel.FavoritesViewModel
import com.example.futview.ui.viewmodel.UiState
import com.example.futview.ui.components.Crest
import com.example.futview.ui.components.EmptyState
import com.example.futview.ui.components.FilterPill
import com.example.futview.ui.components.FutViewSearchField
import com.example.futview.ui.components.MatchCard
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
import com.example.futview.ui.util.formatThousands

@Composable
fun FavoritesScreen(
    favoriteTeamIds: List<String>,
    favoriteMatchIds: List<String>,
    favoriteStadiumIds: List<String>,
    onToggleTeamFavorite: (String) -> Unit,
    onToggleMatchFavorite: (String) -> Unit,
    onToggleStadiumFavorite: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onExploreTeams: () -> Unit,
    onViewMatches: () -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    var query by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("Todos") }

    val viewModel: FavoritesViewModel = viewModel(factory = FavoritesViewModel.Factory)
    val favoritesState by viewModel.favorites.collectAsStateWithLifecycle()
    val data = (favoritesState as? UiState.Success)?.data ?: FavoritesUiData()

    val teams = data.teams.filter { it.id in favoriteTeamIds }
    val matches = data.matches
        .filter { it.id in favoriteMatchIds }
        .sortedWith(compareBy({ it.matchday }, { it.time }))
    val stadiums = data.stadiums.filter { it.id in favoriteStadiumIds }

    val visibleTeams = if (type == "Todos" || type == "Equipos") {
        teams.filter {
            query.isBlank() ||
                it.name.contains(query, ignoreCase = true) ||
                it.city.contains(query, ignoreCase = true)
        }
    } else {
        emptyList()
    }
    val visibleMatches = if (type == "Todos" || type == "Partidos") {
        matches.filter {
            query.isBlank() ||
                it.home.name.contains(query, ignoreCase = true) ||
                it.away.name.contains(query, ignoreCase = true) ||
                it.stadium.contains(query, ignoreCase = true)
        }
    } else {
        emptyList()
    }
    val visibleStadiums = if (type == "Todos" || type == "Estadios") {
        stadiums.filter {
            query.isBlank() ||
                it.name.contains(query, ignoreCase = true) ||
                it.city.contains(query, ignoreCase = true)
        }
    } else {
        emptyList()
    }

    val total = teams.size + matches.size + stadiums.size
    val matchesByDay = visibleMatches.groupBy { it.dayLabel }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Mis favoritos",
            subtitle = "Tu fútbol, siempre a mano.",
            onAvatarClick = onAvatarClick
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (favoritesState) {
                is UiState.Loading -> item(key = "state") { LoadingState() }
                is UiState.Error -> item(key = "state") { FirestoreErrorBanner(onRetry = viewModel::retry) }
                else -> {}
            }


            item {
                FutViewSearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Buscar en mis favoritos",
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
                        label = "Todos $total",
                        selected = type == "Todos",
                        onClick = { type = "Todos" }
                    )
                    FilterPill(
                        label = "Equipos ${teams.size}",
                        selected = type == "Equipos",
                        onClick = { type = "Equipos" }
                    )
                    FilterPill(
                        label = "Partidos ${matches.size}",
                        selected = type == "Partidos",
                        onClick = { type = "Partidos" }
                    )
                    FilterPill(
                        label = "Estadios ${stadiums.size}",
                        selected = type == "Estadios",
                        onClick = { type = "Estadios" }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Card)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.StarBorder,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Mi selección",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "${teams.size} equipos · ${matches.size} partidos · ${stadiums.size} ${if (stadiums.size == 1) "estadio" else "estadios"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            if (visibleTeams.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Mis equipos",
                        trailing = "${visibleTeams.size} equipos"
                    )
                }
                items(visibleTeams, key = { "team-${it.id}" }) { team ->
                    FavoriteTeamRow(
                        team = team,
                        onToggleFavorite = { onToggleTeamFavorite(team.id) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            if (visibleMatches.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Partidos que sigo",
                        trailing = "${visibleMatches.size} partidos"
                    )
                }
                matchesByDay.forEach { (day, dayMatches) ->
                    item(key = "fav-day-$day") {
                        Text(
                            text = "${day.substringAfter(", ")} · Jornada ${dayMatches.first().matchday}",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                    items(dayMatches, key = { "fav-match-${it.id}" }) { match ->
                        MatchCard(
                            match = match,
                            favorite = true,
                            onToggleFavorite = { onToggleMatchFavorite(match.id) },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }

            if (visibleStadiums.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Mis estadios",
                        trailing = "${visibleStadiums.size} ${if (visibleStadiums.size == 1) "estadio" else "estadios"}"
                    )
                }
                items(visibleStadiums, key = { "stadium-${it.id}" }) { stadium ->
                    FavoriteStadiumRow(
                        stadium = stadium,
                        onToggleFavorite = { onToggleStadiumFavorite(stadium.id) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            if (total == 0 && favoritesState is UiState.Success) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.StarBorder,
                        title = "Tu selección empieza aquí",
                        message = "Todavía no guardas favoritos. Sigue los clubes, partidos y estadios que más te interesan.",
                        primaryText = "Explorar equipos",
                        onPrimary = onExploreTeams,
                        secondaryText = "Ver próximos partidos",
                        onSecondary = onViewMatches,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            } else if (favoritesState is UiState.Success && visibleTeams.isEmpty() && visibleMatches.isEmpty() && visibleStadiums.isEmpty()) {
                item {
                    Text(
                        text = "No encontramos favoritos con esos filtros. Toca una estrella para guardar equipos, partidos o estadios.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteTeamRow(
    team: Team,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Crest(initials = team.short, width = 34, height = 40)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = team.name,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (team.nextMatch.isBlank()) {
                    "Sin próximo partido programado"
                } else {
                    "Próximo: ${team.nextMatch}"
                },
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        StarToggle(selected = true, onToggle = onToggleFavorite)
    }
}

@Composable
private fun FavoriteStadiumRow(
    stadium: Stadium,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Card)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Field),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalance,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stadium.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${stadium.city} · ${stadium.capacity.formatThousands()} personas",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            StarToggle(selected = true, onToggle = onToggleFavorite)
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Ver ubicación y cómo llegar",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Outlined.ArrowOutward,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}
