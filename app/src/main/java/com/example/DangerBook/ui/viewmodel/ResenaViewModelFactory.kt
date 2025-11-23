package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.DangerBook.data.repository.ResenaRepository

class ResenaViewModelFactory(
    private val repository: ResenaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResenaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ResenaViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}