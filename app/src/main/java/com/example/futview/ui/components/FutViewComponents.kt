package com.example.futview.ui.components

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.futview.data.Match
import com.example.futview.data.MatchStatus
import com.example.futview.data.StandingRow
import com.example.futview.data.Team
import com.example.futview.ui.theme.Border
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.CrestBackground
import com.example.futview.ui.theme.Field
import com.example.futview.ui.theme.FieldStrong
import com.example.futview.ui.theme.Ink
import com.example.futview.ui.theme.InkElevated
import com.example.futview.ui.theme.Page
import com.example.futview.ui.theme.Success
import com.example.futview.ui.theme.SuccessSoft
import com.example.futview.ui.theme.TextOnDark
import com.example.futview.ui.theme.TextOnDarkMuted
import com.example.futview.ui.theme.TextPrimary
import com.example.futview.ui.theme.TextSecondary
import com.example.futview.ui.theme.TextTertiary
import androidx.compose.runtime.SideEffect

/** Forma de escudo usada por el logo y los escudos de los clubes. */
val FutViewShieldShape: GenericShape = GenericShape { size, _ ->
    moveTo(size.width * 0.10f, 0f)
    lineTo(size.width * 0.90f, 0f)
    quadraticTo(size.width, 0f, size.width, size.height * 0.12f)
    lineTo(size.width, size.height * 0.56f)
    quadraticTo(
        size.width,
        size.height * 0.80f,
        size.width * 0.50f,
        size.height
    )
    quadraticTo(0f, size.height * 0.80f, 0f, size.height * 0.56f)
    lineTo(0f, size.height * 0.12f)
    quadraticTo(0f, 0f, size.width * 0.10f, 0f)
    close()
}

@Composable
fun SetStatusBarAppearance(lightIcons: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity() ?: return@SideEffect
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars =
                !lightIcons
        }
    }
}

private fun android.content.Context.findActivity(): Activity? {
    var context: android.content.Context = this
    while (context is android.content.ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun BrandMark(
    modifier: Modifier = Modifier,
    container: Color = Color.White,
    content: Color = Ink,
    letterSize: Int = 18,
    width: Int = 28,
    height: Int = 33
) {
    Box(
        modifier = modifier
            .size(width = width.dp, height = height.dp)
            .clip(FutViewShieldShape)
            .background(container),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "F",
            color = content,
            fontSize = letterSize.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun Crest(
    initials: String,
    modifier: Modifier = Modifier,
    width: Int = 36,
    height: Int = 42,
    container: Color = CrestBackground,
    content: Color = TextPrimary
) {
    Box(
        modifier = modifier
            .size(width = width.dp, height = height.dp)
            .clip(FutViewShieldShape)
            .background(container),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.uppercase(),
            color = content,
            fontSize = (width * 0.28f).sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp
        )
    }
}

@Composable
fun Avatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Int = 36,
    container: Color = InkElevated,
    content: Color = Color.White
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(container),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = content,
            fontSize = (size * 0.34f).sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.2.sp
        )
    }
}

@Composable
fun FutViewTopBar(
    onAvatarClick: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val userIdentity = LocalUserIdentity.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    tint = TextOnDark,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
        } else {
            BrandMark()
            Spacer(Modifier.width(10.dp))
        }

        Text(
            text = "FutView",
            style = MaterialTheme.typography.titleLarge,
            color = TextOnDark,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = userIdentity.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = TextOnDarkMuted
        )
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.clickable(onClick = onAvatarClick)) {
            Avatar(initials = userIdentity.initials)
        }
    }
}

/** Cabecera negra común a todas las pantallas principales. */
@Composable
fun ScreenHeader(
    title: String,
    subtitle: String,
    onAvatarClick: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Ink)
            .statusBarsPadding()
    ) {
        FutViewTopBar(
            onAvatarClick = onAvatarClick,
            onBack = onBack
        )
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(TextOnDark)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "CHILE / PRIMERA DIVISIÓN",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextOnDarkMuted,
                    letterSpacing = 1.sp
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                color = TextOnDark
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextOnDarkMuted
            )
        }
    }
}

