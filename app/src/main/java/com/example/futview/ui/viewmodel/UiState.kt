package com.example.futview.ui.viewmodel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Estado de pantalla: carga, datos o error. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

/** Envuelve un flujo del repositorio en [UiState], capturando errores de conexión. */
internal fun <T> Flow<T>.toUiState(): Flow<UiState<T>> =
    map { UiState.Success(it) as UiState<T> }
        .catch { emit(UiState.Error(it.message ?: "No se pudo conectar. Inténtalo de nuevo.")) }
