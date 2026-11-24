package com.example.futview.ui.screens

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Tune
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.futview.ui.components.Avatar
import com.example.futview.ui.components.FormDropdown
import com.example.futview.ui.components.FormLabel
import com.example.futview.ui.components.FormTextField
import com.example.futview.ui.components.FutViewBottomBar
import com.example.futview.ui.components.FutViewSearchField
import com.example.futview.ui.components.OutlineButton
import com.example.futview.ui.components.PrimaryButton
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.components.ToggleRow
import com.example.futview.ui.theme.Border
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Field
import com.example.futview.ui.theme.Ink
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary
import com.example.futview.ui.theme.TextTertiary

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onViewFavorites: () -> Unit,
    onSelectTab: (Int) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Mi perfil",
            subtitle = "Gestiona tus datos personales.",
            onAvatarClick = onBack,
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Card)
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(initials = "AG", size = 52)
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Ana García",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Cuenta de usuario · Chile",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Nombre completo")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(value = name, onValueChange = { name = it })

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Correo electrónico")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(
                        value = email,
                        onValueChange = { email = it },
                        leadingIcon = Icons.Outlined.Mail,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(Modifier.height(18.dp))

                    PrimaryButton(text = "Guardar cambios", onClick = {})
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Card)
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Tu fútbol en FutView",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "0 equipos · 0 partidos · 0 estadios",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Aún no sigues favoritos. Explora equipos, partidos y estadios para seguir tu fútbol.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(14.dp))
                    OutlineButton(text = "Ver mis favoritos", onClick = onViewFavorites)
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Card)
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Configuración de la cuenta",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Solo tú puedes ver y editar tus datos personales.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        FutViewBottomBar(selectedIndex = -1, onSelect = onSelectTab)
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit,
    onSelectTab: (Int) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    var language by rememberSaveable { mutableStateOf("Español · Chile") }
    var timezone by rememberSaveable { mutableStateOf("Chile continental") }
    var matchAlerts by rememberSaveable { mutableStateOf(true) }
    var resultAlerts by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Configuración",
            subtitle = "Personaliza tu experiencia.",
            onAvatarClick = onBack,
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Card)
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Preferencias generales",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(16.dp))

                    FormLabel("Idioma")
                    Spacer(Modifier.height(8.dp))
                    FormDropdown(
                        value = language,
                        options = listOf("Español · Chile", "English", "Português"),
                        onSelect = { language = it }
                    )

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Zona horaria")
                    Spacer(Modifier.height(8.dp))
                    FormDropdown(
                        value = timezone,
                        options = listOf("Chile continental", "Chile insular"),
                        onSelect = { timezone = it }
                    )

                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Los partidos se muestran en la hora de Chile continental.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            item {
                ToggleRow(
                    title = "Avisos de partidos",
                    subtitle = "Recordatorios 30 minutos antes del inicio.",
                    enabled = matchAlerts,
                    onToggle = { matchAlerts = !matchAlerts },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                ToggleRow(
                    title = "Resultados de mis equipos",
                    subtitle = "Recibe el marcador al finalizar el partido.",
                    enabled = resultAlerts,
                    onToggle = { resultAlerts = !resultAlerts },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                PrimaryButton(
                    text = "Guardar preferencias",
                    onClick = {},
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
                ) {
                    Text(
                        text = "Cuenta y seguridad",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(18.dp)
                    )
                    HorizontalDivider(color = Border)
                    SettingsRow(
                        icon = Icons.Outlined.Lock,
                        label = "Cambiar contraseña",
                        onClick = onChangePassword
                    )
                    HorizontalDivider(color = Border, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        icon = Icons.Outlined.Tune,
                        label = "Cerrar sesión",
                        onClick = onLogout
                    )
                }
            }

            item {
                Text(
                    text = "FutView · v. 2.8",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        FutViewBottomBar(selectedIndex = -1, onSelect = onSelectTab)
    }
}

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onViewMatch: () -> Unit,
    onViewResult: () -> Unit,
    onExploreTeams: () -> Unit,
    onSelectTab: (Int) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Notificaciones",
            subtitle = "Novedades de tus equipos y partidos.",
            onAvatarClick = onBack,
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "3 notificaciones nuevas",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Marcar todas como leídas",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                NotificationCard(
                    icon = Icons.Outlined.StarBorder,
                    meta = "NUEVA · 12 FEB · 10:00",
                    title = "Tu fútbol, a tu manera",
                    body = "Sigue equipos, partidos y estadios para tenerlos siempre a mano.",
                    action = "Explorar equipos",
                    onClick = onExploreTeams,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Text(
                    text = "Avisos de ejemplo · Calendario ficticio de 2027.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        FutViewBottomBar(selectedIndex = -1, onSelect = onSelectTab)
    }
}

@Composable
fun HelpCenterScreen(
    onBack: () -> Unit,
    onContact: () -> Unit,
    onSelectTab: (Int) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    var query by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Centro de ayuda",
            subtitle = "Resuelve tus dudas sobre FutView.",
            onAvatarClick = onBack,
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                FutViewSearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Escribe tu pregunta",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Text(
                    text = "Preguntas frecuentes",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                FaqCard(
                    question = "¿Cómo guardo un favorito?",
                    answer = "Toca la estrella de un equipo, partido o estadio y quedará guardado en tu cuenta.",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            item {
                FaqCard(
                    question = "¿En qué horario se muestran los partidos?",
                    answer = "Usamos la hora de Chile continental. El calendario es ficticio y de demostración.",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            item {
                FaqCard(
                    question = "¿Cómo recupero mi contraseña?",
                    answer = "Desde Iniciar sesión, elige la opción de recuperar acceso y sigue los pasos.",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            item {
                FaqCard(
                    question = "¿Los resultados son oficiales?",
                    answer = "No. Esta versión utiliza datos de demostración de la temporada 2027.",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                OutlineButton(
                    text = "Contactar con soporte",
                    onClick = onContact,
                    icon = Icons.Outlined.ChatBubbleOutline,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        FutViewBottomBar(selectedIndex = -1, onSelect = onSelectTab)
    }
}

@Composable
fun ContactSupportScreen(
    onBack: () -> Unit,
    onSelectTab: (Int) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    var email by rememberSaveable { mutableStateOf("") }
    var reason by rememberSaveable { mutableStateOf("Problema con mi cuenta") }
    var subject by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Contactar con soporte",
            subtitle = "Estamos aquí para ayudarte.",
            onAvatarClick = onBack,
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Card)
                        .padding(18.dp)
                ) {
                    FormLabel("Tu correo")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(
                        value = email,
                        onValueChange = { email = it },
                        leadingIcon = Icons.Outlined.Mail,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Motivo")
                    Spacer(Modifier.height(8.dp))
                    FormDropdown(
                        value = reason,
                        options = listOf(
                            "Problema con mi cuenta",
                            "Reportar un error",
                            "Sugerencia",
                            "Otro"
                        ),
                        onSelect = { reason = it }
                    )

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Asunto")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        placeholder = "No puedo actualizar mi perfil"
                    )

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Mensaje")
                    Spacer(Modifier.height(8.dp))
                    MultilineField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = "Describe qué ocurrió y qué intentabas hacer."
                    )

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "No incluyas contraseñas ni información sensible.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(Modifier.height(18.dp))

                    PrimaryButton(
                        text = "Enviar solicitud",
                        onClick = {},
                        icon = Icons.Outlined.Send
                    )

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Recibirás la respuesta en el correo indicado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            item {
                OutlineButton(
                    text = "Volver al centro de ayuda",
                    onClick = onBack,
                    icon = Icons.Outlined.HelpOutline,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        FutViewBottomBar(selectedIndex = -1, onSelect = onSelectTab)
    }
}

// ---- Helpers ----

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(19.dp)
        )
    }
}

@Composable
private fun NotificationCard(
    icon: ImageVector,
    meta: String,
    title: String,
    body: String,
    action: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Card)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Field),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = meta,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = action,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

@Composable
private fun FaqCard(
    question: String,
    answer: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .padding(16.dp)
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = answer,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun MultilineField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
        cursorBrush = SolidColor(Ink),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Field)
            .padding(14.dp)
    ) { inner ->
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = TextTertiary
            )
        }
        inner()
    }
}
