package com.example.futview.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.futview.data.AppContainer
import com.example.futview.data.Match
import com.example.futview.data.Stadium
import com.example.futview.data.StandingRow
import com.example.futview.data.Team
import com.example.futview.data.repository.FavoritesRepository
import com.example.futview.data.repository.MatchRepository
import com.example.futview.data.repository.StadiumRepository
import com.example.futview.data.repository.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val STOP_TIMEOUT = 5_000L

/** ViewModel de la pestaña Partidos. */
class MatchesViewModel(matchRepository: MatchRepository) : ViewModel() {
    private val retryTrigger = MutableStateFlow(0)

    val matches: StateFlow<UiState<List<Match>>> = retryTrigger
        .flatMapLatest { matchRepository.observeUpcomingMatches().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    fun retry() {
        retryTrigger.value += 1
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { MatchesViewModel(AppContainer.matchRepository) }
        }
    }
}

/** ViewModel de la pestaña Resultados. */
class ResultsViewModel(matchRepository: MatchRepository) : ViewModel() {
    private val retryTrigger = MutableStateFlow(0)

    val results: StateFlow<UiState<List<Match>>> = retryTrigger
        .flatMapLatest { matchRepository.observeResults().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    val standings: StateFlow<UiState<List<StandingRow>>> = retryTrigger
        .flatMapLatest { matchRepository.observeStandings().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    fun retry() {
        retryTrigger.value += 1
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { ResultsViewModel(AppContainer.matchRepository) }
        }
    }
}

/** ViewModel de la pestaña Equipos. */
class TeamsViewModel(teamRepository: TeamRepository) : ViewModel() {
    private val retryTrigger = MutableStateFlow(0)

    val teams: StateFlow<UiState<List<Team>>> = retryTrigger
        .flatMapLatest { teamRepository.observeTeams().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    fun retry() {
        retryTrigger.value += 1
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { TeamsViewModel(AppContainer.teamRepository) }
        }
    }
}

/** ViewModel de la pestaña Estadios. */
class StadiumsViewModel(stadiumRepository: StadiumRepository) : ViewModel() {
    private val retryTrigger = MutableStateFlow(0)

    val stadiums: StateFlow<UiState<List<Stadium>>> = retryTrigger
        .flatMapLatest { stadiumRepository.observeStadiums().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    fun retry() {
        retryTrigger.value += 1
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { StadiumsViewModel(AppContainer.stadiumRepository) }
        }
    }
}

/** Datos combinados para la pestaña Favoritos. */
data class FavoritesUiData(
    val teams: List<Team> = emptyList(),
    val matches: List<Match> = emptyList(),
    val stadiums: List<Stadium> = emptyList()
)

/** ViewModel de la pestaña Favoritos: une equipos, partidos y estadios en un solo estado. */
class FavoritesViewModel(
    teamRepository: TeamRepository,
    matchRepository: MatchRepository,
    stadiumRepository: StadiumRepository
) : ViewModel() {
    private val retryTrigger = MutableStateFlow(0)

    val favorites: StateFlow<UiState<FavoritesUiData>> = retryTrigger
        .flatMapLatest {
            combine(
                teamRepository.observeTeams(),
                matchRepository.observeUpcomingMatches(),
                matchRepository.observeResults(),
                stadiumRepository.observeStadiums()
            ) { teams, upcoming, results, stadiums ->
                FavoritesUiData(teams = teams, matches = upcoming + results, stadiums = stadiums)
            }.toUiState()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    fun retry() {
        retryTrigger.value += 1
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                FavoritesViewModel(
                    AppContainer.teamRepository,
                    AppContainer.matchRepository,
                    AppContainer.stadiumRepository
                )
            }
        }
    }
}

/**
 * Favoritos del usuario autenticado, persistidos en Firestore.
 * Expone el conjunto de ids con prefijo de tipo ("team:x", "match:y", "stadium:z").
 */
class UserFavoritesViewModel(private val favoritesRepository: FavoritesRepository) : ViewModel() {

    private val authRepository = AppContainer.authRepository

    val favoriteIds: StateFlow<UiState<Set<String>>> = authRepository.observeAuth()
        .flatMapLatest { user ->
            val favoritesFlow: Flow<Set<String>> = if (user == null) {
                flowOf(emptySet())
            } else {
                favoritesRepository.observeFavorites(user.uid)
            }
            favoritesFlow.toUiState()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    fun toggle(uid: String?, type: String, id: String) {
        if (uid.isNullOrBlank()) return
        viewModelScope.launch {
            favoritesRepository.toggle(uid, "$type:$id")
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { UserFavoritesViewModel(AppContainer.favoritesRepository) }
        }
    }
}
