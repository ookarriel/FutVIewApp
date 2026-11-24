package com.example.futview.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.futview.data.AppContainer
import com.example.futview.data.FixtureDay
import com.example.futview.data.Match
import com.example.futview.data.MatchStatus
import com.example.futview.data.Stadium
import com.example.futview.data.StandingRow
import com.example.futview.data.SystemUser
import com.example.futview.data.Team
import com.example.futview.data.model.initialsOf
import android.content.Context
import com.example.futview.data.repository.EspnSyncRepository
import com.example.futview.data.repository.FirestoreSeedRepository
import com.example.futview.data.repository.MatchRepository
import com.example.futview.data.repository.StadiumRepository
import com.example.futview.data.repository.TeamRepository
import com.example.futview.data.repository.UserRepository
import com.example.futview.data.repository.WikipediaStandingsRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val STOP_TIMEOUT = 5_000L

/** Lista de clubes existentes para la pestaña admin de Equipos. */
class ClubFormViewModel(private val teamRepository: TeamRepository) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    val teams: StateFlow<UiState<List<Team>>> = retryTrigger
        .flatMapLatest { teamRepository.observeTeams().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun consumeMessage() {
        _message.value = null
    }

    fun createTeam(name: String, city: String) {
        if (name.isBlank()) {
            _message.value = "Escribe el nombre del club."
            return
        }
        viewModelScope.launch {
            teamRepository.createTeam(name.trim(), initialsOf(name.trim()), city.trim(), "")
                .onSuccess { _message.value = "Club creado." }
                .onFailure { _message.value = "Error al guardar: ${it.message}" }
        }
    }

    fun updateTeam(id: String, name: String, city: String) {
        if (name.isBlank()) {
            _message.value = "Escribe el nombre del club."
            return
        }
        viewModelScope.launch {
            teamRepository.updateTeam(id, name.trim(), initialsOf(name.trim()), city.trim(), "")
                .onSuccess { _message.value = "Club actualizado." }
                .onFailure { _message.value = "Error al actualizar: ${it.message}" }
        }
    }

    fun deleteTeam(id: String) {
        viewModelScope.launch {
            teamRepository.deleteTeam(id)
                .onSuccess { _message.value = "Club eliminado." }
                .onFailure { _message.value = "Error al eliminar: ${it.message}" }
        }
    }

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
class StadiumFormViewModel(private val stadiumRepository: StadiumRepository) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    val stadiums: StateFlow<UiState<List<Stadium>>> = retryTrigger
        .flatMapLatest { stadiumRepository.observeStadiums().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun consumeMessage() {
        _message.value = null
    }

    fun createStadium(name: String, city: String, capacity: String, address: String, latitude: String, longitude: String) {
        if (name.isBlank()) {
            _message.value = "Escribe el nombre del estadio."
            return
        }
        val latitudeValue = latitude.replace(",", ".").toDoubleOrNull()
        val longitudeValue = longitude.replace(",", ".").toDoubleOrNull()
        viewModelScope.launch {
            stadiumRepository.createStadium(
                name.trim(),
                city.trim(),
                capacity.toIntOrNull() ?: 0,
                address.trim(),
                latitudeValue,
                longitudeValue
            )
                .onSuccess { _message.value = "Estadio guardado." }
                .onFailure { _message.value = "Error al guardar: ${it.message}" }
        }
    }

    fun updateStadium(id: String, name: String, city: String, capacity: String, address: String, latitude: String, longitude: String) {
        if (name.isBlank()) {
            _message.value = "Escribe el nombre del estadio."
            return
        }
        val latitudeValue = latitude.replace(",", ".").toDoubleOrNull()
        val longitudeValue = longitude.replace(",", ".").toDoubleOrNull()
        viewModelScope.launch {
            stadiumRepository.updateStadium(
                id,
                name.trim(),
                city.trim(),
                capacity.toIntOrNull() ?: 0,
                address.trim(),
                latitudeValue,
                longitudeValue
            )
                .onSuccess { _message.value = "Estadio actualizado." }
                .onFailure { _message.value = "Error al actualizar: ${it.message}" }
        }
    }

    fun deleteStadium(id: String) {
        viewModelScope.launch {
            stadiumRepository.deleteStadium(id)
                .onSuccess { _message.value = "Estadio eliminado." }
                .onFailure { _message.value = "Error al eliminar: ${it.message}" }
        }
    }

    fun retry() {
        retryTrigger.value += 1
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { StadiumFormViewModel(AppContainer.stadiumRepository) }
        }
    }
}

