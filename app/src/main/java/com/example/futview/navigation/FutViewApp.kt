package com.example.futview.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.futview.data.AppContainer
import com.example.futview.data.Match
import com.example.futview.data.Stadium
import com.example.futview.data.Team
import com.example.futview.data.model.initialsOf
import com.example.futview.ui.viewmodel.EspnSyncViewModel
import com.example.futview.ui.viewmodel.AuthStateViewModel
import com.example.futview.ui.viewmodel.UiState
import com.example.futview.ui.viewmodel.UserFavoritesViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import com.example.futview.ui.components.AdminBottomBar
import com.example.futview.ui.components.FutViewBottomBar
import com.example.futview.ui.components.ScreenHeader
import com.example.futview.ui.components.SetStatusBarAppearance
import com.example.futview.ui.components.EmptyState
import com.example.futview.ui.screens.CheckEmailScreen
import com.example.futview.ui.screens.ConnectionErrorScreen
import com.example.futview.ui.screens.ContactSupportScreen
import com.example.futview.ui.screens.FavoritesScreen
import com.example.futview.ui.screens.HelpCenterScreen
import com.example.futview.ui.screens.LoginScreen
import com.example.futview.ui.screens.MatchDetailScreen
import com.example.futview.ui.screens.MatchesScreen
import com.example.futview.ui.screens.NewPasswordScreen
import com.example.futview.ui.screens.NotificationsScreen
import com.example.futview.ui.screens.ProfileScreen
import com.example.futview.ui.screens.RecoverAccessScreen
import com.example.futview.ui.screens.ResultDetailScreen
import com.example.futview.ui.screens.ResultsScreen
import com.example.futview.ui.screens.SettingsScreen
import com.example.futview.ui.screens.SignUpScreen
import com.example.futview.ui.screens.StadiumDetailScreen
import com.example.futview.ui.screens.StadiumsScreen
import com.example.futview.ui.screens.TeamDetailScreen
import com.example.futview.ui.screens.TeamsScreen
import com.example.futview.ui.screens.UserMenuScreen
import com.example.futview.ui.screens.admin.ActaScreen
import com.example.futview.ui.screens.admin.ClubFormScreen
import com.example.futview.ui.screens.admin.ConfirmDeleteScreen
import com.example.futview.ui.screens.admin.InviteUserScreen
import com.example.futview.ui.screens.admin.MatchFormScreen
import com.example.futview.ui.screens.admin.StadiumFormScreen
import com.example.futview.ui.screens.admin.UserFormScreen
import com.example.futview.ui.theme.Card
import com.example.futview.ui.theme.Page

