package com.example.futview.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Place
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import com.example.futview.data.Match
import com.example.futview.data.Stadium
import com.example.futview.ui.components.DetailField
import com.example.futview.ui.components.FutViewBottomBar
import com.example.futview.ui.components.MatchCard
import com.example.futview.ui.components.OutlineButton
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.components.StarToggle
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Field
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary
import com.example.futview.ui.util.formatThousands

@Composable
fun StadiumDetailScreen(
    stadium: Stadium,
    nextMatch: Match?,
    favorite: Boolean,
    onToggleFavorite: () -> Unit,
    favoriteMatchIds: List<String>,
    onToggleMatchFavorite: (String) -> Unit,
    onBack: () -> Unit,
    onViewMatch: (String) -> Unit,
    onAvatarClick: () -> Unit,
    onSelectTab: (Int) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    // nextMatch llega calculado desde el repositorio (navigation/FutViewApp).

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Detalle del estadio",
            subtitle = "Todo para planificar tu próxima visita.",
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
                    text = "Volver a estadios",
                    onClick = onBack,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }


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
                        text = "Local: ${stadium.homeTeam}",
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
                            text = stadium.nextMatch.ifBlank { "Por confirmar" },
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
                        text = "Ubicación y acceso",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(16.dp))
                    DetailField(label = "Dirección", value = stadium.address)
                    Spacer(Modifier.height(14.dp))
                    DetailField(label = "Comuna", value = "${stadium.city} · Región Metropolitana")
                    Spacer(Modifier.height(14.dp))
                    DetailField(label = "Aforo de referencia", value = "${stadium.capacity.formatThousands()} personas")
                    Spacer(Modifier.height(14.dp))
                    DetailField(label = "Equipo local", value = stadium.homeTeam)
                    Spacer(Modifier.height(16.dp))

                    val context = LocalContext.current
                    val latitude = stadium.latitude.toDoubleOrNull()
                    val longitude = stadium.longitude.toDoubleOrNull()

                    // Mapa MapLibre + OpenFreeMap (gratuito, sin clave, permitido para apps).
                    if (latitude != null && longitude != null) {
                        AndroidView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            factory = { mapContext ->
                                MapView(mapContext).apply {
                                    getMapAsync { map ->
                                        map.setStyle("https://tiles.openfreemap.org/styles/liberty")
                                        map.moveCamera(
                                            CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 15.0)
                                        )
                                        map.addMarker(
                                            MarkerOptions()
                                                .position(LatLng(latitude, longitude))
                                                .title(stadium.name)
                                                .snippet(stadium.address)
                                        )
                                    }
                                }
                            }
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    OutlineButton(
                        text = "Cómo llegar en mapas",
                        onClick = {
                            val uri = if (latitude != null && longitude != null) {
                                Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
                            } else {
                                Uri.parse("geo:0,0?q=" + Uri.encode("${stadium.name} ${stadium.city}"))
                            }
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            } catch (error: Exception) {
                                Toast.makeText(context, "No se encontró una app de mapas.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Se abrirá tu aplicación de mapas. Consulta las condiciones de acceso antes de asistir.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            if (nextMatch != null) {
                item {
                    Text(
                        text = "Próximo encuentro",
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

            item {
                Text(
                    text = "Programación sincronizada desde Firestore.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        FutViewBottomBar(
            selectedIndex = 3,
            onSelect = onSelectTab
        )
    }
}