/** Lista de usuarios del panel admin con gestión de roles. */
class UserFormViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    val users: StateFlow<UiState<List<SystemUser>>> = retryTrigger
        .flatMapLatest { userRepository.observeUsers().toUiState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), UiState.Loading)

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun consumeMessage() {
        _message.value = null
    }

    fun updateRole(uid: String?, role: String) {
        if (uid.isNullOrBlank()) {
            _message.value = "Selecciona un usuario de la lista."
            return
        }
        viewModelScope.launch {
            userRepository.updateRole(uid, role)
                .onSuccess { _message.value = "Rol actualizado." }
                .onFailure { _message.value = "Error al actualizar el rol: ${it.message}" }
        }
    }

    fun removeUser(uid: String) {
        if (uid.isBlank()) {
            _message.value = "Selecciona un usuario de la lista."
            return
        }
        viewModelScope.launch {
            userRepository.removeUserDoc(uid)
                .onSuccess { _message.value = "Usuario eliminado del panel." }
                .onFailure { _message.value = "Error al eliminar: ${it.message}" }
        }
    }

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
    private val teamRepository: TeamRepository,
    private val stadiumRepository: StadiumRepository,
    private val matchRepository: MatchRepository
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

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun consumeMessage() {
        _message.value = null
    }

    fun createMatch(date: String, matchday: String, time: String, stadiumName: String, homeTeamName: String, awayTeamName: String) {
        val teamsData = (teams.value as? UiState.Success)?.data.orEmpty()
        val stadiumsData = (stadiums.value as? UiState.Success)?.data.orEmpty()

        val home = teamsData.firstOrNull { it.name == homeTeamName }
        val away = teamsData.firstOrNull { it.name == awayTeamName }
        val parsedDate = parseDate(date)

        when {
            parsedDate == null -> { _message.value = "Fecha inválida. Usa el formato dd/MM/yyyy."; return }
            matchday.toIntOrNull() == null || matchday.toInt() <= 0 -> { _message.value = "Escribe una jornada válida."; return }
            time.isBlank() -> { _message.value = "Escribe la hora del partido."; return }
            home == null -> { _message.value = "Selecciona un equipo local válido."; return }
            away == null -> { _message.value = "Selecciona un equipo visitante válido."; return }
            home.id == away.id -> { _message.value = "El local y el visitante no pueden ser el mismo equipo."; return }
        }

        viewModelScope.launch {
            matchRepository.createMatch(
                dayLabel = parsedDate.first,
                shortDate = parsedDate.second,
                matchday = matchday.toInt(),
                time = time.trim(),
                homeTeamId = home.id,
                awayTeamId = away.id,
                stadiumName = stadiumName.trim(),
                status = MatchStatus.PROGRAMADO
            )
                .onSuccess { _message.value = "Partido programado." }
                .onFailure { _message.value = "Error al programar: ${it.message}" }
        }
    }

    fun deleteMatch(id: String) {
        viewModelScope.launch {
            matchRepository.deleteMatch(id)
                .onSuccess { _message.value = "Partido eliminado." }
                .onFailure { _message.value = "Error al eliminar: ${it.message}" }
        }
    }

    fun retry() {
        retryTrigger.value += 1
    }

    private fun parseDate(date: String): Pair<String, String>? = try {
        val localDate = LocalDate.parse(date.trim(), DateTimeFormatter.ofPattern("d/M/yyyy"))
        val dayLabel = localDate
            .format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "CL")))
            .replaceFirstChar { it.uppercase() }
        val shortDate = localDate.format(DateTimeFormatter.ofPattern("d MMM", Locale("es", "CL")))
        dayLabel to shortDate
    } catch (exception: Exception) {
        null
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

/** Sincroniza la tabla de posiciones real 2026 desde Wikipedia. */
class WikipediaSyncViewModel : ViewModel() {

    private val standingsRepository = WikipediaStandingsRepository()

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun consumeMessage() {
        _message.value = null
    }

    fun sync() {
        if (_busy.value) return
        _busy.value = true
        viewModelScope.launch {
            standingsRepository.syncStandings()
                .onSuccess { _message.value = it }
                .onFailure { _message.value = "Error: ${it.message}" }
            _busy.value = false
        }
    }

    companion object {
        val Factory = viewModelFactory { initializer { WikipediaSyncViewModel() } }
    }
}

/** Tabla de posiciones y partidos disponibles para el acta de resultados. */
class ActaViewModel(private val matchRepository: MatchRepository) : ViewModel() {

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

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun consumeMessage() {
        _message.value = null
    }

    fun updateResult(matchId: String?, homeGoals: String, awayGoals: String, statusIndex: Int) {
        if (matchId.isNullOrBlank()) {
            _message.value = "Selecciona un partido del acta."
            return
        }
        val homeValue = homeGoals.toIntOrNull()
        val awayValue = awayGoals.toIntOrNull()
        if (homeValue == null || awayValue == null || homeValue < 0 || awayValue < 0) {
            _message.value = "Escribe marcadores válidos."
            return
        }
        val status = when (statusIndex) {
            0 -> MatchStatus.PENDIENTE
            1 -> MatchStatus.EN_JUEGO
            else -> MatchStatus.FINALIZADO
        }
        viewModelScope.launch {
            matchRepository.updateMatchResult(matchId, homeValue, awayValue, status)
                .onSuccess { _message.value = "Resultado actualizado." }
                .onFailure { _message.value = "Error al actualizar: ${it.message}" }
        }
    }

    fun retry() {
        retryTrigger.value += 1
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { ActaViewModel(AppContainer.matchRepository) }
        }
    }
}

