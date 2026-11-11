package com.example.futview.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.futview.data.FutViewData
import com.example.futview.ui.components.AdminFormActions
import com.example.futview.ui.components.AdminSectionHeader
import com.example.futview.ui.components.DemoBanner
import com.example.futview.ui.components.FormCard
import com.example.futview.ui.components.FormLabel
import com.example.futview.ui.components.FormTextField
import com.example.futview.ui.components.InfoNote
import com.example.futview.ui.theme.Border
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Field
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary
import com.example.futview.ui.util.formatThousands

@Composable
fun StadiumFormScreen() {
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("Monumental") }
    var address by rememberSaveable { mutableStateOf("Av. Marathon 5300, Macul") }
    var capacity by rememberSaveable { mutableStateOf("47347") }
    var latitude by rememberSaveable { mutableStateOf("-33.5066") }
    var longitude by rememberSaveable { mutableStateOf("-70.6054") }
    var enabled by rememberSaveable { mutableStateOf(true) }

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
                    title = "Ficha del estadio",
                    subtitle = "Registro seleccionado · Monumental",
                    icon = Icons.Outlined.AccountBalance,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    FormLabel("Nombre del Estadio")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(value = name, onValueChange = { name = it })

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Dirección")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(
                        value = address,
                        onValueChange = { address = it },
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

                    FormLabel("Capacidad")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(
                        value = capacity,
                        onValueChange = { capacity = it.filter(Char::isDigit).take(6) },
                        keyboardType = KeyboardType.Number,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Groups,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Aforo en personas · Valor de demostración",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(Modifier.height(18.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.GpsFixed,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(7.dp))
                        FormLabel("Latitud y Longitud GPS")
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            FormLabel("Latitud")
                            Spacer(Modifier.height(8.dp))
                            FormTextField(
                                value = latitude,
                                onValueChange = { latitude = it },
                                keyboardType = KeyboardType.Number
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            FormLabel("Longitud")
                            Spacer(Modifier.height(8.dp))
                            FormTextField(
                                value = longitude,
                                onValueChange = { longitude = it },
                                keyboardType = KeyboardType.Number
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Grados decimales · Sistema WGS 84",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(Modifier.height(18.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Field)
                            .clickable { enabled = !enabled }
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = if (enabled) TextPrimary else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Habilitado para el fixture",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (enabled) "Sí" else "No",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    AdminFormActions(
                        onSave = {
                            Toast.makeText(context, "Estadio guardado", Toast.LENGTH_SHORT).show()
                        },
                        onEdit = {
                            Toast.makeText(context, "Edición habilitada", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = {
                            Toast.makeText(context, "Estadio eliminado", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            item {
                AdminSectionHeader(
                    title = "Estadios habilitados",
                    subtitle = "Sedes disponibles para programar partidos.",
                    trailing = "4 sedes"
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
                            text = "ESTADIO / COMUNA",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "AFORO",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                    FutViewData.stadiums.forEachIndexed { index, stadium ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stadium.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "${stadium.city} · Habilitado",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = stadium.capacity.formatThousands(),
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary
                            )
                        }
                        if (index != FutViewData.stadiums.lastIndex) {
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
                    text = "Aforos y coordenadas de ejemplo. Valida la sede antes de publicar un partido."
                )
            }
        }
    }
}
