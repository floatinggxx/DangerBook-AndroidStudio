package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.DangerBook.data.local.storage.UserPreferences
import com.example.DangerBook.data.repository.CitaRepository

// Factory para crear instancias de AppointmentViewModel con sus nuevas dependencias
class CitaViewModelFactory(
    private val repository: CitaRepository,
    private val userPreferences: UserPreferences // Ahora recibe el gestor de sesión
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Pasa las nuevas dependencias al ViewModel
            return AppointmentViewModel(repository, userPreferences) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}