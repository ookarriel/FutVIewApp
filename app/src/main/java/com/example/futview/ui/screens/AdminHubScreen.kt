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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Stadium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.futview.ui.components.AdminSectionHeader
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Field
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary
import com.example.futview.ui.theme.TextTertiary

@Composable
fun AdminHubScreen(
    onBack: () -> Unit,
    onOpenMatch: () -> Unit,
    onOpenActa: () -> Unit,
    onOpenClub: () -> Unit,
    onOpenStadium: () -> Unit,
    onOpenUser: () -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Administración",
            subtitle = "Gestiona la información de la liga.",
            onAvatarClick = {},
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item {
                AdminSectionHeader(
                    title = "Mantenedores",
                    subtitle = "Fichas para administrar la competencia.",
                    trailing = "5 fichas"
                )
            }

            item {
                AdminHubCard(
                    icon = Icons.Outlined.CalendarMonth,
                    title = "Ficha del partido",
                    subtitle = "Programa el fixture y sus horarios",
                    onClick = onOpenMatch,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            item {
                AdminHubCard(
                    icon = Icons.Outlined.EmojiEvents,
                    title = "Acta del partido",
                    subtitle = "Registra el resultado y actualiza la tabla",
                    onClick = onOpenActa,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            item {
                AdminHubCard(
                    icon = Icons.Outlined.Shield,
                    title = "Ficha del club",
                    subtitle = "Edita los clubes de la competencia",
                    onClick = onOpenClub,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            item {
                AdminHubCard(
                    icon = Icons.Outlined.Stadium,
                    title = "Ficha del estadio",
                    subtitle = "Administra sedes, aforos y ubicaciones",
                    onClick = onOpenStadium,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            item {
                AdminHubCard(
                    icon = Icons.Outlined.AdminPanelSettings,
                    title = "Ficha del usuario",
                    subtitle = "Gestiona usuarios y roles del sistema",
                    onClick = onOpenUser,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun AdminHubCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Card)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Field),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(23.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(20.dp)
        )
    }
}
