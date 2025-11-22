package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.DangerBook.data.local.service.ServiceEntity
import com.example.DangerBook.data.local.barbero.BarberEntity
import com.example.DangerBook.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de UI para la pantalla de servicios
data class ServicesUiState(
    val services: List<ServiceEntity> = emptyList(),
    val barbers: List<BarberEntity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMsg: String? = null
)

// ViewModel para gestionar los servicios y barberos
class ServicesViewModel(
    private val repository: ServicioRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ServicesUiState())
    val state: StateFlow<ServicesUiState> = _state.asStateFlow()

    init {
        // Al iniciar, refrescar y cargar el contenido.
        loadAndRefreshContent()
    }

    private fun loadAndRefreshContent() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }

            // 1. Intentar refrescar los datos de servicios desde la API.
            // Si falla, el error se mostrará, pero la app seguirá funcionando con datos locales si existen.
            repository.refreshServices().onFailure { error ->
                _state.update { it.copy(errorMsg = "Fallo al sincronizar: ${error.message}") }
            }

            // 2. Lanzar colectores para observar la base de datos local.
            // Estos se actualizarán automáticamente cuando 'refreshServices' inserte los nuevos datos.
            launch {
                repository.getAllServices().collectLatest { services ->
                    _state.update { it.copy(services = services, isLoading = false) } // La carga principal termina al recibir los servicios
                }
            }
            launch {
                repository.getAllAvailableBarbers().collectLatest { barbers ->
                    _state.update { it.copy(barbers = barbers) }
                }
            }
        }
    }

    // Función pública para permitir refrescar manualmente (ej: con un pull-to-refresh en la UI)
    fun onRefresh() {
        loadAndRefreshContent()
    }
}
