package com.example.futview.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.futview.data.FutViewData
import com.example.futview.data.Team
import com.example.futview.ui.components.Crest
import com.example.futview.ui.components.DemoBanner
import com.example.futview.ui.components.DetailField
import com.example.futview.ui.components.FutViewBottomBar
import com.example.futview.ui.components.MatchCard
import com.example.futview.ui.components.OutlineButton
import com.example.futview.ui.components.ResultCard
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.components.StarToggle
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Field
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary
import com.example.futview.ui.theme.TextTertiary

@Composable
fun TeamDetailScreen(
    team: Team,
    favorite: Boolean,
    onToggleFavorite: () -> Unit,
    favoriteMatchIds: List<String>,
    onToggleMatchFavorite: (String) -> Unit,
    onBack: () -> Unit,
    onViewStadium: () -> Unit,
    onViewMatch: (String) -> Unit,
    onViewResult: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onSelectTab: (Int) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    val nextMatch = FutViewData.upcomingMatches.firstOrNull {
        it.home.id == team.id || it.away.id == team.id
    }
    val lastResult = FutViewData.results.firstOrNull {
        it.home.id == team.id || it.away.id == team.id
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Detalle del equipo",
            subtitle = "Conoce y sigue a ${team.name}.",
            onAvatarClick = onAvatarClick,
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlineButton(
                    text = "Volver a equipos",
                    onClick = onBack,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item { DemoBanner() }

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Card)
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
                            text = team.nextMatch,
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

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Card)
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Sobre el equipo",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(16.dp))
                    DetailField(label = "Nombre", value = team.name)
                    Spacer(Modifier.height(14.dp))
                    DetailField(label = "Ciudad", value = team.city)
                    Spacer(Modifier.height(14.dp))
                    DetailField(label = "Competición", value = "Primera División · Temporada 2027")
                    Spacer(Modifier.height(14.dp))
                    DetailField(label = "Estadio local", value = team.stadium)
                    Spacer(Modifier.height(16.dp))
                    OutlineButton(text = "Ver estadio", onClick = onViewStadium)
                }
            }

            if (nextMatch != null) {
                item {
                    Text(
                        text = "Próximo partido",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                item {
                    MatchCard(
                        match = nextMatch,
                        favorite = nextMatch.id in favoriteMatchIds,
                        onToggleFavorite = { onToggleMatchFavorite(nextMatch.id) },
                        onClick = { onViewMatch(nextMatch.id) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            if (lastResult != null) {
                item {
                    Text(
                        text = "Último resultado",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                item {
                    ResultCard(
                        match = lastResult,
                        favorite = lastResult.id in favoriteMatchIds,
                        onToggleFavorite = { onToggleMatchFavorite(lastResult.id) },
                        onClick = { onViewResult(lastResult.id) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            item {
                Text(
                    text = "Escudo ilustrativo · Datos de demostración, no oficiales.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        FutViewBottomBar(
            selectedIndex = 2,
            onSelect = onSelectTab
        )
    }
}
