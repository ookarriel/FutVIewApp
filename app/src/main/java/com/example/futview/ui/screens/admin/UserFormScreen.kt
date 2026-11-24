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
import androidx.compose.material.icons.outlined.GppGood
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.futview.data.SystemUser
import com.example.futview.ui.components.FirestoreErrorBanner
import com.example.futview.ui.components.LoadingState
import com.example.futview.ui.viewmodel.UiState
import com.example.futview.ui.viewmodel.UserFormViewModel
import com.example.futview.ui.components.AdminFormActions
import com.example.futview.ui.components.AdminSectionHeader
import com.example.futview.ui.components.Avatar
import com.example.futview.ui.components.FormCard
import com.example.futview.ui.components.FormLabel
import com.example.futview.ui.components.FormTextField
import com.example.futview.ui.components.InfoNote
import com.example.futview.ui.components.InfoPill
import com.example.futview.ui.components.PrimaryButton
import com.example.futview.ui.components.RoleBadge
import com.example.futview.ui.components.SegmentedControl
import com.example.futview.ui.theme.Border
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Field
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary

@Composable
fun UserFormScreen(
    onInviteUser: () -> Unit = {}
) {
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val viewModel: UserFormViewModel = viewModel(factory = UserFormViewModel.Factory)
    val usersState by viewModel.users.collectAsStateWithLifecycle()
    val users = (usersState as? UiState.Success)?.data.orEmpty()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var roleIndex by rememberSaveable { mutableStateOf(0) }

    var selectedUid by rememberSaveable { mutableStateOf<String?>(null) }
    val message by viewModel.message.collectAsStateWithLifecycle()
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.consumeMessage()
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

            item {
                PrimaryButton(
                    text = "Invitar usuario",
                    onClick = onInviteUser,
                    icon = Icons.Outlined.PersonAdd,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                FormCard(
                    title = "Ficha del usuario",
                    subtitle = if (selectedUid != null) "Editando el rol del usuario seleccionado" else "Selecciona un usuario de la lista",
                    icon = Icons.Outlined.ManageAccounts,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    FormLabel("Nombre")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(
                        value = name,
                        onValueChange = { name = it },
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

                    FormLabel("Correo Electrónico")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(
                        value = email,
                        onValueChange = { email = it },
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

                    FormLabel("Contraseña")
                    Spacer(Modifier.height(8.dp))
                    FormTextField(
                        value = password,
                        onValueChange = { password = it },
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { passwordVisible = !passwordVisible },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Outlined.Visibility
                                    } else {
                                        Icons.Outlined.VisibilityOff
                                    },
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                        }
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Mínimo 8 caracteres. Se muestra enmascarada.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(Modifier.height(18.dp))

                    FormLabel("Rol del usuario")
                    Spacer(Modifier.height(8.dp))
                    SegmentedControl(
                        options = listOf("Admin", "Editor"),
                        selectedIndex = roleIndex,
                        onSelect = { roleIndex = it }
                    )

                    Spacer(Modifier.height(12.dp))

                    InfoPill(
                        text = "Admin: gestiona usuarios y todos los mantenedores.",
                        icon = Icons.Outlined.GppGood
                    )

                    Spacer(Modifier.height(18.dp))

                    AdminFormActions(
                        onSave = {
                            viewModel.updateRole(selectedUid, if (roleIndex == 0) "Admin" else "Editor")
                        },
                        onEdit = {
                            Toast.makeText(context, "Toca un usuario de la lista para cargar sus datos.", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = {
                            selectedUid?.let { viewModel.removeUser(it) }
                                ?: run {
                                    Toast.makeText(context, "Selecciona un usuario de la lista.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    )
                }
            }

            item {
                AdminSectionHeader(
                    title = "Usuarios del sistema",
                    subtitle = "${users.count { it.role == "Admin" }} administradores · ${users.count { it.role != "Admin" }} editores",
                    trailing = "${users.size} usuarios"
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
                            text = "NOMBRE / CORREO",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "ROL",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                    when (val s = usersState) {
                        is UiState.Loading -> LoadingState()
                        is UiState.Error -> FirestoreErrorBanner(onRetry = viewModel::retry)
                        is UiState.Success ->
                            if (s.data.isEmpty()) {
                                Text(
                                    text = "No hay usuarios registrados todavía.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp)
                                )
                            }
                    }
                    users.forEachIndexed { index, user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedUid = user.uid
                                    name = user.name
                                    email = user.email
                                    roleIndex = if (user.role == "Admin") 0 else 1
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Avatar(
                                initials = user.initials,
                                size = 34,
                                container = Field,
                                content = TextPrimary
                            )
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.height(1.dp))
                                Text(
                                    text = user.email,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            RoleBadge(user.role)
                        }
                        if (index != users.lastIndex) {
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
                    text = "Las contraseñas nunca aparecen en el listado. Editor puede gestionar clubes, estadios, fixture y resultados."
                )
            }
        }
    }
}
