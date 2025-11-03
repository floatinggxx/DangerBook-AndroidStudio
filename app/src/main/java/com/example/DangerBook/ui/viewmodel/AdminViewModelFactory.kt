package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.DangerBook.data.repository.AppointmentRepository
import com.example.DangerBook.data.repository.UserRepository

class AdminViewModelFactory(
    private val userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(userRepository, appointmentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}