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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.futview.data.Match
import com.example.futview.ui.components.DemoBanner
import com.example.futview.ui.components.DetailField
import com.example.futview.ui.components.FutViewBottomBar
import com.example.futview.ui.components.MatchCard
import com.example.futview.ui.components.OutlineButton
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary

@Composable
fun MatchDetailScreen(
    match: Match,
    favorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit,
    onViewStadium: () -> Unit,
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
            title = "Detalle del partido",
            subtitle = "Jornada ${match.matchday} · Primera División",
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
                    text = "Volver a partidos",
                    onClick = onBack,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item { DemoBanner() }

            item {
                MatchCard(
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
                        text = "Información del partido",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(16.dp))
                    DetailField(
                        label = "Fecha y hora",
                        value = "${match.dayLabel} · ${match.time}"
                    )
                    Spacer(Modifier.height(14.dp))
                    DetailField(
                        label = "Competición",
                        value = "Primera División · Jornada ${match.matchday}"
                    )
                    Spacer(Modifier.height(14.dp))
                    DetailField(
                        label = "Sede",
                        value = match.stadium
                    )
                    Spacer(Modifier.height(14.dp))
                    DetailField(
                        label = "Estado",
                        value = "${match.status.label.lowercase().replaceFirstChar { it.uppercase() }} · Hora de Chile continental"
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlineButton(
                        text = "Ver estadio y dirección",
                        onClick = onViewStadium
                    )
                }
            }

            item {
                OutlineButton(
                    text = if (favorite) "Quitar de mis favoritos" else "Agregar a mis favoritos",
                    onClick = onToggleFavorite,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Text(
                    text = "Horarios en hora de Chile continental.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        FutViewBottomBar(
            selectedIndex = 0,
            onSelect = onSelectTab
        )
    }
}
