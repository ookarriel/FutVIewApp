package com.example.futview.ui.screens.admin

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.futview.data.FixtureDay
import com.example.futview.data.Match
import com.example.futview.ui.components.FirestoreErrorBanner
import com.example.futview.ui.components.LoadingState
import com.example.futview.ui.viewmodel.MatchFormViewModel
import com.example.futview.ui.viewmodel.UiState
import com.example.futview.ui.components.AdminFormActions
import com.example.futview.ui.components.AdminSectionHeader
import com.example.futview.ui.components.DemoBanner
import com.example.futview.ui.components.FormCard
import com.example.futview.ui.components.FormDropdown
import com.example.futview.ui.components.FormLabel
import com.example.futview.ui.components.FormTextField
import com.example.futview.ui.components.InfoNote
import com.example.futview.ui.components.StatusChip
import com.example.futview.ui.theme.Border
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Field
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary

@Composable
fun MatchFormScreen(
    onDelete: () -> Unit = {}
) {
    val context = LocalContext.current

    var date by rememberSaveable { mutableStateOf("") }
    var time by rememberSaveable { mutableStateOf("") }
    var stadium by rememberSaveable { mutableStateOf("") }
    var homeTeam by rememberSaveable { mutableStateOf("") }
    var awayTeam by rememberSaveable { mutableStateOf("") }

    val viewModel: MatchFormViewModel = viewModel(factory = MatchFormViewModel.Factory)
    val teamsState by viewModel.teams.collectAsStateWithLifecycle()
    val stadiumsState by viewModel.stadiums.collectAsStateWithLifecycle()
    val fixtureState by viewModel.fixtureDays.collectAsStateWithLifecycle()
    val teams = (teamsState as? UiState.Success)?.data.orEmpty()
    val stadiums = (stadiumsState as? UiState.Success)?.data.orEmpty()
    val fixtureDays = (fixtureState as? UiState.Success)?.data.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { DemoBanner() }

            item {
                FormCard(
                    title = "Ficha del partido",
                    subtitle = "Jornada 2 · Partido seleccionado",
                    icon = Icons.Outlined.CalendarMonth,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            FormLabel("Fecha")
                            Spacer(Modifier.height(8.dp))
                            FormTextField(
                                value = date,
                                onValueChange = { date = it },
                                keyboardType = KeyboardType.Number,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.CalendarToday,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            FormLabel("Hora")
                            Spacer(Modifier.height(8.dp))
                            FormTextField(
                                value = time,
                                onValueChange = { time = it },
                                keyboardType = KeyboardType.Number,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.AccessTime,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Estadio")
                    Spacer(Modifier.height(8.dp))
                    FormDropdown(
                        value = stadium,
                        options = stadiums.map { it.name },
                        onSelect = { stadium = it }
                    )

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Equipo Local")
                    Spacer(Modifier.height(8.dp))
                    FormDropdown(
                        value = homeTeam,
                        options = teams.map { it.name },
                        onSelect = { homeTeam = it }
                    )

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Equipo Visitante")
                    Spacer(Modifier.height(8.dp))
                    FormDropdown(
                        value = awayTeam,
                        options = teams.map { it.name },
                        onSelect = { awayTeam = it }
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Solo clubes de Primera División · Hora de Chile",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    AdminFormActions(
                        onSave = {
                            Toast.makeText(context, "Partido guardado", Toast.LENGTH_SHORT).show()
                        },
                        onEdit = {
                            Toast.makeText(context, "Edición habilitada", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = onDelete
                    )
                }
            }

            item {
                AdminSectionHeader(
                    title = "Fixture cronológico",
                    subtitle = "Próximas jornadas del campeonato",
                    trailing = "${fixtureDays.size} partidos"
                )
            }

            when (fixtureState) {
                is UiState.Loading -> item(key = "fixture-state") { LoadingState() }
                is UiState.Error -> item(key = "fixture-state") { FirestoreErrorBanner(onRetry = viewModel::retry) }
                else -> {}
            }

            items(fixtureDays, key = { it.dateLabel }) { day ->
                FixtureDayCard(
                    day = day,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                InfoNote(
                    text = "Calendario ficticio, no oficial. El partido resaltado corresponde al formulario."
                )
            }
        }
    }
}

@Composable
private fun FixtureDayCard(
    day: FixtureDay,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Card)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Field)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = day.dateLabel,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "Jornada ${day.matchday}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        day.matches.forEachIndexed { index, match ->
            FixtureRow(match = match)
            if (index != day.matches.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    color = Border
                )
            }
        }
    }
}

@Composable
private fun FixtureRow(match: Match) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.width(54.dp)) {
            Text(
                text = match.time,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Chile",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = match.home.name,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = match.away.name,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = match.stadium,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Spacer(Modifier.height(8.dp))
            StatusChip(text = match.status.label)
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = if (match.homeGoals != null && match.awayGoals != null) {
                "${match.homeGoals} – ${match.awayGoals}"
            } else {
                "–"
            },
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}
