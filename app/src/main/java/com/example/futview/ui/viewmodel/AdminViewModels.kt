package com.example.futview.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.futview.data.AppContainer
import com.example.futview.data.FixtureDay
import com.example.futview.data.Match
import com.example.futview.data.Stadium
import com.example.futview.data.StandingRow
import com.example.futview.data.SystemUser
import com.example.futview.data.Team
import com.example.futview.data.repository.MatchRepository
import com.example.futview.data.repository.StadiumRepository
import com.example.futview.data.repository.TeamRepository
import com.example.futview.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val STOP_TIMEOUT = 5_000L

/** Lista de clubes existentes para la pestaña admin de Equipos. */
class ClubFormViewModel(teamRepository: TeamRepository) : ViewModel() {
    private val retryTrigger = MutableStateFlow(0)

    val teams: StateFlow<UiState<List<Team>>> = retryTrigger
        .flatMapLatest { teamRepository.observeTeams().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    fun retry() {
        retryTrigger.value += 1
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { ClubFormViewModel(AppContainer.teamRepository) }
        }
    }
}

/** Lista de sedes existentes para la pestaña admin de Estadios. */
class StadiumFormViewModel(stadiumRepository: StadiumRepository) : ViewModel() {
    private val retryTrigger = MutableStateFlow(0)

    val stadiums: StateFlow<UiState<List<Stadium>>> = retryTrigger
        .flatMapLatest { stadiumRepository.observeStadiums().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    fun retry() {
        retryTrigger.value += 1
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { StadiumFormViewModel(AppContainer.stadiumRepository) }
        }
    }
}

/** Lista de usuarios del panel admin. */
class UserFormViewModel(userRepository: UserRepository) : ViewModel() {
    private val retryTrigger = MutableStateFlow(0)

    val users: StateFlow<UiState<List<SystemUser>>> = retryTrigger
        .flatMapLatest { userRepository.observeUsers().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    fun retry() {
        retryTrigger.value += 1
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { UserFormViewModel(AppContainer.userRepository) }
        }
    }
}

/** Opciones y fixture para la pestaña admin de Partidos. */
class MatchFormViewModel(
    teamRepository: TeamRepository,
    stadiumRepository: StadiumRepository,
    matchRepository: MatchRepository
) : ViewModel() {
    private val retryTrigger = MutableStateFlow(0)

    val teams: StateFlow<UiState<List<Team>>> = retryTrigger
        .flatMapLatest { teamRepository.observeTeams().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    val stadiums: StateFlow<UiState<List<Stadium>>> = retryTrigger
        .flatMapLatest { stadiumRepository.observeStadiums().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    /** Fixture agrupado por fecha, derivado de los partidos programados. */
    val fixtureDays: StateFlow<UiState<List<FixtureDay>>> = retryTrigger
        .flatMapLatest {
            matchRepository.observeUpcomingMatches()
                .map { matches ->
                    matches
                        .groupBy { Pair(it.matchday, it.dayLabel) }
                        .map { (key, dayMatches) ->
                            FixtureDay(dateLabel = key.second, matchday = key.first, matches = dayMatches)
                        }
                        .sortedBy { it.matchday }
                }
                .toUiState()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    fun retry() {
        retryTrigger.value += 1
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                MatchFormViewModel(
                    AppContainer.teamRepository,
                    AppContainer.stadiumRepository,
                    AppContainer.matchRepository
                )
            }
        }
    }
}

/** Tabla de posiciones y partidos disponibles para el acta de resultados. */
class ActaViewModel(matchRepository: MatchRepository) : ViewModel() {
    private val retryTrigger = MutableStateFlow(0)

    val matches: StateFlow<UiState<List<Match>>> = retryTrigger
        .flatMapLatest {
            combine(
                matchRepository.observeUpcomingMatches(),
                matchRepository.observeResults()
            ) { upcoming, results -> upcoming + results }.toUiState()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    val standings: StateFlow<UiState<List<StandingRow>>> = retryTrigger
        .flatMapLatest { matchRepository.observeStandings().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    fun retry() {
        retryTrigger.value += 1
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { ActaViewModel(AppContainer.matchRepository) }
        }
    }
}
