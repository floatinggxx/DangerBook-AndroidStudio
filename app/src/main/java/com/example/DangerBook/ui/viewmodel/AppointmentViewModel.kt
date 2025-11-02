package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.DangerBook.data.local.appointment.AppointmentEntity
import com.example.DangerBook.data.local.storage.UserPreferences
import com.example.DangerBook.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

sealed interface BookingResult {
    object Success : BookingResult
}

// Estado para agendar una nueva cita
data class BookAppointmentUiState(
    val selectedServiceId: Long? = null,
    val selectedBarberId: Long? = null,
    val selectedDate: Calendar? = null,
    val selectedTimeSlot: Long? = null,
    val availableTimeSlots: List<Long> = emptyList(),
    val notes: String = "",
    val isLoadingSlots: Boolean = false,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val errorMsg: String? = null
)

// Estado para visualizar las citas del usuario
data class MyAppointmentsUiState(
    val upcomingAppointments: List<AppointmentEntity> = emptyList(),
    val allAppointments: List<AppointmentEntity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMsg: String? = null
)

// ViewModel para gestionar las citas
class AppointmentViewModel(
    private val repository: AppointmentRepository,
    private val userPreferences: UserPreferences // Gestor de sesión para obtener el ID de usuario reactivamente
) : ViewModel() {

    private val _bookState = MutableStateFlow(BookAppointmentUiState())
    val bookState: StateFlow<BookAppointmentUiState> = _bookState

    private val _bookingResult = MutableSharedFlow<BookingResult>()
    val bookingResult = _bookingResult.asSharedFlow()

    private val _myAppointmentsState = MutableStateFlow(MyAppointmentsUiState())
    val myAppointmentsState: StateFlow<MyAppointmentsUiState> = _myAppointmentsState

    init {
        // Cargar citas del usuario al iniciar y cada vez que el usuario cambie
        loadUserAppointments()
    }

    private fun loadUserAppointments() {
        viewModelScope.launch {
            // Reaccionar a cambios en el ID de usuario (login/logout)
            userPreferences.userId.collectLatest { userId ->
                if (userId != null && userId != -1L) {
                    _myAppointmentsState.update { it.copy(isLoading = true, errorMsg = null) }
                    try {
                        repository.getUserAppointments(userId).collectLatest { allUserAppointments ->
                            val upcoming = allUserAppointments.filter { it.status in listOf("pending", "confirmed") }
                            _myAppointmentsState.update {
                                it.copy(
                                    isLoading = false,
                                    allAppointments = allUserAppointments,
                                    upcomingAppointments = upcoming
                                )
                            }
                        }
                    } catch (e: Exception) {
                        _myAppointmentsState.update {
                            it.copy(isLoading = false, errorMsg = "Error al cargar las citas: ${e.message}")
                        }
                    }
                } else {
                    // Si no hay usuario, limpiar el estado de las citas
                    _myAppointmentsState.update { MyAppointmentsUiState(isLoading = false) }
                }
            }
        }
    }

    // ---- Handlers para agendar cita ----

    fun onSelectService(serviceId: Long, durationMinutes: Int) {
        _bookState.update {
            it.copy(selectedServiceId = serviceId, selectedTimeSlot = null, availableTimeSlots = emptyList())
        }
        val state = _bookState.value
        if (state.selectedDate != null && state.selectedBarberId != null) {
            loadAvailableTimeSlots(state.selectedBarberId, state.selectedDate, durationMinutes)
        }
        recomputeCanSubmit()
    }

    fun onSelectBarber(barberId: Long?) {
        _bookState.update {
            it.copy(selectedBarberId = barberId, selectedTimeSlot = null, availableTimeSlots = emptyList())
        }
        recomputeCanSubmit()
    }

    fun onSelectDate(date: Calendar, serviceDurationMinutes: Int) {
        _bookState.update {
            it.copy(selectedDate = date, selectedTimeSlot = null, availableTimeSlots = emptyList())
        }
        val barberId = _bookState.value.selectedBarberId
        if (barberId != null) {
            loadAvailableTimeSlots(barberId, date, serviceDurationMinutes)
        }
        recomputeCanSubmit()
    }

    fun onSelectTimeSlot(timeSlot: Long) {
        _bookState.update { it.copy(selectedTimeSlot = timeSlot) }
        recomputeCanSubmit()
    }

    fun onNotesChange(notes: String) {
        _bookState.update { it.copy(notes = notes) }
    }

    private fun loadAvailableTimeSlots(barberId: Long, date: Calendar, durationMinutes: Int) {
        viewModelScope.launch {
            _bookState.update { it.copy(isLoadingSlots = true) }
            try {
                val slots = repository.getAvailableTimeSlotsForDay(barberId, date, durationMinutes)
                _bookState.update { it.copy(availableTimeSlots = slots, isLoadingSlots = false) }
            } catch (e: Exception) {
                _bookState.update { it.copy(isLoadingSlots = false, errorMsg = "Error al cargar horarios: ${e.message}") }
            }
        }
    }

    private fun recomputeCanSubmit() {
        val s = _bookState.value
        val canSubmit = s.selectedServiceId != null && s.selectedBarberId != null && s.selectedDate != null && s.selectedTimeSlot != null
        _bookState.update { it.copy(canSubmit = canSubmit) }
    }

    fun submitBooking(serviceDurationMinutes: Int) {
        val state = _bookState.value
        if (!state.canSubmit || state.isSubmitting) return

        val currentBarberId = state.selectedBarberId
        val currentServiceId = state.selectedServiceId
        val currentTimeSlot = state.selectedTimeSlot

        if (currentBarberId == null || currentServiceId == null || currentTimeSlot == null) {
            _bookState.update { it.copy(errorMsg = "Por favor, complete todos los campos requeridos.") }
            return
        }

        viewModelScope.launch {
            _bookState.update { it.copy(isSubmitting = true, errorMsg = null) }

            val currentUserId = userPreferences.userId.first()
            if (currentUserId == null || currentUserId == -1L) {
                _bookState.update { it.copy(isSubmitting = false, errorMsg = "Debes iniciar sesión para agendar una cita.") }
                return@launch
            }

            try {
                val result = repository.createAppointment(
                    userId = currentUserId,
                    barberId = currentBarberId,
                    serviceId = currentServiceId,
                    dateTime = currentTimeSlot,
                    durationMinutes = serviceDurationMinutes,
                    notes = state.notes.ifBlank { null }
                )

                if (result.isSuccess) {
                    _bookState.update { it.copy(isSubmitting = false) }
                    _bookingResult.emit(BookingResult.Success)
                } else {
                    _bookState.update { it.copy(isSubmitting = false, errorMsg = result.exceptionOrNull()?.message ?: "Error desconocido al agendar") }
                }
            } catch (e: Exception) {
                _bookState.update { it.copy(isSubmitting = false, errorMsg = "Error inesperado: ${e.message}") }
            }
        }
    }

    fun clearBookingState() {
        _bookState.update { BookAppointmentUiState() }
    }

    private fun executeAppointmentAction(action: suspend () -> Result<Unit>, errorPrefix: String) {
        viewModelScope.launch {
            _myAppointmentsState.update { it.copy(errorMsg = null) }
            try {
                val result = action()
                if (result.isFailure) {
                    _myAppointmentsState.update { it.copy(errorMsg = "$errorPrefix: ${result.exceptionOrNull()?.message}") }
                }
            } catch (e: Exception) {
                _myAppointmentsState.update { it.copy(errorMsg = "$errorPrefix: ${e.message}") }
            }
        }
    }

    fun cancelAppointment(appointmentId: Long) {
        executeAppointmentAction(
            action = { repository.cancelAppointment(appointmentId) },
            errorPrefix = "Error al cancelar"
        )
    }

    fun confirmAppointment(appointmentId: Long) {
        executeAppointmentAction(
            action = { repository.confirmAppointment(appointmentId) },
            errorPrefix = "Error al confirmar"
        )
    }

    fun completeAppointment(appointmentId: Long) {
        executeAppointmentAction(
            action = { repository.completeAppointment(appointmentId) },
            errorPrefix = "Error al completar"
        )
    }

    fun loadBarberAppointments(barberId: Long) {
        viewModelScope.launch {
            try {
                _myAppointmentsState.update { it.copy(isLoading = true, errorMsg = null) }

                repository.getBarberAppointments(barberId).collectLatest { appointments ->
                    _myAppointmentsState.update {
                        it.copy(
                            allAppointments = appointments,
                            upcomingAppointments = appointments.filter { it.status in listOf("pending", "confirmed") },
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _myAppointmentsState.update {
                    it.copy(isLoading = false, errorMsg = "Error al cargar citas: ${e.message}")
                }
            }
        }
    }
}