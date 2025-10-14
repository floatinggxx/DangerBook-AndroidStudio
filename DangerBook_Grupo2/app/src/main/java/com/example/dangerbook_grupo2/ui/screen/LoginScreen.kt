package com.example.dangerbook_grupo2.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable // Pantalla Login (solo navegación, sin formularios)
fun LoginScreen(
    onLoginOk: () -> Unit,   // Acción para “volver” a Home
    onGoRegister: () -> Unit // Acción para ir a Registro
) {
    val bg = MaterialTheme.colorScheme.secondaryContainer // Fondo distinto para contraste

    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo
            .background(bg) // Fondo
            .padding(16.dp), // Margen
        contentAlignment = Alignment.Center // Centro
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally // Centrado horizontal
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineSmall // Título
            )
            Spacer(Modifier.height(12.dp)) // Separación
            Text(
                text = "Pantalla de Login (demo). Usa la barra superior, el menú lateral o los botones.",
                textAlign = TextAlign.Center // Alineación centrada
            )
            Spacer(Modifier.height(20.dp)) // Separación

            // Fila de botones para practicar Row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { // Espaciado
                Button(onClick = onLoginOk) { Text("Ir a Home") } // Navega a Home
                OutlinedButton(onClick = onGoRegister) { Text("Ir a Registro") } // A Registro
            }
        }
    }
}