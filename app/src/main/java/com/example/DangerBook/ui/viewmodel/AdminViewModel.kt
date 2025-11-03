package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.DangerBook.data.repository.AppointmentRepository
import com.example.DangerBook.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminDashboardUiState(
    val totalAppointments: Int = 0,
    val totalUsers: Int = 0,
    val totalBarbers: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

class AdminViewModel(
    private val userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardStats()
    }

    private fun loadDashboardStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val appointmentCount = appointmentRepository.getTotalAppointmentsCount()
                val userCount = userRepository.getUsersByRole("user").first().size
                val barberCount = userRepository.getUsersByRole("barber").first().size

                _uiState.update {
                    it.copy(
                        totalAppointments = appointmentCount,
                        totalUsers = userCount,
                        totalBarbers = barberCount,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar las estadísticas: ${e.message}") }
            }
        }
    }
}