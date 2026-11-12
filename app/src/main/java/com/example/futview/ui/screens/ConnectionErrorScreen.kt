package com.example.futview.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.futview.ui.components.EmptyState
import com.example.futview.ui.components.FutViewBottomBar
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextSecondary

@Composable
fun ConnectionErrorScreen(
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenHelp: () -> Unit,
    onSelectTab: (Int) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Error de conexión",
            subtitle = "Comprueba tu red y vuelve a intentarlo.",
            onAvatarClick = onBack,
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                EmptyState(
                    icon = Icons.Outlined.CloudOff,
                    title = "No pudimos conectar",
                    message = "Comprueba tu conexión a internet y vuelve a intentarlo. Tus favoritos no se han eliminado; podrás consultarlos cuando recuperes la conexión.",
                    primaryText = "Volver a intentar",
                    onPrimary = onRetry,
                    secondaryText = "Abrir centro de ayuda",
                    onSecondary = onOpenHelp
                )
            }

            item {
                Text(
                    text = "No se muestran datos antiguos para evitar confusiones con el calendario vigente.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        FutViewBottomBar(selectedIndex = -1, onSelect = onSelectTab)
    }
}
