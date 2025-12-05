
package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.DangerBook.data.remoto.dto.usuarios.UsuarioDto
import com.example.DangerBook.data.remoto.service.UsuarioApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserListViewModel(
    private val usuarioApiService: UsuarioApiService
) : ViewModel() {

    private val _users = MutableStateFlow<List<UsuarioDto>>(emptyList())
    val users: StateFlow<List<UsuarioDto>> = _users.asStateFlow()

    init {
        getUsers()
    }

    private fun getUsers() {
        viewModelScope.launch {
            try {
                _users.value = usuarioApiService.findAll()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