/** Invitación de un usuario al panel (quedan registradas en la colección `invites`). */
class InviteUserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun consumeMessage() {
        _message.value = null
    }

    fun createInvite(email: String, role: String) {
        if (_busy.value) return
        if (email.isBlank() || !email.contains("@")) {
            _message.value = "Escribe un correo válido."
            return
        }
        _busy.value = true
        viewModelScope.launch {
            userRepository.createInvite(email.trim(), role)
                .onSuccess { _message.value = "Invitación enviada." }
                .onFailure { _message.value = "Error al invitar: ${it.message}" }
            _busy.value = false
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { InviteUserViewModel(AppContainer.userRepository) }
        }
    }
}

/** Siembra el dataset inicial de la Liga de Primera 2026 en Firestore (solo si está vacío). */
class SeedViewModel : ViewModel() {

    private val seedRepository = FirestoreSeedRepository()

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun consumeMessage() {
        _message.value = null
    }

    fun seed() {
        if (_busy.value) return
        _busy.value = true
        viewModelScope.launch {
            seedRepository.seedLiga2026()
                .onSuccess { _message.value = it }
                .onFailure { _message.value = it.message ?: "Error al sembrar los datos." }
            _busy.value = false
        }
    }

    companion object {
        val Factory = viewModelFactory { initializer { SeedViewModel() } }
    }
}

/** Sincronización con ESPN (botón admin + automática cada 6 h). */
class EspnSyncViewModel(private val syncRepository: EspnSyncRepository) : ViewModel() {

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun consumeMessage() {
        _message.value = null
    }

    fun sync(context: Context, force: Boolean) {
        if (_busy.value) return
        _busy.value = true
        viewModelScope.launch {
            syncRepository.sync(context, force)
                .onSuccess { _message.value = it }
                .onFailure { _message.value = it.message ?: "Error de sincronización." }
            _busy.value = false
        }
    }

    fun syncIfDue(context: Context) {
        viewModelScope.launch {
            syncRepository.sync(context, force = false)
        }
    }

    companion object {
        val Factory = viewModelFactory { initializer { EspnSyncViewModel(EspnSyncRepository()) } }
    }
}
