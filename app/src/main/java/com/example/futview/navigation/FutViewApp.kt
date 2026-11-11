package com.example.futview.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.futview.ui.components.AdminBottomBar
import com.example.futview.ui.components.FutViewBottomBar
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.screens.FavoritesScreen
import com.example.futview.ui.screens.LoginScreen
import com.example.futview.ui.screens.MatchesScreen
import com.example.futview.ui.screens.ResultsScreen
import com.example.futview.ui.screens.StadiumsScreen
import com.example.futview.ui.screens.TeamsScreen
import com.example.futview.ui.screens.UserMenuScreen
import com.example.futview.ui.screens.admin.ActaScreen
import com.example.futview.ui.screens.admin.ClubFormScreen
import com.example.futview.ui.screens.admin.MatchFormScreen
import com.example.futview.ui.screens.admin.StadiumFormScreen
import com.example.futview.ui.screens.admin.UserFormScreen
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Page

private enum class Route {
    LOGIN,
    HOME,
    MENU,
    ADMIN
}

private enum class MainTab(val label: String) {
    PARTIDOS("Partidos"),
    RESULTADOS("Resultados"),
    EQUIPOS("Equipos"),
    ESTADIOS("Estadios"),
    FAVORITOS("Favoritos")
}

private enum class AdminTab(val title: String, val subtitle: String) {
    EQUIPOS("Equipos", "Administra los clubes de la competición."),
    ESTADIOS("Estadios", "Gestiona las sedes y su ubicación GPS."),
    FIXTURE("Partidos / Fixture", "Programa y organiza cada jornada."),
    RESULTADOS("Resultados", "Registra marcadores y actualiza posiciones."),
    USUARIOS("Usuarios", "Controla los accesos al panel de administración.")
}

private fun MutableList<String>.toggle(id: String) {
    if (!remove(id)) add(id)
}

@Composable
fun FutViewApp() {
    var route by rememberSaveable { mutableStateOf(Route.LOGIN) }
    var tab by rememberSaveable { mutableStateOf(MainTab.PARTIDOS) }
    var adminTab by rememberSaveable { mutableStateOf(AdminTab.EQUIPOS) }

    val favoriteTeams = remember { mutableStateListOf("cc", "uch") }
    val favoriteMatches = remember { mutableStateListOf("m2", "m6") }
    val favoriteStadiums = remember { mutableStateListOf("monumental") }

    BackHandler(
        enabled = route != Route.LOGIN &&
            !(route == Route.HOME && tab == MainTab.PARTIDOS)
    ) {
        when (route) {
            Route.HOME -> tab = MainTab.PARTIDOS
            Route.MENU, Route.ADMIN -> route = Route.HOME
            else -> Unit
        }
    }

    when (route) {
        Route.LOGIN -> LoginScreen(
            onLogin = {
                tab = MainTab.PARTIDOS
                route = Route.HOME
            }
        )

        Route.HOME -> Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Card)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                when (tab) {
                    MainTab.PARTIDOS -> MatchesScreen(
                        favoriteTeamIds = favoriteTeams,
                        favoriteMatchIds = favoriteMatches,
                        onToggleMatchFavorite = { favoriteMatches.toggle(it) },
                        onAvatarClick = { route = Route.MENU }
                    )

                    MainTab.RESULTADOS -> ResultsScreen(
                        favoriteMatchIds = favoriteMatches,
                        onToggleMatchFavorite = { favoriteMatches.toggle(it) },
                        onAvatarClick = { route = Route.MENU }
                    )

                    MainTab.EQUIPOS -> TeamsScreen(
                        favoriteTeamIds = favoriteTeams,
                        onToggleTeamFavorite = { favoriteTeams.toggle(it) },
                        onAvatarClick = { route = Route.MENU }
                    )

                    MainTab.ESTADIOS -> StadiumsScreen(
                        favoriteStadiumIds = favoriteStadiums,
                        onToggleStadiumFavorite = { favoriteStadiums.toggle(it) },
                        onAvatarClick = { route = Route.MENU }
                    )

                    MainTab.FAVORITOS -> FavoritesScreen(
                        favoriteTeamIds = favoriteTeams,
                        favoriteMatchIds = favoriteMatches,
                        favoriteStadiumIds = favoriteStadiums,
                        onToggleTeamFavorite = { favoriteTeams.toggle(it) },
                        onToggleMatchFavorite = { favoriteMatches.toggle(it) },
                        onToggleStadiumFavorite = { favoriteStadiums.toggle(it) },
                        onAvatarClick = { route = Route.MENU }
                    )
                }
            }
            FutViewBottomBar(
                selectedIndex = tab.ordinal,
                onSelect = { tab = MainTab.entries[it] }
            )
        }

        Route.MENU -> UserMenuScreen(
            onClose = { route = Route.HOME },
            onAdmin = {
                adminTab = AdminTab.EQUIPOS
                route = Route.ADMIN
            },
            onLogout = {
                tab = MainTab.PARTIDOS
                adminTab = AdminTab.EQUIPOS
                route = Route.LOGIN
            }
        )

        Route.ADMIN -> {
            SetStatusBarAppearance(lightIcons = true)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Page)
            ) {
                ScreenHeader(
                    title = adminTab.title,
                    subtitle = adminTab.subtitle,
                    onAvatarClick = { route = Route.MENU },
                    userLabel = "Admin",
                    userInitials = "CM"
                )

                Box(modifier = Modifier.weight(1f)) {
                    when (adminTab) {
                        AdminTab.EQUIPOS -> ClubFormScreen()
                        AdminTab.ESTADIOS -> StadiumFormScreen()
                        AdminTab.FIXTURE -> MatchFormScreen()
                        AdminTab.RESULTADOS -> ActaScreen()
                        AdminTab.USUARIOS -> UserFormScreen()
                    }
                }

                AdminBottomBar(
                    selectedIndex = adminTab.ordinal,
                    onSelect = { adminTab = AdminTab.entries[it] }
                )
            }
        }
    }
}
