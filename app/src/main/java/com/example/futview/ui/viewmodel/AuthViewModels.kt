package com.example.futview.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.futview.data.AppContainer
import com.example.futview.data.model.AuthUser
import com.example.futview.data.repository.AuthRepository
import com.example.futview.data.repository.authErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Estado global de sesión: se emite al arrancar la app y con cada cambio de sesión. */
class AuthStateViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val authUser: StateFlow<UiState<AuthUser?>> =
        authRepository.observeAuth()
            .map { user -> UiState.Success(user) as UiState<AuthUser?> }
            .catch { emit(UiState.Error(it.message ?: "Error de autenticación.")) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Loading)

    fun signOut() {
        authRepository.signOut()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { AuthStateViewModel(AppContainer.authRepository) }
        }
    }
}

/** Login con correo y contraseña. */
class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun signIn(email: String, password: String) {
        if (_busy.value) return
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Escribe tu correo y contraseña."
            return
        }
        _busy.value = true
        _error.value = null
        viewModelScope.launch {
            authRepository.signIn(email, password)
                .onFailure { _error.value = authErrorMessage(it) }
            _busy.value = false
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { LoginViewModel(AppContainer.authRepository) }
        }
    }
}

/** Registro con nombre, correo y contraseña. Al terminar envía verificación de email. */
class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _registered = MutableStateFlow(false)
    val registered: StateFlow<Boolean> = _registered.asStateFlow()

    fun signUp(name: String, email: String, password: String, confirm: String) {
        if (_busy.value) return
        when {
            name.isBlank() -> { _error.value = "Escribe tu nombre completo."; return }
            email.isBlank() -> { _error.value = "Escribe tu correo electrónico."; return }
            password.length < 6 -> { _error.value = "La contraseña debe tener al menos 6 caracteres."; return }
            password != confirm -> { _error.value = "Las contraseñas no coinciden."; return }
        }
        _busy.value = true
        _error.value = null
        viewModelScope.launch {
            authRepository.signUp(name, email, password)
                .onSuccess { _registered.value = true }
                .onFailure { _error.value = authErrorMessage(it) }
            _busy.value = false
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { SignUpViewModel(AppContainer.authRepository) }
        }
    }
}

/** Recuperación de contraseña por correo. */
class RecoverViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _sent = MutableStateFlow(false)
    val sent: StateFlow<Boolean> = _sent.asStateFlow()

    fun sendReset(email: String) {
        if (_busy.value) return
        if (email.isBlank()) {
            _error.value = "Escribe el correo de tu cuenta."
            return
        }
        _busy.value = true
        _error.value = null
        viewModelScope.launch {
            authRepository.sendPasswordReset(email)
                .onSuccess { _sent.value = true }
                .onFailure { _error.value = authErrorMessage(it) }
            _busy.value = false
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { RecoverViewModel(AppContainer.authRepository) }
        }
    }
}

/** Cambio de contraseña con sesión activa (desde Configuración). */
class NewPasswordViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    fun changePassword(password: String, confirm: String) {
        if (_busy.value) return
        when {
            password.length < 6 -> { _error.value = "La contraseña debe tener al menos 6 caracteres."; return }
            password != confirm -> { _error.value = "Las contraseñas no coinciden."; return }
        }
        _busy.value = true
        _error.value = null
        viewModelScope.launch {
            authRepository.changePassword(password)
                .onSuccess {
                    // Fuerza un nuevo inicio de sesión con la contraseña actualizada.
                    authRepository.signOut()
                    _saved.value = true
                }
                .onFailure { _error.value = authErrorMessage(it) }
            _busy.value = false
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { NewPasswordViewModel(AppContainer.authRepository) }
        }
    }
}

/** Reenvío del enlace pendiente (verificación de email o recuperación de contraseña). */
class CheckEmailViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _info = MutableStateFlow<String?>(null)
    val info: StateFlow<String?> = _info.asStateFlow()

    fun resend(email: String) {
        if (_busy.value) return
        _busy.value = true
        _info.value = null
        viewModelScope.launch {
            authRepository.resendLink(email)
                .onSuccess { _info.value = "Enlace enviado. Revisa tu bandeja de entrada." }
                .onFailure { _info.value = authErrorMessage(it) }
            _busy.value = false
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { CheckEmailViewModel(AppContainer.authRepository) }
        }
    }
}