@Composable
fun FutViewSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) { innerTextField ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(14.dp))
                .background(Card)
                .border(1.dp, Border, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                }
                innerTextField()
            }
            if (value.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .clickable { onValueChange("") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Limpiar búsqueda",
                        tint = TextSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterPill(
    label: String,
    selected: Boolean = false,
    trailingIcon: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(50))
            .background(if (selected) MaterialTheme.colorScheme.primary else Card)
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else Border,
                shape = RoundedCornerShape(50)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else TextPrimary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
        if (trailingIcon) {
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onPrimary else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun FilterDropdownPill(
    label: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    selected: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        FilterPill(
            label = label,
            selected = selected,
            trailingIcon = true,
            onClick = { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Card
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailing: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun StarToggle(
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (selected) Icons.Filled.Star else Icons.Outlined.StarBorder,
            contentDescription = if (selected) "Quitar de favoritos" else "Agregar a favoritos",
            tint = if (selected) MaterialTheme.colorScheme.onPrimary else TextTertiary,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun TeamRow(
    team: Team,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Crest(initials = team.short)
        Spacer(Modifier.width(12.dp))
        Text(
            text = team.name,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun MatchCard(
    match: Match,
    favorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Card)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${match.time} · ${match.status.label}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(Modifier.weight(1f))
            StarToggle(selected = favorite, onToggle = onToggleFavorite)
        }
        Spacer(Modifier.height(12.dp))
        TeamRow(team = match.home)
        Spacer(Modifier.height(10.dp))
        TeamRow(team = match.away)
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = match.stadium,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ResultCard(
    match: Match,
    favorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Card)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = match.status.label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(Modifier.weight(1f))
            StarToggle(selected = favorite, onToggle = onToggleFavorite)
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                TeamRow(team = match.home)
                Spacer(Modifier.height(10.dp))
                TeamRow(team = match.away)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "${match.homeGoals ?: 0}–${match.awayGoals ?: 0}",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = match.stadium,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun StandingsTable(
    rows: List<StandingRow>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Card)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.width(22.dp)
            )
            Text(
                text = "CLUB",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "PJ",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.width(28.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "PTS",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.width(34.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "DG",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.width(34.dp),
                textAlign = TextAlign.Center
            )
        }
        HorizontalDivider(color = Border)
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = row.position.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    modifier = Modifier.width(22.dp)
                )
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Crest(initials = row.team.short, width = 26, height = 30)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = row.team.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = row.played.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.width(28.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = row.points.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    modifier = Modifier.width(34.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (row.goalDifference > 0) "+${row.goalDifference}" else row.goalDifference.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.width(34.dp),
                    textAlign = TextAlign.Center
                )
            }
            if (row != rows.last()) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    color = Border.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun InfoNote(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier
                .padding(top = 1.dp)
                .size(15.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
fun InfoPill(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    container: Color = Field,
    content: Color = TextSecondary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(container)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(17.dp)
            )
            Spacer(Modifier.width(10.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = content
        )
    }
}

@Composable
private fun NavigationBar(
    items: List<Pair<String, ImageVector>>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Card)
            .border(width = 1.dp, color = Border, shape = RoundedCornerShape(0.dp))
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val selected = selectedIndex == index
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onSelect(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.second,
                        contentDescription = item.first,
                        tint = if (selected) TextPrimary else TextTertiary,
                        modifier = Modifier.size(23.dp)
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = item.first,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) TextPrimary else TextTertiary,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 0.1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FutViewBottomBar(
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    NavigationBar(
        items = listOf(
            "Partidos" to Icons.Outlined.CalendarMonth,
            "Resultados" to Icons.Outlined.EmojiEvents,
            "Equipos" to Icons.Outlined.Shield,
            "Estadios" to Icons.Outlined.AccountBalance,
            "Favoritos" to Icons.Outlined.StarBorder
        ),
        selectedIndex = selectedIndex,
        onSelect = onSelect
    )
}

/** Navegación propia del panel de administración. */
@Composable
fun AdminBottomBar(
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    NavigationBar(
        items = listOf(
            "Equipos" to Icons.Outlined.Shield,
            "Estadios" to Icons.Outlined.AccountBalance,
            "Fixture" to Icons.Outlined.CalendarMonth,
            "Resultados" to Icons.Outlined.EmojiEvents,
            "Usuarios" to Icons.Outlined.People
        ),
        selectedIndex = selectedIndex,
        onSelect = onSelect
    )
}

@Composable
fun MenuItemRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    badge: String? = null,
    tint: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = tint,
            modifier = Modifier.weight(1f)
        )
        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(FieldStrong)
                    .padding(horizontal = 9.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.width(8.dp))
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(19.dp)
        )
    }
}

@Composable
fun FormLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = TextPrimary,
        modifier = modifier
    )
}

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation =
        androidx.compose.ui.text.input.VisualTransformation.None
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp),
        readOnly = readOnly,
        singleLine = true,
        placeholder = {
            if (placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary
                )
            }
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        trailingIcon = trailingIcon,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Field,
            unfocusedContainerColor = Field,
            disabledContainerColor = Field,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            disabledTextColor = TextPrimary
        )
    )
}

