package com.example.futview.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.futview.data.StandingRow
import com.example.futview.data.Team
import com.example.futview.ui.components.FirestoreErrorBanner
import com.example.futview.ui.components.LoadingState
import com.example.futview.ui.viewmodel.ActaViewModel
import com.example.futview.ui.viewmodel.UiState
import com.example.futview.ui.components.AdminSectionHeader
import com.example.futview.ui.components.Crest
import com.example.futview.ui.components.DemoBanner
import com.example.futview.ui.components.FormCard
import com.example.futview.ui.components.FormDropdown
import com.example.futview.ui.components.FormLabel
import com.example.futview.ui.components.InfoNote
import com.example.futview.ui.components.InfoPill
import com.example.futview.ui.components.PrimaryButton
import com.example.futview.ui.components.SegmentedControl
import com.example.futview.ui.components.StandingsTable
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Field
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary

@Composable
fun ActaScreen() {
    val context = LocalContext.current

    var selectedMatchId by rememberSaveable { mutableStateOf<String?>(null) }
    var homeGoals by rememberSaveable { mutableStateOf("") }
    var awayGoals by rememberSaveable { mutableStateOf("") }
    var statusIndex by rememberSaveable { mutableStateOf(2) }

    val viewModel: ActaViewModel = viewModel(factory = ActaViewModel.Factory)
    val matchesState by viewModel.matches.collectAsStateWithLifecycle()
    val standingsState by viewModel.standings.collectAsStateWithLifecycle()

    // TODO Fase 3: guardar el acta en Firestore al confirmar.
    val matches = (matchesState as? UiState.Success)?.data.orEmpty()
    val standings = (standingsState as? UiState.Success)?.data.orEmpty()

    val message by viewModel.message.collectAsStateWithLifecycle()
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.consumeMessage()
        }
    }
    val matchLabels = matches.associate { it.id to "${it.home.name} vs. ${it.away.name}" }
    val selectedMatch = matches.firstOrNull { it.id == selectedMatchId }
    val blankTeam = Team(id = "", name = "", short = "", city = "", stadium = "", lastResult = "", nextMatch = "")
    val home = selectedMatch?.home ?: blankTeam
    val away = selectedMatch?.away ?: blankTeam

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
                    title = "Acta del partido",
                    subtitle = "Jornada 1 · Resultado seleccionado",
                    icon = Icons.Outlined.EmojiEvents,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    FormLabel("Seleccionar Partido de Primera División")
                    Spacer(Modifier.height(8.dp))
                    FormDropdown(
                        value = matchLabels[selectedMatchId] ?: "",
                        options = matchLabels.values.toList(),
                        onSelect = { label ->
                            val id = matchLabels.entries.firstOrNull { it.value == label }?.key
                            selectedMatchId = id
                            matches.firstOrNull { it.id == id }?.let { selected ->
                                homeGoals = selected.homeGoals?.toString() ?: ""
                                awayGoals = selected.awayGoals?.toString() ?: ""
                            }
                        }
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = selectedMatch?.let { "${it.shortDate} · ${it.time} · ${it.stadium}" } ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Field)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        ScoreColumn(
                            initials = home.short,
                            teamName = home.name,
                            label = "Goles Local",
                            value = homeGoals,
                            onValueChange = { homeGoals = it },
                            modifier = Modifier.weight(1f)
                        )
                        ScoreColumn(
                            initials = away.short,
                            teamName = away.name,
                            label = "Goles Visitante",
                            value = awayGoals,
                            onValueChange = { awayGoals = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Estado del partido")
                    Spacer(Modifier.height(8.dp))
                    SegmentedControl(
                        options = listOf("Pendiente", "En Juego", "Finalizado"),
                        selectedIndex = statusIndex,
                        onSelect = { statusIndex = it }
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Solo los partidos finalizados suman a la tabla.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(Modifier.height(18.dp))

                    PrimaryButton(
                        text = "Actualizar Tabla",
                        icon = Icons.Outlined.Refresh,
                        onClick = {
                            viewModel.updateResult(selectedMatchId, homeGoals, awayGoals, statusIndex)
                        }
                    )
                }
            }

            when (standingsState) {
                is UiState.Loading -> item(key = "standings-state") { LoadingState() }
                is UiState.Error -> item(key = "standings-state") { FirestoreErrorBanner(onRetry = viewModel::retry) }
                else -> {}
            }

            item {
                AdminSectionHeader(
                    title = "Tabla de posiciones",
                    subtitle = "Primera División · Simulación de la jornada 1",
                    trailing = "Vista previa"
                )
            }

            item {
                InfoPill(
                    text = "${standings.count { it.played > 0 }} partidos finalizados · ${standings.size} clubes",
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
                    text = "Clasificación calculada a partir de los resultados registrados; los empates se ordenan por nombre."
                )
            }
        }
    }
}

@Composable
private fun ScoreColumn(
    initials: String,
    teamName: String,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Crest(initials = initials, width = 34, height = 40)
        Spacer(Modifier.height(8.dp))
        Text(
            text = teamName,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Card),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = { onValueChange(it.filter(Char::isDigit).take(2)) },
                singleLine = true,
                textStyle = MaterialTheme.typography.displayMedium.copy(
                    color = TextPrimary,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
