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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.futview.data.model.initialsOf
import com.example.futview.ui.components.Avatar
import com.example.futview.ui.components.MenuItemRow
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.components.ToggleRow
import com.example.futview.ui.theme.Border
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.IsDarkTheme
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary

@Composable
fun UserMenuScreen(
    onClose: () -> Unit,
    isAdmin: Boolean = false,
    userName: String = "",
    userEmail: String = "",
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    SetStatusBarAppearance(lightIcons = IsDarkTheme)

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
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Layers,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
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
            ToggleRow(
                title = "Modo oscuro",
                subtitle = "Cambia la apariencia de la app",
                enabled = darkTheme,
                onToggle = { onDarkThemeChange(!darkTheme) }
            )
            if (isAdmin) {
                MenuItemRow(
                    icon = Icons.Outlined.AdminPanelSettings,
                    label = "Administración",
                    onClick = onAdmin
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        MenuItemRow(
            icon = Icons.Outlined.Logout,
            label = "Cerrar sesión",
            onClick = onLogout,
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(Modifier.height(24.dp))
    }
}
