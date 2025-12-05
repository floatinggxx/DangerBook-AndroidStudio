
package com.example.DangerBook.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.DangerBook.data.remoto.dto.usuarios.UsuarioDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    users: List<UsuarioDto>,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Usuarios") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(users) { user ->
                UserItem(user = user)
            }
        }
    }
}

@Composable
fun UserItem(user: UsuarioDto) {
    Column(modifier = Modifier.padding()) {
        Text(text = "Nombre: ${user.nombre}")
        Text(text = "Email: ${user.email}")
    }
}
