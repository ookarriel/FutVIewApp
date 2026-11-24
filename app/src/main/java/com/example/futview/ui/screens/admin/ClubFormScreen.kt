package com.example.futview.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.futview.data.Team
import com.example.futview.ui.components.FirestoreErrorBanner
import com.example.futview.ui.components.LoadingState
import com.example.futview.ui.viewmodel.EspnSyncViewModel
import com.example.futview.ui.viewmodel.ClubFormViewModel
import com.example.futview.ui.viewmodel.SeedViewModel
import com.example.futview.ui.viewmodel.UiState
import com.example.futview.ui.components.AdminFormActions
import com.example.futview.ui.components.AdminSectionHeader
import com.example.futview.ui.components.Crest
import com.example.futview.ui.components.FormCard
import com.example.futview.ui.components.FormLabel
import com.example.futview.ui.components.FormTextField
import com.example.futview.ui.components.InfoNote
import com.example.futview.ui.components.OutlineButton
import com.example.futview.ui.theme.Border
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Field
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary

@Composable
fun ClubFormScreen() {
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }

    val viewModel: ClubFormViewModel = viewModel(factory = ClubFormViewModel.Factory)
    val teamsState by viewModel.teams.collectAsStateWithLifecycle()
    val teams = (teamsState as? UiState.Success)?.data.orEmpty()

    var editingTeamId by rememberSaveable { mutableStateOf<String?>(null) }
    val message by viewModel.message.collectAsStateWithLifecycle()
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.consumeMessage()
        }
    }

    val seedViewModel: SeedViewModel = viewModel(factory = SeedViewModel.Factory)
    val seedBusy by seedViewModel.busy.collectAsStateWithLifecycle()
    val seedMessage by seedViewModel.message.collectAsStateWithLifecycle()
    LaunchedEffect(seedMessage) {
        seedMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            seedViewModel.consumeMessage()
        }
    }

    val espnSyncViewModel: EspnSyncViewModel = viewModel(factory = EspnSyncViewModel.Factory)
    val apiBusy by espnSyncViewModel.busy.collectAsStateWithLifecycle()
    val apiMessage by espnSyncViewModel.message.collectAsStateWithLifecycle()
    LaunchedEffect(apiMessage) {
        apiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            espnSyncViewModel.consumeMessage()
        }
    }

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

            if (teams.isEmpty() && teamsState is UiState.Success) {
                item {
                    OutlineButton(
                        text = if (seedBusy) "Cargando datos…" else "Cargar datos de la Liga 2026",
                        onClick = { seedViewModel.seed() },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            item {
                OutlineButton(
                    text = if (apiBusy) "Sincronizando…" else "Sincronizar con ESPN (fixture, resultados y tabla)",
                    onClick = { espnSyncViewModel.sync(context.applicationContext, force = true) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                FormCard(
                    title = "Ficha del club",
                    subtitle = if (editingTeamId != null) "Editando el club seleccionado" else "Nuevo registro",
                    icon = Icons.Outlined.Shield,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    FormLabel("Nombre del Club")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(value = name, onValueChange = { name = it })

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Ciudad")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(
                        value = city,
                        onValueChange = { city = it },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Place,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Competición")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(
                        value = "Primera División",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Escudo del club")
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Field)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Crest(initials = "CC", width = 46, height = 52)
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.clickable {
                                    Toast.makeText(context, "Selector de escudo", Toast.LENGTH_SHORT).show()
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Upload,
                                    contentDescription = null,
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Cargar Escudo",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = "PNG o SVG · Máx. 2 MB",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Iniciales del club",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    AdminFormActions(
                        onSave = {
                            val id = editingTeamId
                            if (id == null) {
                                viewModel.createTeam(name, city)
                            } else {
                                viewModel.updateTeam(id, name, city)
                            }
                        },
                        onEdit = {
                            Toast.makeText(
                                context,
                                if (editingTeamId != null) {
                                    "Editando el club seleccionado."
                                } else {
                                    "Toca un club de la lista para cargarlo al formulario."
                                },
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onDelete = {
                            editingTeamId?.let { viewModel.deleteTeam(it) }
                                ?: run {
                                    Toast.makeText(context, "Toca un club de la lista para eliminarlo.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    )
                }
            }

            item {
                AdminSectionHeader(
                    title = "Clubes creados",
                    subtitle = "Solo equipos de Primera División.",
                    trailing = "${teams.size} clubes"
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Card)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Field)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "CLUB / CIUDAD",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "ESTADO",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                    when (val s = teamsState) {
                        is UiState.Loading -> LoadingState()
                        is UiState.Error -> FirestoreErrorBanner(onRetry = viewModel::retry)
                        is UiState.Success ->
                            if (s.data.isEmpty()) {
                                Text(
                                    text = "No hay clubes registrados todavía.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp)
                                )
                            }
                    }
                    teams.forEachIndexed { index, team ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    editingTeamId = team.id
                                    name = team.name
                                    city = team.city
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Crest(initials = team.short, width = 30, height = 34)
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = team.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.height(1.dp))
                                Text(
                                    text = team.city,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = "Activo",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (index != teams.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                color = Border
                            )
                        }
                    }
                }
            }

            item {
                InfoNote(
                    text = "Selecciona un club de la lista para editarlo."
                )
            }
        }
    }
}
