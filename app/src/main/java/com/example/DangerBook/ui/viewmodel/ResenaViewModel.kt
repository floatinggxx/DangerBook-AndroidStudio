package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.DangerBook.data.repository.ResenaRepository
import com.example.DangerBook.data.remoto.dto.resenas.ResenaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

// Estado de la UI para la pantalla de reseñas
data class ResenasUiState(
    val resenas: List<ResenaDto> = emptyList(),
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val isSubmitting: Boolean = false, // Para el formulario de nueva reseña
    val submissionSuccess: Boolean = false // Para saber cuándo la reseña se envió bien
)

class ResenaViewModel(
    private val repository: ResenaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResenasUiState())
    val uiState: StateFlow<ResenasUiState> = _uiState.asStateFlow()

    init {
        // Cargar las reseñas al iniciar el ViewModel
        loadResenas()
    }

    // Carga las reseñas desde el repositorio
    fun loadResenas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }

            val result = repository.findAll()

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, resenas = result.getOrThrow()) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMsg = "Error al cargar las reseñas: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    // Envía una nueva reseña
    fun submitResena(comentario: String, calificacion: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMsg = null, submissionSuccess = false) }

            // Crear el objeto DTO para enviar a la API
            val nuevaResena = ResenaDto(
                f_publicacion = LocalDate.now().toString(),
                comentario = comentario,
                calificacion = calificacion,
                f_baneo = null,
                motivo_baneo = null
            )

            val result = repository.save(nuevaResena)

            if (result.isSuccess) {
                _uiState.update { it.copy(isSubmitting = false, submissionSuccess = true) }
                // Refrescar la lista de reseñas después de enviar una nueva
                loadResenas()
            } else {
                _uiState.update { it.copy(isSubmitting = false, errorMsg = "Error al enviar la reseña: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    fun deleteResena(id: Int) {
        viewModelScope.launch {
            val result = repository.delete(id)

            if (result.isSuccess) {
                _uiState.update { currentState ->
                    val updatedResenas = currentState.resenas.filterNot { it.id_resena == id }
                    currentState.copy(resenas = updatedResenas)
                }
            } else {
                _uiState.update { it.copy(errorMsg = "Error al eliminar la reseña: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    // Resetea el estado de éxito para evitar que la UI reaccione múltiples veces
    fun resetSubmissionStatus() {
        _uiState.update { it.copy(submissionSuccess = false, errorMsg = null) }
    }
}
