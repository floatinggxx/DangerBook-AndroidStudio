
package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.DangerBook.data.remoto.service.UsuarioApiService

class UserListViewModelFactory(
    private val usuarioApiService: UsuarioApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserListViewModel(usuarioApiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
