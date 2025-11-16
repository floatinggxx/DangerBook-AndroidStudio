package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.DangerBook.data.repository.CitaRepository
import com.example.DangerBook.data.repository.UsuarioRepository

class AdminViewModelFactory(
    private val usuarioRepository: UsuarioRepository,
    private val citaRepository: CitaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(usuarioRepository, citaRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}