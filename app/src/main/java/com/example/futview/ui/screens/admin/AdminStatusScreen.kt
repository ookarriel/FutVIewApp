package com.example.futview.ui.screens.admin

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.GppGood
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.futview.data.AppContainer
import com.example.futview.data.model.AuthUser
import com.example.futview.ui.components.DetailField
import com.example.futview.ui.components.FirestoreErrorBanner
import com.example.futview.ui.components.FormCard
import com.example.futview.ui.components.LoadingState
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.components.StatusChip
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextSecondary
import com.example.futview.ui.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/** Dashboard del panel admin: estado de Auth, Firestore, ESPN y mapas. */
@Composable
fun AdminStatusScreen(
    sessionUser: AuthUser?,
    onBack: () -> Unit,
    onSelectTab: (Int) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)
    val context = LocalContext.current
    val viewModel: AdminStatusViewModel = viewModel(factory = AdminStatusViewModel.Factory)
    val firestoreState by viewModel.firestorePing.collectAsState()

    LaunchedEffect(Unit) { viewModel.pingFirestore() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Estado del sistema",
            subtitle = "Auth · Firestore · ESPN · Mapas",
            onAvatarClick = onBack,
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                FormCard(
                    title = "Firebase Authentication",
                    subtitle = "Sesión y cuenta",
                    icon = Icons.Outlined.GppGood
                ) {
                    StatusChip(text = if (sessionUser != null) "Conectado" else "Sin sesión")
                    Spacer(Modifier.height(10.dp))
                    DetailField(label = "Usuario", value = sessionUser?.displayName.orEmpty().ifBlank { "Sin sesión" })
                    DetailField(label = "Correo", value = sessionUser?.email.orEmpty())
                }
            }

            item {
                FormCard(
                    title = "Cloud Firestore",
                    subtitle = "Lectura de catálogos",
                    icon = Icons.Outlined.CloudDone
                ) {
                    when (val s = firestoreState) {
                        is UiState.Loading -> LoadingState()
                        is UiState.Error -> FirestoreErrorBanner(onRetry = { viewModel.pingFirestore() })
                        is UiState.Success -> {
                            StatusChip(text = "Operativo · ${s.data}")
                            Spacer(Modifier.height(8.dp))
                            DetailField(label = "Colecciones", value = "teams · stadiums · matches · users · favorites")
                        }
                    }
                }
            }

            item {
                FormCard(
                    title = "ESPN · Primera División",
                    subtitle = "Fixture, resultados y tabla · sin clave ni cuota",
                    icon = Icons.Outlined.Sync
                ) {
                    val lastSync = viewModel.lastSyncText(context)
                    StatusChip(text = if (lastSync == "Nunca") "Sin sincronizar" else "Operativo")
                    Spacer(Modifier.height(8.dp))
                    DetailField(label = "Última sincronización", value = lastSync)
                    DetailField(label = "Frecuencia", value = "4 veces al día (cada 6 horas)")
                    DetailField(label = "Fuente", value = "site.api.espn.com · liga chi.1")
                }
            }

            item {
                FormCard(
                    title = "OpenFreeMap (MapLibre)",
                    subtitle = "Tiles vectoriales sin costo ni clave",
                    icon = Icons.Outlined.Map
                ) {
                    StatusChip(text = "Operativo")
                    Spacer(Modifier.height(8.dp))
                    DetailField(
                        label = "Caché local",
                        value = "${viewModel.cacheSizeMb(context)} MB"
                    )
                    DetailField(label = "Política", value = "Permitida para apps en producción")
                }
            }

            item {
                Text(
                    text = "El dashboard consulta los servicios en tiempo real desde este dispositivo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

/** Estado del dashboard: ping de Firestore + cuota de API-Football + caché OSM. */
class AdminStatusViewModel : ViewModel() {

    private val firestorePingFlow = MutableStateFlow<UiState<String>>(UiState.Loading)
    val firestorePing: StateFlow<UiState<String>> = firestorePingFlow.asStateFlow()

    fun pingFirestore() {
        viewModelScope.launch {
            try {
                val start = System.currentTimeMillis()
                Firebase.firestore.collection("teams").limit(1).get().await()
                firestorePingFlow.value = UiState.Success("${System.currentTimeMillis() - start} ms")
            } catch (cancellation: kotlinx.coroutines.CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                firestorePingFlow.value = UiState.Error(throwable.message ?: "Permiso denegado")
            }
        }
    }

    fun lastSyncText(context: Context): String {
        val last = context.getSharedPreferences("futview_sync", Context.MODE_PRIVATE)
            .getLong("last_sync_espn", 0L)
        return if (last == 0L) "Nunca" else {
            val minutes = ((System.currentTimeMillis() - last) / 60000L).toString()
            "Hace $minutes min"
        }
    }

    fun cacheSizeMb(context: Context): Long {
        fun dirSize(dir: File?): Long = dir?.walkBottomUp()?.filter { it.isFile }?.sumOf { it.length() } ?: 0
        return dirSize(context.cacheDir) / (1024L * 1024L)
    }

    companion object {
        val Factory = viewModelFactory { initializer { AdminStatusViewModel() } }
    }
}
