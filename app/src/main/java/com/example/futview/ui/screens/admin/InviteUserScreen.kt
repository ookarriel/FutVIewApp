package com.example.futview.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.futview.ui.components.AdminBottomBar
import com.example.futview.ui.components.FormCard
import com.example.futview.ui.components.FormDropdown
import com.example.futview.ui.components.FormLabel
import com.example.futview.ui.components.FormTextField
import com.example.futview.ui.components.OutlineButton
import com.example.futview.ui.components.PrimaryButton
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextSecondary

@Composable
fun InviteUserScreen(
    onBack: () -> Unit,
    onSelectTab: (Int) -> Unit
) {
    SetStatusBarAppearance(lightIcons = true)

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf("Editor") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        ScreenHeader(
            title = "Invitar usuario",
            subtitle = "Da acceso al panel de administración.",
            onAvatarClick = onBack,
            onBack = onBack,
            userLabel = "Admin",
            userInitials = "CM"
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlineButton(
                    text = "Volver a usuarios",
                    onClick = onBack,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                FormCard(
                    title = "Invitación de usuario",
                    subtitle = "Acceso al panel",
                    icon = Icons.Outlined.ManageAccounts,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    FormLabel("Nombre *")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "Nombre completo",
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    )

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Correo electrónico *")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "nombre@ejemplo.cl",
                        keyboardType = KeyboardType.Email,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Mail,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Rol *")
                    Spacer(Modifier.height(8.dp))
                    FormDropdown(
                        value = role,
                        options = listOf("Editor", "Admin"),
                        onSelect = { role = it }
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Editor puede gestionar clubes, estadios, fixture y resultados.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlineButton(
                        text = "Cambiar rol a Admin",
                        onClick = { role = if (role == "Admin") "Editor" else "Admin" }
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Admin también puede gestionar los usuarios del sistema.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(Modifier.height(18.dp))

                    PrimaryButton(
                        text = "Enviar invitación",
                        onClick = {},
                        icon = Icons.Outlined.Send
                    )

                    Spacer(Modifier.height(10.dp))

                    OutlineButton(text = "Cancelar", onClick = onBack)
                }
            }

            item {
                Text(
                    text = "La persona recibirá un enlace para establecer su contraseña y acceder al panel.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        AdminBottomBar(selectedIndex = 4, onSelect = onSelectTab)
    }
}
