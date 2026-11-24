package com.example.futview.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.futview.ui.components.BrandMark
import com.example.futview.ui.components.FormLabel
import com.example.futview.ui.components.FormTextField
import com.example.futview.ui.components.OutlineButton
import com.example.futview.ui.components.PrimaryButton
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Field
import com.example.futview.ui.theme.Ink
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary
import com.example.futview.ui.theme.TextTertiary

/** Cabecera común a las pantallas de acceso (marca + volver + título). */
@Composable
private fun AccessHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Volver",
                tint = TextPrimary,
                modifier = Modifier.size(21.dp)
            )
        }
        Spacer(Modifier.width(10.dp))
        BrandMark(
            container = Color.White,
            content = Ink,
            letterSize = 20,
            width = 32,
            height = 38
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = "FutView",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(Modifier.height(30.dp))

    Text(
        text = title,
        style = MaterialTheme.typography.displaySmall,
        color = TextPrimary
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary
    )
}

@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    onLogin: () -> Unit
) {
    SetStatusBarAppearance(lightIcons = false)

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var visible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        AccessHeader(
            title = "Crear cuenta",
            subtitle = "Tu fútbol comienza aquí.",
            onBack = onBack
        )

        Spacer(Modifier.height(26.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Card)
                .padding(20.dp)
        ) {
            FormLabel("Nombre completo")
            Spacer(Modifier.height(8.dp))
            FormTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Ana García",
                keyboardType = KeyboardType.Text
            )

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

            FormLabel("Contraseña")
            Spacer(Modifier.height(8.dp))
            FormTextField(
                value = password,
                onValueChange = { password = it },
                leadingIcon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (visible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { visible = !visible },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (visible) {
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
                text = "Usa al menos 8 caracteres, una letra y un número.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(Modifier.height(18.dp))

            FormLabel("Confirmar contraseña")
            Spacer(Modifier.height(8.dp))
            FormTextField(
                value = confirm,
                onValueChange = { confirm = it },
                leadingIcon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (visible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Al crear tu cuenta aceptas los Términos de uso y la Política de privacidad de FutView.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = "Crear mi cuenta",
                onClick = onLogin
            )

            Spacer(Modifier.height(12.dp))

            OutlineButton(
                text = "Ya tengo cuenta · Iniciar sesión",
                onClick = onLogin
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Tu cuenta permite seguir clubes, partidos y estadios desde cualquier dispositivo.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
fun RecoverAccessScreen(
    onBack: () -> Unit,
    onSent: () -> Unit
) {
    SetStatusBarAppearance(lightIcons = false)

    var email by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        AccessHeader(
            title = "Recuperar acceso",
            subtitle = "Te ayudamos a volver a tu cuenta.",
            onBack = onBack
        )

        Spacer(Modifier.height(26.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Card)
                .padding(20.dp)
        ) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Escribe el correo de tu cuenta. Te enviaremos un enlace para restablecerla.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(Modifier.height(18.dp))

            FormLabel("Correo electrónico")
            Spacer(Modifier.height(8.dp))
            FormTextField(
                value = email,
                onValueChange = { email = it },
                leadingIcon = Icons.Outlined.Mail,
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = "Enviar enlace de recuperación",
                onClick = onSent,
                icon = Icons.Outlined.Send
            )

            Spacer(Modifier.height(12.dp))

            OutlineButton(
                text = "Volver a iniciar sesión",
                onClick = onBack
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Por seguridad, no confirmamos si un correo está registrado en FutView.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
fun CheckEmailScreen(
    email: String,
    onBack: () -> Unit,
    onResend: () -> Unit,
    onLogin: () -> Unit
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
        Spacer(Modifier.height(20.dp))

        AccessHeader(
            title = "Revisa tu correo",
            subtitle = "Te enviamos un enlace de recuperación.",
            onBack = onBack
        )

        Spacer(Modifier.height(26.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Card)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(Field),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Solicitud recibida",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Si existe una cuenta asociada a $email, recibirás un enlace para crear una nueva contraseña.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Revisa también la carpeta de spam. El enlace caduca en 30 minutos.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(Modifier.height(22.dp))

            PrimaryButton(
                text = "Reenviar enlace",
                onClick = onResend,
                icon = Icons.Outlined.Send
            )

            Spacer(Modifier.height(12.dp))

            OutlineButton(
                text = "Usar otro correo electrónico",
                onClick = onBack
            )

            Spacer(Modifier.height(12.dp))

            OutlineButton(
                text = "Volver a iniciar sesión",
                onClick = onLogin
            )
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
fun NewPasswordScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    SetStatusBarAppearance(lightIcons = false)

    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var visible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        AccessHeader(
            title = "Nueva contraseña",
            subtitle = "Protege tu cuenta con una nueva clave.",
            onBack = onBack
        )

        Spacer(Modifier.height(26.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Card)
                .padding(20.dp)
        ) {
            FormLabel("Nueva contraseña")
            Spacer(Modifier.height(8.dp))
            FormTextField(
                value = password,
                onValueChange = { password = it },
                leadingIcon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (visible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { visible = !visible },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (visible) {
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

            Spacer(Modifier.height(18.dp))

            FormLabel("Confirmar nueva contraseña")
            Spacer(Modifier.height(8.dp))
            FormTextField(
                value = confirm,
                onValueChange = { confirm = it },
                leadingIcon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                visualTransformation = if (visible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Mínimo 8 caracteres. Incluye al menos una letra y un número.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = "Guardar nueva contraseña",
                onClick = onSaved
            )

            Spacer(Modifier.height(12.dp))

            OutlineButton(
                text = "Volver a iniciar sesión",
                onClick = onBack
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Al guardar, podrás iniciar sesión con tu nueva contraseña.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(28.dp))
    }
}