private enum class Route {
    LOGIN,
    SIGN_UP,
    RECOVER,
    CHECK_EMAIL,
    NEW_PASSWORD,
    HOME,
    MENU,
    PROFILE,
    SETTINGS,
    NOTIFICATIONS,
    HELP,
    CONTACT,
    ADMIN,
    INVITE_USER,
    CONFIRM_DELETE,
    CONNECTION_ERROR,
    ADMIN_STATUS,
    MATCH_DETAIL,
    RESULT_DETAIL,
    TEAM_DETAIL,
    STADIUM_DETAIL
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

@Composable
fun FutViewApp() {
    val authViewModel: AuthStateViewModel = viewModel(factory = AuthStateViewModel.Factory)
    val authState by authViewModel.authUser.collectAsStateWithLifecycle()
    val sessionUser = (authState as? UiState.Success)?.data
    val signedIn = sessionUser != null

    if (authState is UiState.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Page),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val espnSyncViewModel: EspnSyncViewModel = viewModel(factory = EspnSyncViewModel.Factory)
    val appContext = LocalContext.current.applicationContext

    val roleFlow = remember(sessionUser?.uid) {
        AppContainer.userRepository.observeUserRole(sessionUser?.uid.orEmpty())
    }
    val userRole by roleFlow.collectAsState(initial = null)
    val isAdmin = userRole in setOf("Admin", "Editor")

    var route by rememberSaveable { mutableStateOf(Route.LOGIN) }
    var tab by rememberSaveable { mutableStateOf(MainTab.PARTIDOS) }
    var adminTab by rememberSaveable { mutableStateOf(AdminTab.EQUIPOS) }
    var selectedMatchId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedTeamId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedStadiumId by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingEmail by rememberSaveable { mutableStateOf("") }

    // Datos del repositorio para las rutas de detalle y las búsquedas de estadio.
    // Sin sesión no se suscriben listeners (evita PERMISSION_DENIED); con sesión, cualquier
    // denegación se absorbe aquí (los banners de cada pantalla informan al usuario).
    val teams by remember(signedIn) {
        if (signedIn) {
            AppContainer.teamRepository.observeTeams().catch { emit(emptyList()) }
        } else {
            flowOf(emptyList())
        }
    }.collectAsState(initial = emptyList())
    val stadiums by remember(signedIn) {
        if (signedIn) {
            AppContainer.stadiumRepository.observeStadiums().catch { emit(emptyList()) }
        } else {
            flowOf(emptyList())
        }
    }.collectAsState(initial = emptyList())
    val upcomingMatches by remember(signedIn) {
        if (signedIn) {
            AppContainer.matchRepository.observeUpcomingMatches().catch { emit(emptyList()) }
        } else {
            flowOf(emptyList())
        }
    }.collectAsState(initial = emptyList())
    val results by remember(signedIn) {
        if (signedIn) {
            AppContainer.matchRepository.observeResults().catch { emit(emptyList()) }
        } else {
            flowOf(emptyList())
        }
    }.collectAsState(initial = emptyList())
    val standings by remember(signedIn) {
        if (signedIn) {
            AppContainer.matchRepository.observeStandings().catch { emit(emptyList()) }
        } else {
            flowOf(emptyList())
        }
    }.collectAsState(initial = emptyList())

    // Actualización silenciosa desde ESPN (cada 6 h). Se espera a que existan equipos:
    // en una instalación nueva primero se siembra el catálogo y luego corre el primer sync.
    LaunchedEffect(signedIn, teams.isNotEmpty()) {
        if (signedIn && teams.isNotEmpty()) {
            espnSyncViewModel.syncIfDue(appContext)
        }
    }

    val favoritesViewModel: UserFavoritesViewModel = viewModel(factory = UserFavoritesViewModel.Factory)
    val favoritesState by favoritesViewModel.favoriteIds.collectAsStateWithLifecycle()
    val favoriteIds = (favoritesState as? UiState.Success)?.data.orEmpty()
    val favoriteTeams = favoriteIds.filter { it.startsWith("team:") }.map { it.removePrefix("team:") }
    val favoriteMatches = favoriteIds.filter { it.startsWith("match:") }.map { it.removePrefix("match:") }
    val favoriteStadiums = favoriteIds.filter { it.startsWith("stadium:") }.map { it.removePrefix("stadium:") }

    val toggleTeamFavorite: (String) -> Unit = { favoritesViewModel.toggle(sessionUser?.uid, "team", it) }
    val toggleMatchFavorite: (String) -> Unit = { favoritesViewModel.toggle(sessionUser?.uid, "match", it) }
    val toggleStadiumFavorite: (String) -> Unit = { favoritesViewModel.toggle(sessionUser?.uid, "stadium", it) }

    val selectMainTab: (Int) -> Unit = { index ->
        tab = MainTab.entries[index]
        route = Route.HOME
    }
    val selectAdminTab: (Int) -> Unit = { index ->
        adminTab = AdminTab.entries[index]
        route = Route.ADMIN
    }

    val openMatchDetail: (String) -> Unit = { id ->
        selectedMatchId = id
        route = Route.MATCH_DETAIL
    }
    val openResultDetail: (String) -> Unit = { id ->
        selectedMatchId = id
        route = Route.RESULT_DETAIL
    }
    val openTeamDetail: (String) -> Unit = { id ->
        selectedTeamId = id
        route = Route.TEAM_DETAIL
    }
    val openStadiumDetail: (String) -> Unit = { id ->
        selectedStadiumId = id
        route = Route.STADIUM_DETAIL
    }
    val openMatchStadium: (Match) -> Unit = { match ->
        val stadium = stadiums.firstOrNull { it.homeTeam == match.home.name }
        if (stadium != null) {
            selectedStadiumId = stadium.id
            route = Route.STADIUM_DETAIL
        }
    }
    val openTeamStadium: (Team) -> Unit = { team ->
        val stadium = stadiums.firstOrNull { it.name == team.stadium }
        if (stadium != null) {
            selectedStadiumId = stadium.id
            route = Route.STADIUM_DETAIL
        }
    }

    BackHandler(
        enabled = route != Route.LOGIN &&
            !(route == Route.HOME && tab == MainTab.PARTIDOS)
    ) {
        route = when (route) {
            Route.HOME -> {
                tab = MainTab.PARTIDOS
                route
            }

            Route.MENU -> Route.HOME
            Route.PROFILE,
            Route.SETTINGS,
            Route.NOTIFICATIONS,
            Route.HELP,
            Route.CONTACT -> Route.MENU

            Route.ADMIN -> Route.MENU
            Route.INVITE_USER,
            Route.CONFIRM_DELETE -> Route.ADMIN

            Route.MATCH_DETAIL,
            Route.RESULT_DETAIL,
            Route.TEAM_DETAIL,
            Route.STADIUM_DETAIL,
            Route.CONNECTION_ERROR -> Route.HOME

            Route.SIGN_UP,
            Route.RECOVER,
            Route.CHECK_EMAIL,
            Route.NEW_PASSWORD -> Route.LOGIN

            else -> route
        }
    }

    // Protección de rutas según sesión.
    val authRoutes = setOf(Route.LOGIN, Route.SIGN_UP, Route.RECOVER, Route.CHECK_EMAIL, Route.NEW_PASSWORD)
    val activeRoute = when {
        sessionUser != null && (route == Route.LOGIN || route == Route.SIGN_UP || route == Route.RECOVER) -> Route.HOME
        sessionUser == null && route !in authRoutes -> Route.LOGIN
        else -> route
    }

    when (activeRoute) {
        Route.LOGIN -> LoginScreen(
            onSignUp = { route = Route.SIGN_UP },
            onRecover = { route = Route.RECOVER }
        )

        Route.SIGN_UP -> SignUpScreen(
            onBack = { route = Route.LOGIN },
            onLogin = { route = Route.LOGIN },
            onRegistered = { email ->
                pendingEmail = email
                route = Route.CHECK_EMAIL
            }
        )

        Route.RECOVER -> RecoverAccessScreen(
            onBack = { route = Route.LOGIN },
            onSent = { email ->
                pendingEmail = email
                route = Route.CHECK_EMAIL
            }
        )

        Route.CHECK_EMAIL -> CheckEmailScreen(
            email = pendingEmail,
            onBack = { route = Route.RECOVER },
            onLogin = { route = Route.LOGIN }
        )

        Route.NEW_PASSWORD -> NewPasswordScreen(
            onBack = { route = Route.SETTINGS },
            onSaved = { route = Route.LOGIN }
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
                        onToggleMatchFavorite = { toggleMatchFavorite(it) },
                        onAvatarClick = { route = Route.MENU },
                        onMatchClick = openMatchDetail
                    )

                    MainTab.RESULTADOS -> ResultsScreen(
                        favoriteMatchIds = favoriteMatches,
                        onToggleMatchFavorite = { toggleMatchFavorite(it) },
                        onAvatarClick = { route = Route.MENU },
                        onResultClick = openResultDetail
                    )

                    MainTab.EQUIPOS -> TeamsScreen(
                        favoriteTeamIds = favoriteTeams,
                        onToggleTeamFavorite = { toggleTeamFavorite(it) },
                        onAvatarClick = { route = Route.MENU },
                        onTeamClick = openTeamDetail
                    )

                    MainTab.ESTADIOS -> StadiumsScreen(
                        favoriteStadiumIds = favoriteStadiums,
                        onToggleStadiumFavorite = { toggleStadiumFavorite(it) },
                        onAvatarClick = { route = Route.MENU },
                        onStadiumClick = openStadiumDetail
                    )

                    MainTab.FAVORITOS -> FavoritesScreen(
                        favoriteTeamIds = favoriteTeams,
                        favoriteMatchIds = favoriteMatches,
                        favoriteStadiumIds = favoriteStadiums,
                        onToggleTeamFavorite = { toggleTeamFavorite(it) },
                        onToggleMatchFavorite = { toggleMatchFavorite(it) },
                        onToggleStadiumFavorite = { toggleStadiumFavorite(it) },
                        onAvatarClick = { route = Route.MENU },
                        onExploreTeams = { selectMainTab(MainTab.EQUIPOS.ordinal) },
                        onViewMatches = { selectMainTab(MainTab.PARTIDOS.ordinal) }
                    )
                }
            }
            FutViewBottomBar(
                selectedIndex = tab.ordinal,
                onSelect = selectMainTab
            )
        }

        Route.MENU -> UserMenuScreen(
            onClose = { route = Route.HOME },
            isAdmin = isAdmin,
            userName = sessionUser?.displayName.orEmpty(),
            userEmail = sessionUser?.email.orEmpty(),
            onAdmin = {
                adminTab = AdminTab.EQUIPOS
                route = Route.ADMIN
            },
            onLogout = {
                authViewModel.signOut()
                tab = MainTab.PARTIDOS
                adminTab = AdminTab.EQUIPOS
                route = Route.LOGIN
            },
            onStatus = { route = Route.ADMIN_STATUS },
            onProfile = { route = Route.PROFILE },
            onSettings = { route = Route.SETTINGS },
            onNotifications = { route = Route.NOTIFICATIONS },
            onHelp = { route = Route.HELP },
            onContact = { route = Route.CONTACT }
        )

        Route.PROFILE -> ProfileScreen(
            onBack = { route = Route.MENU },
            onViewFavorites = { selectMainTab(MainTab.FAVORITOS.ordinal) },
            onSelectTab = selectMainTab,
            userName = sessionUser?.displayName.orEmpty(),
            userEmail = sessionUser?.email.orEmpty()
        )

        Route.SETTINGS -> SettingsScreen(
            onBack = { route = Route.MENU },
            onChangePassword = { route = Route.NEW_PASSWORD },
            onLogout = {
                authViewModel.signOut()
                tab = MainTab.PARTIDOS
                adminTab = AdminTab.EQUIPOS
                route = Route.LOGIN
            },
            onSelectTab = selectMainTab
        )

        Route.NOTIFICATIONS -> NotificationsScreen(
            onBack = { route = Route.MENU },
            onViewMatch = { openMatchDetail("m2") },
            onViewResult = { openResultDetail("m1") },
            onExploreTeams = { selectMainTab(MainTab.EQUIPOS.ordinal) },
            onSelectTab = selectMainTab
        )

        Route.HELP -> HelpCenterScreen(
            onBack = { route = Route.MENU },
            onContact = { route = Route.CONTACT },
            onSelectTab = selectMainTab
        )

        Route.CONTACT -> ContactSupportScreen(
            onBack = { route = Route.HELP },
            onSelectTab = selectMainTab
        )

        Route.ADMIN -> {
            SetStatusBarAppearance(lightIcons = true)
            if (isAdmin) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Page)
            ) {
                ScreenHeader(
                    title = adminTab.title,
                    subtitle = adminTab.subtitle,
                    onAvatarClick = { route = Route.MENU },
                    userLabel = sessionUser?.displayName.orEmpty().ifBlank { "Administración" },
                    userInitials = initialsOf(sessionUser?.displayName)
                )

                Box(modifier = Modifier.weight(1f)) {
                    when (adminTab) {
                        AdminTab.EQUIPOS -> ClubFormScreen()
                        AdminTab.ESTADIOS -> StadiumFormScreen()
                        AdminTab.FIXTURE -> MatchFormScreen()
                        AdminTab.RESULTADOS -> ActaScreen()
                        AdminTab.USUARIOS -> UserFormScreen(
                            onInviteUser = { route = Route.INVITE_USER }
                        )
                    }
                }

                AdminBottomBar(
                    selectedIndex = adminTab.ordinal,
                    onSelect = selectAdminTab
                )
            }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Page)
                ) {
                    ScreenHeader(
                        title = "Administración",
                        subtitle = "Panel restringido a administradores.",
                        onAvatarClick = { route = Route.MENU },
                        userLabel = sessionUser?.displayName.orEmpty().ifBlank { "Usuario" },
                        userInitials = initialsOf(sessionUser?.displayName)
                    )
                    EmptyState(
                        icon = Icons.Outlined.Lock,
                        title = "Acceso restringido",
                        message = "Tu cuenta no tiene permisos de administración. Si crees que deberías tenerlos, contacta a un administrador.",
                        primaryText = "Volver al menú",
                        onPrimary = { route = Route.MENU }
                    )
                }
            }
        }

        Route.ADMIN_STATUS -> {
            SetStatusBarAppearance(lightIcons = true)
            if (isAdmin) {
                com.example.futview.ui.screens.admin.AdminStatusScreen(
                    sessionUser = sessionUser,
                    onBack = { route = Route.MENU },
                    onSelectTab = selectMainTab
                )
            } else {
                route = Route.MENU
            }
        }

        Route.INVITE_USER -> InviteUserScreen(
            onBack = { route = Route.ADMIN },
            onSelectTab = selectAdminTab
        )

        Route.CONFIRM_DELETE -> {
            val match = selectedMatchId?.let { id ->
                upcomingMatches.firstOrNull { it.id == id }
            }
            if (match != null) {
                ConfirmDeleteScreen(
                    match = match,
                    onCancel = { route = Route.ADMIN },
                    onConfirm = { route = Route.ADMIN },
                    onSelectTab = selectAdminTab
                )
            }
        }

        Route.MATCH_DETAIL -> {
            val match = upcomingMatches.firstOrNull { it.id == selectedMatchId }
            if (match != null) {
                MatchDetailScreen(
                    match = match,
                    favorite = match.id in favoriteMatches,
                    onToggleFavorite = { toggleMatchFavorite(match.id) },
                    onBack = {
                        tab = MainTab.PARTIDOS
                        route = Route.HOME
                    },
                    onViewStadium = { openMatchStadium(match) },
                    onAvatarClick = { route = Route.MENU },
                    onSelectTab = selectMainTab
                )
            }
        }

        Route.RESULT_DETAIL -> {
            val match = results.firstOrNull { it.id == selectedMatchId }
            if (match != null) {
                ResultDetailScreen(
                    match = match,
                    standings = standings,
                    finishedMatches = results.size,
                    totalGoals = results.sumOf { (it.homeGoals ?: 0) + (it.awayGoals ?: 0) },
                    favorite = match.id in favoriteMatches,
                    onToggleFavorite = { toggleMatchFavorite(match.id) },
                    onBack = {
                        tab = MainTab.RESULTADOS
                        route = Route.HOME
                    },
                    onAvatarClick = { route = Route.MENU },
                    onSelectTab = selectMainTab
                )
            }
        }

        Route.TEAM_DETAIL -> {
            val team = teams.firstOrNull { it.id == selectedTeamId }
            if (team != null) {
                TeamDetailScreen(
                    team = team,
                    nextMatch = upcomingMatches.firstOrNull {
                        it.home.id == team.id || it.away.id == team.id
                    },
                    lastResult = results.firstOrNull {
                        it.home.id == team.id || it.away.id == team.id
                    },
                    favorite = team.id in favoriteTeams,
                    onToggleFavorite = { toggleTeamFavorite(team.id) },
                    favoriteMatchIds = favoriteMatches,
                    onToggleMatchFavorite = { toggleMatchFavorite(it) },
                    onBack = {
                        tab = MainTab.EQUIPOS
                        route = Route.HOME
                    },
                    onViewStadium = { openTeamStadium(team) },
                    onViewMatch = openMatchDetail,
                    onViewResult = openResultDetail,
                    onAvatarClick = { route = Route.MENU },
                    onSelectTab = selectMainTab
                )
            }
        }

        Route.STADIUM_DETAIL -> {
            val stadium = stadiums.firstOrNull { it.id == selectedStadiumId }
            if (stadium != null) {
                StadiumDetailScreen(
                    stadium = stadium,
                    nextMatch = upcomingMatches.firstOrNull {
                        it.stadium.startsWith(stadium.name)
                    },
                    favorite = stadium.id in favoriteStadiums,
                    onToggleFavorite = { toggleStadiumFavorite(stadium.id) },
                    favoriteMatchIds = favoriteMatches,
                    onToggleMatchFavorite = { toggleMatchFavorite(it) },
                    onBack = {
                        tab = MainTab.ESTADIOS
                        route = Route.HOME
                    },
                    onViewMatch = openMatchDetail,
                    onAvatarClick = { route = Route.MENU },
                    onSelectTab = selectMainTab
                )
            }
        }

        Route.CONNECTION_ERROR -> ConnectionErrorScreen(
            onBack = { route = Route.HOME },
            onRetry = { route = Route.HOME },
            onOpenHelp = { route = Route.HELP },
            onSelectTab = selectMainTab
        )
    }
}
