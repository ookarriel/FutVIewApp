package com.example.futview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.futview.data.Match
import com.example.futview.data.StandingRow
import com.example.futview.ui.components.DemoBanner
import com.example.futview.ui.components.DetailField
import com.example.futview.ui.components.FutViewBottomBar
import com.example.futview.ui.components.InfoPill
import com.example.futview.ui.components.OutlineButton
import com.example.futview.ui.components.ResultCard
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SectionHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.components.StandingsTable
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary

@Composable
fun ResultDetailScreen(
    match: Match,
    favorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit,
    onAvatarClick: () -> Unit,
    onSelectTab: (Int) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Detalle del resultado",
            subtitle = "Jornada ${match.matchday} · Primera División 2027",
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
                    text = "Volver a resultados",
                    onClick = onBack,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item { DemoBanner() }

            item {
                ResultCard(
                    match = match,
                    favorite = favorite,
                    onToggleFavorite = onToggleFavorite,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
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
                        text = "Partido finalizado",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(16.dp))
                    DetailField(
                        label = "Encuentro",
                        value = "${match.home.name} ${match.homeGoals}–${match.awayGoals} ${match.away.name}"
                    )
                    Spacer(Modifier.height(14.dp))
                    DetailField(
                        label = "Sede",
                        value = match.stadium.substringBefore(" · ")
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "${match.home.name} suma 3 puntos. ${match.away.name} permanece sin sumar tras la jornada.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "No hay acta de goles ni estadísticas adicionales disponibles para este encuentro de demostración.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            item {
                SectionHeader(title = "Tabla de posiciones", trailing = "Jornada ${match.matchday}")
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
                    // TODO Fase 3: leer la tabla desde el repositorio de Firestore.
                    rows = emptyList<StandingRow>(),
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
        }

        FutViewBottomBar(
            selectedIndex = 1,
            onSelect = onSelectTab
        )
    }
}