@Composable
fun FormDropdown(
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Field)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Card
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(19.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FieldStrong,
            contentColor = TextPrimary
        ),
        contentPadding = PaddingValues(horizontal = 14.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(7.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Field)
            .padding(4.dp)
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else TextSecondary,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun RoleBadge(role: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (role == "Admin") SuccessSoft else Field)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = role,
            style = MaterialTheme.typography.labelMedium,
            color = if (role == "Admin") Success else TextSecondary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AdminTopBar(
    title: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Card)
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Volver",
                tint = TextPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = LocalUserIdentity.current.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )
        Spacer(Modifier.width(8.dp))
        Avatar(initials = LocalUserIdentity.current.initials, size = 32)
    }
    HorizontalDivider(color = Border)
}

@Composable
fun FormCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Card)
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Field),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Icon(
                imageVector = Icons.Outlined.MoreHoriz,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = Border)
        Spacer(Modifier.height(18.dp))
        content()
    }
}

@Composable
fun AdminSectionHeader(
    title: String,
    subtitle: String,
    trailing: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Field)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = trailing,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
fun AdminPageBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Page)
    ) {
        content()
    }
}

@Composable
fun AdminFormActions(
    onSave: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        PrimaryButton(
            text = "Guardar",
            onClick = onSave,
            icon = Icons.Outlined.Save
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SecondaryButton(
                text = "Editar",
                onClick = onEdit,
                icon = Icons.Outlined.Edit,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = "Eliminar",
                onClick = onDelete,
                icon = Icons.Outlined.Delete,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatusChip(
    text: String,
    modifier: Modifier = Modifier,
    container: Color = FieldStrong,
    content: Color = TextSecondary
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .padding(horizontal = 9.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = content,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.2.sp
        )
    }
}

/** Botón de ancho completo con borde (acciones secundarias tipo "Volver a…"). */
@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Card)
            .border(1.dp, Border, RoundedCornerShape(10.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) TextPrimary else TextTertiary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = if (enabled) TextPrimary else TextTertiary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Fila etiqueta + valor usada en las fichas de detalle. */
@Composable
fun DetailField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 0.6.sp
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}

/** Fila de ajuste con interruptor visual (Configuración, Notificaciones…). */
@Composable
fun ToggleRow(
    title: String,
    subtitle: String,
    enabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Card)
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .width(46.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(50))
                .background(if (enabled) MaterialTheme.colorScheme.primary else FieldStrong),
            contentAlignment = if (enabled) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surface)
            )
        }
    }
}

/** Estado vacío / error centrado con acciones principales. */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    primaryText: String,
    onPrimary: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    onSecondary: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Card)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(Field),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(27.dp)
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(18.dp))
        PrimaryButton(text = primaryText, onClick = onPrimary)
        if (secondaryText != null && onSecondary != null) {
            Spacer(Modifier.height(10.dp))
            OutlineButton(text = secondaryText, onClick = onSecondary)
        }
    }
}

/** Indicador de carga centrado, para listas y secciones que esperan datos del repositorio. */
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/** Tarjeta de error con reintento, consistente con ConnectionErrorScreen. */
@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    message: String = "Comprueba tu conexión a internet y vuelve a intentarlo.",
    onRetry: () -> Unit
) {
    EmptyState(
        icon = Icons.Outlined.CloudOff,
        title = "No pudimos conectar",
        message = message,
        primaryText = "Volver a intentar",
        onPrimary = onRetry,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    )
}

/** Aviso pasivo de problema con la base de datos (Firestore): no bloquea la pantalla. */
@Composable
fun FirestoreErrorBanner(
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Field)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.CloudOff,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = "Hay un problema con la base de datos. Estamos intentando reconectar.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        if (onRetry != null) {
            Text(
                text = "Reintentar",
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                modifier = Modifier.clickable(onClick = onRetry)
            )
        }
    }
}

