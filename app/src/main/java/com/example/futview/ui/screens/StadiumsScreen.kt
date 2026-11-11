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
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.futview.data.FutViewData
import com.example.futview.data.Stadium
import com.example.futview.ui.components.DemoBanner
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
import com.example.futview.ui.util.formatThousands

@Composable
fun StadiumsScreen(
    favoriteStadiumIds: List<String>,
    onToggleStadiumFavorite: (String) -> Unit,
    onAvatarClick: () -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("Todas") }
    var city by rememberSaveable { mutableStateOf("Todas las comunas") }

    val filtered = FutViewData.stadiums.filter { stadium ->
        val matchesFilter = filter != "Mis estadios" || stadium.id in favoriteStadiumIds
        val matchesCity = city == "Todas las comunas" || stadium.city == city
        val matchesQuery = query.isBlank() ||
            stadium.name.contains(query, ignoreCase = true) ||
            stadium.city.contains(query, ignoreCase = true)
        matchesFilter && matchesCity && matchesQuery
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Estadios",
            subtitle = "Encuentra las sedes y planifica tu visita.",
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
                    placeholder = "Buscar estadio o comuna",
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
                        label = city,
                        options = listOf(
                            "Todas las comunas",
                            "Macul",
                            "Ñuñoa",
                            "La Cisterna",
                            "Independencia"
                        ),
                        onSelect = { city = it },
                        selected = true
                    )
                    FilterPill(
                        label = "Mis estadios",
                        selected = filter == "Mis estadios",
                        onClick = {
                            filter = if (filter == "Mis estadios") "Todas" else "Mis estadios"
                        }
                    )
                }
            }

            item {
                SectionHeader(
                    title = "Sedes de la competición",
                    trailing = "${filtered.size} estadios"
                )
            }

            item {
                Text(
                    text = "Región Metropolitana · Temporada 2027",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            items(filtered, key = { it.id }) { stadium ->
                StadiumCard(
                    stadium = stadium,
                    favorite = stadium.id in favoriteStadiumIds,
                    onToggleFavorite = { onToggleStadiumFavorite(stadium.id) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            if (filtered.isEmpty()) {
                item {
                    Text(
                        text = "No encontramos estadios con esos filtros.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                    )
                }
            }

            item {
                InfoNote(
                    text = "Direcciones, aforos y sedes de ejemplo. Consulta las condiciones de acceso antes de ir al estadio."
                )
            }
        }
    }
}

@Composable
private fun StadiumCard(
    stadium: Stadium,
    favorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Card)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Field),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalance,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(23.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stadium.name,
                    style = MaterialTheme.typography.titleMedium,
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
            StarToggle(selected = favorite, onToggle = onToggleFavorite)
        }

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stadium.address,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = buildString {
                append("Local: ${stadium.homeTeam}")
                stadium.note?.let { append(" · $it") }
            },
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Field)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = "PRÓXIMO PARTIDO",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stadium.nextMatch,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Ver ubicación y cómo llegar",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Outlined.OpenInNew,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}
