package com.example.futview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.futview.data.model.initialsOf
import com.example.futview.ui.components.Avatar
import com.example.futview.ui.components.MenuItemRow
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.theme.Border
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Ink
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary

@Composable
fun UserMenuScreen(
    onClose: () -> Unit,
    isAdmin: Boolean = false,
    userName: String = "",
    userEmail: String = "",
    onAdmin: () -> Unit,
    onStatus: () -> Unit = {},
    onLogout: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onNotifications: () -> Unit,
    onHelp: () -> Unit,
    onContact: () -> Unit
) {
    SetStatusBarAppearance(lightIcons = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(18.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Ink),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Layers,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = "FutView",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Border, RoundedCornerShape(10.dp))
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Cerrar menú",
                    tint = TextPrimary,
                    modifier = Modifier.size(19.dp)
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(initials = initialsOf(userName), size = 56)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = userName.ifBlank { "Usuario" },
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        Spacer(Modifier.height(26.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MenuItemRow(
                icon = Icons.Outlined.Person,
                label = "Mi perfil",
                onClick = onProfile
            )
            MenuItemRow(
                icon = Icons.Outlined.CreditCard,
                label = "Mi cuenta",
                onClick = onProfile
            )
            MenuItemRow(
                icon = Icons.Outlined.Tune,
                label = "Configuración",
                onClick = onSettings
            )
            MenuItemRow(
                icon = Icons.Outlined.Notifications,
                label = "Notificaciones",
                badge = "3",
                onClick = onNotifications
            )
            if (isAdmin) {
                MenuItemRow(
                    icon = Icons.Outlined.AdminPanelSettings,
                    label = "Administración",
                    onClick = onAdmin
                )
                MenuItemRow(
                    icon = Icons.Outlined.Info,
                    label = "Estado del sistema",
                    onClick = onStatus
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MenuItemRow(
                icon = Icons.Outlined.HelpOutline,
                label = "Centro de ayuda",
                onClick = onHelp
            )
            MenuItemRow(
                icon = Icons.Outlined.ChatBubbleOutline,
                label = "Contactar con soporte",
                onClick = onContact
            )
        }

        Spacer(Modifier.height(18.dp))

        MenuItemRow(
            icon = Icons.Outlined.Logout,
            label = "Cerrar sesión",
            onClick = onLogout
        )

        Spacer(Modifier.height(30.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Tu espacio, a tu manera.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "v. 2.8",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}
